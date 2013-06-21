package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.creativetoolbox.database.PlotApproval;
import no.runsafe.creativetoolbox.database.PlotVoteRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.event.IAsyncEvent;
import no.runsafe.framework.api.event.player.IPlayerInteractEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.minecraft.RunsafeLocation;
import no.runsafe.framework.minecraft.RunsafeServer;
import no.runsafe.framework.minecraft.block.RunsafeBlock;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerInteractEntityEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.framework.minecraft.player.RunsafePlayer;
import no.runsafe.worldguardbridge.WorldGuardInterface;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InteractEvents implements IPlayerRightClickBlock, IPlayerInteractEntityEvent, IConfigurationChanged, IAsyncEvent
{
	public InteractEvents(
		PlotFilter plotFilter,
		WorldGuardInterface worldGuard,
		PlotManager manager, ApprovedPlotRepository plotRepository,
		PlotVoteRepository votes)
	{
		this.worldGuardInterface = worldGuard;
		this.plotFilter = plotFilter;
		this.manager = manager;
		this.plotRepository = plotRepository;
		this.votes = votes;
	}

	@Override
	public boolean OnPlayerRightClick(RunsafePlayer player, RunsafeMeta itemInHand, RunsafeBlock block)
	{
		if (extensions.containsKey(player.getName()))
		{
			String target = extensions.get(player.getName());
			extensions.remove(player.getName());
			manager.extendPlot(player, target, block.getLocation());
			return false;
		}
		if (itemInHand != null && itemInHand.getItemId() == listItem)
		{
			this.listPlotsByLocation(block.getLocation(), player);
			return false;
		}
		return true;
	}

	@Override
	public void OnPlayerInteractEntityEvent(RunsafePlayerInteractEntityEvent event)
	{
		if (event.getRightClicked() instanceof RunsafePlayer && event.getPlayer().hasPermission("runsafe.creative.list"))
		{
			if (event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().getItemId() == listItem)
			{
				this.listPlotsByPlayer((RunsafePlayer) event.getRightClicked(), event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		listItem = configuration.getConfigValueAsInt("list_item");
	}

	public void startPlotExtension(RunsafePlayer player, String plot)
	{
		extensions.put(player.getName(), plot);
	}

	private void listPlotsByPlayer(RunsafePlayer checkPlayer, RunsafePlayer triggerPlayer)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			triggerPlayer.sendMessage("Error: No WorldGuard installed.");
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getOwnedRegions(checkPlayer, checkPlayer.getWorld()));

		if (!regions.isEmpty())
			triggerPlayer.sendColouredMessage(Strings.join(
				manager.tag(triggerPlayer, regions),
				"\n"
			));
		else if (triggerPlayer.hasPermission("runsafe.creative.list.showname"))
			triggerPlayer.sendMessage(String.format("%s does not own any regions.", checkPlayer.getPrettyName()));
		else
			triggerPlayer.sendMessage("No regions owned by this player.");
	}

	private void listPlotsByLocation(RunsafeLocation location, RunsafePlayer player)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			player.sendMessage("Error: No WorldGuard installed.");
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(location));

		if (regions != null && !regions.isEmpty())
			for (String regionName : regions)
				this.listRegion(regionName, player);
		else
			player.sendMessage("No regions found at this point.");
	}

	private void listRegion(String regionName, RunsafePlayer player)
	{
		if (player.hasPermission("runsafe.creative.approval.read"))
		{
			PlotApproval approval = plotRepository.get(regionName);
			if (approval == null || approval.getApproved() == null)
				player.sendColouredMessage("Region: " + regionName);
			else
				player.sendColouredMessage("Region: %s [Approved %s]", regionName, dateFormat.print(approval.getApproved()));
		}
		else
			player.sendMessage("Region: " + regionName);

		if (player.hasPermission("runsafe.creative.vote.tally"))
		{
			int tally = votes.tally(regionName);
			if (tally > 0)
				player.sendColouredMessage("  This plot has %d votes!", tally);
		}

		Set<String> owners = worldGuardInterface.getOwners(player.getWorld(), regionName);
		Set<String> members = worldGuardInterface.getMembers(player.getWorld(), regionName);

		for (String owner : owners)
		{
			RunsafePlayer theOwner = RunsafeServer.Instance.getPlayer(owner);
			if (theOwner != null)
			{
				player.sendColouredMessage("     Owner: " + theOwner.getPrettyName());

				if (player.hasPermission("runsafe.creative.list.seen"))
				{
					String seen = theOwner.getLastSeen(player);
					player.sendColouredMessage("     " + (seen == null ? "Player never seen" : seen));
				}
			}
		}

		for (String member : members)
		{
			RunsafePlayer theMember = RunsafeServer.Instance.getPlayer(member);
			if (theMember != null)
				player.sendColouredMessage("     Member: " + theMember.getPrettyName());
		}
	}

	private final WorldGuardInterface worldGuardInterface;
	private int listItem;
	private final PlotManager manager;
	private final PlotFilter plotFilter;
	private final ApprovedPlotRepository plotRepository;
	private final PlotVoteRepository votes;
	private final DateTimeFormatter dateFormat = DateTimeFormat.forPattern("dd.MM.YYYY");
	private final ConcurrentHashMap<String, String> extensions = new ConcurrentHashMap<String, String>();
}