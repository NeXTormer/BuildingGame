package structures;

import org.bukkit.Location;
import org.bukkit.Material;

public class Structure {

	public byte size = 3;
	public String name;
	
	public Material[][][] blocks;
	
	public Structure(String name)
	{
		blocks = new Material[size][size][size];
		this.name = name;
	}
	
	public void testStructure()
	{
		Material[][][] blocks =
            {
                    {
                            {Material.STONE, Material.STONE, Material.STONE},
                            {Material.STONE, Material.STONE, Material.STONE},
                            {Material.STONE, Material.STONE, Material.STONE}
                    },
                    {
                            {Material.WOOD, Material.WOOD, Material.WOOD},
                            {Material.WOOD, Material.WOOD, Material.WOOD},
                            {Material.WOOD, Material.WOOD, Material.WOOD}
                    },
                    {
                            {Material.ANVIL, Material.ANVIL, Material.ANVIL},
                            {Material.ANVIL, Material.ANVIL, Material.ANVIL},
                            {Material.ANVIL, Material.ANVIL, Material.ANVIL}
                    }
            };
		
		this.blocks = blocks;
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
	
	@Deprecated
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
