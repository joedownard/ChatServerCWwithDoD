import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;

public class ChatServer {

    private ArrayList<ClientConnection> connectionList;
    private ServerSocket serverSocket;


    public ChatServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            connectionList = new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void go() {
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    ClientConnection newConnection = new ClientConnection(serverSocket.accept(), this);
                    newConnection.start();
                    connectionList.add(newConnection);
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

    public void broadcastMessage(String message, ClientConnection client) {
        for (ClientConnection c : connectionList) {
            if (!client.equals(c))
                c.sendMessage(message);
        }
    }

    public void directMessage (String message, String receivingUser) {
        for (ClientConnection c : connectionList) {
           if (c.getUsername().equals(receivingUser))
                c.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        int port = 14001;

        for (int i = 0; i < args.length; i++) {
            if (args[i] == "-csp") {
                port = Integer.parseInt(args[i + 1]);
            }
        }

        new ChatServer(port).go();
    }
}
