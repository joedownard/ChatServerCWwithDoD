import java.util.ArrayList;

public class RemoteHumanPlayer extends HumanPlayer {
    private ArrayList<String> commandQueue;
    private ChatDoD doDClient;

    /**
     * Sets up the field values.
     * @param doDClient The DoDClient object.
     */
    public RemoteHumanPlayer (ChatDoD doDClient) {
        commandQueue = new ArrayList<>();
        this.doDClient = doDClient;
    }

    /**
     * Adds a command to the command queue.
     * @param command The command to add.
     */
    public void addCommand (String command) {
        System.out.println("USER: " + command);
        commandQueue.add(command);
    }

    /**
     * Gets the oldest command that needs to be dealt with.
     * @return The oldest command.
     */
    @Override
    public String getCommand () {
        while (commandQueue.isEmpty()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return commandQueue.remove(0); // allow the player to input their next command
    }

    /**
     * Sends the result of a command back to the player.
     * @param result The result of processing a command.
     */
    @Override
    public void processResult (String result) {
        System.out.println(result);
        if (result != null) {
            doDClient.gameOutput(result); // output the result of the last command to the player
        }
    }

    /**
     * Sends the map to the player.
     * @param viewArea The view area of the player.
     */
    @Override
    public void processResult (char[][] viewArea) {
        StringBuilder tempRow = new StringBuilder();
        for (char[] row : viewArea) { // print the view area if the user requested it
            for (char tile : row) {
                tempRow.append(tile).append(" ");
            }
            doDClient.gameOutput(tempRow.toString());
            tempRow = new StringBuilder();
        }
    }
}
