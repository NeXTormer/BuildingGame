
package structures;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

public class Structure {

	public byte size = 3;
	public String name;
	
	public Material[][][] blocks;
	public byte[][][] nbtTags;
	
	private Location origin;
	
	public Structure(String name)
	{
		blocks = new Material[size][size][size];
		nbtTags = new byte[size][size][size];
		this.name = name;
	}
	
	public void setOrigin(Location origin1)
	{
		origin = origin1;
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
		
		this.nbtTags = new byte[][][]
			{
					{
						{ 1, 2, 3 },
						{ 4, 5, 6 },
						{ 7, 8, 9 }
					},
					{
						{ 10, 20, 30 },
						{ 40, 50, 60 },
						{ 70, 80, 90 }
					},
					{
						{ 012, 023, 034 },
						{ 045, 056, 067 },
						{ 78,  89,  90  }
					}
			};
		
		this.blocks = blocks;
	}
	
	public boolean compareTo(Location origin)
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
                    if(origin.getBlock().getRelative(relx, rely, relz).getType() == Material.BARRIER)
                    {
                    	continue;
                    }
                    if(!(blocks[x][y][z].equals(origin.getBlock().getRelative(relx, rely, relz).getType())))
                    {
                    	return false;
                    }
                    if(!(nbtTags[x][y][z] == origin.getBlock().getRelative(relx, rely, relz).getData()))
                    {
                    	return false;
                    }
                }
            }
        }
		return true;
	}
	
	/**
	 * Sets the blocks around the origin to air and plays an effect
	 */
	public void destroy()
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
         
                    	if(origin.getBlock().getType() != Material.ARMOR_STAND)
                    	{
                    		origin.getBlock().getRelative(relx, rely, relz).setType(Material.AIR);                    		
                    		origin.getWorld().playEffect(origin.getBlock().getRelative(relx, rely, relz).getLocation(), Effect.EXPLOSION_LARGE, 1);
                    	}    
                }
            }
        }
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
                    nbtTags[x][y][z] = origin.getBlock().getRelative(relx, rely, relz).getData();
                }
            }
        }
		
	}	
	
	
}
