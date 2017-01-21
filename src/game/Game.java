package game;

import java.util.List;
import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Game {
	
	public static String prefix = "§1§ll§r§9 BuildingGame§1§l>> §r§7";
	
	private List<Player> players = new ArrayList<>();
	public boolean inProgress = true;
	
	public Game()
	{
		
	}
	
	
	public void start(Player p)
	{
		if((players.size() >= 2) && players.size() <=16)
		{
			inProgress = true;
		//voting	
		}
		else
		{
			p.sendMessage(prefix + "Ungueltige Spieleranzahl (2 - 16 Spieler)");
		}
	}
	
	public void addPlayer(Player p)
	{
		if(players.contains(p))
		{
			p.sendMessage(prefix + "Unbekannter Fehler");
		}
		else
		{
			if(inProgress)
			{
				p.kickPlayer(prefix + "Das Spiel laeuft bereits");
			}
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
