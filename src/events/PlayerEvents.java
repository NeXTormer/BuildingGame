package events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import game.Game;
import game.GameState;
import structures.Structure;
import structures.StructureParser;

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
				if(game.spectators.contains(player.getUniqueId())) return;
				Location blocklocation = e.getPlayer().getLocation();
				int maxHeight = game.configCfg.getInt("maxHeight");
				if(blocklocation.getBlockY()>maxHeight)
				{
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
    		if(game.spectators.contains(p.getUniqueId()))
    		{
    			p.setFireTicks(0);
    			e.setCancelled(true);
    		}
    	}
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent e)
    {
    	if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Player p = e.getPlayer();
			Bukkit.broadcastMessage("peter: " + game.getMetadataBoolean(p, "savingStructure"));
			if(game.getMetadataBoolean(p, "savingStructure"))
			{
				String name = game.getMetadata(p, "savingStructureName").get(0).asString();
				Structure s = new Structure(name);
				StructureParser.addStructure(s);
				game.setMetadata(p, "savingStructure", false);
				p.sendMessage(game.playerprefix + "Die Struktur wurde unter dem Namen §6\"" + name + "\" §7gespeichert");
				e.setCancelled(true);
			}
		}
    	
    	if(e.getAction().equals(Action.PHYSICAL))
    	{
			Location blocklocation = e.getClickedBlock().getLocation();
			blocklocation.setY(1);
    		if(e.getClickedBlock().getType() == Material.GOLD_PLATE && e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.MELON_BLOCK)
    		{
    			e.getPlayer().setGameMode(GameMode.CREATIVE);
    			UUID playerUUID = e.getPlayer().getUniqueId();
    			for(int i = 0; i<game.plotArray.length; i++)
    			{
    				if(playerUUID.equals(game.plotArray[i].getOwner().getUniqueId()))
    				{
    					Bukkit.getPlayer(playerUUID).teleport(game.plotArray[i].getSpawnLocation());
    				}
    			}
    		}
    	}

    }
    
    @EventHandler
    public void onPayerCombust(EntityCombustEvent e)
    {
    	if(e.getEntityType() == EntityType.PLAYER)
    	{
    		Player p = (Player) e.getEntity();
    		if(game.spectators.contains(p.getUniqueId()))
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
