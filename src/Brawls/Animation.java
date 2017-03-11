package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import game.Game;
import game.Plot;

public class Animation {
	
	private Location sourceLocation;
	private Location destinationLocation;
	private Location currentLocation;
	private int startSceduler, flySceduler;
	private World world;
	private Player p;
	private int i;
	private double x, y, z, dx, dy, dz, aP, bP, aL, xS, yS, zS, xD, yD, zD, yOffset, startHeight, velocity, deltaz, deltay, deltax;
	private Game game;
	
	public Animation(Location l1, Location l2, Game game)
	{
		sourceLocation = l1;
		destinationLocation = l2;
		this.game = game;
	}
	
	public void prepare()
	{
		world = sourceLocation.getWorld();
		ArmorStand ast = (ArmorStand) world.spawnEntity(sourceLocation, EntityType.ARMOR_STAND);
		
		
	}
	
	public void start()
	{
		p = Bukkit.getPlayer(game.players.get(0));
		currentLocation = sourceLocation;
		y = sourceLocation.getY();
		startHeight = 71;
		velocity = 2.5;
		

		startSceduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run() {
				y+=velocity;
				currentLocation.setY(y); 
				p.teleport(currentLocation);
				//TODO: Teleportieren
				if(y>=71)
				{
					fly();
					System.out.println("stopppedStart");
					game.cancelScheduler(startSceduler);
				}
			}
		}, 0, 1);

	}
	
	public void fly()
	{
		yOffset = 50;
		xS = currentLocation.getX();
		zS = currentLocation.getZ();
		yS = currentLocation.getY();
		xD = destinationLocation.getX();
		yD = destinationLocation.getY();
		zD = destinationLocation.getZ();
		dz = zD-zS;
		dx = xD-xS;
		
		deltaz = dz/100;
		
		bP = yOffset/((dz/2)-(Math.pow(dz/2, 2)/dz));
		aP = -bP/dz;
		
		aL = dz/dx;
		
		z = 0;
		
		i = 0;
		
		flySceduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run() {
				i+=1;
				z+=deltaz;
				
				x = z/aL;
				
				y = aP*Math.pow(z, 2)+bP*z;
				
				currentLocation.setX(x+xS);
				currentLocation.setY(y+yS);
				currentLocation.setZ(z+zS);
				//TODO: Tp
				p.teleport(currentLocation);

				if(i>=100)
				{
					game.cancelScheduler(flySceduler);
				}
			}
		}, 0, 1);
		
		
	}
	
	private void move(Location loc)
	{
		
	}
	
	

}
