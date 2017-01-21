package game;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingGame§1§l>> §r§7";
	
	private List<Player> players = new ArrayList<>();
	
	
	public Game()
	{
		
	}
	
	
	public void start()
	{
		
	}
	
	public void addPlayer(Player p)
	{
		if(players.contains(p))
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
		else
		{
			players.add(p);
			p.sendMessage(prefix + "Du bist dem Spiel beigetreten");
		}
	}
	
	public void removePlayer(Player p)
	{
		if(players.contains(p))
		{
			players.add(p);
		}
		else
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
	}
	
	

}
