package structures;

import org.bukkit.Location;
import org.bukkit.Material;

public class Structure {


	public int size = 3;
	public String name;
	
	private Material[][][] blocks;
	
	
	public Structure(String name, int size)
	{
		blocks = new Material[size][size][size];	
		this.name = name;
		
	}
	
	
	public void setStructure(Location origin)
	{
		for(int x = 0; x < size; x++)
        {
            for(int y = 0; y < size; y++)
            {
                for(int z = 0; z < size; z++)
                {
                    int relx = ((int) (size / 2)) - x;
                    int rely = ((int) (size / 2)) - y;
                    int relz = ((int) (size / 2)) - z;
                    blocks[x][y][z] = origin.getBlock().getRelative(relx, rely, relz).getType();
                }
            }
        }
	}
	
	
}
