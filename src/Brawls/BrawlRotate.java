package brawls;

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

public class BrawlRotate extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	
	public BrawlRotate(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationRotate");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() 
			{
				if(ItemEvents.victimRotate.contains(victim))
				{
					ItemEvents.victimRotate.remove(victim);					
				}
				victim.playSound(victim.getLocation(), Sound.CHEST_OPEN, 1, 1);
			}
		}, 20 * duration);
		ItemEvents.victimRotate.add(victim);
		victim.playSound(victim.getLocation(), Sound.WOLF_HOWL, 1, 1);
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Rotate-Brawl§r§7 getroffen!");
		
	}
	
	@Override
	public void stop()
	{
		if(ItemEvents.victimRotate.contains(victim))
		{
			ItemEvents.victimRotate.remove(victim);					
		}
	}
}
