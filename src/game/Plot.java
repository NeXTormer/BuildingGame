package game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

public class Plot {

	public Location originLocation = new Location(Bukkit.getWorld(Game.worldname), 1, 1, 1);
	public Location spawnLocation = new Location(Bukkit.getWorld(Game.worldname), 1, 1, 1);
	

	public UUID owner;
	
	private int finalGradeCreativity;
	private int finalGradeLook;
	private int finalGradeFitting;
	
	public int finalTotalGrade;
	
	public boolean ownerLeft = false;
	
	private boolean shield = false;
	private int shieldHealth = 0;
	
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
	
	public OfflinePlayer getOwner()
	{
		
		if(owner == null) return null;
		OfflinePlayer op = Bukkit.getOfflinePlayer(owner);
//		if(op.isOnline())
//		{
			return op;	
//		}
//		else
//		{
//			return null;
//		}
	}
	
	public void setOwner(OfflinePlayer p)
	{
		this.owner = p.getUniqueId();
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
	
	public void setShielded(boolean b)
	{
		shield=b;
	}
	
	public boolean isShielded()
	{
		return shield;
	}
	
	public int getShield()
	{
		return shieldHealth;
	}
	
	public void setShield(int i)
	{
		shieldHealth=i;
	}
	
	public void damageShield()
	{
		shieldHealth-=1;
		Location temp =getSpawnLocation();
        Location currentLoc = new Location(temp.getWorld(), temp.getX(), temp.getY(), temp.getZ());
        currentLoc.setX(currentLoc.getX()+4);
        currentLoc.setY(58);
        currentLoc.setZ(currentLoc.getZ()-4);
     
		World world = Bukkit.getWorld("BuildingGame");
		Location edgeMin = new Location(world, currentLoc.getX(), currentLoc.getY(), currentLoc.getZ());
		Location edgeMax = new Location(world, currentLoc.getX()-42, currentLoc.getY(), currentLoc.getZ()+42);
		for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --) {
				for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
					Block b = new Location(world, x, 58, z).getBlock();
					if(b.getType().equals(Material.STAINED_GLASS))
					{
						if(shieldHealth<=0)
						{
							b.setType(Material.AIR);
						}
						else
						{
							boolean random = Math.random() < 0.5;
							if(random == true)
							{
								b.setType(Material.AIR);													
							}							
						}
					}
			     
			 }
		}
	}
}
