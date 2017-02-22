package events;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import game.Game;
import game.GameState;

public class BlockEvents implements Listener {
	
	private Game game;
	private Random random;
	private List<Integer> replaceBlocks = new ArrayList<>();
	private List<Byte> replaceDatas = new ArrayList<>();
	private int blockAmount = 1;
	
	public BlockEvents(Game game)
	{
		this.game = game;
		random = new Random();
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(!game.globalBuildMode)
		{
			if(game.gamestate == GameState.BUILDING)
			{
				Location blocklocation = e.getBlock().getLocation();
				blocklocation.setY(1);
				if(p.getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
				{
					if(e.getBlock().getType() == Material.BEDROCK)
					{
						if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
					}
				}
				else if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE && e.getBlock().getLocation().getBlockY()>4)
				{
					
				}
				else
				{
					e.setCancelled(true);
				}
	
			}
			else{
				e.setCancelled(true);
			}
		}

	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		if(!game.globalBuildMode)
		{
			if(game.gamestate == GameState.BUILDING)
			{
				Location blocklocation = e.getBlock().getLocation();
				blocklocation.setY(1);
				if(p.getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
				{
					if(e.getBlock().getType() == Material.BEDROCK)
					{
						if(e.getBlock().getLocation().getY() == 1) e.setCancelled(true);
					}
				}
				else if(p.getWorld().getBlockAt(blocklocation).getType() == Material.SPONGE && e.getBlock().getLocation().getBlockY()>4)
				{
					if(game.forbiddenBlocks.contains(e.getBlock().getType().toString()))
					{
						e.getPlayer().sendMessage(game.playerprefix+"Ungueltiger Block");
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CHICKEN_HURT, 1, 1);
					}
					else 
					{
						replaceBlocks.clear();
						replaceDatas.clear();
						blockAmount = 1;
						for(int i = 0; i<blockAmount;i++)
						{
							Location replaceMaterialLocation = e.getBlock().getLocation();
							replaceMaterialLocation.setY(blockAmount+4);
							if(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==101 || Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==102 || Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==30)
							{
								if(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==102)
								{
									replaceBlocks.add(9);
									replaceDatas.add((byte)0);
								}
								
								if(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==101)
								{
									replaceBlocks.add(11);
									replaceDatas.add((byte)0);
								}
								if(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId()==30)
								{
									replaceBlocks.add(0);
									replaceDatas.add((byte)0);
								}
							}
							else
							{
								replaceBlocks.add(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getTypeId());
								replaceDatas.add(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getData());
							}
							blockAmount++;
							replaceMaterialLocation.setY(blockAmount+4);
							if(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getType()==Material.AIR)
							{
								blockAmount--;
							}
							if(game.forbiddenBlocks.contains(Bukkit.getServer().getWorld("BuildingGame").getBlockAt(replaceMaterialLocation).getType().toString()))
							{
								e.getPlayer().sendMessage(game.playerprefix+"Ungueltiger Block");
								e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CHICKEN_HURT, 1, 1);
								break;
							}
						}
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.CREEPER_DEATH, 1, 1);
						if(e.getBlock().getType().equals(Material.WOOD_PLATE))
	            	{
	            		e.getPlayer().sendMessage(game.playerprefix+"Der Boden wurde zu WATER geändert");
	            	}
	            	else if(e.getBlock().getType().equals(Material.STONE_PLATE))
	            	{
	            		e.getPlayer().sendMessage(game.playerprefix+"Der Boden wurde zu LAVA geändert");
	            	}
	            	else
	            	{
	            		e.getPlayer().sendMessage(game.playerprefix+"Der Boden wurde zu "+e.getBlock().getType()+"§7 geändert");
	            	}
					
					Location replaceLoc = e.getBlock().getLocation();
					replaceLoc.setY(5);
					replaceLoc.setX(replaceLoc.getX()-2);
					replaceLoc.setZ(replaceLoc.getZ()+2);
					replaceLoc.setY(replaceLoc.getY()-3);
				    World world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
				    Location edgeMin = new Location(world, replaceLoc.getX(), replaceLoc.getY(), replaceLoc.getZ());
				    Location edgeMax = new Location(world, replaceLoc.getX()-32, replaceLoc.getY()+2, replaceLoc.getZ()+32);
				    
				    int blockID = e.getBlock().getTypeId();
				    byte meta = e.getBlock().getData();
				   
				    for (int x = edgeMin.getBlockX(); x > edgeMax.getBlockX(); x --) {
				        for (int y = edgeMin.getBlockY(); y < edgeMax.getBlockY(); y ++) {
				            for (int z = edgeMin.getBlockZ(); z < edgeMax.getBlockZ(); z ++) {
//				            	if(e.getBlock().getType().equals(Material.WOOD_PLATE))
//				            	{
//				            		new Location(world, x, y, z).getBlock().setType(Material.STATIONARY_WATER);
//				            	}
//				            	else if(e.getBlock().getType().equals(Material.STONE_PLATE))
//				            	{
//				            		new Location(world, x, y, z).getBlock().setType(Material.STATIONARY_LAVA);
//				            	}
//				            	else
//				            	{
				            		replaceMaterial(replaceBlocks, replaceDatas, world, x, y, z);
				            		//new Location(world, x, y, z).getBlock().setTypeIdAndData(blockID, meta, false);
				            		
//				            	}
				            }
				        }
					}
					}
				}
				else
				{
					e.setCancelled(true);
				}	
			}
			else
			{
				e.setCancelled(true);
			}
			if(e.getBlock().getType().equals(Material.MONSTER_EGG) || e.getBlock().getType().equals(Material.MONSTER_EGGS))
			{
				e.setCancelled(true);
			}

		}	
	}
	
	@EventHandler
	public void onWaterSpread(BlockFromToEvent e)
	{
		if(game.gamestate == GameState.BUILDING)
		{
			Location blocklocation = e.getBlock().getLocation();
			blocklocation.setY(1);
			if(e.getBlock().getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
			{
				
			}
			else
			{
				e.setCancelled(true);
			}
		}
	}
	
	public void replaceMaterial(List<Integer> materials, List<Byte> data, World world, int x, int y, int z)
	{
		int r = random.nextInt(materials.size());
		new Location(world, x, y, z).getBlock().setTypeIdAndData(materials.get(r), data.get(r), false);
		//return materials.get(r);
	}

}
