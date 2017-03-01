package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlSpeed extends PlayerBrawl {

	private Game game;
	private Player starter;
	private int duration;
	
	public BrawlSpeed(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationSpeed");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() {
				for(UUID uuid : game.players)
				{
					if(!(starter.getUniqueId() == uuid))
					{
						Player p = Bukkit.getPlayer(uuid);
				    	p.setFlySpeed(0.1f);
				    	p.setWalkSpeed(0.2f);
				    	p.playSound(p.getLocation(), Sound.ENDERMAN_DEATH, 1, 1);
				    	p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 1, 1));
					}
				}
				
				
			}
		}, 20 * duration);
		
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				Player p = Bukkit.getPlayer(uuid);
				p.setFlySpeed(100000000);
				p.setWalkSpeed(100000000);
				p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
				p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 2, 1));
			}
			
		}
		
	}
}
