package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
			OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				
				if(op.isOnline())
				{
					Player p = Bukkit.getPlayer(uuid);
					p.playSound(p.getLocation(), Sound.STEP_SNOW, 1, 1);
					
					Location replaceLoc = p.getLocation();
					replaceLoc.setY(p.getLocation().getY());
					replaceLoc.setX(p.getLocation().getX()-2);
					replaceLoc.setZ(p.getLocation().getZ()-2);
				    World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
				    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
				    Location edgeMax = new Location(world, replaceLoc.getX()+4, replaceLoc.getY()+4, replaceLoc.getZ()+4);
				   
				    for (int x = edgeMin.getBlockX(); x < edgeMax.getBlockX(); x ++) {
				        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
				            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
				            	Location currentLocation = new Location(world, x, y, z);
				            	Location plotLocation = new Location(world, x, 1, z);
				            	if(p.getWorld().getBlockAt(currentLocation).getType() == Material.AIR && p.getWorld().getBlockAt(plotLocation).getType() == Material.BEDROCK)
				            	{
							    	p.setFlySpeed(0.0f);
							    	p.setWalkSpeed(0.0f);
							    	p.setVelocity(new Vector(0, 0, 0));
				            		currentLocation.getBlock().setTypeId(79);
				            	}
				            }
				        }
				    }
				}
			    
				Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
					
					@Override
					public void run() {
						for(UUID uuid : game.players)
						{
							if(!(starter.getUniqueId() == uuid))
							{
								OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
								
								if(op.isOnline())
								{
									Player p = Bukkit.getPlayer(uuid);
							    	p.setFlySpeed(0.1f);
							    	p.setWalkSpeed(0.2f);
								}
							}
						}
						
						
					}
				}, 20);
			}
		}
	}
}
