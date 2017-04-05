package brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;

import game.Game;
import game.GameState;
import game.Plot;
import net.minecraft.server.v1_8_R3.EntityLiving;

public class Animation {
	
	private Location sourceLocation;
	private Location destinationLocation;
	private Location currentLocation;
	private Location astLocation;
	private int startSceduler, flySceduler, prepareSceduler;
	private World world;
	private Player p;
	private Material material;
	private int i, prepCounter;
	private ArmorStand ast;
	private FallingBlock fallingBlock;
	private double x, y, z, dx, dz, aP, bP, aL, xS, yS, zS, xD, zD, yD, yOffset, startHeight, velocity, deltaz;
	private Game game;
	private Brawl brawl;
	
	public Animation(Location l1, Location targetorigin, Brawl brawl, Material material, Game game)
	{
		Location l2 = new Location(targetorigin.getWorld(), targetorigin.getX() - 17, targetorigin.getY(), targetorigin.getZ() + 17);
		sourceLocation = l1;
		destinationLocation = l2;
		this.brawl = brawl;
		this.material = material;
		this.game = game;
	}
	
	
	public void prepare()
	{
		world = sourceLocation.getWorld();
		Location temp = sourceLocation;
        astLocation = new Location(temp.getWorld(), temp.getX(), temp.getY(), temp.getZ());
        astLocation.setX(astLocation.getX() + 0.5);
        astLocation.setZ(astLocation.getZ() + 0.5);
        astLocation.setY(astLocation.getY() - 1);
		ast = world.spawn(astLocation, ArmorStand.class);
		fallingBlock = world.spawnFallingBlock(sourceLocation, material, (byte) 0);
		ast.setPassenger(fallingBlock);
		ast.setVisible(false);
		ast.setGravity(false);
		prepCounter = 0;
		astLocation.setY(astLocation.getY()+2);
		prepareSceduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run() {
				{
					prepCounter+=1;
					world.playEffect(astLocation, Effect.SPELL, 5);
					world.playEffect(astLocation, Effect.COLOURED_DUST, 1);
					world.playEffect(astLocation, Effect.FIREWORKS_SPARK, 1);
					world.playSound(astLocation, Sound.FIREWORK_TWINKLE, 1, 1);
					if(prepCounter>=50)
					{
						world.playSound(astLocation, Sound.FIREWORK_LAUNCH, 1, 1);
						world.playSound(astLocation, Sound.LEVEL_UP, 1, 1);
						start();
						game.cancelScheduler(prepareSceduler);						
					}
				}
			}
		}, 0, 2);
		

		
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
				//p.teleport(currentLocation);
				//ast.teleport(currentLocation);
				move(currentLocation);
				if(y>=71)
				{
					fly();
					game.cancelScheduler(startSceduler);
				}
			}
		}, 0, 1);

	}
	
	public void fly()
	{
		yOffset = 30;
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
				world.playEffect(currentLocation, Effect.FIREWORKS_SPARK, 1);
				move(currentLocation);
				if(i>=100)
				{
					ast.remove();
					fallingBlock.remove();
					if(game.gamestate == GameState.BUILDING )
					{
						brawl.start();	
						game.brawlList.add(brawl);						
					}
					game.cancelScheduler(flySceduler);
				}
			}
		}, 0, 1);
		
		
	}
	
	private void move(Location loc)
	{
		ast.eject();
		ast.teleport(loc);
		ast.setPassenger(fallingBlock);
	}
	
	public void stop()
	{
		game.cancelScheduler(flySceduler);
		game.cancelScheduler(startSceduler);
		game.cancelScheduler(prepareSceduler);
		fallingBlock.remove();
		ast.remove();
	}
	
	

}
