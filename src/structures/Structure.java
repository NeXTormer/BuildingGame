package structures;

import org.bukkit.Location;
import org.bukkit.Material;

import com.thecherno.raincloud.serialization.RCArray;
import com.thecherno.raincloud.serialization.RCField;

public class Structure {


	public byte size = 3;
	public String name;
	
	public Material[][][] blocks;
	private RCField field;
	
	public Structure()
	{
		blocks = new Material[size][size][size];
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
	
	/**
	 * [0] size (1B)
	 * [..] id (1B)
	 * @param blocks
	 * @return
	 */
	public byte[] getBytes()
	{
		byte[] bytes = new byte[(int) Math.pow(size, 3) + 1];
		bytes[0] = size;
		for(int x = 0; x < size; x++)
		{
			for(int y = 0; y < size; y++)
			{
				for(int z = 0; z < size; z++)
				{
					bytes[(x + size * (y + size * z)) + 1] = (byte) blocks[x][y][z].getId();
				}
			}
		}
		return bytes;
	}
	
	
	
}
