package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

public class ListCommand extends PlayerAsyncCommand
{
	public ListCommand(
		RunsafeServer server,
		WorldGuardInterface worldGuard,
		PlotFilter filter,
		IScheduler scheduler, PlotManager manager, PlotList plotList)
	{
		super("list", "lists plots owned by a player.", "runsafe.creative.list", scheduler, "player");
		this.server = server;
		this.worldGuard = worldGuard;
		this.filter = filter;
		this.manager = manager;
		this.plotList = plotList;
	}

	@Override
	public String OnAsyncExecute(RunsafePlayer executor, Map<String, String> parameters)
	{
		if (!worldGuard.serverHasWorldGuard())
			return "Unable to find WorldGuard!";

		if (filter.getWorld() == null)
			return "No world defined!";

		RunsafePlayer player = server.getPlayer(parameters.get("player"));

		if (player == null)
			return "&cNo such player";

		if (player instanceof RunsafeAmbiguousPlayer)
			return player.toString();

		List<String> plots = filter.apply(worldGuard.getOwnedRegions(player, filter.getWorld()));
		List<String> property = manager.tag(executor, plots);
		if(plots.size() > 0)
			plotList.set(executor, plots);
		return String.format(
			"%d plots owned by %s:\n  %s",
			property.size(),
			player.getPrettyName(),
			StringUtils.join(property, "\n  ")
		);
	}

	private final RunsafeServer server;
	private final WorldGuardInterface worldGuard;
	private final PlotFilter filter;
	private final PlotManager manager;
	private final PlotList plotList;
}
