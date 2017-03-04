package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import events.BlockEvents;
import game.Game;

public class BrawlPolymorph extends PlayerBrawl {

	private Game game;
	private Player starter;
	private int duration;
	
	public BrawlPolymorph(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationPolymorph");
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
							BlockEvents.brawlPolymorph = false;
							p.playSound(p.getLocation(), Sound.SHEEP_WALK, 1, 1);
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
					BlockEvents.brawlPolymorph = true;
					p.playSound(p.getLocation(), Sound.SHEEP_IDLE, 1, 1);
				}
			}
			
		}
		
	}
}
