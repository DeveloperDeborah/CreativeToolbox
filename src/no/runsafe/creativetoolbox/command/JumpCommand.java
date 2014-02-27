package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.PlotFilter;
import no.runsafe.creativetoolbox.PlotList;
import no.runsafe.creativetoolbox.database.ApprovedPlotRepository;
import no.runsafe.framework.api.IScheduler;
import no.runsafe.framework.api.command.argument.Enumeration;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.player.PlayerAsyncCallbackCommand;
import no.runsafe.framework.api.player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JumpCommand extends PlayerAsyncCallbackCommand<JumpCommand.Sudo>
{
	public enum JumpKinds
	{
		Approved,
		Unapproved
	}

	public JumpCommand(IScheduler scheduler, PlotFilter plotFilter, ApprovedPlotRepository approval, PlotList plotList)
	{
		super("jump", "Find a random plot of a given kind", "runsafe.creative.teleport.random", scheduler, new Enumeration.Required("kind", JumpKinds.values()));
		this.plotFilter = plotFilter;
		this.approval = approval;
		this.plotList = plotList;
	}

	@Override
	public Sudo OnAsyncExecute(IPlayer executor, IArgumentList params)
	{
		JumpKinds kind = params.getValue("kind");
		if (kind == null)
			return null;
		console.debugFine("Jumping to an %s plot", kind.name());
		List<String> approved = approval.getApprovedPlots();
		Sudo target = new Sudo();
		target.player = executor;
		if (kind == JumpKinds.Approved)
		{
			int r = rng.nextInt(approved.size());
			plotList.set(executor, approved);
			plotList.wind(executor, approved.get(r));
			target.command = String.format("creativetoolbox teleport %s", approved.get(r));
			return target;
		}
		List<String> plots = plotFilter.getFiltered();
		ArrayList<String> result = new ArrayList<String>();
		for (String value : plots)
			if (!approved.contains(value))
				result.add(value);
		int r = rng.nextInt(result.size());
		plotList.set(executor, result);
		plotList.wind(executor, result.get(r));
		target.command = String.format("creativetoolbox teleport %s", result.get(r));
		return target;
	}

	@Override
	public void SyncPostExecute(Sudo result)
	{
		if (result != null)
			result.player.performCommand(result.command);
	}

	class Sudo
	{
		public IPlayer player;
		public String command;
	}

	private final PlotFilter plotFilter;
	private final ApprovedPlotRepository approval;
	private final Random rng = new Random();
	private final PlotList plotList;
}
