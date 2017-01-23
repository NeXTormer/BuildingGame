package game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

public class Plot {

	public Location originLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);
	public Location spawnLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);
	

	public Player p;
	
	private int gradeCreativity, gradeLooking, gradeFitting;

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
		return p;
	}
	
	public void setOwner(Player player)
	{
		this.p = player;
	}

	public int getGradeCreativity() {
		return gradeCreativity;
	}

	public void setGradeCreativity(int gradeCreativity) {
		this.gradeCreativity = gradeCreativity;
	}

	public int getGradeLooking() {
		return gradeLooking;
	}

	public void setGradeLooking(int gradeLooking) {
		this.gradeLooking = gradeLooking;
	}

	public int getGradeFitting() {
		return gradeFitting;
	}

	public void setGradeFitting(int gradeFitting) {
		this.gradeFitting = gradeFitting;
	}
	
	
	
	
	
	
}
