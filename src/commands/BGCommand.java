package commands;

import game.Game;
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
				if(args[0].equalsIgnoreCase("startgame"))
				{
					game.start(p);
					return true;
				}
				if(args[0].equalsIgnoreCase("debug"))
				{
					p.sendMessage(game.playerdata.toString());
					p.sendMessage(game.gamestate.toString());
					p.sendMessage(game.votes[1] + "");
				}
				if(args[0].equalsIgnoreCase("mode"))
				{
					game.globalBuildMode = !game.globalBuildMode;
					p.sendMessage(game.prefix + "Der Globale Baumodus wurde §6" + (game.globalBuildMode ? "aktiviert" : "deaktiviert"));
				}

			}
			else if(args.length == 2)
			{
				if(args[0].equalsIgnoreCase("tp"))
				{
					p.teleport(game.plotSpawns[Integer.valueOf(args[1])].getSpawnLocation());
					return true;
				}
			}
			
		}
		
		
		return true;
	}


}
