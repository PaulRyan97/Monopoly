package org.example.game;

/** Represents the GO space. */
public class Go extends Tiles{
	
	@Override
	public void Action(Turn turn, Player player) {
		if(turn.getPreviousPos() > turn.getCurrentPos())
			turn.getPlayer().CollectGo();
	}	
}
