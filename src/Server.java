import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

/**
 * Created by user on 4/10/2017.
 */
public class Server implements Runnable{
    private ConcurrentHashMap<Integer, String>  passwordHashTable = new ConcurrentHashMap<>();
    private ServerSocketChannel 			    serverSocket;
    private String message;
    private Socket con;
    private ObjectInputStream is;
    private ObjectOutputStream os;

    public Server(int port){
        init(port);
    }

    void init(int port){
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
        } catch (IOException e){
            System.err.println("Could not bind socket to port");
            System.exit(1);
        }
    }

    public void acceptConnection() throws IOException {
        SocketChannel channel = serverSocket.accept();

        if (channel == null)
            return;

        con = channel.socket();
        try {
            is = new ObjectInputStream(con.getInputStream());
            os = new ObjectOutputStream(con.getOutputStream());
            System.out.println("Connected to a new Client");
        } catch (IOException e) {
            System.err.println("Connection error");
        }
    }

    public void run(){
        while(true) {
            try {
                acceptConnection();
                sleep(1);
            }
            catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            if(con!=null) {
                try {
                    message = (String) is.readObject();
                    System.out.println("Client > " + message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
