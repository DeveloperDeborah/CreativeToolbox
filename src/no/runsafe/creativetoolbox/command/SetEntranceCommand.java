package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotEntrance;
import no.runsafe.creativetoolbox.database.PlotEntranceRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.IRegionControl;

import java.util.List;

public class SetEntranceCommand extends PlayerAsyncCommand
{
	public SetEntranceCommand(
		PlotEntranceRepository repository,
		IDebug output,
		PlotFilter filter,
		IScheduler scheduler,
		IRegionControl worldGuard,
		PlotManager manager)
	{
		super("setentrance", "define where users teleport to in a plot.", null, scheduler);
		this.repository = repository;
		this.debugger = output;
		this.plotFilter = filter;
		this.worldGuard = worldGuard;
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		if (manager.isInWrongWorld(executor))
			return Config.Message.wrongWorld;

		String currentRegion = getCurrentRegion(executor);
		if (currentRegion == null)
			return Config.Message.Plot.invalid;

		debugger.debugFine(String.format("Player is in region %s", currentRegion));
		if (!(executor.hasPermission("runsafe.creative.entrance.set")
			|| worldGuard.getOwnerPlayers(executor.getWorld(), currentRegion).contains(executor)))
			return String.format(Config.Message.Plot.SetEntrance.failNoPermission, currentRegion);

		return super.OnExecute(executor, parameters);
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		if (manager.isInWrongWorld(executor))
			return Config.Message.wrongWorld;

		String currentRegion = getCurrentRegion(executor);
		if (currentRegion == null)
			return Config.Message.Plot.invalid;

		debugger.debugFine(String.format("Player is in region %s", currentRegion));
		if (!(executor.hasPermission("runsafe.creative.entrance.set")
			|| worldGuard.getOwnerPlayers(executor.getWorld(), currentRegion).contains(executor)))
			return String.format(Config.Message.Plot.SetEntrance.failNoPermission, currentRegion);

		PlotEntrance entrance = new PlotEntrance();
		entrance.setName(currentRegion);
		entrance.setLocation(executor.getLocation());
		repository.persist(entrance);
		return String.format(Config.Message.Plot.SetEntrance.success, currentRegion);
	}

	private String getCurrentRegion(IPlayer player)
	{
		List<String> regions = plotFilter.apply(worldGuard.getRegionsAtLocation(player.getLocation()));
		if (regions == null || regions.isEmpty())
			return null;
		return regions.get(0);
	}

	private final PlotEntranceRepository repository;
	private final IDebug debugger;
	private final PlotFilter plotFilter;
	private final IRegionControl worldGuard;
	private final PlotManager manager;
}
