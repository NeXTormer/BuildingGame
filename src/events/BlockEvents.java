package events;

import java.awt.Point;

import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import game.Game;
import game.GameState;

public class BlockEvents implements Listener {
	
	private Game game;
	public BlockEvents(Game game)
	{
		this.game = game;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		Player p = e.getPlayer();
		if(game.gamestate == GameState.BUILDING)
		{
			p.sendMessage(p.getStatistic(Statistic.CAKE_SLICES_EATEN) + "Peter" + (int) System.currentTimeMillis() % 100);
		}
		else
		{
			if(!game.globalBuildMode) e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();

		
		
	}
	
	

}
