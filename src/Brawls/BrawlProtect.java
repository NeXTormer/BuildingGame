package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import game.Game;
import game.Plot;

public class BrawlProtect extends PlotBrawl {

	private Game game;
	private Plot victimPlot;
	private int duration;
	private OfflinePlayer p;
	private Random random;
	private int x, y, z;
	private Location currentLoc;
	private World world;
	
	public BrawlProtect(Plot victimPlot, Game game)
	{
		super();
		this.game = game;
		this.victimPlot = victimPlot;
	}
	
	@Override
	public void start()
	{	
		victimPlot.setShield(3);
		Location temp = victimPlot.getSpawnLocation();
        currentLoc = new Location(temp.getWorld(), temp.getX(), temp.getY(), temp.getZ());
        currentLoc.setX(currentLoc.getX()+4);
        currentLoc.setY(58);
        currentLoc.setZ(currentLoc.getZ()-4);
     
		World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
		Location edgeMin = new Location(world, currentLoc.getX(), currentLoc.getY(), currentLoc.getZ());
		Location edgeMax = new Location(world, currentLoc.getX()-42, currentLoc.getY(), currentLoc.getZ()+42);
		for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x--) {
			for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z++) {
				Block b = new Location(world, x, 58, z).getBlock();
				if(b.getType().equals(Material.AIR))
				{
					b.setType(Material.STAINED_GLASS);						
				}
			 }
		}
		OfflinePlayer op = victimPlot.getOwner();
		Player p = op.getPlayer();
		p.sendMessage(game.playerprefix+"Du hast das §l§6Shield-Brawl§r§7 für dein Grundstück aktiviert!");
	}
}
