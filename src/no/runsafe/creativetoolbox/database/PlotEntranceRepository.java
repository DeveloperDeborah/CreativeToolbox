package no.runsafe.creativetoolbox.database;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.database.IDatabase;
import no.runsafe.framework.database.Repository;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.server.RunsafeLocation;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.RunsafeWorld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotEntranceRepository extends Repository implements IConfigurationChanged
{
	public PlotEntranceRepository(IDatabase database)
	{
		this.database = database;
	}

	public PlotEntrance get(String regionName)
	{
		if (cache.containsKey(regionName.toLowerCase()))
			return cache.get(regionName.toLowerCase());

		Map<String, Object> data = database.QueryRow("SELECT * FROM creativetoolbox_plot_entrance WHERE name=?", regionName);

		if (data == null || data.isEmpty())
			cache.put(regionName.toLowerCase(), null);

		else
		{
			RunsafeLocation location = new RunsafeLocation(
				world,
					getDoubleValue(data, "x"),
					getDoubleValue(data, "y"),
					getDoubleValue(data, "z"),
					getFloatValue(data, "yaw"),
					getFloatValue(data, "pitch")
			);
			PlotEntrance entrance = new PlotEntrance();
			entrance.setName(regionName);
			entrance.setLocation(location);
			cache.put(regionName.toLowerCase(), entrance);
		}

		return cache.get(regionName.toLowerCase());
	}

	public void persist(PlotEntrance entrance)
	{
		database.Update(
			"INSERT INTO creativetoolbox_plot_entrance (name, x, y, z, yaw, pitch) VALUES (?, ?, ?, ?, ?, ?)" +
				"ON DUPLICATE KEY UPDATE x=VALUES(x), y=VALUES(y), z=VALUES(z), yaw=VALUES(yaw), pitch=VALUES(pitch)",
			entrance.getName(),
			entrance.getLocation().getX(),
			entrance.getLocation().getY(),
			entrance.getLocation().getZ(),
			entrance.getLocation().getYaw(),
			entrance.getLocation().getPitch()
		);
		cache.put(entrance.getName().toLowerCase(), entrance);
	}

	public void delete(PlotEntrance entrance)
	{
		delete(entrance.getName());
	}

	public void delete(String region)
	{
		database.Execute("DELETE FROM creativetoolbox_plot_entrance WHERE name=?", region);
		if (cache.containsKey(region))
			cache.remove(region);
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		world = RunsafeServer.Instance.getWorld(configuration.getConfigValueAsString("world"));
	}

	@Override
	public String getTableName()
	{
		return "creativetoolbox_plot_entrance";
	}

	@Override
	public HashMap<Integer, List<String>> getSchemaUpdateQueries()
	{
		HashMap<Integer, List<String>> queries = new HashMap<Integer, List<String>>();
		List<String> sql = new ArrayList<String>();
		sql.add(
			"CREATE TABLE creativetoolbox_plot_entrance (" +
				"`name` varchar(255) NOT NULL," +
				"`x` double NOT NULL," +
				"`y` double NOT NULL," +
				"`z` double NOT NULL," +
				"`pitch` float NOT NULL," +
				"`yaw` float NOT NULL," +
				"PRIMARY KEY(`name`)" +
				")"
		);
		queries.put(1, sql);
		return queries;
	}

	private final IDatabase database;
	private final HashMap<String, PlotEntrance> cache = new HashMap<String, PlotEntrance>();
	private RunsafeWorld world;
}
