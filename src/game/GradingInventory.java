package game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_8_R3.BlockMinecartTrackAbstract.MinecartTrackLogic;

/**
 * This inventory is used for grading
 * @author Iris
 *
 */
public class GradingInventory {

	public Inventory inv;

	/**
	 * index 0: Creativity
	 * index 1: Look
	 * index 2: Fitting
	 */
	public int[] voteBuffer = new int[3];
	
	private static ItemStack optikIS = createItemStack(Material.DIAMOND, "�6Optik", "�7Bewerte, wie gut das Gebaute aussieht");
	private static ItemStack stimmigkeitIS = createItemStack(Material.SIGN, "�6Stimmigkeit", "�7Bewerte, ob das Gebaute zum Thema passt");
	private static ItemStack kreativitaetIS = createItemStack(Material.EMERALD, "�6Kreativitaet", "�7Bewerte, wie Kreativ das Gebaute umgesetzt wurde");
	
	
	private static ItemStack sehrGutGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 13, "�2Sehr Gut", "");
	private static ItemStack gutGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 5, "�aGut", "");
	private static ItemStack mittelGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 4, "�eMittel", "");
	private static ItemStack schlechtGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 1, "�6Schlecht", "");
	private static ItemStack sehrSchlechtGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 14,
			"�4Sehr Schlecht", "");
	private static ItemStack nichtBewertetGIS = createItemStackColor(Material.STAINED_GLASS, 1, (short) 0,
			"�fNicht bewertet", "");
	private static ItemStack bewertungAbschliessenIS = createItemStack(Material.EMERALD_BLOCK,
			"�2Bewertung abschliessen", "");

	private static ItemStack sehrGutCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 13, "�2Sehr Gut", "");
	private static ItemStack gutCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 5, "�aGut", "");
	private static ItemStack mittelCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 4, "�eMittel", "");
	private static ItemStack schlechtCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 1, "�6Schlecht", "");
	private static ItemStack sehrSchlechtCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 14,
			"�4Sehr Schlecht", "");
	private static ItemStack nichtBewertetCIS = createItemStackColor(Material.STAINED_CLAY, 1, (short) 0,
			"�fNicht bewertet", "");

	public GradingInventory() {
		inv = Bukkit.createInventory(null, 54, "�6Plot bewerten");
		voteBuffer[0] = 8;
		voteBuffer[1] = 8;
		voteBuffer[2] = 8;
		updateInventory();
	}
	
	public void saveRating()
	{
		
	}

	public void invClicked(InventoryClickEvent e) {
		int slot = e.getRawSlot();
		if(slot >= 11 && slot <= 16)
		{
			int x = (slot + 1) % 9;
			voteBuffer[0] = x;
		}
		
		if(slot >= 20 && slot <= 25)
		{
			int x = (slot + 1) % 9;
			voteBuffer[1] = x;
		}
		
		if(slot >= 29 && slot <= 34)
		{
			int x = (slot + 1) % 9;
			voteBuffer[2] = x;
		}
		
//		
//		for(int row = 2; row <= 4; row++)
//		{
//			int c = ((row - 1) * 9 + slot) - 1;
//			if(c < 3 || c > 8)                                  
//			{
//				//return;
//			}
//			Bukkit.getServer().broadcastMessage("PETER: " + c);
//			voteBuffer[row - 2] = c;
//		}	
		updateInventory();
	}
	
	public void updateInventory()
	{
		resetInventory();
		inv.setItem(8 + voteBuffer[0], getItem(voteBuffer[0]));
		inv.setItem(17 + voteBuffer[1], getItem(voteBuffer[1]));
		inv.setItem(26 + voteBuffer[2], getItem(voteBuffer[2]));
	}
	
	private ItemStack getItem(int i)
	{
		if(i == 3)
		{
			return sehrGutCIS;
		}
		else if(i == 4)
		{
			return gutCIS;
		}
		else if(i == 5)
		{
			return mittelCIS;
		}
		else if(i == 6)
		{
			return schlechtCIS;
		}
		else if(i == 7)
		{
			return sehrSchlechtCIS;
		}
		else if(i == 8)
		{
			return nichtBewertetCIS;
		}
		return new ItemStack(Material.JACK_O_LANTERN);
	}
	
	public void resetInventory()
	{
		//inv = Bukkit.createInventory(null, 54, "�6Plot bewerten");
		inv.setItem(10, optikIS);
		inv.setItem(11, sehrGutGIS);
		inv.setItem(12, gutGIS);
		inv.setItem(13, mittelGIS);
		inv.setItem(14, schlechtGIS);
		inv.setItem(15, sehrSchlechtGIS);
		inv.setItem(16, nichtBewertetGIS);
		
		inv.setItem(19, kreativitaetIS);
		inv.setItem(20, sehrGutGIS);
		inv.setItem(21, gutGIS);
		inv.setItem(22, mittelGIS);
		inv.setItem(23, schlechtGIS);
		inv.setItem(24, sehrSchlechtGIS);
		inv.setItem(25, nichtBewertetGIS);
		
		inv.setItem(28, stimmigkeitIS);
		inv.setItem(29, sehrGutGIS);
		inv.setItem(30, gutGIS);
		inv.setItem(31, mittelGIS);
		inv.setItem(32, schlechtGIS);
		inv.setItem(33, sehrSchlechtGIS);
		inv.setItem(34, nichtBewertetGIS);
		
		inv.setItem(48, bewertungAbschliessenIS);
		inv.setItem(49, bewertungAbschliessenIS);
		inv.setItem(50, bewertungAbschliessenIS);
	
	}

	private static ItemStack createItemStack(Material material, String name, String lore) {
		ItemStack is = new ItemStack(material);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> loreText = new ArrayList<>();
		loreText.add(lore);
		im.setLore(loreText);
		is.setItemMeta(im);
		return is;
	}

	private static ItemStack createItemStackColor(Material material, int amount, short damage, String name,
			String lore) {
		ItemStack is = new ItemStack(material, amount, damage);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		List<String> loreText = new ArrayList<>();
		loreText.add(lore);
		im.setLore(loreText);
		is.setItemMeta(im);
		return is;
	}

}
