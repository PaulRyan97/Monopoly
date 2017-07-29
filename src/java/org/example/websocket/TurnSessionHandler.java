package org.example.websocket;

import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import java.util.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.example.game.*;





@ApplicationScoped
public class TurnSessionHandler {
    
    private GameSessionHandler gameSessionHandler;
    
    private static int turnID = 1;
    private Turn turn;
    private List<Tiles> tiles;
    private boolean isReplied = false;
    private Set<Player> players = new HashSet<>();
    private boolean minBidRaised = false;
    private Auction auction;
    Iterator<Player> iter;
    private int[] hard = {1,3,6};
    private int i = 0;
  
    
    public Turn getTurn(){
        return this.turn;
    }
    
    /** Sets up everything necessary for the turn and starts it by rolling the dice. */
    public void TurnProcessor(Bank bank, Board board, Player player, GameSessionHandler gameSessionHandler){
		this.turn = new Turn();
		turn.setID(turnID++);
		turn.setBank(bank);
		turn.setBoard(board);
        tiles = turn.getBoard().getTiles();
		turn.setPlayer(player);
        this.gameSessionHandler = gameSessionHandler;
		
		rollMessage(player);
		
	}
    
    /** Moves the player and informs them where they have landed on the board. */
    public void rollMessage(Player player){
            turn.setPreviousPos(player.getPosition());
            this.turn.movePlayer();

            JsonProvider provider = JsonProvider.provider();
            JsonObject rollMessage = provider.createObjectBuilder()
                .add("action", "roll")
                .add("position", this.turn.getPlayer().getPosition())
                .build();
            String name = this.tiles.get(this.turn.getPlayer().getPosition()).getName();
            gameSessionHandler.logToSession(this.turn.getPlayer().getSession(), "You have landed on " + name);
            gameSessionHandler.logExceptSession(this.turn.getPlayer().getSession(), this.turn.getPlayer().getUsername() + " has landed on " + name);
            gameSessionHandler.sendToSession(this.turn.getPlayer().getSession(), rollMessage);
            
            System.out.println("Send roll message to client");
            
            if(turn.getPreviousPos() > turn.getCurrentPos()){
                Go go = new Go();
                gameSessionHandler.logToSession(this.turn.getPlayer().getSession(), "You have passed Go, Collect $200"); 
                go.Action(this.turn, this.turn.getPlayer());
                System.out.println(this.turn.getPlayer().getMoney());
                updateInfoMessage(this.turn.getPlayer());
            }
       
    }
    
    /** Deals with whatever action needs to be executed based on where the player landed. */
    public void turnProcess(){
        int currentType = this.tiles.get(this.turn.getPlayer().getPosition()).getType();
        
        //Type 1:Property
        if(currentType == 1){
            Property property = (Property) this.tiles.get(this.turn.getPlayer().getPosition());
           
            if(property.getOwner() == null){
               String str = "Buy this property for " + property.getCost() + "?";
               gameSessionHandler.logToSession(this.turn.getPlayer().getSession(), str);
               
               optionsMessage(property);
            }
            else if (property.getOwner() != null && property.getOwner() != this.turn.getPlayer())
            {
                gameSessionHandler.log("This property is owned by " + property.getOwner().getUsername());
                JsonProvider provider = JsonProvider.provider();
                JsonObject payMessage = provider.createObjectBuilder()
               .add("action", "payRent")
               .add("propertyName", property.getOwner().getUsername())
               .add("propertyCost", property.getRent())
               .build();
                
                this.turn.getPlayer().Pay(property.getRent(), property.getOwner());
                gameSessionHandler.sendToSession(turn.getPlayer().getSession(), payMessage);
                
                JsonObject receiveRentMessage = provider.createObjectBuilder()
               .add("action", "receieveRent")
               .add("propertyName", property.getOwner().getUsername())
               .add("propertyCost", property.getRent())
               .build();
                
                gameSessionHandler.sendToSession(property.getOwner().getSession(), receiveRentMessage);
                
                String str = this.turn.getPlayer().getUsername() + " has paid " + property.getRent() + " to " + property.getOwner().getUsername();
                gameSessionHandler.log(str);
                this.setReplied(true);
            } else {
                gameSessionHandler.logToSession(property.getOwner().getSession(), "You own this property");
//                gameSessionHandler.logExceptSession(property.getOwner().getSession(), "---This is his property---");
                this.setReplied(true);
            }
        }
      
        //Type 2:GO
        else if(currentType == 2){
//            gameSessionHandler.log("---No any actions in this tiles---");
            this.setReplied(true);
        }
        
        //Type 3/4:Jail Visiting/Free Parking
        else if(currentType == 3||currentType == 4){
//            gameSessionHandler.log("---No any actions in this tiles---");
            this.setReplied(true);
        }
        
        //Type 5:Go to Jail
        else if(currentType == 5){
             GoToJail goToJail = (GoToJail) this.tiles.get(this.turn.getPlayer().getPosition());
             goToJail.Action(turn, this.turn.getPlayer());
             this.setReplied(true);
        }
        
        //Type 6:Tax
        else if(currentType == 6){
            Tax tax = new Tax();
            tax = (Tax) this.turn.getBoard().getTiles().get(this.turn.getPlayer().getPosition());
            this.turn.getPlayer().Pay(tax.getAmount());
            
            String str = this.turn.getPlayer().getUsername() + " has paid " + tax.getAmount() + " in tax";
            gameSessionHandler.log(str);
           
           updateInfoMessage(this.turn.getPlayer());
            this.setReplied(true);
            
        }

        //Type 7:CommunityChest Cards
        else if(currentType == 7){
            Card card = new Card();
            card.setPlayersList(players);
           
//            String str = "Size:" + this.turn.getBoard().getCommucardslinked().size();
//            gameSessionHandler.log(str);
            card = this.turn.getBoard().getCommucardslinked().removeFirst();
            gameSessionHandler.logExceptSession(turn.getPlayer().getSession(),card.getDescription());
            
            int preCardPosition = turn.getPlayer().getPosition();
            
            card.Action(turn, turn.getPlayer());
            
            int postCardPosition = turn.getPlayer().getPosition();
            
            this.turn.getBoard().getCommucardslinked().addLast(card);
            
            String cardDescription = card.getDescription();
            gameSessionHandler.logToSession(turn.getPlayer().getSession(), "Community Chest: " + cardDescription );
            
            JsonObject cardMessage = card.getCardMessage();
            gameSessionHandler.sendToSession(this.turn.getPlayer().getSession(), cardMessage);
            
            if(preCardPosition != postCardPosition){
                System.out.println("recalling turnprocess");
                turnProcess();
            }
            else{
            this.setReplied(true);
            
        }
        }

        
        //Type 8:Chance Cards
        else if(currentType == 8){
            Card card = new Card();
            card.setPlayersList(players);
            
//            String str = "Size:" + this.turn.getBoard().getChancecardslinked().size();
//            gameSessionHandler.log(str);
            card = this.turn.getBoard().getChancecardslinked().removeFirst();
            gameSessionHandler.log(card.getDescription());
            
            int preCardPosition = turn.getPlayer().getPosition();
            
            card.Action(turn, turn.getPlayer());
            
            int postCardPosition = turn.getPlayer().getPosition();
            
            
            this.turn.getBoard().getChancecardslinked().addLast(card);
            
            String cardDescription = card.getDescription();
            gameSessionHandler.logToSession(turn.getPlayer().getSession(), "Chance: " + cardDescription);
            
            JsonObject cardMessage = card.getCardMessage();
            gameSessionHandler.sendToSession(this.turn.getPlayer().getSession(),cardMessage);
            
            
            if(preCardPosition != postCardPosition){
                System.out.println("recalling turnprocess");
                turnProcess();
            }
            else{
            this.setReplied(true);
        }
        }

        
    }
  
    /** Sends a message to the player giving them the option to buy a property or auction it. */
    public void optionsMessage(Property property){
         JsonProvider provider = JsonProvider.provider();
         JsonObject buyMessage = provider.createObjectBuilder()
              .add("action", "buyOption")
              .add("playerMoney",turn.getPlayer().getMoney())
              .add("propertyName", property.getName())
              .add("propertyCost", property.getCost())
              .build();
         gameSessionHandler.sendToSession(turn.getPlayer().getSession(), buyMessage);
    }
    
    
    /** Buys the property and sends the updated info to the client. */
    public void buyProperty(Property property){
            turn.getPlayer().Buy(property);
            JsonProvider provider = JsonProvider.provider();
            JsonObject buyMessage = provider.createObjectBuilder()
                .add("action", "buy")
                .add("propertyName", property.getName())
                .add("playerMoney", this.turn.getPlayer().getMoney())
                .add("mortgageStatus", property.isMortgageStatus())
                .add("propertyPosition", property.getPosition())
                .add("propertyID", property.getID())
                .build();
            gameSessionHandler.sendToSession(this.turn.getPlayer().getSession(),buyMessage);
            gameSessionHandler.log(this.turn.getPlayer().getUsername() + " has bought " + property.getName());      
    }
    
    /** Sells the player's property. */
    public void sellProperty(Session session, int position){
        Property property = (Property) this.tiles.get(position);
        Player player = gameSessionHandler.getPlayerBySessionId(session.getId());
        player.Sell(property);
        updateInfoMessage(player);
        gameSessionHandler.log(player.getUsername() + " has sold  " + property.getName() + " for €" + property.getCost()/2 );
    }
   
    /** Waits for a reply from the client. */
    public boolean isReplied(){
        return this.isReplied;
    }
    
    public void setReplied(boolean replied){
        this.isReplied = replied;
    }
    
    /** Sets up the auction, the bidding players and the minimum bid. */
    public void startAuction(){
        //Creating a list of players without the auctioning player
        for (int i = 0; i < gameSessionHandler.getGame().getPlayers().size(); i+= 1){
            if (gameSessionHandler.getGame().getPlayers().get(i) != this.turn.getPlayer()){
                players.add(gameSessionHandler.getGame().getPlayers().get(i));
            }
        }
        iter = players.iterator();
        //Initialising the auction
        auction = new Auction(this.turn);
        //Sets up the minimum bid
        auction.initMinBid();
        gameSessionHandler.log("Now auctioning " + this.tiles.get(this.turn.getPlayer().getPosition()).getName());
        //Begins the auction
        this.playAuction();
    }
    
    /** Runs the auction until a winner is found. */
    public void playAuction(){
   
        //Bidding cycle
        gameSessionHandler.log("Current Bid: " + this.getAuction().getMinBid());
        Player biddingPlayer = new Player();
        Player winningPlayer;
        Property property = (Property) this.tiles.get(this.turn.getPlayer().getPosition());

        if (this.getAuction().getLastBid() < this.getAuction().getMinBid()){
          minBidRaised = true;
        }
        else{
            minBidRaised = false;
        }

        if (players.size() > 1){
            if (iter.hasNext()){
                biddingPlayer = iter.next();
            }
            else{
                //New cycle
                if (minBidRaised == true){
                    iter = players.iterator();
                    biddingPlayer = iter.next();
                }
                //Tiebreak
                else{
                    winningPlayer = getAuction().tiebreak(players);
                    winningPlayer.winAuction( property, this.getAuction().getMinBid());
                    this.endAuction(winningPlayer, property);
                    return;
                }
            }
        }
        else{
            iter = players.iterator();
            winningPlayer = iter.next();
            winningPlayer.winAuction(property, this.getAuction().getMinBid());
            this.endAuction(winningPlayer, property);
            return;
        }   
        JsonProvider provider = JsonProvider.provider();
            JsonObject passAuctionMessage = provider.createObjectBuilder()
                .add("action", "auction")
                .add("currentBid", getAuction().getMinBid())
                .build();
            gameSessionHandler.sendToSession(biddingPlayer.getSession(), passAuctionMessage);
    }
      
    public void endAuction(Player winner, Property property){
        gameSessionHandler.log("Player " + winner.getUsername() + " has bought " + property.getName() + " at auction for " + this.getAuction().getMinBid()  );
        this.setReplied(true);
    }

    public Auction getAuction(){
        return auction;
    }
    
    public Set getBiddingPlayers(){
        return players;
    }
    
    /** Updates the player stats on the client side. */
    public void updateInfoMessage(Player player){
        JsonProvider provider = JsonProvider.provider();
        JsonObject infoMessage = provider.createObjectBuilder()
        .add("action", "updateInfo")
        .add("money", player.getMoney())
        .add("position", player.getPosition())
        .build();
        gameSessionHandler.sendToSession(this.turn.getPlayer().getSession(), infoMessage);
    }
    
    
}
