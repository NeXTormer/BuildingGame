package events;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import game.Game;
import game.GameState;

/**
 * Created by Iris on 22-Jan-17.
 */
public class InventoryEvents implements Listener {

    private Game game;
    public InventoryEvents(Game game)
    {
        this.game = game;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
    	Player p = (Player) e.getWhoClicked();
        if(game.gamestate == GameState.VOTING)
        {
            e.setCancelled(true);
            if(e.getInventory().getTitle().equals(game.votingInventory.getTitle())) {
                if (e.getRawSlot() > game.themes.size() - 1) return;
                game.votes[e.getRawSlot()]++;
                p.sendMessage(game.playerprefix + "Du hast fuer das Thema �6�l" + game.themes.get(e.getRawSlot()) + "�r�7 abgestimmt");
                p.playSound(p.getLocation(), Sound.BURP, 1, 1);
                e.getWhoClicked().closeInventory();
            }
        }
        else if (game.gamestate == GameState.GRADING)
        {
            e.setCancelled(true);
        	//if(e.getInventory().getName().equals(game.gradingInventories.get(p).inv.getName())) //if the clicked inventory is a grading inventory
        	{
        		if(e.getRawSlot() == 59 || e.getRawSlot() == 58 || e.getRawSlot() == 57)
        		{
        			p.closeInventory();
        			p.sendMessage(game.playerprefix + "Deine Bewertung wurde gespeichert");
        			return;
        		}
        		
        		if(true)//check which item has been clicked and set the rating per plot accordingly
        		{
        			game.plotArray[game.gradingCurrentPlotId].addGradeCreativity(1111); //add modularity
        		}
        		
        	}
        }
        if(e.getCurrentItem().getType().equals(Material.ENDER_PEARL))
        {
        	e.setCancelled(true);
        }
        if(e.getCurrentItem().getType().equals(Material.MONSTER_EGG) || e.getCurrentItem().getType().equals(Material.MONSTER_EGGS) || e.getCurrentItem().getType().equals(Material.MOB_SPAWNER))
        {
        	e.setCancelled(true);
        }
    }
}
