package org.example.game;

import java.util.List;

/** Represents the four transport spaces. */
public class Transport extends Property{
	
	public void calculateRent(Turn turn){
		int multi = 0;
		
		List<Property> property = this.getOwner().getProperties();
		for(int i=0;i<property.size();i++){
			if(property.get(i).getID() == 2 ||
				property.get(i).getID() == 10 ||
				property.get(i).getID() == 17 ||
				property.get(i).getID() == 25 ){
				multi++;
			}
		}
		
		int amount = 25 * multi;
		turn.getPlayer().Pay(amount, this.getOwner());
	}
}
