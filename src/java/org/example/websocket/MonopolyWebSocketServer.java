package org.example.websocket;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.example.game.*;

@ApplicationScoped
@ServerEndpoint("/actions") 
public class MonopolyWebSocketServer {
    
    //this is where all the input from the client to the server will be handled
    
    @Inject
    private GameSessionHandler gameSessionHandler;
    @Inject
    private TurnSessionHandler turnSessionHandler;
     
    @OnOpen
        public void open(Session session) {
            gameSessionHandler.addSession(session);
    
    }

    @OnClose
        public void close(Session session) {
            gameSessionHandler.removeSession(session);
    }

    @OnError
        public void onError(Throwable error) {
            Logger.getLogger(MonopolyWebSocketServer.class.getName()).log(Level.SEVERE, null, error);
    }
        
    


//      any data that is sent to the websocket using socket.send in the js file will be sent to this
//     example:
//     socket.send(JSON.stringify(DeviceAction));

    @OnMessage
        public void handleMessage(String message, Session session) {
            System.out.println(session.getId());
            
           //gameSessionHandler.initiateGame();
//            takes the input from the client and runs the relevant function in the session handler
            try (JsonReader reader = Json.createReader(new StringReader(message))) {
            JsonObject jsonMessage = reader.readObject();
//            the reader is used to turn the message into a json object, it is sent as a JSON string

//            checks the value of the input the client has sent, our options would be buy/sell/auction
//            if a new player joins
            if ("add".equals(jsonMessage.getString("action"))) {
                
                System.out.println("Handle Message called: add");
                Player player = new Player();
                player.setSession(session);
                player.setSessionID(session.getId());
                player.setUsername(jsonMessage.getString("name"));
                gameSessionHandler.addPlayer(player);
            }
            
            if ("start".equals(jsonMessage.getString("action"))) {
                System.out.println("Handle Message called: start");
                gameSessionHandler.playerReady();
            }

            //if the player choses to buy a property
            if ("buy".equals(jsonMessage.getString("action"))) {
                turnSessionHandler.setReplied(true);
                Property property = (Property) turnSessionHandler.getTurn().getBoard().getTiles().get(turnSessionHandler.getTurn().getPlayer().getPosition());
                turnSessionHandler.buyProperty(property);
            }
            
            if ("sell".equals(jsonMessage.getString("action"))) {
                turnSessionHandler.sellProperty(session, jsonMessage.getInt("position"));
            }
            
            if ("remove".equals(jsonMessage.getString("action"))) {
                int id = (int) jsonMessage.getInt("id");
                gameSessionHandler.removePlayer(session,id);
            }
            
            //deals with the players bid
            if ("bid".equals(jsonMessage.getString("action"))) {
                int amount = (int) jsonMessage.getInt("amount");
                turnSessionHandler.getAuction().setMinBid(amount);
                turnSessionHandler.playAuction();
            }
            
            //if the player choses not to continue bidding they are removed from the bidding players
            if ("passAuction".equals(jsonMessage.getString("action"))) {
                Player passingPlayer = gameSessionHandler.getPlayerBySessionId(session.getId());
                turnSessionHandler.getBiddingPlayers().remove(passingPlayer);
                turnSessionHandler.playAuction();
            }
            
            //player chooses auction instead of buying, starting an auction
            if ("pass".equals(jsonMessage.getString("action"))) {
                turnSessionHandler.startAuction();
            }
            
            if ("chat".equals(jsonMessage.getString("action"))) {
                gameSessionHandler.chat(session, jsonMessage.getString("message"));
            }
             
            // player clicks on tile on board to find out information.
            if ("cardInfo".equals(jsonMessage.getString("action"))) {
                int tilePosition = (int) jsonMessage.getInt("position");
                gameSessionHandler.getTileInformation(session, tilePosition);
            }

    }
    }
    
}
