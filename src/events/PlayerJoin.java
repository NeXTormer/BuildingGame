package events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import game.Game;

public class PlayerJoin implements Listener
{
	private Game game;
	
	public PlayerJoin(Game game)
	{
		this.game = game;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
    	//e.getPlayer().getInventory().clear();
    	game.addPlayer(e.getPlayer());
    	e.getPlayer().setFlySpeed(0.1f);
    	e.getPlayer().setWalkSpeed(0.2f);
    	e.getPlayer().setSaturation(10);
    }





}