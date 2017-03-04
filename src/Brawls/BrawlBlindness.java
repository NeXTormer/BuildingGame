package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlBlindness extends PlayerBrawl {

	private Game game;
	private Player starter;
	private int duration;
	
	public BrawlBlindness(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationBlindness");
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				if(op.isOnline())
				{
					Player p = Bukkit.getPlayer(uuid);
					p.playSound(p.getLocation(), Sound.CAT_MEOW, 1, 1);
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 1));
				}
			}
			
		}
		
	}
}
