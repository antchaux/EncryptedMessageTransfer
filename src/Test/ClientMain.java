package Test;

import Classes.Client;

import java.net.InetSocketAddress;

/**
 * Created by antoine on 12/04/2017.
 */
public class ClientMain {

    public static void main(String[] args) {
        String name = "Client 1";
        String serverHostName = "localhost";
        int serverPort = 2000;
        InetSocketAddress serverAddress = new InetSocketAddress(serverHostName, serverPort);

        Thread client = new Thread(new Client(serverAddress));
            client.setName(name);
            client.start();
    }
}
