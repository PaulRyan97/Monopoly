package org.example.game;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import javax.websocket.Session;
import java.util.ArrayList;

/** Object representation of the player. */
public class Player {
	private int id;
	private int money;
	private List<Property> properties = new ArrayList<>();
	private int position = 0;
	private String username;
	private int jailTimes = 0 ;
	private boolean inJail = false;
    private int getOutOfJailCards = 0;
    
        private String sessionID;
        private Session session;
	
	public Player(int id, int money) {
		super();
		this.id = id;
		this.money = money;
	}
	
	public Player(){
	}

        /** Returns the session linked to this player. */
	public Session getSession(){
            return session;
        }
        
        public void setSession(Session session){
            this.session = session;
        }
	
	/** Returns the number of turns the player has spent in jail. */
	public int getJailTimes() {
		return jailTimes;
	}

	public void setJailTimes(int jailTimes) {
		this.jailTimes = jailTimes;
	}
        
        public String getSessionID(){
            return sessionID;
        }
        
        public void setSessionID(String id){
            sessionID = id;
        }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isInJail() {
		return inJail;
	}

	public void setInJail(boolean inJail) {
        if (!inJail)
            this.jailTimes = 0;
		this.inJail = inJail;
	}
	
	
	
	public void Buy(Property property){
		property.setOwner(this);
		this.Pay(property.getCost());
                properties.add(property);
	}
	
        public void winAuction(Property property, int minBid){
            property.setOwner(this);
            this.Pay(minBid);
	}
	
	public void Mortgage(Property property){
		this.Get(property.getCost() / 2);
		property.setMortgageStatus(true);
	}

	public void Sell(Property property){
            property.setOwner(null);
            for(int i=0;i<properties.size();i++){
                if(this.properties.get(i) == property){
                    this.getProperties().remove(i);
                }
            }
            this.Get(property.getCost() / 2);
	}
	
	public void Pay(int amount, Player player){
		this.setMoney(this.getMoney() - amount);
		player.Get(amount);
	}
	
	public void Pay(int amount){
		this.setMoney(this.getMoney() - amount);
	}
	
	public void Get(int amount){
		this.setMoney(this.getMoney() + amount);
	}
	
	public void CollectGo(){
		this.Get(200);
	}
	
	public void Exit(Game game, Bank bank){
		game.removePlayer(this);
	}
	
	public void GoToJail(){
		this.setInJail(inJail);
	}
	
	public int[] Roll(){
		int dice[];
		dice = new int[2];
		Random random = new Random();
		dice[0] = random.nextInt(6) + 1;
		dice[1] = random.nextInt(6) + 1;
		return dice;
	}
	
	public boolean rollDouble(int dice1, int dice2){
		if(dice1 == dice2)
                    return true;
		return false;
	}
		
    public int getGetOutOfJailCards(){
        return getOutOfJailCards;
	}
    
    public void setGetOutOfJailCards(int numOfTimes){
        this.getOutOfJailCards = numOfTimes;
}
    
    public void addGetOutOfJailCards(){
        setGetOutOfJailCards(getGetOutOfJailCards()+1);
    }
}
