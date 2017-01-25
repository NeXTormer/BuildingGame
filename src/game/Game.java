package game;

import static org.bukkit.Bukkit.getScheduler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import game.EndReason;
import game.GameState;
import game.PlayerData;
import game.Plot;
import game.VotingInventory;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingGame§1§l>> §r§7";
	public static String playerprefix = "§2§ll§r§a BuildingGame§2>> §r§7";
	public static int secondsToGrade = 15;
	public static int MAX_PLAYERS = 16;

	public Plugin plugin;

	public List<Player> players = new ArrayList<>();
	
	public Plot[] plotArray = new Plot[16];

	public Map<Player, PlayerData> playerdata = new HashMap<>();
	
	public boolean inProgress = false;
	public boolean globalBuildMode = false;
	public GameState gamestate = GameState.LOBBY; //default gamestate
	public Inventory votingInventory; //voting inventory preset
	public int[] votes; //theme votes
	public List<?> themes;
	public Map<OfflinePlayer, Score> buildingScoreboard = new HashMap<>();
	public Map<Player, VotingInventory> gradingInventories = new HashMap<>();

	public int gradingNameRevealTime = 5;
	public int buildingTime = 60 * 5;

	public File locationsFile = new File("plugins/BuildingGame", "locations.yml");
	public FileConfiguration locationCfg = YamlConfiguration.loadConfiguration(locationsFile);

	public File themesFile = new File("plugins/BuildingGame", "themes.yml");
	public FileConfiguration themesCfg = YamlConfiguration.loadConfiguration(themesFile);

	public Location lobbyLocation;
	public int max; //max number of votes in VOTING phase
	public int gradingCurrentPlotId; //current plot id which is in the grading progress (GameState.GRADING)

	public	int voteTimer = 10;
	private int gradeTimer = 0;
	private int buildingTimerScheduler;
	private Scoreboard scoreboard;
	private Score timeScore;
	private Objective bgObjective;
	private String currentBuildingtime = "";
	private String finalTheme;
	private int currentPlotInGradingProcess = 1000;


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
		bgObjective = scoreboard.registerNewObjective("BuildingGame", "dummy");
		bgObjective.setDisplayName("§9    - BuildingGame -    ");
		bgObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score2 = bgObjective.getScore("§6Thema:");
		score2.setScore(6);
		Score score3 = bgObjective.getScore("§7" + finalTheme);
		score3.setScore(5);
		Score score4 = bgObjective.getScore(" ");
		score4.setScore(4);
		Score score5 = bgObjective.getScore("§6Zeit:");
		score5.setScore(3);
		timeScore = bgObjective.getScore("");
		timeScore.setScore(2);
		

		
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
			for(int i = 0; i < players.size(); i++)
			{
				if(players.get(i).getName() == p.getName()) //check which index in the arraylist the player is
				{
					plotArray[i].ownerLeft = true; //if the player leaves the plot will know that he has left and will not be graded
				}
			}
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
				plotArray[z * 4 + x] = new Plot(new Location(origin.getWorld(), origin.getX() - 43 * x, origin.getY(), origin.getZ() + 43 * z));
				plotArray[z * 4 + x].setSpawnLocation(new Location(originSpawn.getWorld(), originSpawn.getX() - 43 * x, originSpawn.getY(), originSpawn.getZ() + 43 * z)); 
			}
		}
		
		
		
	}

	private void loadBuildThemes()
	{
		themes = themesCfg.getList("themes");
		votes = new int[themes.size()];
		votingInventory = Bukkit.createInventory(null, 36, "§6§lThemen");
		for(int i = 0; i < themes.size(); i++)
		{
			ItemStack is = new ItemStack(Material.PAPER);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName("§7" + themes.get(i));
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
			timeScore.setScore(2);
		}


	}
	
	private void startGradingProcess()
	{
		//TODO: check if there are any players  left
		gradePlot(0);
		 
	}
	
	private void gradePlot(int id)
	{
		
		currentPlotInGradingProcess = id;
		Iterator<Entry<Player, VotingInventory>> it = gradingInventories.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Player, VotingInventory> entry = it.next();
			entry.getValue().resetInventory();
		}
		
		scoreboard.resetScores("§70:00");
		scoreboard.resetScores(currentBuildingtime);
		scoreboard.resetScores("§6Zeit:");
		Score scoreErbauer = bgObjective.getScore("§6Erbauer:");
		scoreErbauer.setScore(3);
		
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
			p.getActivePotionEffects().clear();
			p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
			p.getInventory().clear();
			p.getInventory().setItem(4, is);
		}

		Score scoreK = bgObjective.getScore("§7§kPeterRendl");
		scoreK.setScore(2);
		
		int schedulerid = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if(timer() > secondsToGrade - 3)
				{
					//playsound
				}
				//update scoreboard grade time				
			}
		}, 0, 20);
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				Bukkit.getServer().getScheduler().cancelTask(schedulerid);
				
				//reveal name
				//remove prismarine shard
				
			}
		}, 20 * secondsToGrade);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			
			@Override
			public void run() {
				if((!plotArray[id + 1].ownerLeft))
				{
					if(id + 1 > players.size())
					{
						//there are no plots availible, change to GameState.END
						endGame(EndReason.NORMAL_END);
					}
				
					//resume with next plot
					gradePlot(id + 1);
				}
				else
				{
					endGame(EndReason.PLAYER_LEFT);
					//player left
				}
			}
		}, 20 * gradingNameRevealTime);
		
		
	}
	
	private void endGame(EndReason reason)
	{
		//TODO: Calculate Winner
	}
	
	
	
	
	
	
	/**
	 * add one to gradeTimer
	 * @return
	 */
	private int timer()
	{
		return gradeTimer++;
	}
	
	
	

	

}
