package org.example.game;

import java.util.Scanner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

/** Handles everything to do with the a property auction. */
public class Auction{
  private Player auctioningPlayer;
  private Player biddingPlayer;
  private List<Tiles> tiles;
  private Player winningPlayer;
  private int lastBid;

  private int minBid;

  public Auction()
  {
    //Intentionally left blank
  }

  public Auction(Turn turn)
  {
    this.auctioningPlayer = turn.getPlayer();
    this.tiles = turn.getBoard().getTiles();
  }

  public void initMinBid()
  {
    //Setting the minimum bid
  	Property property;
  	property = (Property) this.tiles.get(this.auctioningPlayer.getPosition());
        minBid = property.getCost();
  }

  /** Runs a series of dice rolls to determine the winner of the auction in the case of a tiebreak. */
  public Player tiebreak(Set players)
  {
    //Tiebreak
    int[] dice = new int[2];
    int playerRoll = 0;
    int highestRoll = 0;

    HashMap<Object, Integer> rolls = new HashMap<Object, Integer>();
    Iterator iteratorZero = players.iterator();
    while(iteratorZero.hasNext())
    {
      rolls.put(iteratorZero.next(), 0);
    }

    //Gets values from rolls
    Iterator iter = rolls.entrySet().iterator();
    while(rolls.size() > 1)
    {
        System.out.println("in main loop");
      while(iter.hasNext())
      {
        Map.Entry entry = (Map.Entry) iter.next();
        dice = ((Player) entry.getKey()).Roll();
        playerRoll = dice[0] + dice[1];
        rolls.put(entry.getKey(), playerRoll);
        Player player = (Player) entry.getKey();
        System.out.println(player.getUsername() + " has rolled " + playerRoll);
        if(playerRoll > highestRoll)
        {
          highestRoll = playerRoll;
        }
      }

      //Removes low bids
      iter = rolls.entrySet().iterator();
      while(iter.hasNext())
      {
        Map.Entry entry2 = (Map.Entry) iter.next();
        if((int) entry2.getValue() == highestRoll)
        {
          Player player = (Player) entry2.getKey();
          System.out.println(player.getUsername() + " has rolled " + playerRoll + "is == highest");
          rolls.put(entry2.getKey(), 0);
        }
        else
        {
          rolls.remove(entry2.getKey());
        }
      }
    }

    //Gets winner
    Iterator iter2 = rolls.entrySet().iterator();
    while(iter2.hasNext())
    {
       System.out.println("getting winner");
      Map.Entry entry3 = (Map.Entry) iter2.next();
      winningPlayer = (Player) entry3.getKey();
    }
  //Returns the player that has won
  return winningPlayer;
  }

  public int getMinBid()
  {
    return minBid;
  }

  public void setMinBid(int minBid)
  {
    lastBid = this.getMinBid();
    this.minBid = minBid;
  }

  public int getLastBid()
  {
    return lastBid;
  }
}
