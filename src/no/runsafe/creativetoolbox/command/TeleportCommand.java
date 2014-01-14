package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlayerTeleport;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

public class TeleportCommand extends PlayerAsyncCallbackCommand<PlayerTeleport>
{
	public TeleportCommand(IScheduler scheduler, PlotManager manager, PlotFilter filter, IRegionControl worldGuard, PlotArgument plotName)
	{
		super("teleport", "teleport to a plot.", "runsafe.creative.teleport.plot", scheduler, plotName);
		this.manager = manager;
		this.filter = filter;
		this.worldGuard = worldGuard;
	}

	@Override
	public PlayerTeleport OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		PlayerTeleport target = new PlayerTeleport();
		target.who = executor;
		String plot = parameters.get("plotname");
		target.location = manager.getPlotEntrance(plot);
		if (target.location == null)
		{
			target.location = manager.getPlotEntrance(String.format("%s_%s", executor.getName(), plot));
			if (target.location != null)
				plot = String.format("%s_%s", executor.getName(), plot);
		}
		if (target.location == null)
			target.message = String.format("Plot '%s' not found.", plot);
		else
			target.message = String.format("Teleported to '%s'", plot);

		return target;
	}

	@Override
	public void SyncPostExecute(PlayerTeleport result)
	{
		if (result.location != null)
		{
			ILocation target = result.location;
			IWorld world = result.location.getWorld();
			int air = 0;
			int y = target.getBlockY();
			for (; y < 256; ++y)
			{
				if (world.getBlockAt(target.getBlockX(), y, target.getBlockZ()).isAir())
					air++;
				if (air > 1)
					break;
			}
			target.setY(y - 1);
			result.who.teleport(result.location);
		}
		result.who.sendColouredMessage(result.message);
	}

	private final PlotManager manager;
	private final PlotFilter filter;
	private final IRegionControl worldGuard;
}