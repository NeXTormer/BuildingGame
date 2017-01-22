package events;

import game.Game;
import game.GameState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockEvents implements Listener {
	
	private Game game;
	public BlockEvents(Game game)
	{
		this.game = game;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(game.gamestate == GameState.BUILDING)
		{
			Location blocklocation = e.getBlock().getLocation();
			blocklocation.setY(1);
			if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
			{
				if(e.getBlock().getType() == Material.SPONGE)
				{
					if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
				}
			}
			else
			{
				e.setCancelled(true);
			}

		}
		else
		{
			if(!game.globalBuildMode) e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if(game.gamestate == GameState.BUILDING)
		{
			Location blocklocation = e.getBlock().getLocation();
			blocklocation.setY(1);
			if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
			{
				if(e.getBlock().getType() == Material.SPONGE)
				{
					if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
				}
			}
			else
			{
				e.setCancelled(true);
			}

		}
		else
		{
			if(!game.globalBuildMode) e.setCancelled(true);
		}
		
		
	}
	
	

}
