package game;

import static org.bukkit.Bukkit.getScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingBrawl§1§l>> §r§7";
	public static String playerprefix = "§2§ll§r§a BuildingBrawl§2>> §r§7";
	public static int secondsToGrade = 30;
	public static int MAX_PLAYERS = 16;
	
	public static List<Material> forbiddenBlocks = new ArrayList<>();

	public Plugin plugin;

	public List<Player> players = new ArrayList<>();
	public List<Player> spectators = new ArrayList<>();
	
	public Plot[] plotArray = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY; //default gamestate
	public Inventory votingInventory; //voting inventory preset
	public int[] votes; //theme votes
	public List<String> themes;
	public Map<OfflinePlayer, Score> buildingScoreboard = new HashMap<>();
	public Map<Player, VotingInventory> gradingInventories = new HashMap<>();
	
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

	public Location lobbyLocation;
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


	public Game(Plugin plugin)
	{
		lobbyLocation = new Location(Bukkit.getWorlds().get(1), 1, 1, 1);
		Bukkit.getServer().getWorld(locationCfg.getString("locations.lobby.world")).setAnimalSpawnLimit(100);
		Bukkit.getServer().getWorld(locationCfg.getString("locations.lobby.world")).setMonsterSpawnLimit(100);
		loadLocations();
		setConfigDefaults();
		loadPlots();
		loadBuildThemes();
		loadForbiddenBlocks();
		this.plugin = plugin;
		
		random = new Random();
		
//		forbiddenBlocks.add(Material.WOOD_BUTTON);
//		forbiddenBlocks.add(Material.STONE_BUTTON);
//		forbiddenBlocks.add(Material.CHEST);
//		forbiddenBlocks.add(Material.ENDER_CHEST);
//		forbiddenBlocks.add(Material.SIGN_POST);
//		forbiddenBlocks.add(Material.COBBLE_WALL);
//		forbiddenBlocks.add(Material.WOOD_BUTTON);
//		forbiddenBlocks.add(Material.ANVIL);
//		forbiddenBlocks.add(Material.FENCE);
//		forbiddenBlocks.add(Material.FENCE_GATE);
//		forbiddenBlocks.add(Material.STAINED_GLASS_PANE);
//		forbiddenBlocks.add(Material.THIN_GLASS);
//		forbiddenBlocks.add(Material.ENDER_PORTAL_FRAME);
//		forbiddenBlocks.add(Material.SNOW);
//		forbiddenBlocks.add(Material.IRON_FENCE);
//		forbiddenBlocks.add(Material.CARPET);
//		forbiddenBlocks.add(Material.CAKE_BLOCK);
//		forbiddenBlocks.add(Material.SKULL);
//		forbiddenBlocks.add(Material.FLOWER_POT);
//		forbiddenBlocks.add(Material.STANDING_BANNER);
//		forbiddenBlocks.add(Material.BREWING_STAND);
//		forbiddenBlocks.add(Material.IRON_TRAPDOOR);
//		forbiddenBlocks.add(Material.WOODEN_DOOR);
//		forbiddenBlocks.add(Material.TRAP_DOOR);
//		forbiddenBlocks.add(Material.TRAPPED_CHEST);
//		forbiddenBlocks.add(Material.REDSTONE_WIRE);
//		forbiddenBlocks.add(Material.IRON_PLATE);
//		forbiddenBlocks.add(Material.GOLD_PLATE);
//		forbiddenBlocks.add(Material.REDSTONE_TORCH_ON);
//		forbiddenBlocks.add(Material.TORCH);
//		forbiddenBlocks.add(Material.DAYLIGHT_DETECTOR);
//		forbiddenBlocks.add(Material.IRON_DOOR_BLOCK);
//		forbiddenBlocks.add(Material.REDSTONE_COMPARATOR_OFF);
//		forbiddenBlocks.add(Material.LEVER);
//		forbiddenBlocks.add(Material.RAILS);
//		forbiddenBlocks.add(Material.ACTIVATOR_RAIL);
//		forbiddenBlocks.add(Material.DETECTOR_RAIL);
//		forbiddenBlocks.add(Material.POWERED_RAIL);
//		forbiddenBlocks.add(Material.WEB);
//		forbiddenBlocks.add(Material.DRAGON_EGG);
//		forbiddenBlocks.add(Material.TNT);
//		forbiddenBlocks.add(Material.DIODE_BLOCK_OFF);
//		forbiddenBlocks.add(Material.JUNGLE_DOOR);
//		forbiddenBlocks.add(Material.BIRCH_DOOR);
//		forbiddenBlocks.add(Material.ACACIA_DOOR);
//		forbiddenBlocks.add(Material.SPRUCE_DOOR);
//		forbiddenBlocks.add(Material.DARK_OAK_DOOR);
//		forbiddenBlocks.add(Material.WOODEN_DOOR);
	}
	
	public void loadForbiddenBlocks()
	{
		forbiddenBlocks = (List<Material>) forbiddenBlocksCfg.getList("forbiddenBlocks");
	}
	
	
	public void start(Player p) {
		if ((players.size() >= 1) && players.size() <= 16) {

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
			p.setLevel(voteTimer);
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

		Score score2 = bgObjective.getScore("§6Thema:");
		score2.setScore(7);
		Score score3 = bgObjective.getScore("§7" + finalTheme);
		score3.setScore(6);
		Score score4 = bgObjective.getScore(" ");
		score4.setScore(5);
		Score score5 = bgObjective.getScore("§6Zeit:");
		score5.setScore(4);
		timeScore = bgObjective.getScore("");
		timeScore.setScore(3);
		

		
		for (int i = 0; i < players.size(); i++) {
			Player p = players.get(i);
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
		if(spectators.contains(p))
		{
			p.sendMessage(playerprefix + "Unbekannter Fehler");
		}
		else
		{
			spectators.add(p);
			p.sendMessage(playerprefix + msg);
			p.teleport(lobbyLocation);
			p.setGameMode(GameMode.ADVENTURE);
			p.setAllowFlight(true);
			p.setFlying(true);
			p.setFoodLevel(20);
			for(Player z : players)
			{
				z.hidePlayer(p);
			}
		}	
	}

	
	public void removeSpectator(Player p)
	{
		if(spectators.contains(p))
		{
			p.teleport(lobbyLocation);
			spectators.remove(p);
			p.setFlying(false);
			p.setAllowFlight(false);
			for(Player z : players)
			{
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
		if(players.contains(p))
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
		else
		{
			if(!(gamestate == GameState.LOBBY))
			{
				addSpectator(p, "Da das Spiel bereits laeuft wurdest du den Zuschauern hinzugefuegt");
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
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).getName() == p.getName()) //check which index in the arraylist the player is
				{
					if(!(gamestate == GameState.LOBBY))
					{
						plotArray[i].ownerLeft = true; //if the player leaves the plot will know that he has left and will not be graded	
					}
				}
			}
			//players.remove(p); //TODO: make this good
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
		int themeCounter = 9;
		random = new Random();
		themes = (List<String>) themesCfg.getList("themes");
		votes = new int[themes.size()];
		votingInventory = Bukkit.createInventory(null, 9, "§6§lThemen");
		
		if(themes.size()>9)
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

	private void cancelScheduler(int id)
	{
		Bukkit.getScheduler().cancelTask(id);
	}

	private void updateScoreboard()
	{
		scoreboard.resetScores(currentBuildingtime);
		buildingTime--;
		for(Player p : players)
		{
			if(buildingTime<=10) p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.0f);
		}
		if(buildingTime <= 0)
		{
			
			gamestate = gamestate.GRADING;
			startGradingProcess();
		}
		else{
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
		//TODO: check if there are any players  left
		for(Player p : players){
			p.sendTitle("§6§lPlots bewerten", "§7Bewerte die Bauwerke mit der Prismarin-Scherbe");
		}
		gradePlot(0);
		 
	}
	
	private void gradePlot(int i)
	{
		int id = i;
		if(plotArray[id+1].ownerLeft)
		{
			gradePlot(id+1);
		}
		
		currentPlotInGradingProcess = id;
		scoreboardSecondsToGrade = secondsToGrade;
		gradeTimer = 0;

		for(Player p : players)
		{
			p.teleport(plotArray[id].getSpawnLocation());
			gradingInventories.put(p, new VotingInventory());
			gradingInventories.get(p).resetInventory();
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
		for(Player p : players)
		{
			for(PotionEffect e : p.getActivePotionEffects())
			{
				p.removePotionEffect(e.getType());
			}
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			p.getInventory().clear();
			if(plotArray[id].getOwner().getPlayer().getName().equals(p.getName()))
			{
				//pfusch
            }
            else 
            {
            	p.getInventory().setItem(4, is);
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
					for(Player p : players)
					{
					p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
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
				for(Player p : players){
					p.sendTitle("§6§l" + plotArray[id].getOwner().getName(), "§7hat dieses Bauwerk errichtet");
					p.getInventory().setItem(4, null);
					p.closeInventory();
				}
				
				//save rating
				for(Player p : players)
				{
					plotArray[id].addGradeCreativity(convertGrade(gradingInventories.get(p).voteBuffer[0]));
					plotArray[id].addGradeLook(convertGrade(gradingInventories.get(p).voteBuffer[1]));
					plotArray[id].addGradeFitting(convertGrade(gradingInventories.get(p).voteBuffer[2]));
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
		
		for(Player p : players)
		{
			p.sendTitle("§6" + plotArray[maxindex].getOwner().getName(), "§7hat das Spiel gewonnen (§6" + maxvalue + "§7)! Glueckwunsch!");
			p.teleport(plotArray[maxindex].spawnLocation);
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
			if(p.getOwner()!=null && !p.ownerLeft)
			{
			Score score = bgObjective.getScore("§7"+p.getOwner().getName());
			score.setScore(p.getFinalTotalGrade());
			}
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(launchFirework){
				launchFirework();
				}
				
			}
		}, 0, 15);
	
	
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				for(Player p : players)
				{
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
		
		Player p = plotArray[maxindex].getOwner();
		 //Spawn the Firework, get the FireworkMeta.
        Firework fw = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
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
	
	public void openTeleportInventory(Player p)
	{
		Inventory inv = Bukkit.createInventory(null, 18, "§6Spieler:");
		for(Player z : players)
		{
			ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
			SkullMeta sm = (SkullMeta) is.getItemMeta();
			sm.setOwner(z.getDisplayName());
			sm.setDisplayName("§6" + p.getDisplayName());
			is.setItemMeta(sm);
			inv.addItem(is);
		}
		p.openInventory(inv);
	}
	
	

	

}
