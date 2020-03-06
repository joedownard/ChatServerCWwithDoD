import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Chat server main class.
 * @author Joe Downard.
 */
public class ChatServer {

    private ArrayList<ClientConnection> connectionList;
    private ServerSocket serverSocket;

    /**
     * Constructor to open a server socket on the port provided and to initialise the connection list array.
     * @param port  The port for the server to start on.
     */
    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            connectionList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main function of the server.
     * Creates a thread which listens for and handles new client connections.
     * Reads console input and listens for the keyword to exit.
     */
    public void go() {
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    ClientConnection newConnection = new ClientConnection(serverSocket.accept(), this); // a new thread is created to handle each connection
                    newConnection.start();
                    connectionList.add(newConnection); // a list of current connections is kept
                    System.out.println("new connection");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            BufferedReader adminIn = new BufferedReader(new InputStreamReader(System.in));
            String adminLineIn;
            while ((adminLineIn = adminIn.readLine()) != null) {
                if (adminLineIn.equals("EXIT")) {
                    System.out.println("Exiting.");
                    serverSocket.close();
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the provided message to all the connected clients except the client who sent it.
     * @param message The message to be broadcast to all clients.
     * @param client The client that sent the message to the server.
     */
    public void broadcastMessage(String message, ClientConnection client) {
        if (client != null) {
            for (ClientConnection c : connectionList) {
                if (!client.equals(c))
                    c.sendMessage(message);
            }
        }
    }

    /**
     * Sends the provided message to the designated user only. Sends the sender an error message if the direct message cannot be delivered.
     * @param message The message to be sent to the receiver.
     * @param receivingUser The user that is to receive the message.
     */
    public void directMessage (String message, String receivingUser, ClientConnection client) {
        boolean userFound = false;
        for (ClientConnection c : connectionList) {
           if (c.getUsername().equals(receivingUser))
               userFound = true;
                c.sendMessage(message);
        }

        if (!userFound) {
            client.sendMessage("User not found! Correct format: /tell <username> <message>");
        }
    }

    /**
     * Receives a message from a client and determines what to do with it
     * @param message The message received from the client
     * @param client The client who sent the message
     */
    public void processChat (String message, ClientConnection client) {
        if (message.startsWith("/")) {
            processCommand(message, client);
        } else {
            broadcastMessage(client.getUsername() + ": " + message, client);
        }
    }

    /**
     * Processes a chat marked as being a command. Such as setting a username and direct messaging a user.
     * @param command The command to be processed.
     * @param client The client that sent the command.
     */
    public void processCommand (String command, ClientConnection client) {
        ArrayList<String> elements = splitCommand(command);

        if (elements.size() >= 2) {
            switch (elements.get(0)) {
                case "/username":
                    String newUsername = elements.get(1);
                    broadcastMessage(client.getUsername() + "has set their username to " + newUsername + ".", null);
                    client.setUsername(newUsername);
                    break;
                case "/tell":
                    String username = elements.get(1);
                    elements.remove(1);
                    elements.remove(0);
                    String message = client.getUsername() + ": " + String.join(" ", elements);
                    directMessage(message, username, client);
                default:
                    break;
            }
        }
    }

    /**
     * Helper function to split a command into elements for furter processing.
     * @param command The command to split.
     * @return The elements of the command.
     */
    public ArrayList<String> splitCommand (String command) {
        ArrayList<String> elements = new ArrayList<>();
        String tempElement = "";
        boolean ignoreSpace = false;

        for (char c : command.toCharArray()) {
            if (c == '"') {
                ignoreSpace = !ignoreSpace;
            } else if (c == ' ' && !ignoreSpace) {
                elements.add(tempElement);
                tempElement = "";
            } else {
                tempElement += c;
            }
        }
        elements.add(tempElement);
        return elements;
    }

    /**
     * Processes arguments passed at runtime and sets up a new object based on the arguments.
     * @param args The arguments passed at runtime.
     */
    public static void main(String[] args) {
        int port = 14001; // default port

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-csp") {
                port = Integer.parseInt(args[i + 1]);
            }
        }

        new ChatServer(port).go();
    }
}
