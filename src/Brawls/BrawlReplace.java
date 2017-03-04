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
	private Player starter;
	private int duration;
	
	public BrawlReplace(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationReplace");
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
							BlockEvents.brawlReplace = false;
							p.playSound(p.getLocation(), Sound.BLAZE_DEATH, 1, 1);
						}						
					}
				}
				
				
			}
		}, 20 * duration);
		
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				
				if(op.isOnline())
				{
					Player p = Bukkit.getPlayer(uuid);
					BlockEvents.brawlStarter = p;
					BlockEvents.brawlReplace = true;
					p.playSound(p.getLocation(), Sound.CHICKEN_HURT, 1, 1);
				}
			}
			
		}
		
	}
}
