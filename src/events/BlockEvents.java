package events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import game.Game;
import game.GameState;

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
		if(!game.globalBuildMode)
		{
			if(game.gamestate == GameState.BUILDING)
			{
				Location blocklocation = e.getBlock().getLocation();
				blocklocation.setY(1);
				if(p.getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
				{
					if(e.getBlock().getType() == Material.BEDROCK)
					{
						if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
					}
				}
				else if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE && e.getBlock().getLocation().getBlockY()==5)
				{
					
				}
				else
				{
					e.setCancelled(true);
				}
	
			}
			else{
				e.setCancelled(true);
			}
		}

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if(!game.globalBuildMode)
		{
			if(game.gamestate == GameState.BUILDING)
			{
				Location blocklocation = e.getBlock().getLocation();
				blocklocation.setY(1);
				if(p.getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
				{
					if(e.getBlock().getType() == Material.BEDROCK)
					{
						if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
					}
				}
				else if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE && e.getBlock().getLocation().getBlockY()==5)
				{
					if(game.forbiddenBlocks.contains(e.getBlock().getType()))
					{
						
					}
					else {
					Location replaceLoc = e.getBlock().getLocation();
					replaceLoc.setX(replaceLoc.getX()-2);
					replaceLoc.setZ(replaceLoc.getZ()+2);
					replaceLoc.setY(replaceLoc.getY()-3);
				    World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
				    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
				    Location edgeMax = new Location(world, replaceLoc.getX()-32, replaceLoc.getY()+2, replaceLoc.getZ()+32);
				   
				    for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --) {
				        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
				            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
				            	new Location(world, x, y, z).getBlock().setType(e.getBlock().getType());
				            }
				        }
					}
					}
				}
				else
				{
					e.setCancelled(true);
				}	
			}
			else
			{
				e.setCancelled(true);
			}
			if(e.getBlock().getType().equals(Material.MONSTER_EGG) || e.getBlock().getType().equals(Material.MONSTER_EGGS))
			{
				e.setCancelled(true);
			}

		}	
	}
	
	@EventHandler
	public void onWaterSpread(BlockFromToEvent e)
	{
		if(game.gamestate == GameState.BUILDING)
		{
			Location blocklocation = e.getBlock().getLocation();
			blocklocation.setY(1);
			if(e.getBlock().getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
			{
				
			}
			else
			{
				e.setCancelled(true);
			}
		}
	}

}
