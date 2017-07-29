package org.example.game;

/** Represents the two tax spaces. */
public class Tax extends Tiles{
	private int amount;
	
	
	
	public Tax(int amount){
		super();
		this.amount = amount;
	}
	
	public Tax() {
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public void Action(Turn turn, Player player) {
		turn.getPlayer().Pay(amount);
	}

}
