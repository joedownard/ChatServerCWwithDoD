import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {

    private Socket server;

    public ChatClient (String ip, int port) {
        try {
            server = new Socket(ip, port);
            sendUsername();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void go () {
        try {
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream()));

            new Thread(() -> {
                System.out.println("server listener running!");
                try {
                    String serverLineIn;
                    while ((serverLineIn = serverIn.readLine()) != null) {
                        System.out.println(serverLineIn);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            String clientLineIn;
            while ((clientLineIn = userIn.readLine()) != null) {
                ChatUtils.sendMessage(clientLineIn, server);
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

    public void sendUsername () {
        String username = "/username Guest";
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Connection Established. Enter a username:");
        try {
            username = "/username " + "\""+ userIn.readLine() + "\"";
            ChatUtils.sendMessage(username, server);
        } catch (IOException e) {
            e.printStackTrace();
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

        new ChatClient(ip, port).go();
    }
}
