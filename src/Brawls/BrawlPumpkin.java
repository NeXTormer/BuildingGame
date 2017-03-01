package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import game.Game;

public class BrawlPumpkin extends PlayerBrawl {

	private Game game;
	private Player starter;
	
	
	public BrawlPumpkin(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				Player p = Bukkit.getPlayer(uuid);
				ItemStack pumpkin = new ItemStack(Material.PUMPKIN);
				p.getInventory().setItem(39, pumpkin);
				p.playSound(p.getLocation(), Sound.BAT_HURT, 1, 1);
			}
			
		}
	}
}
