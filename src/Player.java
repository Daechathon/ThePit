import java.util.Map;

/**
 * Robbie Sollie - Player.java - EldritchZombieHat - 2019-05-15
 * Defines all interfaces for a player's bot strategy
 */
public abstract class Player implements Runnable {
    private static int idCounter = 0;
    private int id;
    private TradingManager trader;
    private Commodity trading;

    /**
     * Initializes a new player with a new id
     */
    public Player() {
        id = idCounter++;
    }

    /**
     * @return the Commodity being traded by this player
     */
    public final Commodity getTrading() {
        return trading;
    }

    /**
     * sets the TradingManager for the player to interact with (called after constructor)
     * @param t the trading manager for the game
     */
    public final void init(TradingManager t) {
        trader = t;
    }

    /**
     * @return a String representation of the player
     */
    public final String toString() {
        return "Trader" + id;
    }



    /**
     * User-defined method, determines behavior when a trade is offered to the player
     * @param player the player requesting a trade
     * @param count the quantity being requested to trade
     * @return a boolean value stating whether the user has accepted the trade
     */
    public abstract boolean receiveTrade(String player, int count);

    /**
     * Called by the user when they want to post a trade
     * @param c the commodity the user would like to trade
     * @param quantity the amount the user would like to trade
     */
    public final void postTrade(Commodity c, int quantity) {
        if (!quantityIsValid(c, quantity)) { //check that the user actually has that quantity of the commodity
            return;
        }
        trading = c;
        trader.postTrade(quantity, this); //posts the trade to the trading manager
    }

    /**
     * Called by the user when they would like to trade with another player
     * @param name the name of the player the user is sending the trade to
     * @param c the commodity the user is trading
     * @param quantity the quantity of the sent commodity the user is trading
     */
    public final void sendTrade(String name, Commodity c, int quantity) {
        if (!quantityIsValid(c, quantity)) { //check that the user actually has that quantity of the commodity
            return;
        }
        postTrade(c, quantity);//posts the trade before sending, so it is visible to all users
        trader.trade(this, name, c, quantity); //sends the trade through the trading manager
    }

    /**
     * Gets all currently posted trades from the trading manager
     * @return a map of player names to quantity of commodities being traded
     *          no player should ever have a reference to another player
     */
    public final Map<String, Integer> getTrades() {
        Map<String, Integer> tradingBoard = trader.getTrades();
        tradingBoard.remove(this.toString()); //do not include this player in the map
        return tradingBoard;
    }

    /**
     * check if the player actually has at least the specified quantity of this commodity
     * @param c the commodity specified
     * @param quantity the quantity specified
     * @return a boolean stating that the player has enough of the specified commodity
     */
    public final boolean quantityIsValid(Commodity c, int quantity) {
        return trader.getHand(this)[c.ordinal()] >= quantity;
    }

    /**
     * gets the player's hand
     * @return an int array defining the player's hand
     *          each index corresponds to a Commodity's ordinal value
     *          with the value at the index corresponding to the quantity
     *          of that commodity owned by the player
     */
    public final int[] getHand() {
        return trader.getHand(this);
    }

    /**
     * User rings the bell to signal that they have collected nine of a kind
     * @return whether or not the player actually won the game
     */
    public final boolean ringBell() {
        int[] hand = getHand();
        for (int commodity : hand) {
            if (commodity == 9) {
                trader.ringBell(this);
                return true;
            }
        }
        return false;
    }

    /**
     * @return is the game currently active
     */
    public boolean isActive() {
        return trader.isActive();
    }
}
