package events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import game.Game;
import game.GameState;

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
		if(game.gamestate == GameState.GRADING)
		{
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(e.getMaterial().equals(Material.PRISMARINE_SHARD))
				{
					Player p = e.getPlayer();
					p.closeInventory();
					p.openInventory(game.gradingInventories.get(p));
				}
			}
		}
	}
}