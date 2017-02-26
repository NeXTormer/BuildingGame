package events;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import game.Game;

public class PlayerJoin implements Listener
{
	private Game game;
	
	public PlayerJoin(Game game)
	{
		this.game = game;
	}
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e)
    {
    	e.getPlayer().setGameMode(GameMode.ADVENTURE);
    	e.getPlayer().getInventory().clear();
    	game.addPlayer(e.getPlayer());
    	e.getPlayer().setFlySpeed(0.1f);
    	e.getPlayer().setWalkSpeed(0.2f);
    	e.getPlayer().setSaturation(20);
    	e.getPlayer().setFoodLevel(20);
    	e.getPlayer().setLevel(0);
    	e.getPlayer().setHealth(20);
    	e.getPlayer().getInventory().clear();
    	e.getPlayer().getEquipment().clear();
    }





}