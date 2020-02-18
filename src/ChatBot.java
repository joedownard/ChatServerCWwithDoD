import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class ChatBot {

    private final String [] helloResponses = {"Hi!", "Hey!", "Good day!", "Greetings!"};
    private final String [] genericResponses = {"What?", "Interesting!", "Sounds great.", "cool story"};

    private Socket server;

    public ChatBot (String ip, int port) {
        try {
            server = new Socket(ip, port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processChat (String message) {
        String processedMessage = message.toLowerCase().substring(message.indexOf(":") + 2);
        System.out.println(processedMessage);

        if (processedMessage.contains("bot")) {
            if (processedMessage.contains("hello") || processedMessage.contains("hi") | processedMessage.contains("hey")) ChatUtils.sendMessage(getRandomMessage(helloResponses), server);
            else ChatUtils.sendMessage(getRandomMessage(genericResponses), server);
        }
    }

    public String getRandomMessage (String[] possibleMessages) {
        Random r = new Random();
        return possibleMessages[r.nextInt(possibleMessages.length)];
    }

    public void go () {
        try {
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

            ChatUtils.sendMessage("/username Bot Bob", server);

            String serverLineIn;
            while ((serverLineIn = serverIn.readLine()) != null) {
                processChat(serverLineIn);
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
