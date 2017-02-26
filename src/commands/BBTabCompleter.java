package commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import game.Game;

public class BBTabCompleter implements TabCompleter {

	
	private Game game;
	
	private List<String> arg0def = new ArrayList<>();
	private List<String> arg1def = new ArrayList<>();
	private List<String> arg2def = new ArrayList<>();
	
	private List<String> temp = new ArrayList<>();
	
	
	public BBTabCompleter(Game g)
	{
		game = g;
		
		arg0def.add("bb");
		
		arg1def.add("debug");
		arg1def.add("mode");
		arg1def.add("skull");
		arg1def.add("save");
		arg1def.add("info");
		arg1def.add("end");
		arg1def.add("launchfirework");
		arg1def.add("startgame");
		arg1def.add("tp");
		arg1def.add("settime");
		arg1def.add("brawl");
		arg1def.add("addtheme");
		arg1def.add("removetheme");
		arg1def.add("spectate");

		Collections.sort(arg1def);

		
	}

	@Override
	public java.util.List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		//temp.clear();
		if(args.length == 0)
		{
			return arg0def;
		}
		
		if(args.length == 1)
		{
			if(args[0].equals(""))
			{
				return arg1def;
			}
			else
			{
				temp.clear();
				for(String s : arg1def)
				{
					if(s.toLowerCase().startsWith(args[0].toLowerCase()))
					{
						temp.add(s);
					}
				}
				return temp;				
			}
		}
		
		if(args.length == 2)
		{
			if(args[0].equalsIgnoreCase("tp"))
			{
				temp.clear();
				for(UUID uuid : game.players)
				{
					Player p = Bukkit.getPlayer(uuid);
					temp.add(p.getDisplayName());
				}
				return temp;
			}
			
			if(args[0].equalsIgnoreCase("removetheme"))
			{
				temp.clear();
				for(String theme : game.themes)
				{
					if(theme.toLowerCase().startsWith(args[1].toLowerCase()))
					{
						temp.add(theme);
					}
				}
				Collections.sort(temp);
				return temp;
			}
		}

		
		
		
		return arg2def;
	}	
	
}
