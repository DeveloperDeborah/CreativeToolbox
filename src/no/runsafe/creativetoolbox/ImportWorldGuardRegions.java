package no.runsafe.creativetoolbox;

import no.runsafe.creativetoolbox.database.PlotLogRepository;
import no.runsafe.creativetoolbox.database.PlotMemberRepository;
import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IOutput;
import no.runsafe.framework.api.event.plugin.IPluginEnabled;
import no.runsafe.framework.minecraft.RunsafeWorld;
import no.runsafe.worldguardbridge.WorldGuardInterface;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ImportWorldGuardRegions implements IPluginEnabled
{
	public ImportWorldGuardRegions(
		PlotManager manager,
		WorldGuardInterface worldGuard,
		PlotLogRepository logRepository,
		PlotMemberRepository memberRepository,
		IConfiguration config, IOutput console)
	{
		this.manager = manager;
		this.worldGuard = worldGuard;
		this.logRepository = logRepository;
		this.memberRepository = memberRepository;
		this.config = config;
		this.console = console;
	}

	@Override
	public void OnPluginEnabled()
	{
		RunsafeWorld world = manager.getWorld();
		List<String> regions = worldGuard.getRegionsInWorld(world);
		for (String region : regions)
		{
			String claim = logRepository.getClaim(region);
			if (claim == null)
				if(!logRepository.log(region, "unknown"))
					console.warning("Unable to import region %s to claim repository!", region);

			for (String member : worldGuard.getMembers(world, region))
				memberRepository.addMember(region, member, false);

			for (String member : worldGuard.getOwners(world, region))
				memberRepository.addMember(region, member, true);
		}
		config.setConfigValue("imported", true);
		config.save();
	}

	private final PlotManager manager;
	private final WorldGuardInterface worldGuard;
	private final PlotLogRepository logRepository;
	private final PlotMemberRepository memberRepository;
	private final IConfiguration config;
	private final IOutput console;
}
