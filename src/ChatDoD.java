public class ChatDoD extends ChatBaseClient{

    private RemoteHumanPlayer player;
    private GameLogic game;
    private boolean joined;
    private String playerName;

    /**
     * Constructor which passes information to base class constructor to set up values.
     * @param ip   IP of the server to connect to.
     * @param port Port of the server to connect to.
     */
    public ChatDoD(String ip, int port) {
        super(ip, port);
        joined = false;
    }

    /**
     * Processes input from the server and checks whether it is from the playing user before sending it to the game.
     * @param message The message to process.
     */
    @Override
    public void processChat(String message) {
        String rawMessage = message.substring(message.indexOf(':') + 2);
        String username = message.substring(0, message.indexOf(':'));

        if (!username.equals(playerName) || !joined) {
            if (joined) {
                player.addCommand(rawMessage);
            } else if (rawMessage.equals("JOIN")) {
                joined = true;
                sendMessage("Player " + username + " has been spawned.", server);
                player = new RemoteHumanPlayer(this);
                new Thread(() -> {
                    game = new GameLogic();
                    game.newRemotePlayer(player);
                    game.go();
                }).start();
            }
        }
    }

    /**
     * Take output from the game and send it to the server.
     * @param output Output from the game
     */
    public void gameOutput (String output) {
        sendMessage(output, server);
    }

    /**
     * Provides the username for DoDClient.
     */
    @Override
    public void sendUsername() {
        sendMessage("/username DoDClient", server);
    }

    /**
     * Processes arguments passed at runtime and sets up a new object based on the arguments.
     * @param args The arguments passed at runtime.
     */
    public static void main(String[] args) {
        String ip = "localhost";
        int port = 14001;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-cca")) {
                ip = args[i+1];
            } else if (args[i].equals("-ccp")) {
                port = Integer.parseInt(args[i+1]);
            }
        }

        new ChatDoD(ip, port).go();
    }
}
