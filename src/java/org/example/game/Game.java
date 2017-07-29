package org.example.game;

import java.util.LinkedList;
import java.util.List;

/** Handles everything to do with the game as a whole. */
public class Game {
	private int roundID;
	private int numPlayers = 0;
	private List<Player> players = new LinkedList<Player>();
	private int startingCash;
	public Game(int roundID, int numPlayers, int startingCash) {
		super();
		this.roundID = roundID;
		this.numPlayers = numPlayers;
		this.startingCash = startingCash;
	}
	
	public Game(){
		
	}
	
	public int getRoundID() {
		return roundID;
	}

	public void setRoundID(int roundID) {
		this.roundID = roundID;
	}

	public int getNumPlayers() {
		return numPlayers;
	}

	public void setNumPlayers(int numPlayers) {
		this.numPlayers = numPlayers;
	}
        
        public void addPlayer(Player player){
            this.players.add(player);
            this.numPlayers++;
        }

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayer(List<Player> player) {
		this.players = player;
	}

	public int getStartingCash() {
		return startingCash;
	}

	public void setStartingCash(int startingCash) {
		this.startingCash = startingCash;
	}
	
	
	public void removePlayer(Player player){
		int numProperties = player.getProperties().size();
		for(int i=0;i<numProperties;i++){
			player.getProperties().get(i).setOwner(null);
		}
		player.Pay(player.getMoney());
		for (int i = 0; i < this.getPlayers().size(); i++) {
			if(this.getPlayers().get(i) == player)
				this.getPlayers().remove(i);
		}
		this.setNumPlayers(this.getNumPlayers() - 1);
		if(this.decideWinner() != null){
			this.stop();
		}
	}
	
	public Player decideWinner(){
            for(int i=0;i<this.getPlayers().size();i++){
                if(this.getPlayers().get(i).getMoney() < 0)
                    removePlayer(this.getPlayers().get(i));
            }
            
		if(this.getNumPlayers() == 1)
			return this.getPlayers().get(0);
		else return null;
	}
	
	public void stop(){
		
	}
}
