import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import commands.BGCommand;
import events.BlockEvents;
import events.PlayerJoin;
import events.PlayerQuit;
import game.Game;

public class Main extends JavaPlugin {

	public PluginManager pluginmanager = Bukkit.getServer().getPluginManager();
	public Game game;
	
	PlayerJoin playerjoinevent;
	PlayerQuit playerquitevent;
	BlockEvents blockevents;
	
	BGCommand bgcommand;
	
	public void onEnable()
	{
		game = new Game();
		playerjoinevent = new PlayerJoin(game);
		playerquitevent = new PlayerQuit(game);
		blockevents = new BlockEvents(game);
		bgcommand = new BGCommand(game);
		
		pluginmanager.registerEvents(playerjoinevent, this);
		pluginmanager.registerEvents(playerquitevent, this);
		pluginmanager.registerEvents(blockevents, this);
		
		getCommand("bg").setExecutor(bgcommand);
		
	}
	
	public void onDisable()
	{
		
	}
	
	
}