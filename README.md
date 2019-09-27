# ThePit
This program models a simple game of Pit (https://en.wikipedia.org/wiki/Pit_(game))
played by human-made bots (TradeSmallest.java is included as an example).

Bots are java classes which inherit from the Runnable abstract class Player
The Player class handles interaction between Bots and the TradingManger, which controls
inter-player interactions and the players' hands.

To create a bot, simply extend the Player class, and implement these methods:
* run() from Runnable - defines main bot behavior, should run for the length of the game
this method makes calls to the Player class to make trades with other players
* trade() from Player - called whenever another player would like to trade with this
player, return true to accept the trade, false to reject

The Player class contains all methods called by user bot to be used for gameplay
the most significant methods being:
* getHand() - gets this player's hand of cards
* postTrade() - signal that this player is wanting to trade
* trade() - initiate a trade with another player
