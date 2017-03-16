package structures;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class StructureParser {

	public static File configFile = new File("plugins/BuildingGame", "structures.yml");
	public static FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

	private static int index;
	
	public static Structure[] getStructures()
	{
		return null;
	
	}
	
	public static void saveStructures(Structure[] structures)
	{
		index = config.getInt("index");
		for(Structure s : structures)
		{
			index++;
			for(int x = 0; x < s.size; x++)
			{
				for(int y = 0; y < s.size; y++)
				{
					for(int z = 0; z < s.size; z++)
					{
						config.set(s.name + "." + String.valueOf(x) + "." + String.valueOf(y) + "." + String.valueOf(z), s.blocks[x][y][z].name());
					}
				}
			}
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	public static void addStructure(Structure s)
	{
		index = config.getInt("index");
		for(int x = 0; x < s.size; x++)
		{
			for(int y = 0; y < s.size; y++)
			{
				for(int z = 0; z < s.size; z++)
				{
					config.set(s.name + "." + String.valueOf(x) + "." + String.valueOf(y) + "." + String.valueOf(z), s.blocks[x][y][z].name());
				}
			}
		}
		try {
			config.save(configFile);
			index++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
