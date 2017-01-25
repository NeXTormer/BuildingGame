package events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
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
    
    public void onVehicleEnter(VehicleEnterEvent e)
    {
    	e.setCancelled(true);
    }
    
    public void onRedstone(BlockRedstoneEvent e)
    {
    	e.setNewCurrent(0);
    }
    
    public void onExplode(BlockExplodeEvent e)
    {
    	e.setCancelled(true);
    }

}
