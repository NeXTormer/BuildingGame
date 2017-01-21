package events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import game.Game;

public class PlayerQuit implements Listener {
	
	
	private Game game;
	
	public PlayerQuit(Game game)
	{
		this.game = game;
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        game.removePlayer(e.getPlayer());
    }

	
	

}
