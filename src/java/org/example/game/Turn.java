package org.example.game;

public class Turn {
	private int ID;
	private int previousPos;
	private int currentPos;
	private Player player;
	private Bank bank;
	private Board board;
	
	public Turn(){
		
	}
	
	public Turn(int ID, Player player){
		this.ID = ID;
		this.previousPos = player.getPosition();
	}
	
	public Board getBoard() {
		return board;
	}

	public void setBoard(Board board) {
            
		this.board = board;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
        
        /** Simulates a dice roll and move the player to their new position. */
	public void movePlayer(){
		int steps = 0;
		int[] dice = new int[2];
		dice = this.getPlayer().Roll();
		
		if( ! this.getPlayer().isInJail() ){	
			steps = dice[0] + dice[1];
			while(this.getPlayer().rollDouble(dice[0], dice[1])){
				dice = this.getPlayer().Roll();
				steps += dice[0] + dice[1];
			}
		}
		else{
			if(this.getPlayer().rollDouble(dice[0], dice[1])){
				this.getPlayer().setInJail(false);
				steps = dice[0] + dice[1];
			}
			this.getPlayer().setJailTimes(this.getPlayer().getJailTimes() + 1);
			if(this.getPlayer().getJailTimes() > 2){
				this.getPlayer().setInJail(false);
				this.getPlayer().Pay(50);
			}
		}
		if(this.getPlayer().getPosition() + steps > 39)
				this.getPlayer().setPosition(this.getPlayer().getPosition() + steps - 40);
		else
			this.getPlayer().setPosition(this.getPlayer().getPosition() + steps);
        
        setCurrentPos(this.getPlayer().getPosition());
	}

    /** Moves player to specific position on board, mainly used by cards. */
    public void movePlayerSpecific(Player player, int position){
        if(this.getPlayer().getPosition() < 39 && position < this.getPlayer().getPosition()){
            player.CollectGo();
        }
        this.getPlayer().setPosition(position);
        setCurrentPos(this.getPlayer().getPosition());
    }
    
	public int getID() {
        return ID;
	}

	public void setID(int iD) {
        ID = iD;
	}

	public int getPreviousPos() {
        return previousPos;
	}

	public void setPreviousPos(int previousPos) {
        this.previousPos = previousPos;
	}

	public int getCurrentPos() {
        return currentPos;
	}

	public void setCurrentPos(int currentPos) {
        this.currentPos = currentPos;
	}

	public Player getPlayer() {
        return player;
	}

    public void setPlayer(Player player) {
        this.player = player;
    }
}
