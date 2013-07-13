package no.runsafe.creativetoolbox.command.Member;

import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotMemberBlacklistRepository;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.player.RunsafeAmbiguousPlayer;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.HashMap;

public class BlacklistCommand extends ExecutableCommand
{
	public BlacklistCommand(PlotMemberBlacklistRepository blacklistRepository, PlotManager manager)
	{
		super("blacklist", "Blocks a certain player from being added to any additional creative plots", "runsafe.creative.blacklist", "player");
		this.blacklistRepository = blacklistRepository;
		this.manager = manager;
	}

	@Override
	protected String OnExecute(ICommandExecutor executor, HashMap<String, String> params)
	{
		RunsafePlayer player = RunsafeServer.Instance.getPlayer(params.get("player"));
		if (player == null)
			return "&cUnable to locate player.";

		if (player instanceof RunsafeAmbiguousPlayer)
			return player.toString();

		if (blacklistRepository.isBlacklisted(player))
			return "&cThat player is already blacklisted.";

		blacklistRepository.add(executor, player);
		manager.removeMember(player);
		return String.format("The player %s has been blacklisted.", player.getPrettyName());
	}

	private final PlotMemberBlacklistRepository blacklistRepository;
	private final PlotManager manager;
}
