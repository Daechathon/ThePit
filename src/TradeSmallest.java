import java.util.Map;

/**
 * Robbie Sollie - TradeSmallest.java - EldritchZombieHat - 2019-06-03
 */
public class TradeSmallest extends Player {
    @Override
    public boolean receiveTrade(String player, int count) {
        return true;
    }

    @Override
    public void run() {
        while (true) {
            if (!isActive()) {
                continue;
            }
            int[] hand = getHand();
            int min = 0;
            for (int i = 0; i < hand.length; i++) {
                if (hand[i] < hand[min] && hand[i] > 0 || hand[min] == 0) {
                    min = i;
                }
            }
            if (hand[min] == 9) {
                ringBell();
                break;
            }
            else {
                Map<String, Integer> tradingBoard = getTrades();
                for (String s : tradingBoard.keySet()) {
                    if (tradingBoard.get(s) == hand[min]) {
                        sendTrade(s, Commodity.values()[min], hand[min]);
                        continue;
                    }
                }
                postTrade(Commodity.values()[min], hand[min]);
            }
        }
    }
}
