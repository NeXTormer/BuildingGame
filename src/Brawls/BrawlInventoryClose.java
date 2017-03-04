package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlInventoryClose extends PlayerBrawl {

	private Game game;
	private Player starter;
	private int duration;
	
	public BrawlInventoryClose(Player starter, Game game)
	{
		super();
		this.game = game;
		this.starter = starter;
	}
	
	@Override
	public void start()
	{	
		duration = game.configCfg.getInt("brawlDurationInventoryClose");
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
				
				if(op.isOnline())
				{
					Player p = Bukkit.getPlayer(uuid);
					p.playSound(p.getLocation(), Sound.VILLAGER_IDLE, 1, 1);
				}
			}

		}
		
		final int votingTimerTask;
		votingTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
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
							p.closeInventory();
						}
					}

				}
			}
		}, 0, 10);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
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
							p.playSound(p.getLocation(), Sound.HORSE_DEATH, 1, 1);
						}
					}

				}
				Bukkit.getScheduler().cancelTask(votingTimerTask);
			}
		}, 20 * duration);
		
	}
}
