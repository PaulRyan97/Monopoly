package org.example.websocket;


import java.io.IOException;
import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.Set;
import java.util.*;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.spi.JsonProvider;
import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.game.*;

@ApplicationScoped
public class GameSessionHandler {
    
    @Inject
    private TurnSessionHandler turnSessionHandler;
    
//    Each client connected to the application has its own session.
    
    private int playerId = 0;
    private int startingCash = 1500;
    private int minPlayers = 3;
    private int readyPlayers = 0;

    private boolean gameStarted = false;
    private final Set<Session> sessions = new HashSet<>();
    private final Set<Player> players = new HashSet<>();
    
    
    private Game game = new Game();
    private Board board;
    private Bank bank = new Bank();
 

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        game = game;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        board = board;
    }

    public Bank getBank() {
        return bank;
    }


    public void setBank(Bank bank) {
        bank = bank;
    }
    
      public int getReadyPlayers() {
        return readyPlayers;
    }
    
      /** Increments and checks if the number players in the session matches
       the minimum number of players required.*/
    public void playerReady(){
        readyPlayers += 1;
        if (this.getReadyPlayers() == minPlayers){
           this.gameReady(); 
        }
    }

    public void gameReady(){
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                     processGame();
                }
            }, 
            2000 
        );
    }
    
    /** Begins the cycle of starting the turns and calling the main aspects of a turn. */
    public void processGame(){
        board = new Board();
        int playerID;
        Player currentPlayer;
        
        playerID = 0;
        log("--------Game Starting--------");
        log(" ");
        log("-----------------------------");
        log(" ");
        do{
            turnSessionHandler.setReplied(false);
            
            currentPlayer = game.getPlayers().get(playerID);
            String str1 = "Your Turn";
            this.logToSession(currentPlayer.getSession(), str1);
            String str2 = "This is " + currentPlayer.getUsername() + "'s turn---";
            this.logExceptSession(currentPlayer.getSession(), str2);
            
            turnSessionHandler.TurnProcessor(this.bank, this.board, currentPlayer,this);
            turnSessionHandler.turnProcess();
            while(!turnSessionHandler.isReplied()){}
            if(playerID < game.getNumPlayers() - 1){
                playerID += 1;
            }
            else{
                playerID = 0;
            }
            log(" ");
            log("---------------------------");
            log(" ");
            this.getGame().decideWinner();
           
        }while(game.getNumPlayers() != 1);
        log("---GAME OVER---");
    }

    public void addSession(Session session) {
        sessions.add(session);
        for (Player player : players) {
            JsonObject addMessage = createAddMessage(player);
            sendToSession(session, addMessage);
        }
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }
    
     public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
     
    public void testInput(JsonObject message){
        sendToAllConnectedSessions(message);
    }
    
    /** Adds a new player to the game */
    public void addPlayer(Player player) {

        player.setId(playerId);
        player.setMoney(startingCash);

        players.add(player);
        playerId +=1;
        JsonObject addMessage = createAddMessage(player);
        this.sendToSession(player.getSession(), addMessage);
        String logMessage = "Player " + player.getUsername() + " has joined";
        log(logMessage);
        game.addPlayer(player);
        System.out.println(logMessage);
//        }
    }

    public void removePlayer(Session session, int id) {
        Player player = getPlayerById(id);
        if (player != null) {
            if (player == turnSessionHandler.getTurn().getPlayer()){
                turnSessionHandler.setReplied(true);
            }
            players.remove(player);
            game.removePlayer(player);
            JsonProvider provider = JsonProvider.provider();
            JsonObject removeMessage = provider.createObjectBuilder()
                    .add("action", "remove")
                    .add("id", id)
                    .build();
            sendToSession(session, removeMessage);
            String logMessage = "Player " + player.getUsername() + " has left the game.";
            log(logMessage);
            
        }
    }

    public Player getPlayerById(int id) {
        for (Player player: players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }
    
    /** Returns the Player with the specified Session ID */
    public Player getPlayerBySessionId(String id) {
        for (Player player: players) {
            if ((player.getSessionID()).equals(id)) {
                return player;
            }
        }
        return null;
    }
  
    /** Creates a message about a new player joining in the form of a JSON Object.*/
    private JsonObject createAddMessage(Player player) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
            .add("action", "add")
            .add("id", player.getId())
            .add("username", player.getUsername())
            .add("money", player.getMoney())
            .add("position", player.getPosition())
            .build();
        return addMessage;
    }
    
     /** Sends a message to every client that is connected. */
    public void sendToAllConnectedSessions(JsonObject message) {
        for (Session session : sessions) {
           sendToSession(session, message);
        }
    }

    /** Sends a message to one client using their session. */
    public void sendToSession(Session session, JsonObject message) {
        try{
            session.getBasicRemote().sendText(message.toString());
        } 
        catch (IOException ex) {
            sessions.remove(session);
            Logger.getLogger(GameSessionHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Outputs the message a client sends in the chat to the log. */
    public void chat(Session session, String message){
        Player player = getPlayerBySessionId(session.getId());
        String playerMessage = player.getUsername() + ": " + message;
        log(playerMessage);
    }
    
    /** Gets information regarding a tile when the user clicks on the imagemap.. */
    public void getTileInformation(Session session, int tilePosition){
        Tiles tile = this.board.getTiles().get(tilePosition);

        String owner = "";
        int rent = 0;
        int cost = 0;
        JsonProvider provider = JsonProvider.provider();
        JsonObject cardInfoMessage;
        if (tile.getType() == 1){
            Property property = (Property) tile;
            if (property.getOwner() != null)
                owner = property.getOwner().getUsername();
            else
                owner = "None";
            cardInfoMessage = provider.createObjectBuilder()
            .add("action", "sendPropertyTileInfo")
            .add("name", tile.getName())
            .add("owner", owner)
            .add("rent", property.getRent())
            .add("cost", property.getCost())
            .build();
        }
        else{
            cardInfoMessage = provider.createObjectBuilder()
            .add("action", "sendTileInfo")
            .add("name", tile.getName())
            .build();
        }
        sendToSession(session, cardInfoMessage);
        
    }
    
    /** Outputs a message in the log to all players. */
    public void log(String logMessage) {
        JsonObject addMessage = createLogMessage(logMessage);
        sendToAllConnectedSessions(addMessage);
//        }
    }
    
     /** Outputs a message in the log to one player. */
    public void logToSession(Session session, String logMessage){
        JsonObject addMessage = createLogMessage(logMessage);
        this.sendToSession(session, addMessage);
    }
    
    /** Outputs a message in the log to all players except the one specified. */
    public void logExceptSession(Session session, String logMessage){
        JsonObject addMessage = createLogMessage(logMessage);
        for (Session session1 : sessions) {
           if(session1 != session)
               this.sendToSession(session1, addMessage);
        }
    }
    
    /** Creates a log message in the form of a JSON Object. */
    private JsonObject createLogMessage(String logMessage) {
        JsonProvider provider = JsonProvider.provider();
        JsonObject addMessage = provider.createObjectBuilder()
            .add("action", "log")
            .add("message", logMessage)
            .build();
        return addMessage;
    }
    
}