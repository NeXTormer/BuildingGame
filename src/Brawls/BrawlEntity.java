package brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;
import game.Plot;

public class BrawlEntity extends PlotBrawl {

	private Game game;
	private Plot victimPlot;
	private int duration;
	private OfflinePlayer p;
	private Random random;
	private int x, y, z;
	private Location spawnLocation;
	private World world;
	private int entityTimerTask = 0;
	
	public BrawlEntity(Plot victimPlot, Game game)
	{
		super();
		this.game = game;
		this.victimPlot = victimPlot;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationEntity");
		p = victimPlot.getOwner();
		p.getPlayer().sendMessage(game.playerprefix+"Du wurdest von einem �l�6Entity-Brawl�r�7 getroffen!");
		//p.playSound(p.getLocation(), Sound.VILLAGER_IDLE, 1, 1);
		

		entityTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
				random = new Random();
				x = (int) ((random.nextInt(32)*(-1))+victimPlot.getSpawnLocation().getX())-1;
				y = 4;
				z = (int) (random.nextInt(32)+victimPlot.getSpawnLocation().getZ())+1;
				spawnLocation = new Location(world, x, y, z);
				world.spawnEntity(spawnLocation, game.randomEntity());
			}
		}, 0, 10);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				Bukkit.getScheduler().cancelTask(entityTimerTask);
			}
		}, 20 * duration);
	}
	
	@Override
	public void stop()
	{
		Bukkit.getScheduler().cancelTask(entityTimerTask);
	}
}
