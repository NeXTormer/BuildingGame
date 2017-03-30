package events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import game.Game;

public class PlayerConnectionEvents implements Listener 
{

	private Game game;
	
	public PlayerConnectionEvents(Game game)
	{
		this.game = game;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
    	game.addPlayer(e.getPlayer());
    }
    
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent e)
    {
        game.removePlayer(e.getPlayer());
        game.removeSpectator(e.getPlayer());
    }


	
}
