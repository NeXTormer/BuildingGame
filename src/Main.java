import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import commands.BGCommand;
import events.BlockEvents;
import events.InventoryEvents;
import events.PlayerJoin;
import events.PlayerQuit;
import game.Game;

public class Main extends JavaPlugin {

	public PluginManager pluginmanager = Bukkit.getServer().getPluginManager();
	public Game game;
	
	PlayerJoin playerjoinevent;
	PlayerQuit playerquitevent;
	BlockEvents blockevents;
	InventoryEvents chestevents;
	
	BGCommand bgcommand;
	
	public void onEnable()
	{
		game = new Game(this);
		playerjoinevent = new PlayerJoin(game);
		playerquitevent = new PlayerQuit(game);
		blockevents = new BlockEvents(game);
		chestevents = new InventoryEvents(game);

		bgcommand = new BGCommand(game);
		
		pluginmanager.registerEvents(playerjoinevent, this);
		pluginmanager.registerEvents(playerquitevent, this);
		pluginmanager.registerEvents(blockevents, this);
		pluginmanager.registerEvents(chestevents, this);

		getCommand("bg").setExecutor(bgcommand);
		
	}
	
	public void onDisable()
	{
		String worldName = (String) game.locationCfg.get("locations.lobby.world");
		//Bukkit.unloadWorld(worldName, false);
		File worldFile = new File(worldName);
		worldFile.delete();
		File srcDir = new File("backupWorld");
		File destDir = new File(worldName);
		try {
			FileUtils.copyDirectory(srcDir, destDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File uiDat = new
		File(worldName+"/uid.dat");
		uiDat.delete();
		System.out.println("[BuildingGame] Disabled");
	}
	
	
}