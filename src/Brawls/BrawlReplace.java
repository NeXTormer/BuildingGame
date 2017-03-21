package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import events.BlockEvents;
import game.Game;

public class BrawlReplace extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlReplace(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationReplace");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() {
				BlockEvents.brawlReplace = false;
				victim.playSound(victim.getLocation(), Sound.BLAZE_DEATH, 1, 1);
				BlockEvents.victimsReplace.remove(victim);
			}
		}, 20 * duration);

		BlockEvents.victimsReplace.add(victim);
		BlockEvents.brawlReplace = true;
		victim.playSound(victim.getLocation(), Sound.CHICKEN_HURT, 1, 1);
		
	}
}
