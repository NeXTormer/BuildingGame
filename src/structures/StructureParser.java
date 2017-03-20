package structures;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StructureParser {

	public static File configFile = new File("plugins/BuildingGame", "structures.yml");
	public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

	private static int index;
	
	public static Structure[] loadStructures()
	{
		index = config.getInt("index");
		Structure[] structures = new Structure[index];
		for(int i = 1; i <= index; i++)
		{
			structures[i-1] = loadStructure(i);
		}
		return structures;
	}
	
	public static Structure loadStructure(int index)
	{
		Structure s = new Structure(config.getString(index + ".name"));
		for(int x = 0; x < s.size; x++)
		{
			for(int y = 0; y < s.size; y++)
			{
				for(int z = 0; z < s.size; z++)
				{
					s.blocks[x][y][z] = Material.getMaterial(index + "." + s.name + "." + config.getString(x + "." + y + "." + z, s.blocks[x][y][z].name()));
				}
			}
		}
		return s;
	}
	
	@Deprecated
	public static void addStructures(Structure[] structures)
	{
		index = config.getInt("index");
		for(Structure s : structures)
		{
			index++;
			config.set("name", s.name);
			for(int x = 0; x < s.size; x++)
			{
				for(int y = 0; y < s.size; y++)
				{
					for(int z = 0; z < s.size; z++)
					{
						config.set(String.valueOf(x) + "." + String.valueOf(y) + "." + String.valueOf(z), s.blocks[x][y][z].name());
					}
				}
			}
			try {
				config.set("index", index);
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void addStructure(Structure s)
	{
		index = config.getInt("index");
		index++;
		config.set(index + ".name", s.name);
		for(int x = 0; x < s.size; x++)
		{
			for(int y = 0; y < s.size; y++)
			{
				for(int z = 0; z < s.size; z++)
				{
					config.set(index + "." + s.name + "." + x + "." + y + "." + z, s.blocks[x][y][z].name().toString());
				}
			}
		}
		try {
			config.set("index", index);
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
