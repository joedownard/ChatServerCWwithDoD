import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Extends the base chat client to provide a client which a user can interact with via the console.
 * @author Joe Downard.
 */
public class ChatClient extends ChatBaseClient{

    /**
     * Constructor which passes information to base class constructor to set up values.
     * @param ip IP of the server to connect to
     * @param port Port of the server to connect to
     */
    public ChatClient (String ip, int port) {
        super(ip, port);
    }

    /**
     * Listens for server input via the socket. Prints any messages received onto the console.
     * Listens for user input via the console. Sends any messages received to the server.
     */
    @Override
    public void go () {
        listen();

        try {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            String clientLineIn;
            while ((clientLineIn = userIn.readLine()) != null) {
                sendMessage(clientLineIn, server);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prompts the user at server connection for a username.
     */
    @Override
    public void sendUsername () {
        String username = "/username Guest";
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Connection Established. Enter a username:");
        try {
            username = "/username " + "\""+ userIn.readLine() + "\"";
            sendMessage(username, server);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        new ChatClient(ip, port).go();
    }
}
