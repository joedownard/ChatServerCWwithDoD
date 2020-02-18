import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatUtils {

    public static void sendMessage (String message, Socket receiver) {
        try {
            PrintWriter serverOut = new PrintWriter(receiver.getOutputStream(), true);
            serverOut.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
