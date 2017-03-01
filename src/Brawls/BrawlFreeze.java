package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import game.Game;

public class BrawlFreeze extends PlayerBrawl {

	private Game game;
	private Player starter;
	
	
	public BrawlFreeze(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				Player p = Bukkit.getPlayer(uuid);
				p.playSound(p.getLocation(), Sound.STEP_SNOW, 1, 1);
				
				Location replaceLoc = p.getLocation();
				replaceLoc.setY(p.getLocation().getY()-1);
				replaceLoc.setX(p.getLocation().getX()-2);
				replaceLoc.setZ(p.getLocation().getZ()-2);
			    World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
			    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
			    Location edgeMax = new Location(world, replaceLoc.getX()+6, replaceLoc.getY()+4, replaceLoc.getZ()+4);
			   
			    for (int x = edgeMin.getBlockX(); x < edgeMax.getBlockX(); x ++) {
			        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
			            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
			            	Location currentLocation = new Location(world, x, y, z);
			            	Location plotLocation = currentLocation;
			            	plotLocation.setY(1);
			            	if(p.getWorld().getBlockAt(currentLocation).getType() == Material.AIR && p.getWorld().getBlockAt(plotLocation).getType() == Material.BEDROCK)
			            	{
			            		currentLocation.getBlock().setTypeId(79);
			            	}
			            }
			        }
			    } 
			}
		}
	}
}
