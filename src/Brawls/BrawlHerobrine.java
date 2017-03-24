package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import game.Game;

public class BrawlHerobrine extends PlayerBrawl {

	private Game game;
	private Player victim;
	private int duration;
	private ArmorStand ast;
	private World world;
	
	public BrawlHerobrine(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		world = victim.getLocation().getWorld();
		game.herobrineCounter++;
					
		final int herobrineTimerTask;
		herobrineTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				victim.closeInventory();
				victim.playSound(victim.getLocation(), Sound.ENDERMAN_DEATH, 5, 1);
				ast = world.spawn(victim.getLocation(), ArmorStand.class);
				ast.setVisible(false);
				ast.setGravity(false);
				SkullMeta  meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				meta.setOwner("Username");
				ItemStack stack = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
				stack.setItemMeta(meta);
				ast.setHelmet(stack);
				victim.sendMessage(herobrineText(game.herobrineCounter));
				victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*game.herobrineCounter, 1));
				victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*game.herobrineCounter, 1));
			}
		}, 0, 100);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				ast.remove();
				victim.playSound(victim.getLocation(), Sound.HORSE_DEATH, 1, 1);
				Bukkit.getScheduler().cancelTask(herobrineTimerTask);
			}
		}, 101 * game.herobrineCounter);
		
	}
	
	private String herobrineText(int i)
	{
		if(i==1)
		{
			return "�4�lEr beobachtet dich...";
		}
		if(i==2)
		{
			return "�4�lEr ist in der N�he...";
		}
		if(i==3)
		{
			return "�4�lEr sieht dich...";
		}
		if(i==4)
		{
			return "�4�lEr kann deinen Atem sp�ren...";
		}
		if(i>=5)
		{
			return "�4�lEr wird dich holen...";
		}
		return null;
	}
}
