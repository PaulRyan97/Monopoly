package org.example.game;


import java.io.File;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileInputStream.*;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.io.*;
import java.util.*;

/** Handles the creation of the game board. */
public class Board {
	private List<Property> properties;
	private int numTiles = 40;
	private static List<Tiles> tiles = new LinkedList<Tiles>();

	private static LinkedList<Card> commucardslinked = new LinkedList<Card>();
	private static LinkedList<Card> chancecardslinked = new LinkedList<Card>();
	
	public Board(){
        for(int i=0;i<40;i++){
            tiles.add(new Property());
        }
            
        readJSON();
       

        readJSONCommunCards();
        // shuffles Community Chest Cards.
        Collections.shuffle(commucardslinked);
            
        readJSONChanceCards();
        // shuffles Chance cards
        Collections.shuffle(chancecardslinked);
            
		Go go = new Go();
		go.setID(0);
		go.setName("Go");
		go.setPosition(0);
		go.setType(2);
		tiles.set(0,go);
		
		Jail jail = new Jail();
		jail.setID(10);
		jail.setName("Jail");
		jail.setPosition(10);
		jail.setType(3);
		tiles.set(10, jail);
		
		FreeParking freeParking = new FreeParking();
		freeParking.setID(20);
		freeParking.setName("Free Parking");
		freeParking.setPosition(20);
		freeParking.setType(4);
		tiles.set(20, freeParking);
		
		GoToJail goToJail = new GoToJail();
		goToJail.setID(30);
		goToJail.setName("Go To Jail");
		goToJail.setPosition(30);
		goToJail.setType(5);
		tiles.set(30, goToJail);
		
		Tax incomeTax = new Tax();
		incomeTax.setID(4);
		incomeTax.setName("Income Tax");
		incomeTax.setPosition(4);
		incomeTax.setAmount(200);
		incomeTax.setType(6);
                  tiles.set(4, incomeTax);
		
		Tax luxuryTax = new Tax();
		luxuryTax.setID(38);
		luxuryTax.setName("Luxury Tax");
		luxuryTax.setPosition(38);
		luxuryTax.setAmount(100);
		luxuryTax.setType(6);
                tiles.set(38, luxuryTax);
		
		int[] communityChestPositions = {2, 17, 33};
		for(int i = 0; i < communityChestPositions.length; i++){
			Card communityChest = new Card();
			communityChest.setID(communityChestPositions[i]);
			communityChest.setName("Pick up Community Chest card");
			communityChest.setPosition(communityChestPositions[i]);
                         communityChest.setType(7);
			tiles.set(communityChestPositions[i], communityChest);
		}

		int[] chancePositions = {7, 22, 36};
		for(int i = 0; i < communityChestPositions.length; i++){
			Card chance = new Card();
			chance.setID(chancePositions[i]);
			chance.setName("Pick up Chance card");
			chance.setPosition(chancePositions[i]);
			chance.setType(8);
			tiles.set(chancePositions[i], chance);
		}
	}
	

    /** Reads in the property information from a JSON file and creates the property objects. */
    public static void readJSON() {
    	JSONParser parser = new JSONParser();
    	try{
            Object obj = parser.parse(new FileReader("http://cs1.ucc.ie/~pwr1/Properties.json"));

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray properties1 = (JSONArray) jsonObject.get("properties");


            for (Object property : properties1){
    //               
                Property newProperty = new Property();
                JSONObject jsonProperty = (JSONObject) property;

                String propName = (String) jsonProperty.get("Name");
                newProperty.setName(propName);

                String pID = jsonProperty.get("PropertyID").toString();
                newProperty.setID(Integer.parseInt(pID));
                String pos = jsonProperty.get("Position").toString();
                newProperty.setPosition(Integer.parseInt(pos));


                String cost = jsonProperty.get("Cost").toString();
                newProperty.setCost(Integer.parseInt(cost));
                String rent = jsonProperty.get("Rent").toString();
                newProperty.setRent(Integer.parseInt(rent));
                newProperty.setType(1);

               tiles.set(newProperty.getPosition(), newProperty);
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
           e.printStackTrace();
        }
        catch (ParseException e) {
           e.printStackTrace();
        }
    }
	
    /** Reads in the Chance cards attributes from a JSON file,creates the Card objects and adds them to the Chance card deck. */
    public static void readJSONChanceCards(){
	JSONParser parser = new JSONParser();
	try{
            Object obj = parser.parse(new FileReader("http://cs1.ucc.ie/~pwr1/ChanceCards.json"));
                
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray chancecardsarray = (JSONArray) jsonObject.get("cards");
                
            for (Object card : chancecardsarray){ 

                Card newCard = new Card();
                JSONObject jsonCard = (JSONObject) card;

                String cardDescription = (String) jsonCard.get("Description");

                newCard.setDescription(cardDescription);

                String cardAction = (String) jsonCard.get("Action");
                newCard.setAction(cardAction);

                String value = jsonCard.get("Value").toString();
                newCard.setValue(Integer.parseInt(value));

                chancecardslinked.addLast(newCard);

            }
        }
                

		catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException q) {
           q.printStackTrace();
        }
        catch (ParseException r) {
           r.printStackTrace();
        }
	}
	
        /** Reads in the Community Chest cards attributes from a JSON file,
         * creates the Card objects and adds them to the Community Chest card deck. */
	public static void readJSONCommunCards(){
		JSONParser parser = new JSONParser();
		try{
            Object obj = parser.parse(new FileReader("http://cs1.ucc.ie/~pwr1/CommunityChestCards.json"));

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray communcardsarray = (JSONArray) jsonObject.get("cards");

            for (Object card : communcardsarray){
                Card newCard = new Card();
                JSONObject jsonCard = (JSONObject) card;
                String cardDescription = (String) jsonCard.get("Description");
                newCard.setDescription(cardDescription);

                String cardAction = (String) jsonCard.get("Action");
                newCard.setAction(cardAction);

                String value = jsonCard.get("Value").toString();
                newCard.setValue(Integer.parseInt(value));

                commucardslinked.addLast(newCard);
            }
        }
		catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        catch (IOException e) {
           e.printStackTrace();
        }
        catch (ParseException e) {
           e.printStackTrace();
        }
	}

	
	

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public int getNumTiles() {
		return numTiles;
	}

	public void setNumTiles(int numTiles) {
		this.numTiles = numTiles;
	}


	public List<Tiles> getTiles() {
		return tiles;
	}


	public void setTiles(List<Tiles> tiles) {
		this.tiles = tiles;
	}


	public LinkedList<Card> getCommucardslinked() {
		return commucardslinked;
	}


	public void setCommucardslinked(LinkedList<Card> commucardslinked) {
		this.commucardslinked = commucardslinked;
	}


	public LinkedList<Card> getChancecardslinked() {
		return chancecardslinked;
	}


	public void setChancecardslinked(LinkedList<Card> chancecardslinked) {
		this.chancecardslinked = chancecardslinked;
	}
	
	
	
}
