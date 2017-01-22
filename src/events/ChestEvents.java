package events;

import game.Game;
import game.GameState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Iris on 22-Jan-17.
 */
public class ChestEvents implements Listener {

    private Game game;
    public ChestEvents(Game game)
    {
        this.game = game;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e)
    {
        if(game.gamestate == GameState.VOTING)
        {
            e.setCancelled(true);
        }
    }


}
