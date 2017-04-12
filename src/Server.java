import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

/**
 * Created by user on 4/10/2017.
 */
public class Server implements Runnable{
    private ConcurrentHashMap<Integer, String>  passwordHashTable = new ConcurrentHashMap<>();
    private ServerSocketChannel 			    serverSocket;
    private String message;

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


        Socket con = channel.socket();
        try {
            ObjectInputStream is = new ObjectInputStream(con.getInputStream());
            String message = (String) is.readObject();
            System.out.println("client> " + message);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Connection error");
        }
    }

    public void run(){
        while(true){
            try {
                acceptConnection();
                sleep(1);
            }
            catch (InterruptedException | IOException e){
                e.printStackTrace();
            }
        }
    }
}
