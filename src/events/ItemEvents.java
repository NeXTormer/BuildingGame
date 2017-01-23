package events;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
	
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if(e.getMaterial().equals(Material.PRISMARINE_SHARD))
			{
				
			}
		}
	}
}
