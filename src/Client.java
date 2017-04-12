import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Client implements Runnable{

    public int clientId;
    protected Socket server;
    protected InetSocketAddress serverAddress;

    public Client(InetSocketAddress address){
        serverAddress = address;
    }

    //@Override
    public void run(){

        try {
            server = new Socket();
            server.setKeepAlive(true);
            server.connect(serverAddress);
            System.out.println("connected");
            ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());

            int i = 1;
            while (true){
                os.writeObject("Hello : "+i);
                os.flush();
                i++;
                sleep(10);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
