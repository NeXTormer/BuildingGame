package brawls;

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
	private Player victim;
	private int duration;
	
	public BrawlBlindness(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationBlindness");
		victim.playSound(victim.getLocation(), Sound.CAT_MEOW, 1, 1);
		victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1));
		victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 1));	
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Blindness-Brawl§r§7 getroffen!");
	}
	
	@Override
	public void stop()
	{
		for(PotionEffect e : victim.getActivePotionEffects())
		{
			victim.removePotionEffect(e.getType());
		}
	}
}
