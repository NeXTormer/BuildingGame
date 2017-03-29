package brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import game.Game;

public class BrawlSound extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	private int soundTimerTask = 0;
	private Random random;
	
	public BrawlSound(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationSound");
		victim.playSound(victim.getLocation(), Sound.VILLAGER_IDLE, 1, 1);
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Sound-Brawl§r§7 getroffen!");
					
		soundTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				victim.playSound(victim.getLocation(), randomSound(), 1, 1);
			}
		}, 0, 10);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				victim.playSound(victim.getLocation(), Sound.HORSE_DEATH, 1, 1);
				Bukkit.getScheduler().cancelTask(soundTimerTask);
			}
		}, 20 * duration);
	}
	
	@Override
	public void stop()
	{
		Bukkit.getScheduler().cancelTask(soundTimerTask);
	}
	
	private Sound randomSound()
	{
		random = new Random();
		int r = random.nextInt(8);
		switch(r)
		{
		case 0:
			return Sound.GHAST_SCREAM;
		case 1:
			return Sound.GHAST_SCREAM2;
		case 2:
			return Sound.PISTON_EXTEND;
		case 3:
			return Sound.PISTON_RETRACT;
		case 4:
			return Sound.ENDERDRAGON_DEATH;
		case 5:
			return Sound.WITHER_DEATH;
		case 6:
			return Sound.SPIDER_IDLE;
		case 7:
			return Sound.ENDERMAN_SCREAM;
		default:
			return Sound.HORSE_DEATH;
		}
	}
}
