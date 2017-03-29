package brawls;

import game.Game;
import game.Plot;

public class BrawlNull extends PlotBrawl {

	private Game game;
	private Plot victimPlot;
	
	public BrawlNull(Plot victimPlot, Game game)
	{
		super();
		this.game = game;
		this.victimPlot = victimPlot;
	}
	
	@Override
	public void start()
	{	
		victimPlot.damageShield();
		if(victimPlot.getShield()<=0)
		{
			victimPlot.getOwner().getPlayer().sendMessage(game.playerprefix+"Dein §l§6Shield-Brawl§r§7 wurde zerstoert!");
		}
		else
		{
			victimPlot.getOwner().getPlayer().sendMessage(game.playerprefix+"Dein §l§6Shield-Brawl§r§7 wurde beschaedigt!");			
		}
	}
	
	@Override
	public void stop()
	{
		
	}
}
