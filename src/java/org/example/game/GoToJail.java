package org.example.game;

/** Represents the Go to Jail space. */
public class GoToJail extends Tiles{
	
	@Override
	public void Action(Turn turn, Player player){
		turn.getPlayer().setInJail(true);
		turn.getPlayer().setPosition(10);
	}
}
