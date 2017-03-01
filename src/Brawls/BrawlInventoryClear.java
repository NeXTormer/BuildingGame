package Brawls;

import java.util.UUID;

import org.bukkit.Bukkit;
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
	
	
	}
}
