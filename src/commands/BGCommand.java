package commands;

import game.Game;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BGCommand implements CommandExecutor {
	
	private Game game;
	public BGCommand(Game game)
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
				if(args[0].equalsIgnoreCase("tp"))
				{
					p.teleport(game.plotArray[Integer.valueOf(args[1])].getSpawnLocation());
					return true;
				}
				if(args[0].equalsIgnoreCase("setTime"))
				{
					game.buildingTime = Integer.valueOf(args[1]);
					p.sendMessage(game.playerprefix+"Die Bauzeit wurde auf §6"+args[1]+"§7 Sekunden gesetzt");
					return true;
				}
			}
			
		}
		
		
		return true;
	}


}
