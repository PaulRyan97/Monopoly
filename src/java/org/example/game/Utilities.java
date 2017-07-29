package org.example.game;

import java.util.List;

/** Represents the two utilities. */
public class Utilities extends Property{
	
	
	public void calculateRent(Turn turn){
		int multi = 4;
		boolean own1 = false,own2 =false;
		
		List<Property> property = this.getOwner().getProperties();
		for(int i=0;i<property.size();i++){
			if(property.get(i).getID() == 7)
				own1 = true;
			if(property.get(i).getID() == 20)
				own2 = true;
		}
		
		if( own1 && own2)
			multi = 10;
		
		int[] dice = new int[2];
		dice = turn.getPlayer().Roll();
		int amount = multi * (dice[0] + dice[1]); 
		
		turn.getPlayer().Pay(amount, this.getOwner());
	}
}
