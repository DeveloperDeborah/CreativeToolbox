package no.runsafe.creativetoolbox.command;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.PlotCalculator;
import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.framework.api.ILocation;
import no.runsafe.framework.api.IWorld;
import no.runsafe.framework.api.block.IBlock;
import no.runsafe.framework.api.command.argument.EnumArgument;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.worldeditbridge.WorldEditInterface;
import no.runsafe.worldguardbridge.IRegionControl;
import org.apache.commons.lang.StringUtils;

import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

public class GriefCleanupCommand extends PlayerCommand
{
	public GriefCleanupCommand(IRegionControl worldGuard, WorldEditInterface worldEdit, PlotCalculator plotCalculator, PlotFilter filter, IConsole output)
	{
		super(
			"griefcleanup", "Cleans up griefed plots.", "runsafe.creative.degrief",
			new EnumArgument("what", Lists.newArrayList("road", "lava", "water", "cobblestone", "obsidian", "all"), true)
		);
		this.worldEdit = worldEdit;
		this.worldGuard = worldGuard;
		this.plotCalculator = plotCalculator;
		this.filter = filter;
		this.output = output;
	}

	@Override
	public String OnExecute(IPlayer executor, Map<String, String> params)
	{
		Rectangle2D area = getArea(executor.getLocation());

		if (area == null)
			return "This does not appear to be a valid plot.";

		String what = params.get("what");

		output.logInformation("%s is running clean-up of '%s' at [%s]", executor.getName(), what, getRegionNameString(executor));

		if (what.equals("road"))
			return regeneratePadding(executor, area);

		if (what.equals("lava"))
			return cleanup(executor, area, Item.Unavailable.Lava, Item.Unavailable.StationaryLava);

		if (what.equals("water"))
			return cleanup(executor, area, Item.Unavailable.Water, Item.Unavailable.StationaryWater);

		if (what.equals("cobblestone"))
			return cleanup(executor, area, Item.BuildingBlock.Cobblestone);

		if (what.equals("obsidian"))
			return cleanup(executor, area, Item.BuildingBlock.Obsidian);

		if (what.equals("all"))
			return cleanup(executor, area,
				Item.Unavailable.Lava, Item.Unavailable.StationaryLava,
				Item.Unavailable.Water, Item.Unavailable.StationaryWater,
				Item.BuildingBlock.Cobblestone,
				Item.BuildingBlock.Obsidian
			);

		return String.format("Unsupported argument \"%s\"", what);
	}

	private Rectangle2D getArea(ILocation location)
	{
		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(location));
		if (candidate != null && candidate.size() == 1)
			return plotCalculator.pad(worldGuard.getRectangle(location.getWorld(), candidate.get(0)));
		else
			return plotCalculator.getPlotArea(location, true);
	}

	private String getRegionNameString(IPlayer player)
	{
		ILocation location = player.getLocation();
		if (location == null)
			return "Unknown";

		List<String> candidate = filter.apply(worldGuard.getRegionsAtLocation(location));
		if (candidate != null && !candidate.isEmpty())
			return StringUtils.join(candidate, ",");

		return String.format("X: %.2f, Z: %.2f", location.getX(), location.getZ());
	}

	private String cleanup(IPlayer player, Rectangle2D area, Item... remove)
	{
		if (remove.length == 0)
			return "Nothing to clean";
		ILocation max = plotCalculator.getMaxPosition(player.getWorld(), area);
		ILocation min = plotCalculator.getMinPosition(player.getWorld(), area);
		IWorld world = player.getWorld();
		if (world == null)
			return "No world!";
		int counter = 0;
		for (int x = min.getBlockX(); x <= max.getBlockX(); ++x)
		{
			for (int y = max.getBlockY(); y >= min.getBlockY(); --y)
			{
				for (int z = min.getBlockZ(); z <= max.getBlockZ(); ++z)
				{
					IBlock block = world.getBlockAt(x, y, z);
					if (block.isAny(remove))
					{
						block.set(Item.Unavailable.Air);
						counter++;
					}
				}
			}
		}
		return String.format("Removed %d blocks.", counter);
	}

	private String regeneratePadding(IPlayer player, Rectangle2D plotArea)
	{
		List<Rectangle2D> padding = plotCalculator.getPaddingSelection(plotArea);
		if (padding == null || padding.isEmpty())
			return "Unable to regenerate roads!";
		for (Rectangle2D area : padding)
		{
			worldEdit.regenerate(
				player,
				plotCalculator.getMinPosition(player.getWorld(), area),
				plotCalculator.getMaxPosition(player.getWorld(), area),
				false
			);
		}
		return "Regenerated roads.";
	}

	private final IRegionControl worldGuard;
	private final WorldEditInterface worldEdit;
	private final PlotCalculator plotCalculator;
	private final PlotFilter filter;
	private final IConsole output;
}
