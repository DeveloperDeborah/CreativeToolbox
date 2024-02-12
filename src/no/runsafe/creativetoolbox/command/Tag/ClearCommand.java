package no.runsafe.creativetoolbox.command.Tag;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

public class ClearCommand extends PlayerAsyncCommand
{
	public ClearCommand(IScheduler scheduler, PlotManager manager, PlotTagRepository tagRepository)
	{
		super("clear", "Clears the tags from the current plot", "runsafe.creative.tag.clear", scheduler);
		this.manager = manager;
		this.tagRepository = tagRepository;
	}

	@Override
	public String OnAsyncExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return Config.Message.wrongWorld;

		String plot = manager.getCurrentRegionFiltered(player);
		if (plot == null)
			return Config.Message.Plot.invalid;

		return tagRepository.setTags(plot, null) ?
			String.format(Config.Message.Plot.Tag.clearSuccess, plot) :
			String.format(Config.Message.Plot.Tag.clearFail, plot);
	}

	private final PlotManager manager;
	private final PlotTagRepository tagRepository;
}
