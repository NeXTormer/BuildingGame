package events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import game.Game;
import game.GameState;

public class PlayerEvents implements Listener {
	
    private Game game;
    public PlayerEvents(Game game)
    {
        this.game = game;
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
		if(game.gamestate == GameState.BUILDING)
		{
			Location blocklocation = e.getPlayer().getLocation();
			blocklocation.setY(1);
			if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
			{

			}
			else
			{
				e.setCancelled(true);
			}

		}
		else
		{
			if(!game.globalBuildMode) e.setCancelled(true);
		}
    }

}
