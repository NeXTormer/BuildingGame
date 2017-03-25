package events;

import static org.bukkit.Bukkit.getScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import game.Game;
import game.GameState;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ItemEvents implements Listener {
	
	private Game game;
	public static boolean brawlRandomTP = false;
	public static boolean brawlRotate = false;
	public static Player brawlStarter;
	public static List<Player> victimRandomTP = new ArrayList<>();
	public static List<Player> victimRotate = new ArrayList<>();
	private Random random;
	private World world;
	public int brawlLookTime;
	public int brawlLookTimeTimer;
	private int brawlLookTimerTask = 0;
	public ItemEvents(Game game)
	{
		random = new Random();
		this.game = game;
		world = Bukkit.getWorld(game.locationCfg.getString("locations.lobby.world"));
		brawlLookTime = game.configCfg.getInt("brawlLookTime");
		brawlLookTimeTimer = game.configCfg.getInt("brawlLookTime");
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
		if(!game.spectators.contains(e.getPlayer().getUniqueId()))
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
						
						if(block.getType().equals(Material.DOUBLE_STEP))
						{
							if(data==(byte)0)
							{
								block.setTypeIdAndData(43, (byte) 8, false);
							}
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
				if(e.getMaterial().equals(Material.INK_SACK))
				{
					if(game.gamestate == GameState.BUILDING && !game.isBrawlProtected(e.getPlayer()))
					{
						Player p = e.getPlayer();
						p.closeInventory();
						p.openInventory(game.resetInventory);						
					}
				}
				if(e.getMaterial().equals(Material.NETHER_STAR))
				{
					if(game.gamestate == GameState.BUILDING && !game.isBrawlProtected(e.getPlayer()))
					{
						Player p = e.getPlayer();
						game.addBrawlProtection(p);
						p.teleport(new Location(world, 0, 100, -100));
						p.setGameMode(GameMode.ADVENTURE);
						p.setAllowFlight(true);
						p.setFlying(true);
						p.setExp(0);
						
						brawlLookTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
							@Override
							public void run()
							{
								if(game.gamestate==GameState.BUILDING)
								{
									p.setLevel(brawlLookTimeTimer);
									brawlLookTimeTimer =- 1;									
								}
								else
								{
									game.removeBrawlProtection(p);
									p.setGameMode(GameMode.CREATIVE);
									Bukkit.getScheduler().cancelTask(brawlLookTimerTask);
								}
							}
						}, 0, 20);
						
						getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
							@Override
							public void run()
							{
								if(game.gamestate==GameState.BUILDING)
								{
									game.removeBrawlProtection(p);
									p.setGameMode(GameMode.CREATIVE);
									p.teleport(game.getPlot(p).getSpawnLocation());
									Bukkit.getScheduler().cancelTask(brawlLookTimerTask);									
								}
							}
						}, 20 * brawlLookTime);
					}
				}
				if(brawlRotate)
				{
					if(victimRotate.contains(e.getPlayer()))
					{
						Location currentLoc = e.getPlayer().getLocation();
						int rYaw = random.nextInt(360);
						currentLoc.setYaw(rYaw);
						Location blocklocation = e.getPlayer().getLocation();
						blocklocation.setY(1);
						if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
						{
							e.getPlayer().teleport(currentLoc);
						}
					}
				}
			}
			
			if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
			{
				if(brawlRandomTP)
				{
					if(victimRandomTP.contains(e.getPlayer()))
					{
						Location currentLoc = e.getPlayer().getLocation();
						int rX = random.nextInt(7)-3;
						int rY = random.nextInt(3)+1;
						int rZ = random.nextInt(7)-3;
						currentLoc.setX(currentLoc.getX()+rX);
						currentLoc.setY(currentLoc.getY()+rY);
						currentLoc.setZ(currentLoc.getZ()+rZ);
						Location blocklocation = e.getPlayer().getLocation();
						blocklocation.setY(1);
						if(e.getPlayer().getWorld().getBlockAt(blocklocation).getType() == Material.BEDROCK)
						{
							e.getPlayer().teleport(currentLoc);
						}
					}
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
