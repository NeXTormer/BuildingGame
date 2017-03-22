package events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import Brawls.Animation;
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
			if(game.getMetadataBoolean(p, "savingStructure"))
			{
				String name = game.getMetadataString(p, "savingStructureName");
				Structure s = new Structure(name);
				s.setStructure(e.getClickedBlock().getLocation());
				StructureParser.addStructure(s);
				game.setMetadata(p, "savingStructure", false);
				p.sendMessage(game.playerprefix + "Die Struktur wurde unter dem Namen §6\"" + name + "\" §7gespeichert");
				e.setCancelled(true);
				Bukkit.getScheduler().cancelTask(game.getMetadataInteger(p, "savingStructureScheduler"));
			}
		}
    	
    	if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
    	{
    		Block block = e.getClickedBlock();
    		if(block.getType().equals(Material.GOLD_BLOCK))
    		{
    			String structureName = "";
    			for(Structure s : game.structures)
    			{
    				if(s.compareTo(block.getLocation()))
    				{
    					structureName = s.name;
    					e.getPlayer().sendMessage(game.playerprefix + "Du hast das Brawl §6" + structureName + "§7 aktiviert");
    					s.setOrigin(e.getClickedBlock().getLocation());
    					game.startBrawl(structureName, e.getPlayer(), block.getLocation(), s);
       					break;
    				}
    			}
    			
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
    
    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e)
    {
		if(game.gamestate == GameState.BUILDING)
		{
	    	ItemStack nametag = new ItemStack(Material.NAME_TAG);
	    	ItemStack potion = new ItemStack(Material.POTION);
	    	ItemStack woodenhoe = new ItemStack(Material.WOOD_HOE);
	    	if(e.getRightClicked().getType() == EntityType.ARMOR_STAND)
	    	{
	    		ArmorStand ast = (ArmorStand) e.getRightClicked();
	    		if(e.getPlayer().getItemInHand().getType() == nametag.getType())
	    		{
	    			String name = e.getPlayer().getItemInHand().getItemMeta().getDisplayName();
	    			e.getRightClicked().setCustomName(name);
	    			e.getRightClicked().setCustomNameVisible(true);
	    		}
	    		
	    		if(e.getPlayer().getItemInHand().getType() == potion.getType())
	    		{
	    			ast.setCustomNameVisible(true);
	    			if(!ast.isVisible())
	    			{
	    				ast.setVisible(true);
	    				ast.setGravity(false);
	    			}
	    			else
	    			{
		    			ast.setVisible(false);
		    			ast.setGravity(true);
	    			}
	    		}
	    		
	    		if(e.getPlayer().getItemInHand().getType() == woodenhoe.getType())
	    		{
	    			ast.setArms(true);
	    			if(!ast.isSmall())
	    			{
	    				ast.setSmall(true);
	    			}
	    			else
	    			{
	    				ast.setSmall(false);
	    			}
	    		}
    	}
    	}
		else
		{
			e.setCancelled(true);
		}
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e)
    {
    	if(e.getDamager() instanceof Player && game.gamestate != GameState.BUILDING)
    	{
    		e.setCancelled(true);
    	}
    }

}
