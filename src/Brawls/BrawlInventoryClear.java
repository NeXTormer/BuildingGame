package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import game.Game;

public class BrawlInventoryClear extends PlayerBrawl {

	private Game game;
	private Player starter;
	
	
	public BrawlInventoryClear(Player starter, Game game)
	{
		super();
		game = game;
		starter = starter;
	}
	
	@Override
	public void start()
	{	
		for(UUID uuid : game.players)
		{
			if(!(starter.getUniqueId() == uuid))
			{
				Player p = Bukkit.getPlayer(uuid);
				p.getInventory().clear();
				p.getEquipment().clear();
				p.playSound(p.getLocation(), Sound.GHAST_SCREAM, 1, 1);
				p.playEffect(p.getLocation(), Effect.MAGIC_CRIT, 1);
			}
			
		}
	}
}
