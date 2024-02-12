package no.runsafe.creativetoolbox.command;

import no.runsafe.creativetoolbox.Config;
import no.runsafe.creativetoolbox.PlotManager;
import no.runsafe.creativetoolbox.Plugin;
import no.runsafe.framework.api.command.ExecutableCommand;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.command.argument.IArgumentList;
import no.runsafe.framework.api.command.argument.RequiredArgument;

public class RenamePlotCommand extends ExecutableCommand
{
	public RenamePlotCommand(PlotManager manager)
	{
		super(
			"renameplot", "Renames a plot in creative.", "runsafe.creative.renameplot",
			new RequiredArgument("CurrentPlotName"), new RequiredArgument("NewPlotName")
		);
		this.manager = manager;
	}

	@Override
	public String OnExecute(ICommandExecutor executor, IArgumentList parameters)
	{
		String currentPlotName = parameters.getRequired("CurrentPlotName");
		String newPlotName = parameters.getRequired("NewPlotName");

		if (manager.renamePlot(currentPlotName, newPlotName))
		{
			Plugin.console.logInformation(String.format("&aPlot %s Renamed to %s.", currentPlotName, newPlotName));
			return String.format(Config.Message.Plot.Rename.success, currentPlotName, newPlotName);
		}
		else
			return Config.Message.Plot.Rename.fail;
	}

	PlotManager manager;
}