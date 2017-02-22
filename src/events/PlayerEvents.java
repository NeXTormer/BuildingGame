package events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
				if(game.spectators.contains(player)) return;
				Location blocklocation = e.getPlayer().getLocation();
				if(blocklocation.getBlockY()>50)
				{
//				if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
//				{
//	
//				}
				Location targetLocation = player.getLocation();
				targetLocation.setY(targetLocation.getY() - 3);
				player.teleport(targetLocation);
				player.sendMessage(game.playerprefix+"Du hast die maximale Hoehe erreicht");
				}
	
			}
    	}
    }
    
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e)
    {
    	if(e.getEntityType() == EntityType.PLAYER)
    	{
    		Player p = (Player) e.getEntity();
    		if(game.spectators.contains(p))
    		{
    			p.setFireTicks(0);
    			e.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void onPayerCombust(EntityCombustEvent e)
    {
    	if(e.getEntityType() == EntityType.PLAYER)
    	{
    		Player p = (Player) e.getEntity();
    		if(game.spectators.contains(p))
    		{
    			e.setCancelled(true);
    			p.setFireTicks(0);
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
			//pfusch
		}
		else
		{
			e.setCancelled(true);
		}
    }

}
