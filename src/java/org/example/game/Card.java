package org.example.game;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;
import javax.json.JsonObject;
import javax.json.spi.JsonProvider;

/** Object representation of Community Chest and Chance cards. */
public class Card extends Tiles{
	
	private String description;
	private String action;
	private int value;
	private Player player;
    private Set<Player> players = new HashSet<>();
    
        JsonProvider provider = JsonProvider.provider();
        JsonObject cardMessage;
	
    public Card(String description, String action, int value){
		setDescription(description);
		setAction(action);
		setValue(value);
	}
	
    public Card(){
	}
	
    public void setPlayersList(Set<Player> players){
        this.players = players;
    }

    public String getDescription(){
		return description;
	}
	

    public void setDescription(String description){
		this.description = description;
	}
	

    public String getAction(){
		return description;
	}
	

    public void setAction(String action){
		this.action = action;
	}
	

    public int getValue(){
		return value;
	}
	
 
    public void setValue(int value){
		this.value = value;
	}
	
 
    public Player getPlayer(){
		return player;
	}
	

    public void setPlayer(Player player){
		this.player = player;
	}
	

	@Override
        /** Takes in the action of the card and invokes the function with that name. */
	public void Action(Turn turn, Player player){
		//player = turn.getPlayer();
		this.player = player;
		
		String methodName = this.action;
		java.lang.reflect.Method method = null;
		try {
            System.out.println("Method name: " + methodName);
  			method = this.getClass().getMethod(methodName, int.class, Turn.class);
                        
		}		
 		catch (SecurityException e){ 
 			System.out.println("Security Exception");
 		}
  		catch (NoSuchMethodException e){ 
  			System.out.println("No such method");
  		}

  		try {
  			method.invoke(this, this.value, turn);
                        
		}
		catch (IllegalArgumentException e){
			System.out.println("illegal argument");
		}
  		catch (IllegalAccessException e){
  			System.out.println("illegal access");

  		}
  		catch (InvocationTargetException e){
                    e.getCause().printStackTrace();
  			System.out.println("target exception");

  		}
	}

    public JsonObject getCardMessage(){
            return this.cardMessage;
        }

    public void Move(int value, Turn turn){
        turn.movePlayerSpecific(player, value);
//		player.setPosition(0); 
        System.out.println("in move value: " + value);

        cardMessage = provider.createObjectBuilder()
        .add("action", "cardMove")
        .add("position", player.getPosition())
        .build();
        System.out.println("card Message");
	}
	
    public void MoveBack(int value, Turn turn){
        player.setPosition(player.getPosition() - value);
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardMove")
        .add("position", player.getPosition())
        .build();
    }
	
    public void Pay(int value, Turn turn){
//		player.money -= value;
        player.setMoney(player.getMoney() - value);
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardPay")
        .add("money", player.getMoney())
        .add("value", value)
        .build();
    }
	
    public void Collect(int value, Turn turn){
//		player.money += value;
        player.setMoney(player.getMoney() + value);
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardCollect")
        .add("money", player.getMoney())
        .add("value", value)
        .build();
    }
	
    public void CollectPlayer(int value, Turn turn){
		// loop through players and for each one 
//		otherplayer.Pay(value, player){
        for(Player otherplayer: players){
            if (otherplayer.getId() != player.getId()){
                otherplayer.Pay(value, player);
            }
        }
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardCollect")
        .add("money", player.getMoney())
        .add("value", value)
        .build();
    }
	
    public void PayEach(int value, Turn turn){
            // loop through players and for each one 
//		player.Pay(value, otherplayer){
        for(Player otherplayer: players){
            if (otherplayer.getId() != player.getId()){
                player.Pay(value, otherplayer);
            }
        }
        cardMessage = provider.createObjectBuilder()
            .add("action", "cardPay")
            .add("money", player.getMoney())
            .add("value", value)
            .build();
    }
	
	
    public void JailCard(Turn turn){
        player.GoToJail();
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardJail")
        .build();
    }
	
    /** Used for moving player to a specific tile. */
    public void MoveSpecial(int value, Turn turn){
        if(value == 0){
            if(getPosition() < 12 && getPosition() > 28){
                turn.movePlayerSpecific(player, 12);
            }
            else{
                turn.movePlayerSpecific(player, 28);
            }
                // move nearest utility

        }
        else{
            //will need to pay double the price
            if(getPosition() < 5 && getPosition() >= 35){
                turn.movePlayerSpecific(player, 5);
            }
            else{
                int[] arrayTransport = {15,25,35};
                for(int i = 0; i < arrayTransport.length; i++){
                    if(getPosition() >= arrayTransport[i] - 10 && getPosition() < arrayTransport[i]){
                        turn.movePlayerSpecific(player, i);
                    }
                }
            }
                // move nearest transport
        }
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardMove")
        .add("position", player.getPosition())
        .build();
    }
	
    public void GetOutOfJail(int value, Turn turn){
        //player.setGetOutOfJail(player.getGetOutOfJail()+1);
        player.addGetOutOfJailCards();
        cardMessage = provider.createObjectBuilder()
        .add("action", "cardOutOfJail")
        .build();
	}
	
    public void GoToJail(int value, Turn turn){
        //player.setGetOutOfJail(player.getGetOutOfJail()+1);
        player.setInJail(true);
        cardMessage = provider.createObjectBuilder()
        .add("action", "goToJail")
        .build();
    }
    
}