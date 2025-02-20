package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.event.SyncInteractEvents;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteHereCommand extends PlayerAsyncCommand
{
	public DeleteHereCommand(
		PlotManager manager, PlotFilter filter,
		IRegionControl worldGuard,
		PlotCalculator plotCalculator,
		SyncInteractEvents interactEvents,
		IScheduler scheduler, ApprovedPlotRepository approvedPlotRepository)
	{
		super("deletehere", "delete the region you are in.", "runsafe.creative.delete", scheduler);
		this.manager = manager;
		this.filter = filter;
		this.worldGuard = worldGuard;
		this.plotCalculator = plotCalculator;
		this.interactEvents = interactEvents;
		this.approvedPlotRepository = approvedPlotRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (manager.isInWrongWorld(executor))
			return Config.Message.wrongWorld;
		List<String> delete = filter.apply(worldGuard.getRegionsAtLocation(executor.getLocation()));
		if (delete == null || delete.isEmpty())
			return Config.Message.Plot.invalid;
		Map<String, Rectangle2D> regions = new HashMap<>();
		for (String region : delete)
		{
			PlotApproval approval = approvedPlotRepository.get(region);
			if (approval != null && approval.getApproved() != null)
				return Config.Message.Plot.Delete.failApproved;

			Rectangle2D area = plotCalculator.pad(worldGuard.getRectangle(executor.getWorld(), region));
			regions.put(region, area);
		}
		interactEvents.startDeletion(executor, regions);
		return String.format(Config.Message.Plot.Delete.rightClick, regions.size(), regions.size() > 1 ? "s" : "");
	}

	private final PlotManager manager;
	private final PlotFilter filter;
	private final IRegionControl worldGuard;
	private final PlotCalculator plotCalculator;
	private final SyncInteractEvents interactEvents;
	private final ApprovedPlotRepository approvedPlotRepository;
}
