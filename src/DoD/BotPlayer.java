package DoD;

import DoD.Player;

import java.util.Random;

public class BotPlayer extends Player {

    private char[][] currentViewArea;
    private int turnsToUpdate;
    private boolean lastCMDSuccess;

    private int localX;
    private int localY;

    /**
     * Sets the indentifier to the char 'b'
     */
    public BotPlayer () {
        identifier = 'B';
        turnsToUpdate = 0;
        lastCMDSuccess = true;
        currentViewArea =  new char[5][5];
    }

    /**
     * A function to determine what move the bot should make then translates this into a command
     * @return the command that the bot has generated
     */
    public String getCommand () {
        String command;

        if (!lastCMDSuccess) { // if the last CMD failed then we may be stuck behind a wall, attempt to fix this by moving randomly
            command = randomMove();
        } else if (turnsToUpdate == 0) { // if the view area has not been recently updated, update it by executing look command
            command = "LOOK";
            turnsToUpdate = 4;
        } else if (containsPlayer(currentViewArea)) { // if we can see the player, move towards them
            command = determineMove();
        } else { // if we cant see the player, move randomly until we can
            command = randomMove();
        }
        turnsToUpdate--;

        return command;
    }

    /**
     * A function that generates and returns a random direction
     * @return a random direction from N, E, S, W
     */
    private String randomMove () {
        String direction = "";

        Random r = new Random();

        switch (r.nextInt(4)) { // simply select a random direction by generating a random int up to 3 to represent the 4 directions
            case 0:
                direction = "N";
                break;
            case 1:
                direction = "E";
                break;
            case 2:
                direction = "S";
                break;
            case 3:
                direction = "W";
                break;
        }
        return "MOVE " + direction;
    }


    /**
     * A function to find the direction to move in in order to catch the player
     * @return the direction towards the player out of N, E, S, W
     */
    private String determineMove () {
        int[] playerPos = getPlayerPos(currentViewArea);
        int yDiff = localY - playerPos[0]; // calculate the x and y distances to the player
        int xDiff = localX - playerPos[1];

        String direction = "";

        if (((yDiff*yDiff) > (xDiff * xDiff)) && yDiff != 0) { // select the direction based on whether the player is further away in the y or x direction
            direction = yDiff > 0 ? "N" : "S";
            localY += yDiff > 0 ? 1 : -1; // update our local representations of our self (the bot) so we can properly calculate the next move to make
        } else if (xDiff != 0){
            direction = xDiff > 0 ? "W" : "E";
            localX += xDiff > 0 ? 1 : -1;
        } else {
            turnsToUpdate = 0;
        }
        return "MOVE " + direction;
    }


    /**
     * A function to determine whether a given area contains the human player
     * @param viewArea the area to search
     * @return whether the area contains the player
     */
    private boolean containsPlayer (char[][] viewArea) {
        for (char[] row : viewArea) { // iterate through all tiles and check each for the player
            for (char c : row) {
                if (c == 'P') {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * A function to search the given area and return the position of the human player
     * @param viewArea the area to search
     * @return the position of the player in the format (y, x)
     */
    private int[] getPlayerPos (char[][] viewArea) {
        int[] pos = new int[]{0, 0};

        for (int i = 0; i < viewArea.length; i++) { // iterate through all tiles and check each for the player
            for (int j = 0; j < viewArea[0].length; j++) {
                if (viewArea[i][j] == 'P') {
                    pos = new int[]{i, j};
                    return pos; // when the player is found, get the position and return it
                }
            }
        }
        return pos;
    }

    /**
     * A function to update the view area the bot has stored after it has run the LOOK command
     * @param viewArea The view area of the bot
     */
    public void processResult (char[][] viewArea) {
        localY = ((currentViewArea.length/2));
        localX = ((currentViewArea[0].length/2));
        currentViewArea = viewArea;
    }

    /**
     * A function to process the result of a command and store that result in a variable
     * @param result The result of a command
     */
    public void processResult(String result) {
        switch (result) {
            case "SUCCESS":
                lastCMDSuccess = true;
                break;
            case "FAIL":
                lastCMDSuccess = false;
                break;
        }
    }

}
