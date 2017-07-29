package org.example.game;

/** Represents the bank. */
public class Bank {
	private int money;

	public Bank(int money) {
		super();
		this.money = money;
	}
	
	public Bank(){
		
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public void receive(int amount){
		this.setMoney(this.getMoney() + amount);
	}
	
	public void give(int amount){
		this.setMoney(this.getMoney() - amount);
	}
}
