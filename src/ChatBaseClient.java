import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Abstract class that all chat clients should be based on. Provides basic functions which can be overwritten to change functionality.
 * @author Joe Downard.
 */
public abstract class ChatBaseClient {

    protected Socket server;

    /**
     * Sets up the socket connection and sends the clients username.
     * @param ip IP of the server to connect to.
     * @param port Port of the server to connect to.
     */
    public ChatBaseClient (String ip, int port) {
        try {
            server = new Socket(ip, port);
            sendUsername(); // send the server the username of the client
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the server via the socket.
     * @param message The message to be sent.
     * @param receiver The socket to send the message to.
     */
    public void sendMessage (String message, Socket receiver) {
        try {
            PrintWriter serverOut = new PrintWriter(receiver.getOutputStream(), true);
            serverOut.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Simply allows the object to listen to the server and does not allow for more input from the object.
     */
    public void go () {
        listen();
    }

    /**
     * Sets up a thread to listen to messages from the server. Passes them to be processed by the object.
     */
    public void listen () {
        new Thread(() -> {
            try {
                BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

                String serverLineIn;
                while ((serverLineIn = serverIn.readLine()) != null) {
                    processChat(serverLineIn);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    server.close(); // ensure the socket is closed once we are done
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Prints the message out to console.
     * @param message The message to print.
     */
    public void processChat (String message) {
        System.out.println(message); // print out messages received
    }

    /**
     * All clients must provide their username on connection.
     */
    public abstract void sendUsername ();

}
