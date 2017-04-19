import Classes.Server;

/**
 * Created by antoine on 12/04/2017.
 */
public class ServerMain {

    public static void main(String[] args) {
        int serverPort = 2000;

        Server server = new Server(serverPort);
        Thread serverThread = new Thread(server);
        serverThread.setName("Server");
        serverThread.start();

    }
}
