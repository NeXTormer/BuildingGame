import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import events.PlayerJoin;
import events.PlayerQuit;
import game.Game;

public class Main extends JavaPlugin {

	public PluginManager pluginmanager = Bukkit.getServer().getPluginManager();
	public Game game;
	
	PlayerJoin playerjoinevent;
	PlayerQuit playerquitevent;
	
	public void onEnable()
	{
		game = new Game();
		playerjoinevent = new PlayerJoin(game);
		playerquitevent = new PlayerQuit(game);
		System.out.println("peter rendl");
		
		pluginmanager.registerEvents(playerjoinevent, this);
		pluginmanager.registerEvents(playerquitevent, this);
	}
	
	public void onDisable()
	{
		
	}
	
	
}