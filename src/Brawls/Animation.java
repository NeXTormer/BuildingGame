package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import game.Game;
import game.Plot;

public class Animation {
	
	private Plot souPlot;
	private Plot desPlot;
	private Game game;
	
	public Animation(Plot p1, Plot p2, Game game)
	{
		souPlot = p1;
		desPlot = p2;
		this.game = game;
	}
	
	public void start()
	{
		Location souPlotMiddle = souPlot.getSpawnLocation();
		souPlotMiddle.setX(souPlotMiddle.getX()-16);
		souPlotMiddle.setY(60);
		souPlotMiddle.setZ(souPlotMiddle.getZ()+16);
		
		Location desPlotMiddle = desPlot.getSpawnLocation();
		desPlotMiddle.setX(desPlotMiddle.getX()-15.5);
		desPlotMiddle.setY(60);
		desPlotMiddle.setZ(desPlotMiddle.getZ()+15.5);
		
		double distanceX = ((desPlotMiddle.getBlockX()-souPlotMiddle.getX())/2);
		double distanceZ = ((desPlotMiddle.getBlockZ()-souPlotMiddle.getZ())/2);
		double middleD = Math.sqrt(Math.pow(distanceX, 2)+Math.pow(distanceZ, 2));
		double fullDistanceX = (desPlotMiddle.getBlockX()-souPlotMiddle.getX());
		double fullDistanceZ = (desPlotMiddle.getBlockZ()-souPlotMiddle.getZ());
		double a = 80/(Math.pow(middleD, 2));
		double finalDistance = Math.sqrt(Math.pow(fullDistanceX, 2)+Math.pow(fullDistanceZ, 2));
		if(finalDistance<0)
		{
			finalDistance = finalDistance*(-1);
		}
		for(double i = 0; i < finalDistance; i++ )
		{
			World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
			double y = a*Math.pow(i, 2);
			double x = desPlotMiddle.getX()-i;
			double z = desPlotMiddle.getZ()+i;
			Location currentLoc = new Location(world, x, y, z);
			Player p = Bukkit.getPlayer(game.players.get(0));
			p.teleport(currentLoc);
			i+=0.1;
		}
		
	}
}
