package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.awt.geom.Rectangle2D;
import java.util.List;

public class ClaimCommand extends PlayerCommand
{
	public ClaimCommand(
		PlotManager manager, PlotCalculator calculator, IRegionControl worldGuard,
		PlotMemberRepository members, ApprovedPlotRepository approvalRepository)
	{
		super("claim", "Claims a plot", "runsafe.creative.claim.self", new Player().onlineOnly().defaultToExecutor());
		this.manager = manager;
		this.calculator = calculator;
		this.worldGuard = worldGuard;
		this.members = members;
		this.approvalRepository = approvalRepository;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList params)
	{
		if (manager.isInWrongWorld(executor))
			return Config.Message.wrongWorld;

		String current = manager.getCurrentRegionFiltered(executor);
		if (current != null)
			return String.format(Config.Message.Plot.Claim.failPreexisting, current);

		if (!manager.isCurrentClaimable(executor))
			return Config.Message.Plot.Claim.failNotClaimable;

		Rectangle2D region = calculator.getPlotArea(executor.getLocation());
		if (region == null)
			return Config.Message.Plot.invalid;

		IWorld world = executor.getWorld();
		IPlayer owner = params.getValue("player");
		if (owner == null)
			return null;
		boolean selfClaim = owner.getName().equals(executor.getName());
		if (!(executor.hasPermission("runsafe.creative.claim.others") || selfClaim))
			return Config.Message.Plot.Claim.failOtherNoPermissions;

		List<String> existing = worldGuard.getOwnedRegions(owner, world);
		console.debugFine("%s has %d plots.", owner, existing.size());
		if (!existing.isEmpty() && selfClaim)
		{
			for (String plot : existing)
			{
				PlotApproval approved = approvalRepository.get(plot);
				console.debugFine("Plot %s is %s.", plot, approved != null ? "approved" : "unapproved");
				if (approved == null)
					return Config.Message.Plot.Claim.failPreviousNotApproved;
			}
		}

		int n = 1;
		String plotName = String.format("%s_%%d", owner.getName().toLowerCase());
		while (existing.contains(String.format(plotName, n)))
			n++;
		plotName = String.format(plotName, n);

		if (manager.claim(executor, owner, plotName, region))
		{
			members.addMember(plotName, owner, true);
			if (owner == executor)
				return String.format(Config.Message.Plot.Claim.succeedSelf, plotName, n);

			return String.format(Config.Message.Plot.Claim.succeedOther, plotName, owner.getPrettyName());
		}

		return String.format(Config.Message.Plot.Claim.fail, owner.getPrettyName());
	}

	private final PlotManager manager;
	private final PlotCalculator calculator;
	private final IRegionControl worldGuard;
	private final PlotMemberRepository members;
	private final ApprovedPlotRepository approvalRepository;
}
