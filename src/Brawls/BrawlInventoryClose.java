package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlInventoryClose extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlInventoryClose(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationInventoryClose");
		victim.playSound(victim.getLocation(), Sound.VILLAGER_IDLE, 1, 1);
					
		final int votingTimerTask;
		votingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				victim.closeInventory();
			}
		}, 0, 10);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				victim.playSound(victim.getLocation(), Sound.HORSE_DEATH, 1, 1);
				Bukkit.getScheduler().cancelTask(votingTimerTask);
			}
		}, 20 * duration);
		
	}
}
