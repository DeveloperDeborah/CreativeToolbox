package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.event.InteractEvents;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ExtendCommand extends PlayerCommand
{
	public ExtendCommand(PlotManager manager, InteractEvents interact)
	{
		super("extend", "Extends a plot", "runsafe.creative.extend");
		this.manager = manager;
		interactEvents = interact;
	}

	@Override
	public String OnExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return Config.Message.wrongWorld;

		String target = manager.getCurrentRegionFiltered(player);
		if (target == null)
			return Config.Message.Plot.invalid;
		interactEvents.startPlotExtension(player, target);
		return String.format(Config.Message.Plot.Extend.rightClick, target);
	}

	private final PlotManager manager;
	private final InteractEvents interactEvents;
}
