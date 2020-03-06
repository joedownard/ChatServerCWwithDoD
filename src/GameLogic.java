import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class GameLogic {

    private Map map;
    private HumanPlayer player;
    private BotPlayer bot;
    private ArrayList<Player> entities;


    private State gameState = State.LOAD;

    enum State {
        LOAD,
        PLAY,
        END
    }

    /**
     * A constructor to initialize the components of the game
     */
    public GameLogic () {
        map = new Map();
        bot = new BotPlayer();
        entities = new ArrayList<>();
        entities.add(bot);
    }

    /**
     * A function to display the available DoD.maps and allow the user to choose a map
     */
    public void selectMap () {
        String selectedMap = "";

        try {
            Stream<Path> fileWalk;
            if (System.getProperty("os.name").equals("Windows")) {
                fileWalk = Files.walk(Paths.get("src/DoD/maps"));
            } else {
                fileWalk = Files.walk(Paths.get("src/DoD/maps"));
            }
            List<String> files = fileWalk.filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList()); // place all the file names within the map directory into a list

            if (files.isEmpty()) { // ensure there are DoD.maps available to play, if not then quit the game
                player.processResult("No DoD.maps in /DoD.maps directory! Add a map to /DoD.maps.");
                player.processResult("Quitting.");
                gameState = State.END;
                return;
            }

            player.processResult("Select a map by its number");
            for (int i = 0; i < files.size(); i++) {
                player.processResult(String.format("[%d] %s", i+1, files.get(i))); // print out all the DoD.maps available
            }

             selectedMap = files.get(Integer.parseInt(player.getCommand()) - 1); // allow the user to select the map by entering its number

        } catch (NoSuchFileException e) { // catch the error if there is no DoD.maps directory and quit the game
            player.processResult("No map directory! Please create a \"DoD.maps\" directory and add a map!");
            player.processResult("Quitting.");
            gameState = State.END;
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        map.readMap(selectedMap);
    }

    /**
     * A function that is called to move an entity in the given direction
     * @param direction A direction from N, E, S, W
     * @param entity An object representing an entity, either a bot or a human player
     * @return Success or failure
     */
    public String move (String direction, Player entity) {
        int newX = entity.getX();
        int newY = entity.getY();
        String result = "";
        switch (direction){ // translate the direction into a change in coordinates
            case "N":
                newY--;
                break;
            case "E":
                newX++;
                break;
            case "S":
                newY++;
                break;
            case "W":
                newX--;
                break;
            default: // if an invalid direction is given the move command fails
                return String.format("FAIL. Incorrect direction \"%s\" specified.", direction);

        }

        if (map.isInBounds(newY, newX)) { // ensure the move will result in the player within the map and not in a wall
            if (map.getTile(newY, newX) != '#') {
                entity.setY(newY);
                entity.setX(newX);
                return "SUCCESS";
            }
        }
        return "FAIL";
    }

    /**
     * A function to process the pickup of gold for a player
     * @return Whether the player successfully picked up or they failed
     */
    public String processPickup () {
        String result;

        if (map.getTile(player) == 'G') { // ensure the player is on a gold tile, if so increment the players gold and remove the gold from the map
            player.incrementGold();
            map.setTile(player, '.');
            result = String.format("SUCCESS. Gold Owned: %d", player.getGold());
        } else {
            result = String.format ("FAIL. Gold Owned: %d", player.getGold());
        }
        return result;
    }

    /**
     * Processes the QUIT command
     * The player wins if they meet the conditions: On a exit tile and sufficient gold
     * Otherwise they lose
     */
    public void processQuit (String reason) {
        if (map.getTile(player) == 'E' && player.getGold() >= map.getGoldRequired()) { // Check the player is on an exit tile and has enough gold to win
            player.processResult("WIN.");
            player.processResult(String.format("You beat %s", map.getMapName()));
        } else {
            player.processResult("LOSE");
            player.processResult(reason);
        }
        gameState = State.END;
    }

    /**
     * A function to process the command for a player, calls the relevant handler for that command
     * @param entity The entity attempting to carry out a command
     * @param cmd The command the entity is attempting to carry out
     */
    public void processCommand (Player entity, String cmd) {

        String[] elements = cmd.split(" ");
        String command = elements[0];
        String parameter = "";
        if (elements.length > 1) {
            parameter = elements[1];
        }

        switch (elements[0].toUpperCase()) { // Call the relevant function dependant on the command given
            case "LOOK":
                entity.processResult(map.getViewArea(entity, entities.toArray(new Player[entities.size()]), 3));
                break;
            case "MOVE":
                entity.processResult(move(parameter.toUpperCase(), entity));
                break;
            case "HELLO":
                entity.processResult(String.format("Gold to win: %d", map.getGoldRequired()));
                break;
            case "PICKUP":
                entity.processResult(processPickup());
                break;
            case "GOLD":
                entity.processResult(String.format("Gold owned: %d", player.getGold()));
                break;
            case "QUIT":
                processQuit("Player quit.");
                break;
            default:
                player.processResult(String.format("Unrecognised command %s", cmd));

        }
    }

    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        game.player = new HumanPlayer();
        game.entities.add(game.player);
        game.go();
    }

    public void newRemotePlayer (HumanPlayer player) {
        this.player = player;
        entities.add(player);
    }

    public void go () {
        while (gameState != State.END) {
            switch (gameState) { // current state determines what to do
                case LOAD:
                    startup();
                    break;
                case PLAY:
                    loop();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * A function to set up objects, ready to begin the game
     */
    public void startup () {
        gameState = State.PLAY;
        selectMap();

        if (gameState == State.PLAY) {
            int[] playerPos = map.getEmptyPos(); // randomly place the player on the map
            player.setX(playerPos[0]);
            player.setY(playerPos[1]);

            int[] botPos;
            do {
                botPos = map.getEmptyPos();
                bot.setX(botPos[0]);
                bot.setY(botPos[1]);
            } while (botPos == playerPos); // randomly place the bot on the map and ensure it isn't placed on the player
        }
    }

    /**
     * The main game loop that is called every time all players have had their turn
     */
    public void loop () {

        if (player.getX() == bot.getX() && player.getY() == bot.getY()) { // check if the bot has captured the player
            processQuit("Caught by bot.");
            return;
        }

        String playerCommand = player.getCommand();
        processCommand(player, playerCommand); // players turn

        String botCommand = bot.getCommand();
        processCommand(bot, botCommand); // bots turn
    }
}
