import java.util.Random;

/**
 * Extends the base client to provide a bot client which automatically responds to messages it receives.
 * @author Joe Downard.
 */
public class ChatBot extends ChatBaseClient{

    private final String [] helloResponses = {"Hi!", "Hey!", "Good day!", "Greetings!"};
    private final String [] genericResponses = {"What?", "Interesting!", "Sounds great.", "cool story"};

    /**
     * Constructor which passes information to base class constructor to set up values.
     * @param ip IP of the server to connect to
     * @param port Port of the server to connect to
     */
    public ChatBot (String ip, int port) {
        super(ip, port);
    }

    /**
     * Automatically sets the bots username as Bot
     */
    @Override
    public void sendUsername() {
        sendMessage("/username Bot", server);
    }

    /**
     * Receives messages from the server and determines whether to reply and what to reply with.
     * @param message The message received from the server.
     */
    @Override
    public void processChat (String message) {
        String processedMessage = message.toLowerCase().substring(message.indexOf(":") + 2);
        System.out.println(processedMessage);

        if (processedMessage.contains("bot")) {
            if (processedMessage.contains("hello") || processedMessage.contains("hi") | processedMessage.contains("hey")) sendMessage(getRandomMessage(helloResponses), server);
            else sendMessage(getRandomMessage(genericResponses), server);
        }
    }

    /**
     * Gets a random message from the provided list of messages.
     * @param possibleMessages A list of possible messages to choose from.
     * @return A random string from the String[] passed to the function.
     */
    public String getRandomMessage (String[] possibleMessages) {
        Random r = new Random();
        return possibleMessages[r.nextInt(possibleMessages.length)];
    }

    /**
     * Processes arguments passed at runtime and sets up a new object based on the arguments.
     * @param args The arguments passed at runtime.
     */
    public static void main(String[] args) {
        String ip = "localhost";
        int port = 14001;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-cca") {
                ip = args[i+1];
            } else if (args[i] == "-ccp") {
                port = Integer.parseInt(args[i+1]);
            }
        }

        new ChatBot(ip, port).go();
    }
}
