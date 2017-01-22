package game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingGame§1§l>> §r§7";

	public Plugin plugin;

	public List<Player> players = new ArrayList<>();
	
	public Plot[] plotSpawns = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean inProgress = false;
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY;
	public Inventory votingInventory;

	public File locationsFile = new File("plugins/BuildingGame", "locations.yml");
	public FileConfiguration locationCfg = YamlConfiguration.loadConfiguration(locationsFile);

	public File themesFile = new File("plugins/BuildingGame", "themes.yml");
	public FileConfiguration themesCfg = YamlConfiguration.loadConfiguration(themesFile);

	public Location lobbyLocation;


	private	int voteTimer = 10;


	public Game(Plugin plugin)
	{
		lobbyLocation = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		loadLocations();
		setConfigDefaults();
		loadPlots();
		loadBuildThemes();
		this.plugin = plugin;
	}
	
	
	public void start(Player p) {
		if ((players.size() >= 2) && players.size() <= 16) {
			inProgress = true;

			startVoting();

		} else {
			p.sendMessage(prefix + "Ungueltige Spieleranzahl (2 - 16 Spieler)");
		}
	}

	private void startVoting()
	{
		gamestate = GameState.VOTING;
		for(Player p : players)
		{
			p.setGameMode(GameMode.ADVENTURE);
			p.openInventory(votingInventory);


		}
	}

	private void startBuilding()
	{

		gamestate = GameState.BUILDING;
		for (int i = 0; i < players.size(); i++) {
			players.get(i).teleport(plotSpawns[i].getSpawnLocation());
			players.get(i).playSound(players.get(i).getLocation(), "random.levelup", 1.0f, 1.0f);

		}
	}

	public void addPlayer(Player p)
	{
		if(players.contains(p))
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
		else
		{
			if(inProgress)
			{
				p.kickPlayer(prefix + "Das Spiel laeuft bereits");
			}
			else
			{
				players.add(p);
				playerdata.put(p, new PlayerData());
				p.sendMessage(prefix + "Du bist dem Spiel beigetreten");
				p.teleport(lobbyLocation);
			}
		}
	}
	
	public void removePlayer(Player p)
	{
		if(players.contains(p))
		{
			players.remove(p);
		}
		else
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
	}
	
	
	//loadlobbylocation
	private void loadLocations()
	{
		lobbyLocation.setX(locationCfg.getDouble("locations.lobby.x"));
		lobbyLocation.setY(locationCfg.getDouble("locations.lobby.y"));
		lobbyLocation.setZ(locationCfg.getDouble("locations.lobby.z"));
		lobbyLocation.setWorld(Bukkit.getWorld(locationCfg.getString("locations.lobby.world")));
		lobbyLocation.setYaw((float) locationCfg.getDouble("locations.lobby.yaw"));
		lobbyLocation.setPitch((float) (locationCfg.getDouble("locations.lobby.pitch")));
	}

	private void setConfigDefaults()
	{
		locationCfg.addDefault("locations.lobby.x", 0d);
		locationCfg.addDefault("locations.lobby.y", 100d);
		locationCfg.addDefault("locations.lobby.z", 0d);
		locationCfg.addDefault("locations.lobby.world", "BuildingGame");
		locationCfg.addDefault("locations.lobby.yaw", 0d);
		locationCfg.addDefault("locations.lobby.pitch", 0d);
		
		locationCfg.addDefault("locations.originplot.x", 23);
		locationCfg.addDefault("locations.originplot.y", 4);
		locationCfg.addDefault("locations.originplot.z", 57);
		locationCfg.addDefault("locations.originplot.world", "BuildingGame");
		locationCfg.addDefault("locations.originplot.yaw", 0d);
		locationCfg.addDefault("locations.originplot.pitch", 0d);
		
		locationCfg.addDefault("locations.originSpawn.x", 15);
		locationCfg.addDefault("locations.originSpawn.y", 10);
		locationCfg.addDefault("locations.originSpawn.z", 63);
		locationCfg.addDefault("locations.originSpawn.world", "BuildingGame");
		locationCfg.addDefault("locations.originSpawn.yaw", 0d);
		locationCfg.addDefault("locations.originSpawn.pitch", 0d);
		
		try {
			locationCfg.save(locationsFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadPlots()
	{
		Location origin = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		origin.setX(locationCfg.getDouble("locations.originplot.x"));
		origin.setY(locationCfg.getDouble("locations.originplot.y"));
		origin.setZ(locationCfg.getDouble("locations.originplot.z"));
		origin.setWorld(Bukkit.getWorld(locationCfg.getString("locations.originplot.world")));
		origin.setYaw((float) locationCfg.getDouble("locations.originplot.yaw"));
		origin.setPitch((float) (locationCfg.getDouble("locations.originplot.pitch")));
	
		Location originSpawn = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		originSpawn.setX(locationCfg.getDouble("locations.originSpawn.x"));
		originSpawn.setY(locationCfg.getDouble("locations.originSpawn.y"));
		originSpawn.setZ(locationCfg.getDouble("locations.originSpawn.z"));
		originSpawn.setWorld(Bukkit.getWorld(locationCfg.getString("locations.originSpawn.world")));
		originSpawn.setYaw((float) locationCfg.getDouble("locations.originSpawn.yaw"));
		originSpawn.setPitch((float) (locationCfg.getDouble("locations.originSpawn.pitch")));
		
		
		for(int x = 0; x < 4; x++)
		{
			for(int z = 0; z < 4; z++)
			{
				plotSpawns[z * 4 + x] = new Plot(new Location(origin.getWorld(), origin.getX() - 43 * x, origin.getY(), origin.getZ() + 43 * z));
				plotSpawns[z * 4 + x].setSpawnLocation(new Location(originSpawn.getWorld(), originSpawn.getX() - 43 * x, originSpawn.getY(), originSpawn.getZ() + 43 * z)); 
			}
		}
		
		
		
	}

	private void loadBuildThemes()
	{
		List<?> themes;
		themes = themesCfg.getList("themes");

		votingInventory = Bukkit.createInventory(null, 36, "§6§lThemen");
		for(int i = 0; i < themes.size(); i++)
		{
			ItemStack is = new ItemStack(Material.PAPER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§7" + (String) themes.get(i));
//			List<String> lore = new ArrayList<>();
//			lore.add("");
//			im.setLore(lore);

			is.setItemMeta(im);

			votingInventory.addItem(is);
		}
	}

	

}
