package game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;

public class Plot {

	public Location originLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);
	public Location spawnLocation = new Location(Bukkit.getWorld("BuildingGame"), 1, 1, 1);
	

	public Player p;
	
	private int finalGradeCreativity;
	private int finalGradeLook;
	private int finalGradeFitting;
	
	private int finalTotalGrade;
	
	public boolean ownerLeft = false;
	
	private ArrayList<Integer> gradesCreativity = new ArrayList<>();
	private ArrayList<Integer> gradesLook= new ArrayList<>();
	private ArrayList<Integer> gradesFitting = new ArrayList<>();
	

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

	public void addGradeCreativity(int g)
	{
		gradesCreativity.add(g);
	}
	
	public void addGradeFitting(int g)
	{
		gradesFitting.add(g);
	}
	
	public void addGradeLook(int g)
	{
		gradesLook.add(g);
	}
	
	public void calculateFinalGrade()
	{
		finalGradeCreativity = 0;
		finalGradeLook = 0;
		finalGradeFitting = 0;
		
		for(int i : gradesCreativity)
		{
				finalGradeCreativity += i;
		}
		
		for(int i : gradesLook)
		{
				finalGradeLook += i;
		}
		
		for(int i : gradesFitting)
		{
				finalGradeFitting += i;
		}	
		
		finalTotalGrade = finalGradeCreativity + finalGradeFitting + finalGradeLook;
	}
	
	public int getFinalCreativityGrade()
	{
		return finalGradeCreativity;
	}
	
	public int getFinalFittingGrade()
	{
		return finalGradeFitting;
	}
	
	public int getFinalLookGrade()
	{
		return finalGradeLook;
	}
	
	public int getFinalTotalGrade()
	{
		return finalTotalGrade;
	}
	
	
	
	
	
}
