package commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import game.Game;

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
					p.sendMessage(game.playerplots.toString());
					p.sendMessage(game.gamestate.toString());
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
		
		
		return false;
	}


}
