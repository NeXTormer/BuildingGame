package events;

import org.bukkit.Bukkit;
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
                if (e.getRawSlot() > game.finalThemes.size() - 1) return;
                game.votes[e.getRawSlot()]++;
                p.sendMessage(game.playerprefix + "Du hast fuer das Thema §6§l" + game.finalThemes.get(e.getRawSlot()) + "§r§7 abgestimmt");
                p.playSound(p.getLocation(), Sound.BURP, 1, 1);
                e.getWhoClicked().closeInventory();
            }
        }
        else if (game.gamestate == GameState.GRADING)
        {
            e.setCancelled(true);
        	if(e.getInventory().getName().equals(game.gradingInventories.get(p).inv.getName())) //if the clicked inventory is a grading inventory
        	{
        		if(e.getRawSlot() == 48 || e.getRawSlot() == 49 || e.getRawSlot() == 50)
        		{
        			p.closeInventory();
        			p.sendMessage(game.playerprefix + "Deine Bewertung wurde gespeichert");
        			p.playSound(p.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
        			return;
        		}
        		
        		{
        			game.gradingInventories.get(p).invClicked(e);
        		}
        		
        	}
        }
    }
}
