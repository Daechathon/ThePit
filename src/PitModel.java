import java.util.ArrayList;
import java.util.List;

/**
 * Robbie Sollie - PitModel.java - EldritchZombieHat - 2019-05-15
 * Runs a basic game of Pit (https://en.wikipedia.org/wiki/Pit_(game))
 */
public class PitModel {
    public static void main(String[] args) {
        List<Player> players = new ArrayList<>();
        //Add players here

        //Fill in remaining slots with basic bots (if desired)
        for (int i = players.size(); i < Commodity.values().length; i++) {
            players.add(new TradeSmallest());
        }

        //initializes the trading manager
        TradingManager trader = new TradingManager(players);

        //gives the players a reference to the trading manager
        for (Player p : players) {
            p.init(trader);
        }

        //starts each player in their own thread
        for (Player p : players) {
            Thread t = new Thread(p);
            t.start();
        }
    }
}
