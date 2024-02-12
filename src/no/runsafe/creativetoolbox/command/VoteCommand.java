package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;

public class VoteCommand extends PlayerCommand
{
	public VoteCommand(PlotManager manager)
	{
		super("vote", "Vote for the plot you are standing in.", "runsafe.creative.vote");
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer player, IArgumentList stringStringHashMap)
	{
		if (manager.isInWrongWorld(player))
			return Config.Message.wrongWorld;

		String region = manager.getCurrentRegionFiltered(player);
		if (region == null)
			return Config.Message.Plot.invalid;

		if (manager.disallowVote(player, region))
			return Config.Message.Plot.Vote.failNoPermission;

		return manager.vote(player, region)
			? String.format(Config.Message.Plot.Vote.success, region)
			: Config.Message.Plot.Vote.failError;
	}

	private final PlotManager manager;
}
