package brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import game.Game;

public class BrawlInventoryClear extends PlayerBrawl {

	private Game game;
	private Player victim;
	
	
	public BrawlInventoryClear(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		victim.sendMessage(game.playerprefix+"Du wurdest von einem �l�6InventoryClear-Brawl�r�7 getroffen!");
		victim.getInventory().clear();
		victim.getEquipment().clear();
		victim.playSound(victim.getLocation(), Sound.GHAST_SCREAM, 1, 1);
		victim.playEffect(victim.getLocation(), Effect.MAGIC_CRIT, 1);
	}
	
	@Override
	public void stop()
	{
		
	}
}
