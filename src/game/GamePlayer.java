package game;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class GamePlayer extends PlayerEvent {

	public int plotNumber;
	
	
	public GamePlayer(Player who) {
		super(who);
		
		
	}

	@Override
	public HandlerList getHandlers() {
		return null;
	}

	
	
}
