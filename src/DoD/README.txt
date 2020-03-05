In order to run the game follow these instructions:
1. Ensure all the .java files and the DoD.maps directory are all within the same directory
2. Open a terminal
3. Navigate to the directory containing the .java files and the DoD.maps directory
4. Perform this command: javac *.java
5. Perform this command: java GameLogic

This process should start the game as long as there is a DoD.maps directory and it contains at least one map.

The dungeons of doom game is implemented through the use of three main classes:
GameLogic
Player
Map
with the Player class having the subclasses: HumanPlayer and DoD.BotPlayer

A map object holds the map data type and various functions to manipulate the map.
It provides functions to:
read a map into itself from a file.
get an empty position on the current map.
get a view area for a player.
check if a tile is in the bounds of the map.
get and set various variables.

A player object has some generic getter and setter functions for variables which apply to both HumanPlayer and DoD.BotPlayer
It also provides two abstract functions for DoD.BotPlayer and HumanPlayer to implement.
The HumanPlayer processes results by printing them to the console for the player to see.
Whereas the DoD.BotPlayer processes results by saving them to variables for use in later decisions.

The DoD.BotPlayer object focuses more on functions that helps it to decide what move to make in order to catch the player.
To do this it has functions to:
check if the player is within its current view area.
get the players position within its current view area.
determine whether it should move N, E, S, W based on the players position relative to the bot.
make a random move if it can't see the player.
decide whether to update its view area or to move towards the player.

The HumanPlayer does not need as many functions as the DoD.BotPlayer as the decision making is left to the player.
Therefore the only extra functions it has are ones to:
get input from the human player and pass it to be processed.
implement the use of gold, an incrementer and a getter.

The GameLogic object links all of the other objects together.
In general it takes input from both
