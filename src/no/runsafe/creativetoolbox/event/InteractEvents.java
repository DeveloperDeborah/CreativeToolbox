package no.runsafe.creativetoolbox.event;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.database.PlotLogRepository;
import no.runsafe.creativetoolbox.database.PlotTagRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.event.IAsyncEvent;
import no.runsafe.framework.api.event.player.IPlayerInteractEntityEvent;
import no.runsafe.framework.api.event.player.IPlayerRightClickBlock;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerInteractEntityEvent;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class InteractEvents implements IPlayerRightClickBlock, IPlayerInteractEntityEvent, IConfigurationChanged, IAsyncEvent
{
	public InteractEvents(
		PlotFilter plotFilter,
		IRegionControl worldGuard,
		PlotManager manager,
		PlotTagRepository tagRepository,
		PlotLogRepository logRepository,
		IScheduler scheduler
	)
	{
		this.worldGuardInterface = worldGuard;
		this.plotFilter = plotFilter;
		this.manager = manager;
		this.tagRepository = tagRepository;
		this.logRepository = logRepository;
		this.scheduler = scheduler;
	}

	@Override
	public boolean OnPlayerRightClick(IPlayer player, RunsafeMeta itemInHand, IBlock block)
	{
		if (manager.isInWrongWorld(player) || stickTimer.containsKey(player))
			return true;
		if (extensions.containsKey(player))
		{
			String target = extensions.get(player);
			extensions.remove(player);
			manager.extendPlot(player, target, block.getLocation());
			return false;
		}

		if (isListItem(itemInHand))
		{
			registerStickTimer(player);
			this.listPlotsByLocation(block.getLocation(), player);
			return false;
		}
		return true;
	}

	@Override
	public void OnPlayerInteractEntityEvent(RunsafePlayerInteractEntityEvent event)
	{
		IPlayer player = event.getPlayer();
		if ( manager.isInWrongWorld(player) || stickTimer.containsKey(player))
			return;
		if (event.getRightClicked() instanceof IPlayer && player.hasPermission("runsafe.creative.list"))
		{
			if (isListItem(player.getItemInMainHand()))
			{
				registerStickTimer(player);
				this.listPlotsByPlayer((IPlayer) event.getRightClicked(), player);
				event.cancel();
			}
		}
	}

	private boolean isListItem(RunsafeMeta item)
	{
		if (item == null)
			return false;

		return item.getNormalName().equals(listItem);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		listItem = configuration.getConfigValueAsString("list_item_name");
	}

	public void startPlotExtension(IPlayer player, String plot)
	{
		extensions.put(player, plot);
	}

	private void registerStickTimer(final IPlayer player)
	{
		if (stickTimer.containsKey(player))
			scheduler.cancelTask(stickTimer.get(player));

		stickTimer.put(player, scheduler.startSyncTask(() ->
			stickTimer.remove(player), 1));
	}

	private void listPlotsByPlayer(IPlayer checkPlayer, IPlayer triggerPlayer)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			triggerPlayer.sendColouredMessage(Config.Message.noWorldGuard);
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getOwnedRegions(checkPlayer, checkPlayer.getWorld()));

		if (!regions.isEmpty())
			triggerPlayer.sendColouredMessage(StringUtils.join(
				manager.tag(triggerPlayer, regions),
				"\n"
			));
		else
			triggerPlayer.sendColouredMessage(Config.Message.Plot.List.Interact.playerDoesNotOwnPlots, checkPlayer.getPrettyName());
	}

	private void listPlotsByLocation(ILocation location, IPlayer player)
	{
		if (!this.worldGuardInterface.serverHasWorldGuard())
		{
			player.sendColouredMessage(Config.Message.noWorldGuard);
			return;
		}

		List<String> regions = plotFilter.apply(worldGuardInterface.getRegionsAtLocation(location));

		if (regions != null && !regions.isEmpty())
			for (String regionName : regions)
			{
				player.sendColouredMessage(Config.Message.Plot.List.Interact.plotName, manager.tag(player, regionName));
				listClaimInfo(player, regionName);
				listTags(player, regionName);
				listPlotMembers(player, regionName);
			}
		else
			player.sendColouredMessage(Config.Message.Plot.invalid2);
	}

	private void listClaimInfo(IPlayer player, String regionName)
	{
		if (player.hasPermission("runsafe.creative.claim.log"))
		{
			String claim = logRepository.getClaim(regionName);
			if (claim == null)
				return;

			player.sendColouredMessage(Config.Message.Plot.List.Interact.claimedBy, claim);
		}
	}

	private void listTags(IPlayer player, String regionName)
	{
		if (player.hasPermission("runsafe.creative.tag.read"))
		{
			List<String> tags = tagRepository.getTags(regionName);
			if (!tags.isEmpty())
				player.sendColouredMessage(Config.Message.Plot.List.Interact.tags, StringUtils.join(tags, " "));
		}
	}

	private void listPlotMembers(IPlayer player, String regionName)
	{
		Set<IPlayer> owners = worldGuardInterface.getOwnerPlayers(manager.getWorld(), regionName);
		for (IPlayer owner : owners)
			listPlotMember(player, Config.Message.Plot.List.Interact.genericOwner, owner, true);

		Set<IPlayer> members = worldGuardInterface.getMemberPlayers(manager.getWorld(), regionName);
		for (IPlayer member : members)
			listPlotMember(player, Config.Message.Plot.List.Interact.genericMember, member, false);
	}

	private void listPlotMember(IPlayer player, String label, IPlayer member, boolean showSeen)
	{
		if (member != null)
		{
			player.sendColouredMessage(Config.Message.Plot.List.Interact.member, label, member.getPrettyName());

			if (showSeen && player.hasPermission("runsafe.creative.list.seen"))
			{
				String seen = member.getLastSeen(player);
				player.sendColouredMessage(
					Config.Message.Plot.List.Interact.memberTime,
					(seen == null ? Config.Message.Plot.List.Interact.memberNeverSeen : seen)
				);
			}
		}
	}

	private final IRegionControl worldGuardInterface;
	private String listItem;
	private final PlotManager manager;
	private final PlotFilter plotFilter;
	private final PlotTagRepository tagRepository;
	private final PlotLogRepository logRepository;
	private final IScheduler scheduler;
	private final ConcurrentHashMap<IPlayer, String> extensions = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<IPlayer, Integer> stickTimer = new ConcurrentHashMap<>();
}