package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import game.Game;

public class BrawlJump extends PlayerBrawl {

	private Game game;
	private Player randomPlayer;
	private int duration;
	
	
	public BrawlJump(Player victim, Game game)
	{
		super();
		this.randomPlayer=victim;
		this.game = game;
	}
	
	@Override
	public void start()
	{
		Player p = randomPlayer;
		p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
		p.teleport(game.jumpLocation);
		p.setGameMode(GameMode.ADVENTURE);			
	}
}
