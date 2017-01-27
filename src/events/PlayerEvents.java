package events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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
    	Player player = e.getPlayer();
    	if(!game.globalBuildMode)
    	{
			if(game.gamestate == GameState.BUILDING || game.gamestate == GameState.GRADING)
			{
				Location blocklocation = e.getPlayer().getLocation();
				if(blocklocation.getBlockY()>50)
				{
//				if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
//				{
//	
//				}
				player.teleport(player.getLocation().add(e.getFrom().toVector().subtract(e.getTo().toVector()).normalize().multiply(2)));
				player.sendMessage(game.playerprefix+"Du hast die maximale Höhe erreicht");
				}
	
			}
    	}
    }

}
