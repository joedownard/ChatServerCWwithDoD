import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientConnection extends Thread {

    private Socket clientSocket;
    private ChatServer server;
    private String username;

    public ClientConnection (Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.username = "Guest";
    }

    @Override
    public void run() {
        try {
            //PrintWriter userOut = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader userIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String userLineIn;
            while ((userLineIn = userIn.readLine()) != null) {
                System.out.println(username + ": " + userLineIn);
                if (userLineIn.startsWith("/")) {
                    processCommand(userLineIn);
                } else {
                    System.out.println("broadcasting msg");
                    server.broadcastMessage(username + ": " + userLineIn, this);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processCommand (String command) {
        ArrayList<String> elements = splitCommand(command);

        if (elements.size() >= 2) {
            switch (elements.get(0)) {
                case "/username":
                    username = elements.get(1);
                    break;
                case "/tell":
                    String username = elements.get(1);
                    elements.remove(1);
                    elements.remove(0);
                    String message = getUsername() + ": " + String.join(" ", elements);
                    server.directMessage(message, username);
                default:
                    break;
            }
        }
    }

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

    public void sendMessage (String message) {
        try {
            PrintWriter userOut = new PrintWriter(clientSocket.getOutputStream(), true);
            userOut.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername () {
        return username;
    }
}
