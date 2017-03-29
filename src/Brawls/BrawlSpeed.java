package brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlSpeed extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlSpeed(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationSpeed");
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Speed-Brawl§r§7 getroffen!");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run()
			{
				victim.setFlySpeed(0.1f);
				victim.setWalkSpeed(0.2f);
				victim.playSound(victim.getLocation(), Sound.ENDERMAN_DEATH, 1, 1);
				victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2, 1));	
			}
		}, 20 * duration);
		victim.setFlySpeed(1.0f);
		victim.setWalkSpeed(1.0f);
		victim.playSound(victim.getLocation(), Sound.LEVEL_UP, 1, 1);
		victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2, 1));
	}
	
	@Override
	public void stop()
	{
		victim.setFlySpeed(0.1f);
		victim.setWalkSpeed(0.2f);
	}
}
