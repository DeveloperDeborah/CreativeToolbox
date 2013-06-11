package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.framework.RunsafePlugin;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldeditbridge.WorldEditInterface;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;

public class SelectCommand extends PlayerCommand
{
	public SelectCommand(PlotCalculator calculator)
	{
		super("select", "Sets your WorldEdit region to the plot you are in", "runsafe.creative.select");
		worldEdit = RunsafePlugin.getFirstPluginAPI(WorldEditInterface.class);
		plotCalculator = calculator;
	}

	@Override
	public String OnExecute(RunsafePlayer executor, HashMap<String, String> parameters)
	{
		Rectangle2D area = plotCalculator.getPlotArea(executor.getLocation());
		worldEdit.select(
			executor,
			plotCalculator.getMinPosition(executor.getWorld(), area),
			plotCalculator.getMaxPosition(executor.getWorld(), area)
		);
		return null;
	}

	private WorldEditInterface worldEdit;
	private final PlotCalculator plotCalculator;
}
