package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.creativetoolbox.event.PlotMembershipGrantedEvent;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddCommand extends PlayerAsyncCommand
{
	public AddCommand(IScheduler scheduler, PlotFilter filter, IRegionControl worldGuard, PlotMemberRepository members, PlotMemberBlacklistRepository blacklistRepository)
	{
		super("add", "Add a member to the plot you are standing in", "runsafe.creative.member.add", scheduler, new Player().require());
		plotFilter = filter;
		worldGuardInterface = worldGuard;
		this.members = members;
		this.blacklistRepository = blacklistRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		IPlayer member = parameters.getValue("player");

		if (member == null)
			return null;

		if (blacklistRepository.isBlacklisted(member))
			return String.format(Config.Message.Plot.Member.addFailBlacklisted, member.getPrettyName());

		List<String> target = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(executor.getLocation()));
		List<String> ownedRegions = worldGuardInterface.getOwnedRegions(executor, plotFilter.getWorld());
		if (target == null || target.isEmpty())
			return Config.Message.Plot.invalid;
		List<String> results = new ArrayList<>();
		for (String region : target)
		{
			if (ownedRegions.contains(region) || executor.hasPermission("runsafe.creative.member.override"))
			{
				if (worldGuardInterface.addMemberToRegion(plotFilter.getWorld(), region, member))
				{
					members.addMember(region, member, false);
					results.add(String.format(Config.Message.Plot.Member.addSuccess, member.getPrettyName(), region));
					new PlotMembershipGrantedEvent(member, region).Fire();
				}
				else
					results.add(String.format(Config.Message.Plot.Member.addFail, member.getPrettyName(), region));
			}
			else
				results.add(String.format(Config.Message.Plot.notOwner, region));
		}
		return StringUtils.join(results, "\n");
	}

	private final IRegionControl worldGuardInterface;
	private final PlotFilter plotFilter;
	private final PlotMemberRepository members;
	private final PlotMemberBlacklistRepository blacklistRepository;
}
