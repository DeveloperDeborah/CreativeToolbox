package no.runsafe.creativetoolbox.command;

import com.google.common.collect.Lists;
import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.Map;

public class OldPlotsCommand extends PlayerAsyncCommand
{
	public OldPlotsCommand(PlotManager manager, IScheduler scheduler, PlotList plotList)
	{
		super("oldplots", "list old plots that may be removed.", "runsafe.creative.scan.old-plots", scheduler);
		this.manager = manager;
		this.plotList = plotList;
	}

	@Override
	public String OnAsyncExecute(IPlayer executor, IArgumentList parameters)
	{
		Map<String, String> hits = manager.getOldPlots();
		int n = 0;
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, String> item : hits.entrySet())
		{
			if (executor == null || n < Config.getOldPlotsListLimit())
			{
				result.append(String.format(Config.Message.Plot.List.Old.listFormat + "\n", item.getKey(), item.getValue()));
				n++;
			}
		}
		if (result.length() == 0)
			return Config.Message.Plot.List.Old.notFound;
		plotList.set(executor, Lists.newArrayList(hits.keySet()));
		if (executor == null || n == hits.size())
			result.append(String.format(Config.Message.Plot.List.Old.foundNumber, hits.size()));
		else
			result.append(String.format(Config.Message.Plot.List.Old.listShowing, n, hits.size()));
		return result.toString();
	}

	private final PlotManager manager;
	private final PlotList plotList;
}
