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
		database = RCDatabase.DeserializeFromFile("BuildingGame/structures.bbschem");
	
	}

	public void save()
	{
		database = new RCDatabase("blocks");
		for(Structure s : structures)
		{
			RCObject o = new RCObject(s.name);
			RCArray bdata = RCArray.Byte("", s.getBytes());
			o.addArray(bdata);
			database.addObject(o);
		}
		
		database.serializeToFile("buidingGame/structures.bbschem");
		
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
