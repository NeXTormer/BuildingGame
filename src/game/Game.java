package game;

import static org.bukkit.Bukkit.getScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import Brawls.PlayerBrawl;
import Brawls.PlotBrawl;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingBrawl§1§l>> §r§7";
	public static String playerprefix = "§2§ll§r§a BuildingBrawl§2>> §r§7";
	public static int secondsToGrade = 25;
	public static int MAX_PLAYERS = 16;
	
	
	
	public static List<Material> forbiddenBlocks = new ArrayList<>();

	public Plugin plugin;
	
	public List<UUID> players = new ArrayList<>();
	public List<UUID> spectators = new ArrayList<>();
	
	public Plot[] plotArray = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY; //default gamestate
	public Inventory votingInventory; //voting inventory preset
	public int[] votes; //theme votes
	public List<String> themes;
	public Map<OfflinePlayer, Score> buildingScoreboard = new HashMap<>();
	public Map<Player, VotingInventory> gradingInventories = new HashMap<>();
	public Map<String, ItemStack> skulls = new HashMap<>();
	
	public List<String> finalThemes = new ArrayList<>();

	public int gradingNameRevealTime = 5;
	public int buildingTime = 10 * 60; //time to build
	public int scoreboardSecondsToGrade;
	public String currentGradingtime = "";
	public String scoreboardPlotOwner = "";
	
	public boolean launchFirework = true;
	
	public File locationsFile = new File("plugins/BuildingGame", "locations.yml");
	public FileConfiguration locationCfg = YamlConfiguration.loadConfiguration(locationsFile);

	public File themesFile = new File("plugins/BuildingGame", "themes.yml");
	public FileConfiguration themesCfg = YamlConfiguration.loadConfiguration(themesFile);
	
	public File forbiddenFile = new File("plugins/BuildingGame", "forbiddenBlocks.yml");
	public FileConfiguration forbiddenBlocksCfg = YamlConfiguration.loadConfiguration(forbiddenFile);
	
	public File configFile = new File("plugins/BuildingGame", "config.yml");
	public FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);

	public Location lobbyLocation;
	public Location jumpLocation;
	public int max; //max number of votes in VOTING phase
	public int gradingCurrentPlotId; //current plot id which is in the grading progress (GameState.GRADING)
	public String finalTheme;

	private Random random;
	
	public	int voteTimer = 10;
	private int gradeTimer = 0;
	private int buildingTimerScheduler;
	private Scoreboard scoreboard;
	private Score timeScore;
	private Objective bgObjective;
	private String currentBuildingtime = "";
	private int currentPlotInGradingProcess = 1000;
	private int maxindex = 0;
	
	/**
	 * scheduler id for the timer which shuts the server down when there are no players left
	 */
	private int playerLeaveTimerScheduler = 0;
	
	private ItemStack compass = new ItemStack(Material.COMPASS);
	private ItemMeta compassmeta = compass.getItemMeta();

	public Game(Plugin plugin)
	{
		loadConfig();
		lobbyLocation = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		jumpLocation = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		Bukkit.getServer().getWorld(locationCfg.getString("locations.lobby.world")).setAnimalSpawnLimit(100);
		Bukkit.getServer().getWorld(locationCfg.getString("locations.lobby.world")).setMonsterSpawnLimit(100);
		loadLocations();
		setConfigDefaults();
		loadPlots();
		loadBuildThemes();
		loadForbiddenBlocks();
		this.plugin = plugin;
		
		compassmeta.setDisplayName("§6Spieler beobachten");
		compass.setItemMeta(compassmeta);
		
		random = new Random();
	}
	
	private void loadConfig()
	{
		secondsToGrade = configCfg.getInt("secondsToGrade");
		buildingTime = configCfg.getInt("secondsToBuild");
	}
	
	private void loadForbiddenBlocks()
	{
		forbiddenBlocks = (List<Material>) forbiddenBlocksCfg.getList("forbiddenBlocks");
	}
	
	
	public void start(Player p) {
		int minPlayers = configCfg.getInt("minPlayers");
		if ((players.size() >= minPlayers) && players.size() <= 16) {

			startVoting();

		} else {
			p.sendMessage(prefix + "Ungueltige Spieleranzahl ("+minPlayers+" - 16 Spieler)");
			Bukkit.getServer().broadcastMessage(prefix + "Start abgebrochen")
;		}
	}

	private void startVoting()
	{
		gamestate = GameState.VOTING;
		
		for(UUID uuid : players) 
		{
			Player p = Bukkit.getPlayer(uuid);
			p.setGameMode(GameMode.ADVENTURE);
			p.openInventory(votingInventory);
			p.setLevel(voteTimer);
		}

		final int votingTimerTask;
		votingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				for(UUID uuid : players)
				{
					Player p = Bukkit.getPlayer(uuid);
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
		}, 20 * voteTimer);


	}

	private void startBuilding()
	{
		finalTheme = calculateFinalTheme();
		gamestate = GameState.BUILDING;

		scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
		bgObjective = scoreboard.registerNewObjective("BuildingBrawl", "dummy");
		bgObjective.setDisplayName("§9    - BuildingBrawl -    ");
		bgObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score scoreThema = bgObjective.getScore("§6Thema:");
		scoreThema.setScore(7);
		Score scoreFinalTheme = bgObjective.getScore("§7" + finalTheme);
		scoreFinalTheme.setScore(6);
		Score scoreSpace1 = bgObjective.getScore(" ");
		scoreSpace1.setScore(5);
		Score scoreZeit = bgObjective.getScore("§6Zeit:");
		scoreZeit.setScore(4);
		timeScore = bgObjective.getScore("");
		timeScore.setScore(3);
		

		
		for (int i = 0; i < players.size(); i++) {
			Player p = Bukkit.getPlayer(players.get(i));
			p.setLevel(buildingTime);
			plotArray[i].setOwner(p);
			p.teleport(plotArray[i].getSpawnLocation());
			p.sendMessage(prefix + "Das Thema ist §6" + finalTheme +"§r§7 ("+max+" Stimme(n))");
			if(buildingTime%60>9)
			{
				p.sendTitle("§7Thema: §6§l" + finalTheme, "§7Noch §6 "+(buildingTime / 60 + ":" + buildingTime % 60)+" §7Minuten verbleiben");
			}
			else
			{
				p.sendTitle("§7Thema: §6§l" + finalTheme, "§7Noch §6 "+(buildingTime / 60 + ":0" + buildingTime % 60)+" §7Minuten verbleiben");
			}
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			p.setGameMode(GameMode.CREATIVE);
			p.setFlying(true);
			p.setScoreboard(scoreboard);

		}
		
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();
		compassMeta.setDisplayName("§6§l - Spieler beobachten - ");
		compass.setItemMeta(compassMeta);
		

		for(UUID uuid : spectators)
		{
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(Bukkit.getPlayer(players.get(0)).getLocation());
			p.setScoreboard(scoreboard);
			p.sendMessage(prefix + "Das Thema ist §6" + finalTheme +"§r§7 ("+max+" Stimme(n))");
			p.sendTitle("§7Thema: §6§l" + finalTheme, "§7Noch §6 "+(buildingTime / 60 + ":" + buildingTime % 60)+" §7Minuten verbleiben");
			p.setFlying(true);
			p.getInventory().setItem(4, compass);
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
	
	public void addSpectator(Player p, String msg)
	{
		if(spectators.contains(p.getUniqueId()))
		{
			p.sendMessage(playerprefix + "Unbekannter Fehler");
		}
		else
		{
			if(players.contains(p.getUniqueId()))
			{
				players.remove(p.getUniqueId());
				Bukkit.getServer().broadcastMessage(prefix + "257"); //============debug
			}
			spectators.add(p.getUniqueId());
			p.sendMessage(playerprefix + msg);
			p.teleport(lobbyLocation);
			p.setGameMode(GameMode.ADVENTURE);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setFoodLevel(20);
			for(UUID uuid : players)
			{
				Player z = Bukkit.getPlayer(uuid);
				z.hidePlayer(p);
			}
		}	
	}

	
	public void removeSpectator(Player p)
	{
		if(spectators.contains(p.getUniqueId()))
		{
			p.teleport(lobbyLocation);
			spectators.remove(p.getUniqueId());
			p.setFlying(false);
			p.setAllowFlight(false);
			for(UUID uuid : players)
			{
				Player z = Bukkit.getPlayer(uuid);
				z.showPlayer(p);
			}
		}
		else
		{
			p.sendMessage(prefix + "Unbekannter Fehler");			
		}
	}

	public void addPlayer(Player p)
	{
		if(players.contains(p.getUniqueId()) || spectators.contains(p.getUniqueId()))
		{
			p.sendMessage(prefix + "Du bist dem Spiel wieder beigetreten");
			for(UUID uuid : spectators)
			{
				Player player = Bukkit.getPlayer(uuid);
				p.hidePlayer(player);
			}
			
			p.setScoreboard(scoreboard);
		}
		else
		{
			if(!(gamestate == GameState.LOBBY))
			{
				addSpectator(p, "Da das Spiel bereits laeuft wurdest du den Zuschauern hinzugefuegt");
			}
			else
			{
				setMetadata(p, "savingStructureName", "NULL");
				setMetadata(p, "savingStructureScheduler", 0);
				setMetadata(p, "savingStructure", false);
				players.add(p.getUniqueId());
				p.sendMessage(prefix + "Du bist dem Spiel beigetreten");
				p.teleport(lobbyLocation);
			
				p.setGameMode(GameMode.ADVENTURE);
		    	p.getInventory().clear();
		    	
		    	p.setFlySpeed(0.1f);
		    	p.setWalkSpeed(0.2f);
		    	p.setSaturation(20);
		    	p.setFoodLevel(20);
		    	p.setLevel(0);
		    	p.setHealth(20);
		    	p.getInventory().clear();
		    	p.getInventory().setHelmet(null);
		    	p.getInventory().setChestplate(null);
		    	p.getInventory().setLeggings(null);
		    	p.getInventory().setBoots(null);				
				ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
				SkullMeta sm = (SkullMeta) is.getItemMeta();
				sm.setOwner(p.getDisplayName());
				sm.setDisplayName("§6" + p.getDisplayName());
				is.setItemMeta(sm);
				if(!skulls.containsKey(p.getDisplayName()))
				{
					skulls.put(p.getDisplayName(), is);					
				}

				//TODO: moeglicherweise unnoetig
				for(UUID uuid : spectators)
				{
					Player player = Bukkit.getPlayer(uuid);
					p.hidePlayer(player);
				}
			}
		}
	}
	
	public void removePlayer(Player p)
	{
		if(players.contains(p.getUniqueId()))
		{
			for(int i = 0; i < players.size(); i++)
			{
				if(Bukkit.getPlayer(players.get(i)).getName().equalsIgnoreCase(p.getName())) //check which index in the arraylist the player is
				{
					if(!(gamestate == GameState.LOBBY))
					{
						plotArray[i].ownerLeft = true; //if the player leaves the plot will know that he has left and will not be graded	
					}
				}
			}
			if(gamestate == GameState.LOBBY)
			{				
				players.remove(p.getUniqueId()); //TODO: make this good, don't remove while game is running, let him reconnect
			}
		}
		else
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
		if(skulls.containsKey(p))
		{
			skulls.remove(p);
		}
		
		//Check if there are no players left
		if(players.size() == 0)
		{
			Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				
				@Override
				public void run() {
					
				}
			}, 20, 20);
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
		
		jumpLocation.setX(locationCfg.getDouble("locations.jumpspawn.x"));
		jumpLocation.setY(locationCfg.getDouble("locations.jumpspawn.y"));
		jumpLocation.setZ(locationCfg.getDouble("locations.jumpspawn.z"));
		jumpLocation.setWorld(Bukkit.getWorld(locationCfg.getString("locations.jumpspawn.world")));
		jumpLocation.setYaw((float) locationCfg.getDouble("locations.jumpspawn.yaw"));
		jumpLocation.setPitch((float) (locationCfg.getDouble("locations.jumpspawn.pitch")));
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
		
		locationCfg.addDefault("locations.originSpawn.x", 24);
		locationCfg.addDefault("locations.originSpawn.y", 10);
		locationCfg.addDefault("locations.originSpawn.z", 56);
		locationCfg.addDefault("locations.originSpawn.world", "BuildingGame");
		locationCfg.addDefault("locations.originSpawn.yaw", 45d);
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
				plotArray[z * 4 + x] = new Plot(new Location(origin.getWorld(), origin.getX() - 43 * x, origin.getY(), origin.getZ() + 43 * z));
				plotArray[z * 4 + x].setSpawnLocation(new Location(originSpawn.getWorld(), originSpawn.getX() - 43 * x, originSpawn.getY(), originSpawn.getZ() + 43 * z)); 
			}
		}
		
		
		
	}

	public void loadBuildThemes()
	{
		int themeAmount = configCfg.getInt("themeAmount");
		int themeCounter = themeAmount;
		random = new Random();
		themes = (List<String>) themesCfg.getList("themes");
		votes = new int[themes.size()];
		votingInventory = Bukkit.createInventory(null, themeAmount, "§6§lThemen");
		
		if(themes.size()>themeAmount)
		{
			for(int i = 0; i < themeCounter; i++)
			{
				int r = random.nextInt(themes.size());
				if(finalThemes.contains(themes.get(r)))
				{
					themeCounter++;
				}
				else
				{
					finalThemes.add(themes.get(r));
					ItemStack is = new ItemStack(Material.PAPER);
					ItemMeta im = is.getItemMeta();
					im.setDisplayName("§7" + themes.get(r));
					is.setItemMeta(im);
					votingInventory.addItem(is);
				}

			}
		}
		else
		{
			for(int i = 0; i < themes.size(); i++)
			{
				ItemStack is = new ItemStack(Material.PAPER);
				ItemMeta im = is.getItemMeta();
				im.setDisplayName("§7" + themes.get(i));
				is.setItemMeta(im);
				finalThemes.add(themes.get(i));
				votingInventory.addItem(is);
			}
		}
	}

	private String calculateFinalTheme()
	{
		random = new Random();
		max = 0;
		ArrayList<Integer> maxindex = new ArrayList<>();
		for(int i = 0; i < finalThemes.size(); i++)
		{
			if(votes[i] > max) {
				max = votes[i];
				maxindex.clear();
				maxindex.add(i);
				
			}
			else if(votes[i]==max)
			{
				maxindex.add(i);
			}
		}
		return (String) finalThemes.get(maxindex.get(random.nextInt(maxindex.size())));
	}

	public void cancelScheduler(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
	}

	private void updateScoreboard()
	{
		scoreboard.resetScores(currentBuildingtime);
		buildingTime--;
		for(UUID uuid : players)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			if(op.isOnline())
			{
				Player p = Bukkit.getPlayer(uuid);
				if(buildingTime<=10 && p.isOnline()) p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);				
			}
		}
		if(buildingTime <= 0)
		{
			
			gamestate = gamestate.GRADING;
			startGradingProcess();
		}
		else
		{
			if(buildingTime%60>9)
			{
				currentBuildingtime = "§7§l" + buildingTime / 60 + ":" + buildingTime % 60;
			} 
			else
			{
				currentBuildingtime = "§7§l" + buildingTime / 60 + ":0" + buildingTime % 60;
			}
			timeScore = bgObjective.getScore(currentBuildingtime);
			timeScore.setScore(3);
		}


	}
	
	private void startGradingProcess()
	{
		for(UUID uuid : players)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			
			if(op.isOnline())
			{
				Player p = Bukkit.getPlayer(uuid);
				p.setGameMode(GameMode.CREATIVE);
				p.sendTitle("§6§lPlots bewerten", "§7Bewerte die Bauwerke mit der Prismarin-Scherbe");				
			}
			
		}
		for(UUID uuid : spectators)
		{
			Player p = Bukkit.getPlayer(uuid);
			p.sendTitle("§6§lPlots bewerten", "");	
		}
		
		gradePlot(0);	 
	}
	
	private void gradePlot(int i)
	{
		int id = i;
		if(plotArray[id+1].ownerLeft) //TODO: Doppelter Sceduler Ablauf
		{
			gradePlot(id+1);
		}
		
		currentPlotInGradingProcess = id;
		scoreboardSecondsToGrade = secondsToGrade;
		gradeTimer = 0;

		for(UUID uuid : players)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			
			if(op.isOnline())
			{
				Player p = Bukkit.getPlayer(uuid);
				p.teleport(plotArray[id].getSpawnLocation());
				gradingInventories.put(p, new VotingInventory());
				gradingInventories.get(p).resetInventory();
			}
		}
		for(UUID uuid : spectators)
		{
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(plotArray[id].getSpawnLocation());
		}
		
		scoreboard.resetScores("§70:00");
		scoreboard.resetScores(currentBuildingtime);
		scoreboard.resetScores("§6Zeit:");
		Score scoreErbauer = bgObjective.getScore("§6Erbauer:");
		scoreErbauer.setScore(4);
		
		Score scoreZeit = bgObjective.getScore("§6Zeit:");
		scoreZeit.setScore(1);
		
		Score scorel = bgObjective.getScore("   ");
		scorel.setScore(2);
		
		Score scoreTime = bgObjective.getScore(currentGradingtime);
		scoreTime.setScore(0);
		
		Bukkit.getScheduler().cancelTask(buildingTimerScheduler);
		
		ItemStack is = new ItemStack(Material.PRISMARINE_SHARD);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName("§6§lBewerten");
		List<String> lore = new ArrayList<>();
		lore.add("§7Rechtsklicke um das Plot zu bewerten");
		im.setLore(lore);
		is.setItemMeta(im);
		for(UUID uuid : players)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			
			if(op.isOnline())
			{
				Player p = Bukkit.getPlayer(uuid);
				for(PotionEffect e : p.getActivePotionEffects())
				{
					p.removePotionEffect(e.getType());
				}
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
				p.getInventory().clear();
				p.getInventory().setHelmet(null);
				p.getInventory().setChestplate(null);
				p.getInventory().setLeggings(null);
				p.getInventory().setBoots(null);	
				
//				if(plotArray[i].getOwner().isOnline())
//				{
					if(plotArray[id].getOwner().getName().equals(p.getName()))
					{
						//pfusch
		            }
		            else 
		            {
		            	p.getInventory().setItem(4, is);
		            }	
//				}
			}
		}

		scoreboard.resetScores(scoreboardPlotOwner);
		Score scoreK = bgObjective.getScore("§7§kPeterRendl");
		scoreK.setScore(3);
		
		
		int schedulerid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(scoreboardSecondsToGrade < 6 && scoreboardSecondsToGrade > 0)
				{
					for(UUID uuid : players)
					{
						OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
						
						if(op.isOnline())
						{
							Player p = Bukkit.getPlayer(uuid);
							p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
						}
					}
					
				}
				scoreboard.resetScores(currentGradingtime);
				if(scoreboardSecondsToGrade%60>9)
				{
					currentGradingtime = "§7§l" + scoreboardSecondsToGrade / 60 + ":" + scoreboardSecondsToGrade % 60;
				} 
				else
				{
					currentGradingtime = "§7§l" + scoreboardSecondsToGrade / 60 + ":0" + scoreboardSecondsToGrade % 60;
				}
				timeScore = bgObjective.getScore(currentGradingtime);
				timeScore.setScore(0);
				scoreboardSecondsToGrade--;
				
							
			}
		}, 0, 20);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getServer().getScheduler().cancelTask(schedulerid);
				
				//reveal name
				scoreboard.resetScores("§7§kPeterRendl");
				scoreboardPlotOwner = "§7"+plotArray[id].getOwner().getName();
				Score scoreK = bgObjective.getScore(scoreboardPlotOwner);
				scoreK.setScore(3);
				
				//remove prismarine shard
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					
					if(op.isOnline())
					{
	
						Player p = Bukkit.getPlayer(uuid);
						p.sendTitle("§6§l" + plotArray[id].getOwner().getName(), "§7hat dieses Bauwerk errichtet");
						p.getInventory().setItem(4, null);
						p.getInventory().setItem(0, null);					
						p.closeInventory();
					}
				}
				
				for(UUID uuid : spectators)
				{
					Player p = Bukkit.getPlayer(uuid);
					p.sendTitle("§6§l" + plotArray[id].getOwner().getName(), "§7hat dieses Bauwerk errichtet");
				}
				
				//save rating
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					
					if(op.isOnline())
					{
						Player p = Bukkit.getPlayer(uuid);
						plotArray[id].addGradeCreativity(convertGrade(gradingInventories.get(p).voteBuffer[0])); //TODO: nullpointer wenn ein spieler waehrend der voting phase leavt
						plotArray[id].addGradeLook(convertGrade(gradingInventories.get(p).voteBuffer[1]));
						plotArray[id].addGradeFitting(convertGrade(gradingInventories.get(p).voteBuffer[2]));
					}
				}
				
				
			}
		}, (20 * secondsToGrade)+1);
		
		
		
		
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if((!plotArray[id + 1].ownerLeft))
				{
					if(id + 2 > players.size())
					{
						//there are no plots availible, change to GameState.END
						Bukkit.getServer().getScheduler().cancelAllTasks();
						endGame(EndReason.NORMAL_END);
						return;
					}
				
					//resume with next plot
					gradePlot(id + 1);
				}
				else
				{
//					Bukkit.getServer().getScheduler().cancelAllTasks();
//					endGame(EndReason.PLAYER_LEFT);
//					return;
					//player left
				}
			}
		}, 20 * (gradingNameRevealTime+secondsToGrade));
		
		
	}
	
	public void endGame(EndReason reason)
	{
		Bukkit.getServer().getScheduler().cancelAllTasks();
		
		//calculate winner
		int[] grades = new int[players.size()];
		for(int i = 0; i < players.size(); i++)
		{
			plotArray[i].calculateFinalGrade();
			grades[i] = plotArray[i].getFinalTotalGrade();
		}
		
		
		
		int maxvalue = 0;
		
		for(int i = 0; i < players.size(); i++)
		{
			if(grades[i] > maxvalue)
			{
				maxvalue = grades[i];
				maxindex = i;
			}
		}

		Bukkit.broadcastMessage(prefix + "Das Spiel ist zu Ende");
		OfflinePlayer winner = plotArray[maxindex].getOwner();
		if(winner.isOnline())
		{
			ItemStack winnerHelmet = new ItemStack(Material.GOLD_HELMET);
			ItemStack firework = new ItemStack(Material.FIREWORK);
			ItemMeta fireworkMeta = firework.getItemMeta();
			fireworkMeta.setDisplayName("§6§lFeuerwerk ein/aus");
			firework.setItemMeta(fireworkMeta);
			winner.getPlayer().getInventory().setItem(39, winnerHelmet);
			winner.getPlayer().getInventory().setItem(4, firework);
			
		}
		
		for(UUID uuid : players)
		{
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
			
			if(op.isOnline())
			{
				Player p = Bukkit.getPlayer(uuid);
				p.sendTitle("§6" + winner.getName(), "§7hat das Spiel gewonnen (§6" + maxvalue + "§7)! Glueckwunsch!");
				p.teleport(plotArray[maxindex].spawnLocation);
			
				for(UUID uuid2 : spectators)
				{
					Player z = Bukkit.getPlayer(uuid2);
					p.showPlayer(z);
				}
			}
			
		}
		for(UUID uuid : spectators)
		{
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(plotArray[maxindex].spawnLocation);
			p.sendTitle("§6" + winner.getName(), "§7hat das Spiel gewonnen (§6" + maxvalue + "§7)! Glueckwunsch!");
		}
		
		scoreboard.resetScores("§7§l0:00");
		scoreboard.resetScores(currentBuildingtime);
		scoreboard.resetScores("§6Zeit:");
		scoreboard.resetScores("§6Erbauer:");
		scoreboard.resetScores("§6Thema:");
		scoreboard.resetScores("§7" + finalTheme);
		scoreboard.resetScores("");
		scoreboard.resetScores(" ");
		scoreboard.resetScores("   ");
		scoreboard.resetScores(scoreboardPlotOwner);
		
		
		Score rangliste = bgObjective.getScore("§6Rangliste:");
		rangliste.setScore(maxvalue+2);
		
		Score space = bgObjective.getScore(" ");
		space.setScore(maxvalue+1);
		
		for(Plot p : plotArray)
		{
			if(p.getOwner()!=null)
			{
			Score score = bgObjective.getScore("§7"+p.getOwner().getName());
			score.setScore(p.getFinalTotalGrade());
			}
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(launchFirework)
				{
					launchFirework();
				}
				
			}
		}, 0, 15);
	
	
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				for(UUID uuid : players)
				{
					Player p = Bukkit.getPlayer(uuid);
					//p.kickPlayer(playerprefix + "Das Spiel ist zu Ende");
				}
				
				Bukkit.getServer().shutdown();
				
			}
		}, 20* 40);
	
	}
	
	private int convertGrade(int g)
	{
		if(g == 3) return 5;
		if(g == 4) return 3;
		if(g == 5) return 1;
		if(g == 6) return -1;
		if(g == 7) return -3;
		if(g == 8) return 0;
		
		return 69;
	}
	
	private void launchFirework()
	{
		
		OfflinePlayer p = plotArray[maxindex].getOwner();
		if(p.isOnline())
		{
			 //Spawn the Firework, get the FireworkMeta.
	        Firework fw = (Firework) p.getPlayer().getWorld().spawnEntity(p.getPlayer().getLocation(), EntityType.FIREWORK);
	        FireworkMeta fwm = fw.getFireworkMeta();
	        //Get the type
	        int rt = random.nextInt(4) + 1;
	        Type type = Type.BALL;
	        if (rt == 1) type = Type.BALL;
	        if (rt == 2) type = Type.BALL_LARGE;
	        if (rt == 3) type = Type.BURST;
	        if (rt == 4) type = Type.CREEPER;
	        if (rt == 5) type = Type.STAR;
	        Color c1 = randomColor();
	        Color c2 = randomColor();
	       
	        //Create our effect with this
	        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(random.nextBoolean()).build();
	       
	        //Then apply the effect to the meta
	        fwm.addEffect(effect);
	       
	        //Generate some random power and set it
	        //int rp = random.nextInt(2) + 1;
	        fwm.setPower(1);
	       
	        //Then apply this to our rocket
	        fw.setFireworkMeta(fwm);
	        
		}
		
	}
	
	
	
	
	/**
	 * add one to gradeTimer
	 * @return 
	 */
	private int timer()
	{
		return gradeTimer++;
	}
	
	
	private Color randomColor()
	{
		int r = random.nextInt(17);
		
		switch(r)
		{
			case 0: return Color.AQUA;
			case 1: return Color.BLACK;
			case 2: return Color.BLUE;
			case 3: return Color.FUCHSIA;
			case 4: return Color.GRAY;
			case 5: return Color.GREEN;
			case 6: return Color.LIME;
			case 7: return Color.MAROON;
			case 8: return Color.NAVY;
			case 9: return Color.OLIVE;
			case 10: return Color.ORANGE;
			case 11: return Color.PURPLE;
			case 12: return Color.RED;
			case 13: return Color.SILVER;
			case 14: return Color.TEAL;
			case 15: return Color.WHITE;
			default: return Color.YELLOW;
		}
		
	}
	
	public DyeColor randomDyeColor()
	{
		int r = random.nextInt(17);
		
		switch(r)
		{
			case 0: return DyeColor.BLUE;
			case 1: return DyeColor.BLACK;
			case 2: return DyeColor.BROWN;
			case 3: return DyeColor.CYAN;
			case 4: return DyeColor.GRAY;
			case 5: return DyeColor.GREEN;
			case 6: return DyeColor.LIGHT_BLUE;
			case 7: return DyeColor.LIME;
			case 8: return DyeColor.MAGENTA;
			case 9: return DyeColor.ORANGE;
			case 10: return DyeColor.PINK;
			case 11: return DyeColor.PURPLE;
			case 12: return DyeColor.RED;
			case 13: return DyeColor.SILVER;
			case 14: return DyeColor.WHITE;
			case 15: return DyeColor.YELLOW;
			default: return DyeColor.YELLOW;
		}
		
	}
	
	public EntityType randomEntity()
	{
		int r = random.nextInt(13);
		
		switch(r)
		{
			case 0: return EntityType.BAT;
			case 1: return EntityType.CHICKEN;
			case 2: return EntityType.COW;
			case 3: return EntityType.PIG;
			case 4: return EntityType.SQUID;
			case 5: return EntityType.HORSE;
			case 6: return EntityType.IRON_GOLEM;
			case 7: return EntityType.WOLF;
			case 8: return EntityType.VILLAGER;
			case 9: return EntityType.SNOWMAN;
			case 10: return EntityType.SHEEP;
			case 11: return EntityType.RABBIT;
			case 12: return EntityType.OCELOT;
			default: return EntityType.SHEEP;
		}
		
	}
	
	public void openTeleportInventory(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 18, "§6§lSpieler beobachten");
		for(UUID uuid : players)
		{
			Player z = Bukkit.getPlayer(uuid);
			inv.addItem(skulls.get(z.getDisplayName()));
		}
		p.openInventory(inv);
	}
		
	public void playPlayerBrawlBrawl(PlayerBrawl brawl)
	{
		brawl.start();
	}
	
	public void playPlotBrawlBrawl(PlotBrawl brawl)
	{
		brawl.start();
	}
	
	public Player randomBrawlVictim(Player sender)
	{
		int r = random.nextInt(players.size());
		boolean isNotSender = false;
		while(!isNotSender)
		{
			if(!players.get(r).equals(sender.getUniqueId()))
			{
				if(Bukkit.getOfflinePlayer(players.get(r)).isOnline())
				{
					isNotSender=true;					
				}
			}
			else
			{
				r = random.nextInt(players.size());
			}
		}
		
//		
//		while(!Bukkit.getOfflinePlayer(players.get(r)).isOnline() && sender.getUniqueId().equals(players.get(r)))
//		{
//			r = random.nextInt(players.size());
//		}
		
		return Bukkit.getPlayer(players.get(r));
		
	}
	
	public Plot getPlot(Player owner)
	{
		for(int i = 0; i<plotArray.length; i++)
		{
			if(plotArray[i].getOwner()!=null)
			{
				if(plotArray[i].getOwner().getUniqueId().equals(owner.getUniqueId()))
				{
					return plotArray[i];
				}
			}
		}
		return null;
	}
	
	public void setMetadata(Player p, String key, Object value)
	{
		p.setMetadata(key, new FixedMetadataValue(plugin, value));
	}
	
	public List<MetadataValue> getMetadata(Player p, String key)
	{
		return p.getMetadata(key);
	}
	
	public boolean getMetadataBoolean(Player p, String key)
	{
		return p.getMetadata(key).get(0).asBoolean();
	}
	
	public String getMetadataString(Player p, String key)
	{
		return p.getMetadata(key).get(0).asString();	
	}
	
	public int getMetadataInteger(Player p, String key)
	{
		return p.getMetadata(key).get(0).asInt();
	}
	


}
