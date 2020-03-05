import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles the connection with a client.
 * @author Joe Downard.
 */
public class ClientConnection extends Thread {

    private Socket clientSocket;
    private ChatServer server;
    private String username;

    /**
     * Constructor to set values
     * @param clientSocket The socket created to connect with the client
     * @param server The main chat server object, to allow individual clients to interact with the server.
     */
    public ClientConnection (Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.username = "Guest";
    }

    /**
     * Listens for input sent to the server from the client.
     * Sends every message
     */
    @Override
    public void run() {
        try {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String userLineIn;
            while ((userLineIn = userIn.readLine()) != null) {
                System.out.println(username + ": " + userLineIn);
                server.processChat(userLineIn, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the client
     * @param message
     */
    public void sendMessage (String message) {
        try {
            PrintWriter userOut = new PrintWriter(clientSocket.getOutputStream(), true);
            userOut.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter function to set username
     * @param username  The username of the client
     */
    public void setUsername (String username) {
        this.username = username;
    }

    /**
     * Gets the username of the client
     * @return The username of the client
     */
    public String getUsername () {
        return username;
    }
}
