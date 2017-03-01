package structures;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Structure {


	public static File structuresFile = new File("plugins/BuildingGame", "structures.yml");
	public static FileConfiguration cfg = YamlConfiguration.loadConfiguration(structuresFile);

	public int size = 3;
	
	private Material[][][] blocks;
	
	public Structure()
	{
		
	}
	
	public void loadFromConfig(String name)
	{
		cfg.get("structures." + name + ".size");
		
		blocks = new Material[size][size][size];
	}
	
}
