package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import events.BlockEvents;
import events.ItemEvents;
import game.Game;

public class BrawlRandomTP extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlRandomTP(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationRandomTP");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() {
				if(ItemEvents.victimRandomTP.contains(victim))
				{
					ItemEvents.victimRandomTP.remove(victim);					
				}
				ItemEvents.brawlRandomTP = false;
				victim.playSound(victim.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);

			}
		}, 20 * duration);
		
		ItemEvents.victimRandomTP.add(victim);
		ItemEvents.brawlRandomTP = true;
		victim.playSound(victim.getLocation(), Sound.ANVIL_BREAK, 1, 1);
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6RandomTP-Brawl§r§7 getroffen!");
		
	}
	
	@Override
	public void stop()
	{
		if(ItemEvents.victimRandomTP.contains(victim))
		{
			ItemEvents.victimRandomTP.remove(victim);					
		}
		ItemEvents.brawlRandomTP = false;
	}
}
