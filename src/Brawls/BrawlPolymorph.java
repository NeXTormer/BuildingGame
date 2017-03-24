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
	private Player victim;
	private int duration;
	
	public BrawlPolymorph(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationPolymorph");
		Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			
			@Override
			public void run() {
				BlockEvents.brawlPolymorph = false;
				victim.playSound(victim.getLocation(), Sound.SHEEP_WALK, 1, 1);
				BlockEvents.victimsPolymorph.remove(victim);
			}
		}, 20 * duration);
		
		BlockEvents.victimsPolymorph.add(victim);
		BlockEvents.brawlPolymorph = true;
		victim.playSound(victim.getLocation(), Sound.SHEEP_IDLE, 1, 1);
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Polymorph-Brawl§r§7 getroffen!");
	}
}
