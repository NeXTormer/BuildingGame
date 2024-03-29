package events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import game.Game;

public class EnvironmentalEvents implements Listener{
	
    private Game game;
    
    
    public EnvironmentalEvents(Game game)
    {
        this.game = game;
    }

    @EventHandler
    public void onPiston(BlockPistonExtendEvent e)
    {
    	if(!game.configCfg.getBoolean("enableRedstone"))
    	{
    		e.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e)
    {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent e)
    {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onRedstone(BlockRedstoneEvent e)
    {
    	if(!game.configCfg.getBoolean("enableRedstone"))
    	{
    		e.setNewCurrent(0);
    	}
    }
    
    @EventHandler
    public void onExplode(EntityExplodeEvent e)
    {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onSummon(EntitySpawnEvent e)
    {
    	if(!game.configCfg.getBoolean("enableMobs"))
    	{
        	e.setCancelled(true);	
    	}
    } 
    
    @EventHandler
    public void onPortalJoin(PlayerPortalEvent e)
    {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockFade(BlockFadeEvent e)
    {
    	e.setCancelled(true);
    }
    
    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e)
    {
    	Location spawnLocation = e.getLocation();
    	spawnLocation.setY(1);
    	if(e.getLocation().getWorld().getBlockAt(spawnLocation).getType() != Material.BEDROCK)
    	{
    		e.setCancelled(true);    		
    	}
    }
    
    @EventHandler
    public void onMobDeath(EntityDeathEvent e)
    {
    	e.getDrops().clear();
    	int exp = e.getDroppedExp();
    	exp = 0;
    }
    
}
