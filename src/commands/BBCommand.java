package commands;

import game.Game;
import game.GameState;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
				

			}
			else if(args.length == 2)
			{
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
				else
				{
					p.teleport(game.plotArray[Integer.valueOf(args[1])].getSpawnLocation());	
				}
				
				if(args[0].equalsIgnoreCase("setTime"))
				{
					game.buildingTime = Integer.valueOf(args[1]);
					Bukkit.getServer().broadcastMessage(game.prefix+"Die Bauzeit wurde auf §6"+args[1]+"§7 Sekunden gesetzt");
					return true;
				}
				
				if(args[0].equalsIgnoreCase("brawl") && game.gamestate==GameState.BUILDING)
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
						// TODO Auto-generated catch block
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
							// TODO Auto-generated catch block
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
