package commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.MetadataValue;

import brawls.Animation;
import brawls.BrawlBlindness;
import brawls.BrawlEntity;
import brawls.BrawlFly;
import brawls.BrawlFreeze;
import brawls.BrawlInventoryClear;
import brawls.BrawlInventoryClose;
import brawls.BrawlJump;
import brawls.BrawlPolymorph;
import brawls.BrawlPumpkin;
import brawls.BrawlRandomTP;
import brawls.BrawlReplace;
import brawls.BrawlRotate;
import brawls.BrawlSandstorm;
import brawls.BrawlSpeed;
import brawls.BrawlUnderwater;
import game.EndReason;
import game.Game;
import game.GameState;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import utils.DeleteWorld;

public class BBCommand implements CommandExecutor {
	
	private TextComponent infoMessage;
	private TextComponent prefixMessage;

	private Game game;
	private boolean saveState = true;
	private Random random;
	private Location loc1, loc2;
	public BBCommand(Game game)
	{
		this.game = game;
		random = new Random();
		infoMessage = new TextComponent("�7Infos findest du �6�lhier");
		prefixMessage = new TextComponent(game.playerprefix);
		infoMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://docs.google.com/document/d/1PMkiqQYeHSQoFZg-LxOkRed9O2VPCyfEpDjsdw516vo/edit"));
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			if(args.length == 0)
			{
				if(p.hasPermission("buildingbrawl.debug"))
				{
					game.addPlayer(p);
					p.sendMessage(game.debugprefix + "Du wurdest dem Spiel hinzugefuegt");
					return true;
				}
			}
			if(args.length == 1)
			{
				if(p.hasPermission("buildingbrawl.debug"))
				{
					if(args[0].equalsIgnoreCase("debug"))
					{
						p.sendMessage(game.debugprefix + "Players: ");
						for(UUID uuid : game.players)
						{
							Player pl = Bukkit.getPlayer(uuid);
							p.sendMessage(game.debugprefix + " - �a" + pl.getDisplayName());
						}
						
						p.sendMessage(game.debugprefix + "Spectators: ");
						for(UUID uuid : game.spectators)
						{
							Player pl = Bukkit.getPlayer(uuid);
							p.sendMessage(game.debugprefix + " - �a" + pl.getDisplayName());
						}
						
						
						p.sendMessage(game.debugprefix + "GameState: �6" + game.gamestate.toString());
						return true;
					}
					
					if(args[0].equalsIgnoreCase("damage"))
					{
						game.plotArray[0].damageShield();
						return true;
					}
					
					if(args[0].equalsIgnoreCase("mode"))
					{
						game.globalBuildMode = !game.globalBuildMode;
						p.sendMessage(game.prefix + "Der Globale Baumodus wurde �6" + (game.globalBuildMode ? "aktiviert" : "deaktiviert"));
						return true;
					}	
					
					if(args[0].equalsIgnoreCase("end"))
					{
						game.endGame();
						return true;
					}
					
				}
				
				if(args[0].equalsIgnoreCase("skull") && game.gamestate == GameState.BUILDING)
				{
					((Player) sender).performCommand("hdb");
					return true;
				}
				
				if(p.hasPermission("buildingbrawl.saveworld"))
				{
					if(args[0].equalsIgnoreCase("save"))
					{
						if(game.gamestate == GameState.GRADING || game.gamestate == GameState.END)
						{
							if(saveState)
							{	
								saveState=false;
								DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
								Date date = new Date();
								String name = "Backup_"+dateFormat.format(date) + "_" + game.finalTheme; //2016_11_16_12:08:43
								String backupPath = game.configCfg.getString("savePath");
								
								try {
									//	DeleteWorld.copyFolder(new File(game.locationCfg.getString("locations.lobby.world")), new File(name));
									DeleteWorld.copyFolder(new File("BuildingGame"), new File(backupPath + "/" + name));
								} catch (IOException e) {
									e.printStackTrace();
									p.sendMessage(game.prefix + "Fehler beim speichern der Welt");
								}
								p.sendMessage(game.prefix + "Die Welt wurde unter dem Name�6 "+name+" �7gespeichert");
							}
							else
							{
								p.sendMessage(game.prefix + "Die Welt wurde bereits gespeichert");
							}
							return true;
						}	
						else
						{
							p.sendMessage(game.prefix + "Die Welt kann nur am Ende des Spiels gespeichert werden");
							return true;
						}
					}	
				}
				
				if(p.hasPermission("buildingbrawl.play"))
				{
					if(args[0].equalsIgnoreCase("info"))
					{
						p.spigot().sendMessage(prefixMessage, infoMessage);
						return true;
					}
					
					if(args[0].equalsIgnoreCase("launchFirework"))
					{
						if(game.launchFirework==true)
						{
							p.sendMessage(game.playerprefix+"Das Feuerwerk wurde ausgestellt");
						}
						if(game.launchFirework==false)
						{
							p.sendMessage(game.playerprefix+"Das Feuerwerk wurde eingeschalten");
						}
						game.launchFirework = !game.launchFirework;
						return true;
					}
					
					if(args[0].equalsIgnoreCase("spectate"))
					{
						if(game.spectators.contains(p.getUniqueId()))
						{
							if(game.gamestate == GameState.LOBBY)
							{
								game.removeSpectator(p);
								game.addPlayer(p);
							}
							else
							{
								p.sendMessage(game.prefix + "Du kannst nicht mehr mitspielen da das Spiel bereits gestartet hat");
							}
						}
						else
						{
							if(game.gamestate == GameState.LOBBY)
							{
								game.removePlayer(p);
								game.addSpectator(p, "Du wurdest den Zuschauern hinzugefuegt");
							}
						}
						return true;
					}
				}
				

			}
			else if(args.length == 2)
			{
				if(p.hasPermission("buildingbrawl.play"))
				{
					if(args[0].equalsIgnoreCase("skull") && game.gamestate == GameState.BUILDING)
					{
						ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
						SkullMeta sm = (SkullMeta) is.getItemMeta();
						sm.setOwner(args[1]);
						is.setItemMeta(sm);
						p.getInventory().addItem(is);
						p.sendMessage(game.playerprefix+"Du hast den Kopf von �6" + args[1] + " �7erhalten!");
						return true;
					}
					
				}
				
				if(p.hasPermission("buildinggame.debug"))
				{					
					if(args[0].equalsIgnoreCase("startgame"))
					{
						Bukkit.getServer().broadcastMessage(game.prefix+"Das Spiel startet in �6"+args[1]+"�7 Sekunden");
						game.voteTimer = Integer.valueOf(args[1])+1;
						game.start(p);
						return true;
					}
					
					if(args[0].equalsIgnoreCase("savestructure"))
					{
						p.sendMessage(game.playerprefix + "Klicke auf den �6Mittelpunkt �7der Struktur");
						p.sendMessage(game.playerprefix + "Du hast �610 Sekunden�7 Zeit");
						game.setMetadata(p, "savingStructureName", args[1]);
						game.setMetadata(p, "savingStructure", true);
						
						int scheduler = Bukkit.getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
							
							@Override
							public void run() {
								if(game.getMetadataBoolean(p, "savingStructure"))
								{
									game.setMetadata(p, "savingStructure", false);
									p.sendMessage(game.playerprefix + "Die Zeit ist abgelaufen");
									
								}
								
								
							}
						}, 20 * 10);
						game.setMetadata(p, "savingStructureScheduler", scheduler);
						return true;
					}
					
					if(args[0].equalsIgnoreCase("tp") && args[1] instanceof String)
					{
						for(int i = 0; i<game.plotArray.length; i++)
						{
							if(game.plotArray[i].getOwner().getName().equalsIgnoreCase(args[1]))
							{
								p.teleport(game.plotArray[i].getSpawnLocation());
								break;
							}
						}
						return true;
					}
					
					if(args[0].equalsIgnoreCase("setTime"))
					{
						game.buildingTime = Integer.valueOf(args[1]);
						Bukkit.getServer().broadcastMessage(game.prefix+"Die Bauzeit wurde auf �6"+args[1]+"�7 Sekunden gesetzt");
						return true;
					}
					
					if(args[0].equalsIgnoreCase("brawl") && game.gamestate == GameState.BUILDING)
					{
						String s = args[1];
						
						if(s.equalsIgnoreCase("speed"))
						{
							game.playPlayerBrawlBrawl(new BrawlSpeed(p, game));
						}
						
						if(s.equalsIgnoreCase("clearinv"))
						{
							game.playPlayerBrawlBrawl(new BrawlInventoryClear(p, game));
						}
						
						if(s.equalsIgnoreCase("freeze"))
						{
							game.playPlayerBrawlBrawl(new BrawlFreeze(p, game));
						}
						
						if(s.equalsIgnoreCase("closeinv"))
						{
							game.playPlayerBrawlBrawl(new BrawlInventoryClose(p, game));
						}
						
						if(s.equalsIgnoreCase("pumpkin"))
						{
							game.playPlayerBrawlBrawl(new BrawlPumpkin(p, game));
						}
						
						if(s.equalsIgnoreCase("blindness"))
						{
							game.playPlayerBrawlBrawl(new BrawlBlindness(p, game));
						}
						
						if(s.equalsIgnoreCase("fly"))
						{
							game.playPlayerBrawlBrawl(new BrawlFly(p, game));
						}
						
						if(s.equalsIgnoreCase("replace"))
						{
							game.playPlayerBrawlBrawl(new BrawlReplace(p, game));
						}
						
						if(s.equalsIgnoreCase("polymorph"))
						{
							game.playPlayerBrawlBrawl(new BrawlPolymorph(p, game));
						}
						
						if(s.equalsIgnoreCase("jump"))
						{
							game.playPlayerBrawlBrawl(new BrawlJump(game.randomBrawlVictim(p), game));
						}
						
						if(s.equalsIgnoreCase("randomtp"))
						{
							game.playPlayerBrawlBrawl(new BrawlRandomTP(p, game));
						}
						
						if(s.equalsIgnoreCase("rotate"))
						{
							game.playPlayerBrawlBrawl(new BrawlRotate(p, game));
						}
						
						if(s.equalsIgnoreCase("sandstorm"))
						{
							game.playPlotBrawlBrawl(new BrawlSandstorm(game.getPlot(game.randomBrawlVictim(p)), game));
						}
						
						if(s.equalsIgnoreCase("entity"))
						{
							game.playPlotBrawlBrawl(new BrawlEntity(game.getPlot(game.randomBrawlVictim(p)), game));
						}
						
						if(s.equalsIgnoreCase("underwater"))
						{
							game.playPlotBrawlBrawl(new BrawlUnderwater(game.getPlot(game.randomBrawlVictim(p)), game));
						}
					
						return true;
					
					}
					
					if(args[0].equalsIgnoreCase("addTheme"))
					{
						
						game.themes.add(args[1]);
						game.themesCfg.set("themes", game.themes);
						game.loadBuildThemes();
						p.sendMessage(game.playerprefix+"Das Thema �6"+args[1]+"�7 wurde zum Themenpool hinzugef�gt");
						try {
							game.themesCfg.save(game.themesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return true;
					}
					
					if(args[0].equalsIgnoreCase("removeTheme"))
					{
						if(game.themes.contains(args[1]))
						{
							game.themes.remove(args[1]);
							game.themesCfg.set("themes", game.themes);
							game.loadBuildThemes();
							p.sendMessage(game.playerprefix+"Das Thema �6"+args[1]+"�7 wurde vom Themenpool entfernt");
							try {
								game.themesCfg.save(game.themesFile);
							} catch (IOException e) {
								e.printStackTrace();
								p.sendMessage(game.debugprefix + "Das Thema konnte nicht gespeichert Werden");
							}
						}
						else
						{
							p.sendMessage(game.playerprefix+"Das Thema �6"+args[1]+"�7 ist nicht im Themenpool vorhanden");
						}
						return true;
					}
				}
				

				
			}
			else
			{
				if(args[0].equalsIgnoreCase("animation"))
				{
					loc1 = p.getLocation();
					Location temp = game.plotArray[Integer.parseInt(args[1])].getSpawnLocation();
                    loc2 = new Location(temp.getWorld(), temp.getX(), temp.getY(), temp.getZ());
//					Location loc2 = game.lobbyLocation;
					loc2.setX(loc2.getX()-17);
					loc2.setZ(loc2.getZ()+17);
					//Animation animation = new Animation(loc1, loc2, args[2], Material.PUMPKIN, game);
					//animation.prepare();
					return true;
				}
			}
			sender.sendMessage(game.playerprefix + "Nicht vorhandener oder nicht vollstaendiger Befehl");
		}
		
		
		return true;
	
	}

}
