package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import game.Game;
import net.minecraft.server.v1_8_R3.Material;

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
				replaceLoc.setY(p.getLocation().getY()-3);
				replaceLoc.setX(p.getLocation().getX()-3);
				replaceLoc.setZ(p.getLocation().getZ()-3);
			    World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
			    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
			    Location edgeMax = new Location(world, replaceLoc.getX()+6, replaceLoc.getY()+6, replaceLoc.getZ()+6);
			   
			    for (int x = edgeMin.getBlockX(); x < edgeMax.getBlockX(); x ++) {
			        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
			            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
			            	Location currentLocation = new Location(world, x, y, z);
			            	if(currentLocation.getBlock().getType().equals(Material.AIR))
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
