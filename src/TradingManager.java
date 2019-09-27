import java.util.*;

/**
 * Robbie Sollie - TradingManager.java - EldritchZombieHat - 2019-05-15
 * Manages the hands of cards and trades between players
 */
public class TradingManager {
    private Map<String, Integer> tradingBoard; //A map of Players (toString) to the number of cards they are currently trading
    private Map<Player, int[]> hands; //A map of players to their hand of cards
    private Map<String, Player> playerNames; //A map reversing a player's toString, mapping String to Object
    private boolean active; //states whether the game is currently active
    private static final int sleepyTime = 0; //how long (milliseconds) a Thread should wait when making or posting a trade

    /**
     * Initializes the manager with empty trades HashMap
     * and deals out the cards to each of the players
     * @param players a list of players in the game's initial state
     */
    public TradingManager(List<Player> players) {
        tradingBoard = new HashMap<>();
        hands = new HashMap<>();
        playerNames = new HashMap<>();
        active = false;

        //creates a deck of cards, with a number of commodities equal to the number of players, nine of each
        int[] deck = new int[players.size()];
        for (int i = 0; i < deck.length; i++) {
            deck[i] = 9;
        }

        //deals out the cards to each player
        Random r = new Random();
        for (Player p : players) {
            int[] hand = new int[Commodity.values().length];
            for (int i = 0; i < 9; i++) {
                int card;

                //if there are no more cards of that type, grab a different card
                do {
                    card = r.nextInt(deck.length);
                } while (deck[card] <= 0);
                //remove card from deck and place in hand
                deck[card]--;
                hand[card]++;
            }
            //record the player's hand and name
            hands.put(p, hand);
            playerNames.put(p.toString(), p);
        }

        //Print out all the players and their hands
        for (Player p : hands.keySet()) {
            System.out.println(p + ": " + Arrays.toString(hands.get(p)));
        }
        active = true;
    }

    /**
     * adds a new trade to the trading board
     * @param count the number of commodities the player is trading
     * @param p the player trading
     */
    public void postTrade(int count, Player p) {
        //Slows down thread by the sleepyTime constant
        try {
            Thread.sleep(sleepyTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //If the game isn't currently running, don't allow trades
        if (!active) {
            return;
        }

        synchronized (TradingManager.class) {
            //synchronize to prevent key from being removed between if conditions
            if (tradingBoard.containsKey(p.toString()) && tradingBoard.get(p.toString()) == count) {
                return;
            }
            tradingBoard.put(p.toString(), count);
            System.out.println(p + " is trading " + count);
        }
    }

    /**
     * gets all the trades that have currently been posted
     * @return a clone of the tradingBoard Map
     */
    public Map<String, Integer> getTrades() {
        try {
            return new HashMap<>(tradingBoard);
        }
        //If another thread updates the trading board while cloning, just try again
        catch (ConcurrentModificationException e) {
            return getTrades();
        }
    }

    /**
     * one player requests a trade with another
     * @param sender the player making the request
     * @param receiverName the string name of the player receiving the trade
     * @param sent the commodity the sending player is trading
     * @param quantity the number of the sent commodity the sender is trading
     */
    public void trade(Player sender, String receiverName, Commodity sent, int quantity) {
        //Slows down thread by the sleepyTime constant
        try {
            Thread.sleep(sleepyTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //If the game isn't currently running, don't allow trades
        if (!active) {
            return;
        }

        //Thread-safe, verifies that the recipient has a posted trade of the desired quantity
        if (!tradingBoard.containsKey(receiverName)) {
            synchronized (TradingManager.class) {
                if (!tradingBoard.containsKey(receiverName) || quantity != tradingBoard.get(receiverName)) {
                    return;
                }
            }
        }

        int sentIndex = sent.ordinal();

        if (hands.get(sender)[sentIndex] >= quantity) { //verify that sender has required quantity
            Player receiver = playerNames.get(receiverName); //convert string name to object reference


            //synchronize to prevent double-trading
            synchronized (TradingManager.class) {

                if (receiver.receiveTrade(sender.toString(), quantity)) { //requests the receiver to approve the trade
                    int receivedIndex = receiver.getTrading().ordinal(); //get the commodity the receiver is trading

                    //Final check for valid quantities before making trade
                    if (hands.get(receiver)[receivedIndex] >= quantity && hands.get(sender)[sentIndex] >= quantity) {
                        hands.get(receiver)[receivedIndex] -= quantity;
                        hands.get(receiver)[sentIndex] += quantity;
                        hands.get(sender)[receivedIndex] += quantity;
                        hands.get(sender)[sentIndex] -= quantity;
                    } else {
                        return;
                    }

                    //Remove any posted trades by the participating traders
                    tradingBoard.remove(sender.toString());
                    tradingBoard.remove(receiverName);

                    //Print out the results of the trade
                    System.out.println(sender + " traded with " + receiver + " " + quantity + " " + sent + " for " + Commodity.values()[receivedIndex]);
                    System.out.println(sender + ": " + Arrays.toString(hands.get(sender)));
                    System.out.println(receiver + ": " + Arrays.toString(hands.get(receiver)));
                }
            }
        }

    }

    /**
     * gets the hand of a player
     * @param p the requested player
     * @return the hand of the player as an int array
     */
    public int[] getHand(Player p) {
        return Arrays.copyOf(hands.get(p), hands.get(p).length);
    }

    /**
     * rings the bell to announce that a player has won the game
     * synchronized so that whoever rings the bell first has won
     * @param p the player ringing the bell
     */
    public synchronized void ringBell(Player p) {
        if (!active) {
            return;
        }
        int[] hand = hands.get(p);
        //check to see if the player actually has a full hand of one commodity
        for (int commodity : hand) {
            if (commodity == 9) {
                System.out.println(p.toString() + " has won: " + p.getClass());
                endRound();
            }
        }

    }

    /**
     * Sets the game active state to false and prints out the final results
     */
    private void endRound() {
        active = false;
        for (Player p : hands.keySet()) {
            System.out.println(p + ", " + p.getClass() + ": " + Arrays.toString(hands.get(p)));
        }
    }

    /**
     * @return is the game active?
     */
    public boolean isActive() {
        return active;
    }
}
