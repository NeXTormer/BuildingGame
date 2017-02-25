package events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;

import game.Game;
import game.GameState;

public class ItemEvents implements Listener {
	
	private Game game;
	public ItemEvents(Game game)
	{
		this.game = game;
	}
	
	@EventHandler
	public void playerDropItemEvent(PlayerDropItemEvent e)
	{
		if(!game.globalBuildMode) e.setCancelled(true);
		if(e.getPlayer().getInventory().getItemInHand().getType().equals(e.getItemDrop().getItemStack().getType()))
		{
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void playerInteractEvent(PlayerInteractEvent e)
	{
		if(!game.spectators.contains(e.getPlayer()))
		{
			if(game.gamestate == GameState.GRADING)
			{
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
				{
					if(e.getMaterial().equals(Material.PRISMARINE_SHARD))
					{
						Player p = e.getPlayer();
						p.closeInventory();
						p.openInventory(game.gradingInventories.get(p).inv);
					}
				}
			}
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(e.getMaterial().equals(Material.WOOD_HOE))
				{
					Block block = e.getClickedBlock();
					byte data = block.getData();
					Location blocklocation = block.getLocation();
					blocklocation.setY(1);
					if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK || e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE)
					{
						if(block.getType().equals(Material.LOG))
						{
							if(data==(byte)0 || data==(byte)4 || data==(byte)8)
							{
								block.setData((byte) 12);
							}
							if(data==(byte)1 || data==(byte)5 || data==(byte)9)
							{
								block.setData((byte) 13);
							}
							if(data==(byte)2 || data==(byte)6 || data==(byte)10)
							{
								block.setData((byte) 14);
							}
							if(data==(byte)3 || data==(byte)7 || data==(byte)11)
							{
								block.setData((byte) 15);
							}
						}
						if(block.getType().equals(Material.LOG_2))
						{
							if(data==(byte)0 || data==(byte)4 || data==(byte)8)
							{
								block.setData((byte) 12);
							}
							if(data==(byte)1 || data==(byte)5 || data==(byte)9)
							{
								block.setData((byte) 13);
							}
						}
						
						if(block.getType().equals(Material.REDSTONE_LAMP_OFF))
						{
							block.setType(Material.REDSTONE_LAMP_ON);
						}
						if(block.getType().equals(Material.REDSTONE_LAMP_ON))
						{
							block.setType(Material.REDSTONE_LAMP_OFF);
						}
						
						if(block.getType().equals(Material.IRON_TRAPDOOR) || block.getType().equals(Material.TRAP_DOOR))
						{
							if(data==(byte)0)
							{
								block.setData((byte) 4);
							}
							if(data==(byte)1)
							{
								block.setData((byte) 5);
							}
							if(data==(byte)2)
							{
								block.setData((byte) 6);
							}
							if(data==(byte)3)
							{
								block.setData((byte) 7);
							}
						}
					}
				}
			}
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(e.getMaterial().equals(Material.FIREWORK))
				{
					Player p = e.getPlayer();
					if(game.launchFirework==true)
					{
						p.sendMessage(game.playerprefix+"Das Feuerwerk wurde ausgestellt");
					}
					if(game.launchFirework==false)
					{
						p.sendMessage(game.playerprefix+"Das Feuerwerk wurde eingeschalten");
					}
					game.launchFirework = !game.launchFirework;
				}
			}
		}
		else
		{
			if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
			{
				if(e.getMaterial() == Material.COMPASS)
				{
					game.openTeleportInventory(e.getPlayer());
				}
			}
		}
	}
}
