package events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import game.Game;

public class ItemEvents implements Listener {
	
	private Game game;
	public ItemEvents(Game game)
	{
		this.game = game;
	}
	
	@EventHandler
	public void playerDropItemEvent(PlayerDropItemEvent e)
	{
		e.setCancelled(true);
	}
}
