package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import game.Game;

public class BrawlPumpkin extends PlayerBrawl {

	private Game game;
	private Player victim;
	
	
	public BrawlPumpkin(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		ItemStack pumpkin = new ItemStack(Material.PUMPKIN);
		victim.getInventory().setItem(39, pumpkin);
		victim.playSound(victim.getLocation(), Sound.BAT_HURT, 1, 1);
		victim.sendMessage(game.playerprefix+"Du wurdest von einem §l§6Pumpkin-Brawl§r§7 getroffen!");

	}
	
	@Override
	public void stop()
	{
		
	}
}
