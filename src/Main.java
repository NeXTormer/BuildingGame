import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import commands.BGCommand;
import events.BlockEvents;
import events.EnvironmentalEvents;
import events.InventoryEvents;
import events.ItemEvents;
import events.PlayerEvents;
import events.PlayerJoin;
import events.PlayerQuit;
import game.Game;
import utils.DeleteWorld;

public class Main extends JavaPlugin {

	public PluginManager pluginmanager = Bukkit.getServer().getPluginManager();
	public Game game;
	
	PlayerJoin playerjoinevent;
	PlayerQuit playerquitevent;
	BlockEvents blockevents;
	InventoryEvents chestevents;
	EnvironmentalEvents environmentalevents;
	ItemEvents itemevents;
	PlayerEvents playerevents;
	
	
	
	
	BGCommand bgcommand;
	
	public void onEnable()
	{
		game = new Game(this);
		playerjoinevent = new PlayerJoin(game);
		playerquitevent = new PlayerQuit(game);
		blockevents = new BlockEvents(game);
		chestevents = new InventoryEvents(game);
		environmentalevents = new EnvironmentalEvents(game);
		itemevents = new ItemEvents(game);
		playerevents = new PlayerEvents(game);

		bgcommand = new BGCommand(game);
		
		pluginmanager.registerEvents(playerjoinevent, this);
		pluginmanager.registerEvents(playerquitevent, this);
		pluginmanager.registerEvents(blockevents, this);
		pluginmanager.registerEvents(chestevents, this);

		pluginmanager.registerEvents(environmentalevents, this);
		pluginmanager.registerEvents(itemevents, this);
		pluginmanager.registerEvents(playerevents, this);
		

		getCommand("bg").setExecutor(bgcommand);
		for(World w : Bukkit.getWorlds())
		{
			w.setAutoSave(false);
		}
	}
	
	public void onDisable()
	{
		String worldName = (String) game.locationCfg.get("locations.lobby.world");
		DeleteWorld.deleteWorld(worldName);
		try {
			DeleteWorld.copyFolder(new File("backupWorld"), new File(worldName));
			File io = new File(worldName+"/uid.dat");
			io.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("[BuildingGame] Disabled");
	}
	
	
}