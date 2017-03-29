package game;

import static org.bukkit.Bukkit.getScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
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

import brawls.Animation;
import brawls.Brawl;
import brawls.BrawlBlindness;
import brawls.BrawlEntity;
import brawls.BrawlFly;
import brawls.BrawlFreeze;
import brawls.BrawlHerobrine;
import brawls.BrawlInventoryClear;
import brawls.BrawlInventoryClose;
import brawls.BrawlJump;
import brawls.BrawlNull;
import brawls.BrawlPolymorph;
import brawls.BrawlProtect;
import brawls.BrawlPumpkin;
import brawls.BrawlRandomTP;
import brawls.BrawlReplace;
import brawls.BrawlRotate;
import brawls.BrawlSandstorm;
import brawls.BrawlSpeed;
import brawls.BrawlUnderwater;
import brawls.PlayerBrawl;
import brawls.PlotBrawl;
import structures.Structure;
import structures.StructureParser;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingBrawl§1§l>> §r§7";
	public static String playerprefix = "§2§ll§r§a BuildingBrawl§2>> §r§7";
	public static int secondsToGrade = 25;
	public static int MAX_PLAYERS = 16;
	
	
	
	public static List<Material> forbiddenBlocks = new ArrayList<>();

	public Plugin plugin;
	
	public List<UUID> players = new ArrayList<>();
	public List<UUID> spectators = new ArrayList<>();
	public List<Brawl> brawlList = new ArrayList<>();
	
	public Structure[] structures;
	
	public Plot[] plotArray = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY; //default gamestate
	public Inventory votingInventory; //voting inventory preset
	public Inventory resetInventory;
	private ItemStack resetIS;
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
	public ItemStack brawlIS;
	
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
	public  int herobrineCounter = 0;
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
		structures = StructureParser.loadStructures();
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
		createResetInv();
		this.plugin = plugin;
		
		compassmeta.setDisplayName("§6Spieler beobachten");
		compass.setItemMeta(compassmeta);
		brawlIS = createItemStack(Material.NETHER_STAR, "§6§lTeleport zum Brawl Raum","§7Teleportiere dich für 20 Sekunden zu dem Brawl-Raum");
		
		random = new Random();
	}
	
	private void createResetInv()
	{
		resetIS = createItemStackColor(Material.INK_SACK, 1, (short)1, "§4§lGrundstück zurücksetzen", null);
		ItemStack greenGlassIS = createItemStackColor(Material.STAINED_GLASS_PANE, 1, (short)5, "", null);
		ItemStack redGlassIS = createItemStackColor(Material.STAINED_GLASS_PANE, 1, (short)14, "", null);
		ItemStack jaIS = createItemStackBetterLore(Material.EMERALD_BLOCK, "§2§lJa", new String[] { "§aIch möchte mein Grundstück zurücksetzen", "§a(Dieser Vorgang kann nicht mehr Rückgängig", "§agemacht werden)!" });
		ItemStack neinIS = createItemStack(Material.REDSTONE_BLOCK, "§4§lNein", "§cNein, ich möchte die Aktion abbrechen");
		resetInventory = Bukkit.createInventory(null, 27, "§6Wirklich zurücksetzen?");
		
		resetInventory.setItem(1, greenGlassIS);
		resetInventory.setItem(2, greenGlassIS);
		resetInventory.setItem(3, greenGlassIS);
		resetInventory.setItem(10, greenGlassIS);
		resetInventory.setItem(12, greenGlassIS);
		resetInventory.setItem(19, greenGlassIS);
		resetInventory.setItem(20, greenGlassIS);
		resetInventory.setItem(21, greenGlassIS);
		
		resetInventory.setItem(5, redGlassIS);
		resetInventory.setItem(6, redGlassIS);
		resetInventory.setItem(7, redGlassIS);
		resetInventory.setItem(14, redGlassIS);
		resetInventory.setItem(16, redGlassIS);
		resetInventory.setItem(23, redGlassIS);
		resetInventory.setItem(24, redGlassIS);
		resetInventory.setItem(25, redGlassIS);
		
		resetInventory.setItem(11, jaIS);
		resetInventory.setItem(15, neinIS);
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
			p.getInventory().setItem(8, resetIS);
			p.getInventory().setItem(7, brawlIS);
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
		
		for(UUID uuid : spectators)
		{
			Player p = Bukkit.getPlayer(uuid);
			p.teleport(Bukkit.getPlayer(players.get(0)).getLocation());
			p.setScoreboard(scoreboard);
			p.sendMessage(prefix + "Das Thema ist §6" + finalTheme +"§r§7 ("+max+" Stimme(n))");
			p.sendTitle("§7Thema: §6§l" + finalTheme, "§7Noch §6 "+(buildingTime / 60 + ":" + buildingTime % 60)+" §7Minuten verbleiben");
			p.setFlying(true);
			p.getInventory().setItem(8, compass);
		}

		//1/s Tick while Building
		final int buildingTimerTask;
		buildingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				updateScoreboard();
				
				//Brawl Cooldowns & brawlroom
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = Bukkit.getPlayer(uuid);
						if(getBrawlCooldown(p) > 0)
						{
							addBrawlCooldown(p, -1);
						}
						
						if(p.hasMetadata("isInBrawlRoom"))
						{
							if(getMetadataBoolean(p, "isInBrawlRoom"))
							{
								if(p.getLevel() <= 0)
								{
									setMetadata(p, "isInBrawlRoom", false);
									if(gamestate == GameState.BUILDING)
									{
										removeBrawlProtection(p);
										p.setGameMode(GameMode.CREATIVE);
										p.teleport(getPlot(p).getSpawnLocation());
									}
								}
								else
								{
									if(gamestate == GameState.BUILDING)
									{
										p.setLevel(p.getLevel() - 1);																			
									}
									else
									{
										removeBrawlProtection(p);
										p.setGameMode(GameMode.CREATIVE);
									}
								}
							}
						}
					}
				}
				
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
			p.getInventory().setItem(8, compass);
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
			
			if(gamestate == GameState.END)
			{
				p.getInventory().clear();
				p.teleport(plotArray[maxindex].getSpawnLocation());			
			}
		}
		else
		{
			setDefaultMetadataValues(p);
			if(!(gamestate == GameState.LOBBY))
			{
				addSpectator(p, "Da das Spiel bereits laeuft wurdest du den Zuschauern hinzugefuegt");
			}
			else
			{
				players.add(p.getUniqueId());
				p.sendMessage(prefix + "Du bist dem Spiel beigetreten");
				p.teleport(lobbyLocation);
			
				p.setGameMode(GameMode.ADVENTURE);
		    	p.getInventory().clear();
		    	
		    	p.setAllowFlight(true);
		    	p.setFlying(true);
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
//			for(int i = 0; i < players.size(); i++)
//			{
//				if(Bukkit.getPlayer(players.get(i)).getName().equalsIgnoreCase(p.getName())) //check which index in the arraylist the player is
//				{
//					if(!(gamestate == GameState.LOBBY))
//					{
//						plotArray[i].ownerLeft = true; //if the player leaves the plot will know that he has left and will not be graded	
//					}
//				}
//			}
			if(gamestate == GameState.LOBBY)
			{				
				players.remove(p.getUniqueId());
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
					ItemStack themeIS = createItemStack(Material.PAPER, "§7" + themes.get(r), null);
					votingInventory.addItem(themeIS);
				}

			}
		}
		else
		{
			for(int i = 0; i < themes.size(); i++)
			{
				ItemStack themeIS = createItemStack(Material.PAPER, "§7" + themes.get(i), null);
				finalThemes.add(themes.get(i));
				votingInventory.addItem(themeIS);
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
		//stop all brawls
		//TODO: maybe brawl used stats?
		for(Brawl b : brawlList)
		{
			b.stop();
		}
		brawlList.clear();
		for(Plot plot : plotArray)
		{
			plot.setShield(1);
			plot.damageShield();
		}
		
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
				VotingInventory vi = new VotingInventory();
				vi.resetInventory();
				vi.updateInventory();
				gradingInventories.put(p, vi);
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
		
		ItemStack prismarinIS = createItemStack(Material.PRISMARINE_SHARD, "§6§lBewerten", "§7Rechtsklicke um das Plot zu bewerten");
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
		            	p.getInventory().setItem(4, prismarinIS);
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
						plotArray[id].addGradeCreativity(convertGrade(gradingInventories.get(p).voteBuffer[0]));
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
						endGame();
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

	public void endGame()
	{
		Bukkit.getServer().getScheduler().cancelAllTasks();
		gamestate = GameState.END;
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
			ItemStack firework = createItemStack(Material.FIREWORK, "§6§lFeuerwerk ein/aus", null);
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
		boolean allProtected = true;
		for(UUID uuid : players)
		{
			if(!isBrawlProtected(Bukkit.getPlayer(uuid)) && !uuid.equals(sender.getUniqueId()))
			{
				allProtected=false;
			}
		}
		if(!allProtected)
		{	
			while(!isNotSender)
			{
				if(!players.get(r).equals(sender.getUniqueId()))
				{
					if(Bukkit.getOfflinePlayer(players.get(r)).isOnline() && !isBrawlProtected(Bukkit.getPlayer(players.get(r))))
					{
						isNotSender=true;					
					}
				}
				else
				{
					r = random.nextInt(players.size());
				}
			}	
			return Bukkit.getPlayer(players.get(r));
		}
		else
		{
			return null;
		}
//		
//		while(!Bukkit.getOfflinePlayer(players.get(r)).isOnline() && sender.getUniqueId().equals(players.get(r)))
//		{
//			r = random.nextInt(players.size());
//		}
			
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
		if(p.hasMetadata(key))
		{
			return p.getMetadata(key);			
		}
		else 
		{
			return null;
		}
	}
	
	public boolean getMetadataBoolean(Player p, String key)
	{
		if(p.hasMetadata(key))
		{
			return p.getMetadata(key).get(0).asBoolean();
		}
		else
		{
			setMetadata(p, key, false);
			return p.getMetadata(key).get(0).asBoolean();
		}
	}
	
	public String getMetadataString(Player p, String key)
	{
		return p.hasMetadata(key) ? p.getMetadata(key).get(0).asString() : null;	
	}
	
	public int getMetadataInteger(Player p, String key)
	{
		return p.hasMetadata(key) ? p.getMetadata(key).get(0).asInt() : null;
	}
	
	private static ItemStack createItemStackBetterLore(Material material, String name, String[] lore) {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> loreText = new ArrayList<>();
		if(lore != null)
		{
			for(int i = 0; i < lore.length; i++)
			{
				loreText.add(lore[i]);
			}			
			im.setLore(loreText);
		}
		
		is.setItemMeta(im);
		return is;
	}
	
	private static ItemStack createItemStack(Material material, String name, String lore) {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		if(lore!=null)
		{
			List<String> loreText = new ArrayList<>();
			loreText.add(lore);			
			im.setLore(loreText);
		}
		is.setItemMeta(im);
		return is;
	}
	
	private static ItemStack createItemStackColor(Material material, int amount, short damage, String name, String lore) {
		ItemStack is = new ItemStack(material, amount, damage);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		if(lore!=null)
		{
			List<String> loreText = new ArrayList<>();
			loreText.add(lore);
			im.setLore(loreText);			
		}
		is.setItemMeta(im);
		return is;
	}
	
	public void resetPlot(Player p)
	{
		Plot plot = getPlot(p);
		Location temp = plot.getSpawnLocation();
		World world = Bukkit.getWorld(locationCfg.getString("locations.lobby.world"));
		Location replaceLoc = new Location(temp.getWorld(), temp.getX(), temp.getY(), temp.getZ());
		replaceLoc.setY(5);
		replaceLoc.setX(replaceLoc.getX()-1);
		replaceLoc.setZ(replaceLoc.getZ()+1);
		replaceLoc.setY(replaceLoc.getY()-3);
		Collection<Entity> entities = world.getNearbyEntities(replaceLoc, -37, 50, 37);
		for(Entity e : entities)
		{
			if(!(e instanceof Player))
			{
				e.remove();
			}
		}
	    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
	    Location edgeMax = new Location(world, replaceLoc.getX()-32, replaceLoc.getY()+52, replaceLoc.getZ()+32);
	    for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --) {
	        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
	            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
	            	if(y<4)
	            	{
	            		new Location(world, x, y, z).getBlock().setType(Material.STAINED_CLAY);	            		
	            	}
	            	else
	            	{
	            		new Location(world, x, y, z).getBlock().setType(Material.AIR);	    
	            	}
	            }
	        }
	    }	    
	    p.sendMessage(playerprefix+"Dein Grundstück wurde erfolgreich zurückgesetzt");
	}
	
	public void startBrawl(String name, Player starter, Location startlocation, Structure s)
	{
		if(gamestate == GameState.BUILDING)
		{
		switch (name)
		{
		case "freeze":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.ICE, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlFreeze(p, this), Material.ICE, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownFreeze"));
					}
				}
				s.destroy();
			}
			break;
		case "speed":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.SUGAR_CANE_BLOCK, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlSpeed(p, this), Material.SUGAR_CANE_BLOCK, this).prepare();		
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownSpeed"));
					}
				}
				s.destroy();	
			}
			break;
		case "invclear":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.WORKBENCH, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlInventoryClear(p, this), Material.WORKBENCH, this).prepare();					
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownInvclear"));
					}
				}
				s.destroy();	
			}
			break;
		case "pumpkin":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.PUMPKIN, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlPumpkin(p, this), Material.PUMPKIN, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownPumpkin"));
					}
				}
				s.destroy();	
			}
			break;
		case "blindness":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.SOUL_SAND, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlBlindness(p, this), Material.SOUL_SAND, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownBlindness"));
					}
				}
				s.destroy();			
			}
			break;
		case "fly":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.STAINED_GLASS, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlFly(p, this), Material.STAINED_GLASS, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownFly"));
					}
				}
				s.destroy();		
			}
			break;
		case "replace":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.PRISMARINE, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlReplace(p, this), Material.PRISMARINE, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownReplace"));
					}
				}
				s.destroy();	
			}
			break;
		case "polymorph":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.WOOL, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlPolymorph(p, this), Material.WOOL, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownPolymorph"));
					}
				}
				s.destroy();
			}
			break;
		case "jump":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				Player p1 = randomBrawlVictim(starter);
				if(p1==null)
				{
					starter.sendMessage(playerprefix+"Es sind keine gültigen Ziele vorhanden!");
					break;
				}
				else
				{
					if(getPlot(p1).getShield()>0)
					{
						new Animation(startlocation, getPlot(p1).spawnLocation, new BrawlNull(getPlot(p1), this), Material.QUARTZ_BLOCK, this).prepare();	
					}
					else
					{
						new Animation(startlocation, getPlot(p1).spawnLocation, new BrawlJump(p1, this), Material.QUARTZ_BLOCK, this).prepare();									
					}
					addBrawlCooldown(starter, configCfg.getInt("brawlCooldownJump"));
					s.destroy();
			}
				break;	
			}
		case "herobrine":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				Player p0 = randomBrawlVictim(starter);
				if(p0==null)
				{
					starter.sendMessage(playerprefix+"Es sind keine gültigen Ziele vorhanden!");
					break;
				}
				else
				{
					if(getPlot(p0).getShield()>0)
					{
						new Animation(startlocation, getPlot(p0).spawnLocation, new BrawlNull(getPlot(p0), this), Material.FIRE, this).prepare();	
					}
					else
					{
						new Animation(startlocation, getPlot(p0).spawnLocation, new BrawlHerobrine(p0, this), Material.FIRE, this).prepare();									
					}
					addBrawlCooldown(starter, configCfg.getInt("brawlCooldownHerobrine"));
					s.destroy();		
			}
				break;
			}
		case "randomtp":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.ENDER_STONE, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlRandomTP(p, this), Material.ENDER_STONE, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownRandomTP"));
					}
				}
				s.destroy();			
			}
			break;
		case "rotate":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.LOG, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlRotate(p, this), Material.LOG, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownRotate"));
					}
				}
				s.destroy();			
			}
			break;
		case "entity":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.GRASS, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlEntity(getPlot(p), this), Material.GRASS, this).prepare();										
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownEntity"));
					}
				}
				s.destroy();	
			}
			break;
		case "underwater":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				Player p2 = randomBrawlVictim(starter);
				if(p2==null)
				{
					starter.sendMessage(playerprefix+"Es sind keine gültigen Ziele vorhanden!");
					break;
				}
				else
				{
					if(getPlot(p2).getShield()>0)
					{
						new Animation(startlocation, getPlot(p2).spawnLocation, new BrawlNull(getPlot(p2), this), Material.STATIONARY_WATER, this).prepare();	
					}
					else
					{
						new Animation(startlocation, getPlot(p2).spawnLocation, new BrawlUnderwater(getPlot(p2), this), Material.STATIONARY_WATER, this).prepare();									
					}
					addBrawlCooldown(starter, configCfg.getInt("brawlCooldownUnderwater"));
					s.destroy();
			}
				break;
			}
		case "invclose":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				for(UUID uuid : players)
				{
					OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
					if(op.isOnline())
					{
						Player p = op.getPlayer();
						if(uuid.equals(starter.getUniqueId())) continue;
						if(isBrawlProtected(p)) continue;
						if(getPlot(p).getShield()>0)
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlNull(getPlot(p), this), Material.CHEST, this).prepare();	
						}
						else
						{
							new Animation(startlocation, getPlot(p).spawnLocation, new BrawlInventoryClose(p, this), Material.CHEST, this).prepare();											
						}
						addBrawlCooldown(starter, configCfg.getInt("brawlCooldownInvclose"));
					}
				}
				s.destroy();
			}
			break;
		case "sandstorm":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				Player p3 = randomBrawlVictim(starter);
				if(p3==null)
				{
					starter.sendMessage(playerprefix+"Es sind keine gültigen Ziele vorhanden!");
					break;
				}
				else
				{
					if(getPlot(p3).getShield()>0)
					{
						new Animation(startlocation, getPlot(p3).spawnLocation, new BrawlNull(getPlot(p3), this), Material.SAND, this).prepare();	
					}
					else
					{
						new Animation(startlocation, getPlot(p3).spawnLocation, new BrawlSandstorm(getPlot(p3), this), Material.SAND, this).prepare();									
					}
					addBrawlCooldown(starter, configCfg.getInt("brawlCooldownSandstorm"));
					s.destroy();		
				}
			}
			break;
			
		case "protect":
			//
			if(getBrawlCooldown(starter)>0)
			{
				starter.sendMessage(playerprefix+"Du kannst dein nächstes Brawl erst in §6§l"+getBrawlCooldown(starter)+" Sekunden §r§7abschicken!");
			}
			else
			{
				new Animation(startlocation, getPlot(starter).spawnLocation, new BrawlProtect(getPlot(starter), this), Material.DIAMOND_BLOCK, this).prepare();		
				addBrawlCooldown(starter, configCfg.getInt("brawlCooldownProtect"));
				s.destroy();				
			}
			break;
			
		default:
			Bukkit.broadcastMessage(prefix + "Der Brawl " + name + " ist nicht verfuegbar");
		}
		}
	}
	
	public void setBrawlCooldown(Player p, int cooldown)
	{
		setMetadata(p, "brawlCooldown", cooldown);
	}
	
	public void addBrawlCooldown(Player p, int acd)
	{
		setMetadata(p, "brawlCooldown", getMetadataInteger(p, "brawlCooldown") + acd);
	}
	
	public int getBrawlCooldown(Player p)
	{
		return getMetadataInteger(p,"brawlCooldown");
	}
	
	
	public void setDefaultMetadataValues(Player p)
	{
		setMetadata(p, "savingStructureName", "NULL");
		setMetadata(p, "savingStructureScheduler", 0);
		setMetadata(p, "savingStructure", false);
		setMetadata(p, "brawlProtection", false);
		setMetadata(p, "brawlCooldown", 0);
		setMetadata(p, "isInBrawlRoom", false);
	}
	
	public void addBrawlProtection(Player p)
	{
		setMetadata(p, "brawlProtection", true);
	}

	public void removeBrawlProtection(Player p)
	{
		setMetadata(p, "brawlProtection", false);
	}
	
	public boolean isBrawlProtected(Player p)
	{
		return p.hasMetadata("brawlProtection") ? getMetadataBoolean(p, "brawlProtection") : null;
	}
}
