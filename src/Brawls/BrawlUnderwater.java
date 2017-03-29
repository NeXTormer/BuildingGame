package brawls;

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
import game.Plot;

public class BrawlUnderwater extends PlotBrawl {

	private Game game;
	private Plot victimPlot;
	private int duration;
	private Location replaceLoc, edgeMin, edgeMax;
	private World world;
	private int x, y, z;
	
	
	public BrawlUnderwater(Plot victimPlot, Game game)
	{
		super();
		this.game = game;
		this.victimPlot = victimPlot;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationUnderwater");
		victimPlot.getOwner().getPlayer().sendMessage(game.playerprefix+"Du wurdest von einem §l§6Underwater-Brawl§r§7 getroffen!");
		replaceLoc = victimPlot.getSpawnLocation();
		replaceLoc.setY(1);
		replaceLoc.setX(replaceLoc.getX());
		replaceLoc.setZ(replaceLoc.getZ());
		world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
		edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
		edgeMax = new Location(world, replaceLoc.getX()-34, replaceLoc.getY()+50, replaceLoc.getZ()+34);   
		for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --)
		{
			for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++)
			{
				for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++)
				{
					Location currentLocation = new Location(world, x, y, z);
					if(currentLocation.getBlock().getType().equals(Material.STATIONARY_WATER))
					{
						currentLocation.getBlock().setType(Material.BARRIER);
					}
					if(currentLocation.getBlock().getType().equals(Material.LAVA) || currentLocation.getBlock().getType().equals(Material.STATIONARY_LAVA))
					{
						currentLocation.getBlock().setType(Material.MOB_SPAWNER);
					}
					if(currentLocation.getBlock().getType().equals(Material.AIR))
					{
						currentLocation.getBlock().setType(Material.WATER);
				    }
				 }
			}
		}
			    
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
					
		@Override
		public void run() {
			for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --)
			{
				for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++)
				{
					for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++)
					{
						Location currentLocation = new Location(world, x, y, z);
						if(currentLocation.getBlock().getType().equals(Material.WATER) || currentLocation.getBlock().getType().equals(Material.STATIONARY_WATER))
						{
							currentLocation.getBlock().setType(Material.AIR);
						}
					 }
				}
			}			
			}
		}, 20 * duration);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
		@Override
		public void run() {
			for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --)
			{
				for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++)
				{
					for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++)
					{
						Location currentLocation = new Location(world, x, y, z);
						if(currentLocation.getBlock().getType().equals(Material.MOB_SPAWNER))
						{
							currentLocation.getBlock().setType(Material.LAVA);
					    }
						if(currentLocation.getBlock().getType().equals(Material.BARRIER))
						{
							currentLocation.getBlock().setType(Material.WATER);
						}
					 }
				}
			}			
			}
		}, 20 * duration+2);
	}
	
	@Override
	public void stop()
	{
		for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --)
		{
			for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++)
			{
				for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++)
				{
					Location currentLocation = new Location(world, x, y, z);
					if(currentLocation.getBlock().getType().equals(Material.WATER) || currentLocation.getBlock().getType().equals(Material.STATIONARY_WATER))
					{
						currentLocation.getBlock().setType(Material.AIR);
					}
				 }
			}
		}
		for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --)
		{
			for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++)
			{
				for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++)
				{
					Location currentLocation = new Location(world, x, y, z);
					if(currentLocation.getBlock().getType().equals(Material.MOB_SPAWNER))
					{
						currentLocation.getBlock().setType(Material.LAVA);
				    }
					if(currentLocation.getBlock().getType().equals(Material.BARRIER))
					{
						currentLocation.getBlock().setType(Material.WATER);
					}
				 }
			}
		}		
		
	}
}
