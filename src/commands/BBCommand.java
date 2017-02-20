package commands;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import game.EndReason;
import game.Game;
import game.GameState;
import utils.DeleteWorld;

public class BBCommand implements CommandExecutor {
	
	private Game game;
	public BBCommand(Game game)
	{
		this.game = game;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(sender instanceof Player)
		{
			Player p = (Player) sender;
			if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("debug"))
				{
					p.sendMessage(game.playerdata.toString());
					p.sendMessage(game.gamestate.toString());
					p.sendMessage(game.votes[1] + "");
					p.sendMessage(game.gradingInventories.toString());
					p.sendMessage("VoteBuffer0: " + game.gradingInventories.get(p).voteBuffer[0]);
					p.sendMessage("VoteBuffer1: " + game.gradingInventories.get(p).voteBuffer[1]);
					p.sendMessage("VoteBuffer2: " + game.gradingInventories.get(p).voteBuffer[2]);
					
				}
				if(args[0].equalsIgnoreCase("mode"))
				{
					game.globalBuildMode = !game.globalBuildMode;
					p.sendMessage(game.prefix + "Der Globale Baumodus wurde §6" + (game.globalBuildMode ? "aktiviert" : "deaktiviert"));
				}
				
				if(args[0].equalsIgnoreCase("skull") && game.gamestate == GameState.BUILDING)
				{
					((Player) sender).performCommand("hdb");
				}
				
				if(args[0].equalsIgnoreCase("save"))
				{
					if(game.gamestate == GameState.GRADING || game.gamestate == GameState.END)
					{
						DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
						Date date = new Date();
						String name = "Backup_"+dateFormat.format(date) + "_" + game.finalTheme; //2016/11/16_12:08:43
						
						try {
							//	DeleteWorld.copyFolder(new File(game.locationCfg.getString("locations.lobby.world")), new File(name));
							DeleteWorld.copyFolder(new File("BuildingGame"), new File("worldbackups/" + name));
						} catch (IOException e) {
							e.printStackTrace();
							p.sendMessage(game.prefix + "Fehler beim speichern der Welt");
						}
						p.sendMessage(game.prefix + "Die Welt wurde unter dem Name§6 "+name+" §7gespeichert");
					}		
				}
				
				if(args[0].equalsIgnoreCase("info"))
				{
					
					p.sendMessage(game.playerprefix + "Infos unter: https://docs.google.com/document/d/1PMkiqQYeHSQoFZg-LxOkRed9O2VPCyfEpDjsdw516vo/edit");
				}
				
				if(args[0].equalsIgnoreCase("end"))
				{
					game.endGame(EndReason.NORMAL_END);
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
					if(game.spectators.contains(p))
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
				}

			}
			else if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("skull") && game.gamestate == GameState.BUILDING)
				{
					ItemStack is = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
					SkullMeta sm = (SkullMeta) is.getItemMeta();
					sm.setOwner(args[1]);
					is.setItemMeta(sm);
					p.getInventory().addItem(is);
					p.sendMessage(game.playerprefix+"Du hast den Kopf von " + args[1] + " erhalten!");
				}
				if(args[0].equalsIgnoreCase("startgame"))
				{
					Bukkit.getServer().broadcastMessage(game.prefix+"Das Spiel startet in §6"+args[1]+"§7 Sekunden");
					game.voteTimer = Integer.valueOf(args[1])+1;
					game.start(p);
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
					Bukkit.getServer().broadcastMessage(game.prefix+"Die Bauzeit wurde auf §6"+args[1]+"§7 Sekunden gesetzt");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("brawl") && game.gamestate==GameState.GRADING)
				{
					p.sendMessage(game.playerprefix+"WIP");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("addTheme"))
				{

					game.themes.add(args[1]);
					game.themesCfg.set("themes", game.themes);
					game.loadBuildThemes();
					p.sendMessage(game.playerprefix+"Das Thema §6"+args[1]+"§7 wurde zum Themenpool hinzugefügt");
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
						p.sendMessage(game.playerprefix+"Das Thema §6"+args[1]+"§7 wurde vom Themenpool entfernt");
						try {
							game.themesCfg.save(game.themesFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						p.sendMessage(game.playerprefix+"Das Thema §6"+args[1]+"§7 ist nicht im Themenpool vorhanden");
					}
					return true;
				}
			}
			
		}
		
		
		return true;
	}


}
