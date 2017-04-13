import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Client implements Runnable{

    public int clientId;
    protected Socket server;
    protected InetSocketAddress serverAddress;
    private String message;

    public Client(InetSocketAddress address){
        serverAddress = address;
    }

    //@Override
    public void run(){

        try {
            server = new Socket();
            server.setKeepAlive(true);
            server.connect(serverAddress);
            System.out.println("Connected to server");
            ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
            ObjectInputStream is = new ObjectInputStream(server.getInputStream());

            System.out.println("Communicate with the serer :");

            while (message != "bye"){
                /*os.writeObject("Hello : "+i);
                os.flush();
                i++;
                sleep(10);*/
                if(is.available()>0) {
                    message = (String) is.readObject();
                    System.out.println("server > " + message);
                }
                Scanner reader = new Scanner(System.in);  // Reading from System.in
                message = reader.nextLine();
                os.writeObject(message);
                os.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
