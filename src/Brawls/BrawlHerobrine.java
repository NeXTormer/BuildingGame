package Brawls;

import static org.bukkit.Bukkit.getScheduler;

import java.util.Random;
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
	private Random random;
	
	public BrawlHerobrine(Player victim, Game game)
	{
		super();
		this.game = game;
		this.victim = victim;
	}
	
	@Override
	public void start()
	{	
		random = new Random();
		world = victim.getLocation().getWorld();
		game.herobrineCounter++;
		ast = world.spawn(victim.getLocation(), ArmorStand.class);
		ast.setVisible(false);
		ast.setGravity(false);
					
		final int herobrineTimerTask;
		herobrineTimerTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				ast.remove();
				victim.closeInventory();
				world.strikeLightning(victim.getLocation());
				victim.playSound(victim.getLocation(), Sound.ENDERMAN_DEATH, 5, 1);
				victim.playSound(victim.getLocation(), Sound.WITHER_SPAWN, 7, 1);
				ast = world.spawn(victim.getLocation(), ArmorStand.class);
				ast.setVisible(false);
				ast.setGravity(false);
				SkullMeta  meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				meta.setOwner("Herobrine");
				ItemStack stack = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
				stack.setItemMeta(meta);
				ast.setHelmet(stack);
				victim.sendMessage(herobrineText());
				victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20*game.herobrineCounter, 1));
				victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 20*game.herobrineCounter, 1));
			}
		}, 0, 120);

		getScheduler().scheduleSyncDelayedTask(game.plugin, new Runnable() {
			@Override
			public void run()
			{
				ast.remove();
				victim.playSound(victim.getLocation(), Sound.WITHER_DEATH, 1, 1);
				Bukkit.getScheduler().cancelTask(herobrineTimerTask);
			}
		}, 100 * game.herobrineCounter);
		
	}
	
	private String herobrineText()
	{
		int i = random.nextInt(6);
		if(i==1)
		{
			return "§4§lEr beobachtet dich...";
		}
		if(i==2)
		{
			return "§4§lEr ist in der Nähe...";
		}
		if(i==3)
		{
			return "§4§lEr sieht dich...";
		}
		if(i==4)
		{
			return "§4§lEr kann deinen Atem spüren...";
		}
		if(i==5)
		{
			return "§4§lEr wird dich holen...";
		}
		if(i==0)
		{
			return "§4§lEr kann deine Angst spüren...";
		}
		return null;
	}
}
