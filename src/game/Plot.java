package game;

import java.awt.Rectangle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Plot {

	Location originLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);
	Location spawnLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);

	public Plot(double x, double y, double z)
	{
		originLocation.setX(x);
		originLocation.setY(y);
		originLocation.setZ(z);
	}
	
	public Plot(Location loc)
	{
		
	}
	
	public Plot()
	{
		//load from cfg
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(originLocation.getBlockX(), originLocation.getBlockZ(), 32, 32);
	}
	
	public void setSpawnLocation(Location loc)
	{
		spawnLocation = loc;
	}
	
	public Location getSpawnLocation()
	{
		return spawnLocation;
		
	}
	
	public Player getOwner()
	{
		return null;
	}
	
	
}