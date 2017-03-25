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
	private Player victim;
	
	
	public BrawlFreeze(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		if(victim.isOnline())
		{
			victim.playSound(victim.getLocation(), Sound.STEP_SNOW, 1, 1);
			victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Freeze-Brawl§r§7 getroffen!");
			Location replaceLoc = victim.getLocation();
			replaceLoc.setY(victim.getLocation().getY());
			replaceLoc.setX(victim.getLocation().getX()-2);
			replaceLoc.setZ(victim.getLocation().getZ()-2);
			World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
			Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
			Location edgeMax = new Location(world, replaceLoc.getX()+4, replaceLoc.getY()+4, replaceLoc.getZ()+4);
			for (int x = edgeMin.getBlockX(); x < edgeMax.getBlockX(); x ++) {
				for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
					for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
						Location currentLocation = new Location(world, x, y, z);
				        Location plotLocation = new Location(world, x, 1, z);
				        if(victim.getWorld().getBlockAt(currentLocation).getType() == Material.AIR && victim.getWorld().getBlockAt(plotLocation).getType() == Material.BEDROCK)
				        {
				        	victim.setFlySpeed(0.0f);
							victim.setWalkSpeed(0.0f);
							victim.setVelocity(new Vector(0, 0, 0));
				            currentLocation.getBlock().setTypeId(79);
				        }
				     }
				 }
			}
		}
			    
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
					
		@Override
		public void run() {
			if(victim.isOnline())
			{
				victim.setFlySpeed(0.1f);
				victim.setWalkSpeed(0.2f);
			}

			}
		}, 20);
	}
	
	@Override
	public void stop()
	{
		
	}
}
