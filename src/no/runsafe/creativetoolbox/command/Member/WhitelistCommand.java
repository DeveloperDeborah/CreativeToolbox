package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.Player;
import no.runsafe.framework.api.player.IPlayer;

public class WhitelistCommand extends ExecutableCommand
{
	public WhitelistCommand(PlotMemberBlacklistRepository blacklistRepository)
	{
		super("whitelist", "Removes a player from the membership blacklist.", "runsafe.creative.whitelist", new Player().require());
		this.blacklistRepository = blacklistRepository;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList params)
	{
		IPlayer player = params.getValue("player");
		if (player == null)
			return null;

		if (!blacklistRepository.isBlacklisted(player))
			return Config.Message.Plot.Member.Blacklist.whitelistFail;

		blacklistRepository.remove(player);
		return String.format(Config.Message.Plot.Member.Blacklist.whitelistSuccess, player.getPrettyName());
	}

	private final PlotMemberBlacklistRepository blacklistRepository;
}
