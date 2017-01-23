package game;

import static org.bukkit.Bukkit.getScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Game {
	
	public static String prefix = "�1�ll�r�9 BuildingGame�1�l>> �r�7";

	public Plugin plugin;

	public List<Player> players = new ArrayList<>();
	
	public Plot[] plotSpawns = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean inProgress = false;
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY;
	public Inventory votingInventory;
	public int[] votes;
	public List<?> themes;
	public Map<OfflinePlayer, Score> buildingScoreboard = new HashMap<>();
	public Map<Player, Inventory> gradingInventories = new HashMap<>();


	public File locationsFile = new File("plugins/BuildingGame", "locations.yml");
	public FileConfiguration locationCfg = YamlConfiguration.loadConfiguration(locationsFile);

	public File themesFile = new File("plugins/BuildingGame", "themes.yml");
	public FileConfiguration themesCfg = YamlConfiguration.loadConfiguration(themesFile);

	public Location lobbyLocation;
	public int max;

	private	int voteTimer = 10;
	private int buildingTimerScheduler;
	private Scoreboard scoreboard;
	private Objective bgObjective;
	private Score timeScore;
	private int buildingTime;
	private String currentBuildingtime = "";
	private String finalTheme;


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
		if(inProgress) return;
		if ((players.size() >= 1) && players.size() <= 16) {
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
			p.setLevel(10);
		}

		final int votingTimerTask;
		votingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for(Player p : players)
				{
					p.setLevel(p.getLevel() - 1);
					if(p.getLevel() <= 3)
					{
						p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
					}
				}
			}
		}, 0, 20);

		getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(votingTimerTask);
				startBuilding();
			}
		}, 20 * 10);


	}

	private void startBuilding()
	{
		finalTheme = calculateFinalTheme();
		gamestate = GameState.BUILDING;


		buildingTime = 60 * 5;

		scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		bgObjective = scoreboard.registerNewObjective("BuildingGame", "dummy");
		bgObjective.setDisplayName("�9    - BuildingGame -    ");
		bgObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score2 = bgObjective.getScore("�6Thema:");
		score2.setScore(6);
		Score score3 = bgObjective.getScore("�7" + finalTheme);
		score3.setScore(5);
		Score score4 = bgObjective.getScore(" ");
		score4.setScore(4);
		Score score5 = bgObjective.getScore("�6Zeit:");
		score5.setScore(3);
		timeScore = bgObjective.getScore("");
		timeScore.setScore(2);
		

		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
			plotSpawns[i].setOwner(p);
			p.teleport(plotSpawns[i].getSpawnLocation());
			p.sendMessage(prefix + "Das Thema ist �6" + finalTheme +"�r�7 ("+max+" Stimme(n))");
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			p.setGameMode(GameMode.CREATIVE);
			p.setFlying(true);
			p.setScoreboard(scoreboard);

		}


		
		final int buildingTimerTask;
		buildingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateScoreboard();
			}
		}, 0, 20);
		buildingTimerScheduler = buildingTimerTask;
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
		themes = themesCfg.getList("themes");
		votes = new int[themes.size()];
		votingInventory = Bukkit.createInventory(null, 36, "�6�lThemen");
		for(int i = 0; i < themes.size(); i++)
		{
			ItemStack is = new ItemStack(Material.PAPER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("�7" + themes.get(i));
//			List<String> lore = new ArrayList<>();
//			lore.add("");
//			im.setLore(lore);

			is.setItemMeta(im);

			votingInventory.addItem(is);
		}
	}

	private String calculateFinalTheme()
	{
		max = 0;
		int maxindex = 42;
		for(int i = 0; i < themes.size(); i++)
		{
			if(votes[i] > max) {
				max = votes[i];
				maxindex = i;
			}
		}
		return (String) themes.get(maxindex);
	}

	private void cancelScheduler(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
	}

	private void updateScoreboard()
	{
		System.out.println(currentBuildingtime);
		scoreboard.resetScores(currentBuildingtime);
		buildingTime--;
		if(buildingTime < 0)
		{
			gamestate = gamestate.GRADING;
			startGrading();
		}
		currentBuildingtime = "�7�l" + buildingTime / 60 + ":" + buildingTime % 60;
		System.out.println(currentBuildingtime);
		timeScore = bgObjective.getScore(currentBuildingtime);
		timeScore.setScore(2);


	}
	
	private void startGrading()
	{
		Bukkit.getScheduler().cancelTask(buildingTimerScheduler);
		ItemStack is = new ItemStack(Material.PRISMARINE_SHARD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("�6�lBewerten");
		List<String> lore = new ArrayList<>();
		lore.add("�7Rechtsklicke um das Plot zu bewerten");
		im.setLore(lore);
		is.setItemMeta(im);
		for(Player p : players)
		{
			p.getInventory().setItem(4, is);
		}
		 
	}
	
	private void gradePlot(int id)
	{
		
		
	}
	
	private void resetVotingInterfaces()
	{
		//Create Items
		ItemStack optikIS = createItemStack(Material.DIAMOND, "�6Optik", "�7Bewerte, wie gut das Gebaute aussieht");
		ItemStack stimmigkeitIS = createItemStack(Material.SIGN, "�6Stimmigkeit", "�7Bewerten, ob das Gebaute zum Thema passt");
		ItemStack kreativitaetIS = createItemStack(Material.EMERALD, "�6Kreativitaet", "�7Bewerte, wie Kreativ das Gebaute umgesetzt wurde");
		ItemStack sehrGutGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 13, "�2Sehr Gut", "");
		ItemStack gutGIS = createItemStackColor(Material.STAINED_GLASS, 1,(short) 5, "�aGut", "");
		ItemStack mittelGIS = createItemStackColor(Material.STAINED_GLASS, 1,(short) 4, "�eMittel", "");
		ItemStack schlechtGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short)1, "�6Schlecht", "");
		ItemStack sehrSchlechtGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short)14, "�4Sehr Schlecht", "");
		ItemStack nichtBewertetGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short)0, "�fNicht bewertet", "");
		ItemStack bewertungAbschliessenIS = createItemStack(Material.EMERALD_BLOCK, "�2Bewertung abschliessen", "");
		
		ItemStack sehrGutCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 13, "�2Sehr Gut", "");
		ItemStack gutCIS = createItemStackColor(Material.STAINED_CLAY, 1,(short) 5, "�aGut", "");
		ItemStack mittelCIS = createItemStackColor(Material.STAINED_CLAY, 1,(short) 4, "�eMittel", "");
		ItemStack schlechtCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short)1, "�6Schlecht", "");
		ItemStack sehrSchlechtCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short)14, "�4Sehr Schlecht", "");
		ItemStack nichtBewertetCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short)0, "�fNicht bewertet", "");
		
		
		
		Iterator it = gradingInventories.entrySet().iterator();
		while(it.hasNext())
		{
			Map.Entry<Player, Inventory> entry = (Map.Entry<Player, Inventory>) it.next();
			
			Inventory i = entry.getValue();
			
			//fill inventory
			i.setItem(10, optikIS);
			i.setItem(11, sehrGutGIS);
			i.setItem(12, gutGIS);
			i.setItem(13, mittelGIS);
			i.setItem(14, schlechtGIS);
			i.setItem(15, sehrSchlechtGIS);
			i.setItem(16, nichtBewertetGIS);
			
			i.setItem(19, kreativitaetIS);
			i.setItem(20, sehrGutGIS);
			i.setItem(21, gutGIS);
			i.setItem(22, mittelGIS);
			i.setItem(23, schlechtGIS);
			i.setItem(24, sehrSchlechtGIS);
			i.setItem(25, nichtBewertetGIS);
			
			i.setItem(28, stimmigkeitIS);
			i.setItem(29, sehrGutGIS);
			i.setItem(30, gutGIS);
			i.setItem(31, mittelGIS);
			i.setItem(32, schlechtGIS);
			i.setItem(33, sehrSchlechtGIS);
			i.setItem(34, nichtBewertetGIS);
			
			i.setItem(48, bewertungAbschliessenIS);
			i.setItem(49, bewertungAbschliessenIS);
			i.setItem(50, bewertungAbschliessenIS);
			
			
		}

	
	}
	
	private ItemStack createItemStack(Material material, String name, String lore)
	{
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> loreText = new ArrayList<>();
		loreText.add(lore);
		im.setLore(loreText);
		is.setItemMeta(im);
		return is;
	}
	private ItemStack createItemStackColor(Material material, int amount, short damage, String name, String lore)
	{
		ItemStack is = new ItemStack(material, amount, damage);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> loreText = new ArrayList<>();
		loreText.add(lore);
		im.setLore(loreText);
		is.setItemMeta(im);
		return is;
	}

	

}
