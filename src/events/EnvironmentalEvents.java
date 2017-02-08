package events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
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
    	e.setNewCurrent(0);
    }
    
    @EventHandler
    public void onExplode(EntityExplodeEvent e)
    {
    	e.setCancelled(true);
    }
    
//    @EventHandler
//    public void onSummon(EntitySpawnEvent e)
//    {
//    	e.setCancelled(true);
//    } 
    
    @EventHandler
    public void onPortalJoin(PlayerPortalEvent e)
    {
    	e.setCancelled(true);
    }

}
