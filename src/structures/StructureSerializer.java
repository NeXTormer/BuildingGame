package structures;

import java.util.ArrayList;
import java.util.List;

import com.thecherno.raincloud.serialization.RCArray;
import com.thecherno.raincloud.serialization.RCDatabase;
import com.thecherno.raincloud.serialization.RCObject;

public class StructureSerializer {
	
	
	private static StructureSerializer instance = new StructureSerializer();
		
	private RCDatabase database;
	
	public List<Structure> structures = new ArrayList<>();
	
	private StructureSerializer() { 
		//TODO: check if file exists
		database = RCDatabase.DeserializeFromFile("buildingGame/structures.bbschem");
	
	}

	public void save()
	{
		for(Structure s : structures)
		{
			database = new RCDatabase("blocks");
			RCObject o = new RCObject(s.name);
			for(int x = 0; x < s.size; x++)
			{
				for(int y = 0; x < s.size; y++)
				{
					for(int z = 0; x < s.size; z++)
					{
						
					}	
				}	
			}
		
		}
	}
	
	public void load()
	{
		
	}

	public void saveToFile(String name)
	{
		database.serializeToFile("buildingGame/" + name + ".bbschem");
	}
	
	
	public static StructureSerializer getInstance()
	{
		return instance;
	}
	

}
