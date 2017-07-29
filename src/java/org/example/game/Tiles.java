package org.example.game;

/** Overall tiles superclass from which the properties, special properties and the four corners are subclasses of. */
public abstract class Tiles {
	private int ID;
	private String Name;
	private int Position;
	private int type;
	
	public Tiles(){
		
	}
	
	public Tiles(int iD, String name, int position) {
		ID = iD;
		Name = name;
		Position = position;
	} 
	
	public Tiles(int iD,int position){
		//super();
		ID = iD;
		Position = position;
	}
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public int getPosition() {
		return Position;
	}
	public void setPosition(int position) {
		Position = position;
	}
	
	public abstract void Action(Turn turn, Player player);
	
}
