package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlFly extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlFly(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationFly");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run()
			{
				victim.setAllowFlight(true);
				victim.playSound(victim.getLocation(), Sound.BAT_DEATH, 1, 1);
			}
		}, 20 * duration);
		
		victim.setAllowFlight(false);
		victim.playSound(victim.getLocation(), Sound.ZOMBIE_PIG_HURT, 1, 1);
	}
}
