import java.util.Scanner;

public class HumanPlayer extends Player {

    private int gold;
    Scanner in;

    /**
     * Constructs the object and gives it the P identifier
     */
    public HumanPlayer () {
        in = new Scanner(System.in);
        identifier = 'P';
    }

    /**
     * A function to get the amount of gold a player has
     * @return The gold a player has
     */
    public int getGold() {
        return gold;
    }

    /**
     * A function to increment the players gold by one
     */
    public void incrementGold() {
        gold++;
    }

    /**
     * A function to return the command the players wants to run by getting input from the command line
     * @return The player's command
     */
    public String getCommand () {
        return in.nextLine(); // allow the player to input their next command
    }

    /**
     * A function to print out the result of processing a command
     * @param result The result of processing a command
     */
    public void processResult (String result) {
        if (result != null) {
            System.out.print(result); // output the result of the last command to the player
        }
    }

    /**
     * A function to print out the view area after the players runs the LOOK command
     * @param viewArea The view area of the player
     */
    public void processResult (char[][] viewArea) {
        for (char[] row : viewArea) { // print the view area if the user requested it
            for (char tile : row) {
                System.out.print(tile + " ");
            }
            System.out.println();
        }
    }
}
