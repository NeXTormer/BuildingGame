package game;

import org.bukkit.entity.Player;

public class GamePlayer {

	public Player player;

	public int plotNumber;

	public boolean hasVoted = false;

	public GamePlayer(Player p)
	{
		player = p;
	}


}
