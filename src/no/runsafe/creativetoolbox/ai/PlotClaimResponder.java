package no.runsafe.creativetoolbox.ai;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.ai.IChatResponseTrigger;
import no.runsafe.framework.api.log.IDebug;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlotClaimResponder implements IChatResponseTrigger
{
	public PlotClaimResponder(PlotManager manager, WorldGuardInterface worldGuard, IServer server, IDebug debug, ApprovedPlotRepository approvalRepository, PlotCalculator calculator)
	{
		this.manager = manager;
		this.worldGuard = worldGuard;
		this.server = server;
		this.debug = debug;
		this.approvalRepository = approvalRepository;
		this.calculator = calculator;
	}

	@Override
	public String getResponse(String playerName, Matcher message)
	{
		IPlayer player = server.getPlayerExact(playerName);
		if (player == null || player.hasPermission("runsafe.creative.claim.others"))
			return null;

		if (!player.hasPermission("runsafe.creative.claim.self"))
		{
			List<IPlayer> onlineStaff = server.getPlayersWithPermission("runsafe.creative.claim.others");
			for (IPlayer staff : onlineStaff)
				if (!player.shouldNotSee(staff))
					return String.format(Config.Message.Ai.needPermissionStaff, playerName, staff.getName());

			return String.format(Config.Message.Ai.needPermissionNoStaff, playerName);
		}

		List<String> existing = worldGuard.getOwnedRegions(player, manager.getWorld());
		debug.debugFine("%s has %d plots.", playerName, existing.size());
		if (!existing.isEmpty())
		{
			for (String plot : existing)
			{
				PlotApproval approved = approvalRepository.get(plot);
				debug.debugFine("Plot %s is %s.", plot, approved != null ? "approved" : "unapproved");
				if (approved == null)
					return String.format(Config.Message.Ai.plotsNotApproved, playerName);
			}
		}

		boolean claimable = manager.isCurrentClaimable(player);
		Rectangle2D region = calculator.getPlotArea(player.getLocation());
		if (!claimable || region  == null)
			return String.format(Config.Message.Ai.findFreePlot, playerName);

		return String.format(Config.Message.Ai.claimCurrentPlot, playerName);
	}

	@Override
	public Pattern getRule()
	{
		return Pattern.compile(Config.Message.Ai.pattern);
	}

	private final PlotManager manager;
	private final WorldGuardInterface worldGuard;
	private final IServer server;
	private final IDebug debug;
	private final ApprovedPlotRepository approvalRepository;
	private final PlotCalculator calculator;
}
