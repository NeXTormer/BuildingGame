package at.timolia.gamezter;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BuildingGame extends JavaPlugin {

	public void onEnable(){
		System.out.println("[BuildingGame] Enabled");
	}
	
	public void onDisable(){
		System.out.println("[BuildingGame] Disabled");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("bginfo")){
			sender.sendMessage("[BuildingGame] Created by: GameZter - Version "+ getDescription().getVersion());
		return true;
		}
		if(cmd.getName().equalsIgnoreCase("bgup")&&sender instanceof Player){
			Player player = (Player) sender;
			if(args.length==1){
				double value = Double.parseDouble(args[0]);
				double y = player.getLocation().getY();
				double z = player.getLocation().getZ();
				double x = player.getLocation().getX();
				
				World world = player.getLocation().getWorld();
				y+=value;
				Location loc = new Location(world, x, y, z);
				player.teleport(loc);
				player.sendMessage("[BuildingGame] Du wurdest "+value+" Blöcke nach oben teleportiert");
				return true;
			} else {
			double y = player.getLocation().getY();
			double z = player.getLocation().getZ();
			double x = player.getLocation().getX();
			
			World world = player.getLocation().getWorld();
			y+=10.0;
			Location loc = new Location(world, x, y, z);
			player.teleport(loc);
			player.sendMessage("[BuildingGame] Du wurdest 10 Blöcke nach oben teleportiert");
			return true;
			}
		}
		return false;
	}
}
