package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PaintbrushManager;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerCommand;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.Item;
import no.runsafe.framework.minecraft.item.meta.RunsafeMeta;

public class Paintbrush extends PlayerCommand
{
	public Paintbrush(PaintbrushManager manager)
	{
		super("paintbrush", "Conjure a paint-brush for creative use!", "runsafe.creative.paintbrush");
		this.manager = manager;
	}

	@Override
	public String OnExecute(IPlayer executor, IArgumentList parameters)
	{
		RunsafeMeta brush = Item.Materials.BlazeRod.getItem();
		brush.setDisplayName(Config.getPaintbrushName());
		brush.addLore("Tool: Paintbrush");
		executor.give(brush);

		manager.setPaintbrushBlock(executor, Item.Unavailable.Air);

		return Config.Message.Plot.Paintbrush.summon;
	}

	private final PaintbrushManager manager;
}
