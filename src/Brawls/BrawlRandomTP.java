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
	private Player starter;
	private int duration;
	
	public BrawlRandomTP(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationRandomTP");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() {
				for(UUID uuid : game.players)
				{
					if(!(starter.getUniqueId() == uuid))
					{
						OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
						
						if(op.isOnline())
						{
							Player p = Bukkit.getPlayer(uuid); 
							ItemEvents.brawlRandomTP = false;
							p.playSound(p.getLocation(), Sound.ZOMBIE_PIG_ANGRY, 1, 1);
						}	
					}
				}
				
				
			}
		}, 20 * duration);
		
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId().equals(uuid)))
			{
				OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				
				if(op.isOnline())
				{
					Player p = Bukkit.getPlayer(uuid);
					ItemEvents.brawlStarter = starter;
					ItemEvents.brawlRandomTP = true;
					p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1, 1);
				}
			}
			
		}
		
	}
}
