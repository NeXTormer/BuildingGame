package events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
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
				player.sendMessage(game.playerprefix+"Du hast die maximale Hoehe erreicht");
				}
	
			}
    	}
    }
    
    @EventHandler
    public void onPlayerBucket(PlayerBucketEmptyEvent e)
    {
    	BlockFace bf = e.getBlockFace();
    	Block b = e.getBlockClicked().getRelative(bf);
    	
		Location blocklocation = b.getLocation();
		blocklocation.setY(1);
		if(b.getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
		{
			
		}
		else
		{
			e.setCancelled(true);
		}
    }

}
