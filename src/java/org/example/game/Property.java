package org.example.game;

/** Represents the coloured, non-special properties. */
public class Property extends Tiles{
	private int PropertyID;
	private int Rent;
	private Player Owner;
	private boolean MortgageStatus;
	private int Cost;
	
	public Property(){
        MortgageStatus = false;
        Owner = null;
	}
	
	public Property(int propertyID, int rent, Player owner, boolean mortgageStatus, int cost) {
		super();
		PropertyID = propertyID;
		Rent = rent;
		Owner = owner;
		MortgageStatus = mortgageStatus;
		Cost = cost;
	}
	
	public Property(int iD, String name, int position) {
		super(iD, name, position);
	}

	public int getPropertyID() {
		return PropertyID;
	}
	public void setPropertyID(int propertyID) {
		PropertyID = propertyID;
	}
	public int getRent() {
		return Rent;
	}
	public void setRent(int rent) {
		Rent = rent;
	}
	public Player getOwner() {
		return Owner;
	}
	public void setOwner(Player player) {
		Owner = player;
	}
	public boolean isMortgageStatus() {
		return MortgageStatus;
	}
	public void setMortgageStatus(boolean mortgageStatus) {
		MortgageStatus = mortgageStatus;
	}
	public int getCost() {
		return Cost;
	}
	public void setCost(int cost) {
		Cost = cost;
	}
	
	@Override
	public void Action(Turn turn, Player player) {
		turn.getPlayer().Pay(this.getRent(), this.getOwner());
	}
	
}
