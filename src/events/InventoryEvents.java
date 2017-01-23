package events;

import game.Game;
import game.GameState;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

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
                e.getWhoClicked().closeInventory();
            }
        }
        else if (game.gamestate == GameState.GRADING)
        {
        	if(e.getInventory().getName().equals(game.gradingInventories.get(p))) //if the clicked inventory is a grading inventory
        	{
        		if(true)//check which item has been clicked and set the rating per plot accordingly
        		{
        			game.plotSpawns[game.gradingCurrentPlotId].setGradeCreativity(1111);
        		}
        		
        	}
        }
    }


}
