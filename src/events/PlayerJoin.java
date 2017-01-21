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
    	game.addPlayer(e.getPlayer());
    }





}