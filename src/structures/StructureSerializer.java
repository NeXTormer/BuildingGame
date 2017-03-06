package structures;

import com.thecherno.raincloud.serialization.RCArray;
import com.thecherno.raincloud.serialization.RCDatabase;
import com.thecherno.raincloud.serialization.RCObject;

public class StructureSerializer {
	
	
	private static StructureSerializer instance = new StructureSerializer();
		
	private RCDatabase database;
	
	private StructureSerializer() { 
		//TODO: check if file exists
		database = RCDatabase.DeserializeFromFile("buildingGame/structures.bbschem");
	
	}

	public void addStructure(Structure s)
	{
		RCObject structure = new RCObject(s.name);
		
	}
	
	public void removeStructure(Structure s)
	{
		
	}
	
	public void saveToFile()
	{
		database.serializeToFile("buildingGame/structures.bbschem");
	}
	
	
	
	
	
	
	
	public static StructureSerializer getInstance()
	{
		return instance;
	}
	

}
