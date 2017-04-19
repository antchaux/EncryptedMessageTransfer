package Classes;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{

    public int clientId;
    protected InetSocketAddress serverAddress;
    private ObjectInputStream serverInputStream;
    private ObjectOutputStream serverOutputStream;
    private String message = "";
    private boolean keepConnectionOpened = true, clientCanWrite = true;

    public Client(InetSocketAddress address){
        serverAddress = address;
    }

    private void connectToServer(InetSocketAddress address) throws IOException{
        Socket server = new Socket();
        server.setKeepAlive(true);
        server.connect(address);
        System.out.println("Connected to server");
        serverOutputStream = new ObjectOutputStream(server.getOutputStream());
        serverInputStream = new ObjectInputStream(server.getInputStream());
    }

    private void readMessage(ObjectInputStream is) throws ClassNotFoundException, IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        message = in.readLine();
        if(message.equals("")) clientCanWrite = true;
        else System.out.println("Server> " + message);
    }

    private void sendMessage(ObjectOutputStream os) throws IOException, ClassNotFoundException{
        System.out.println("Communicate with the server :");
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        message = reader.nextLine();
        os.writeObject(message);
        os.flush();
        if(message.equals("bye")){
            keepConnectionOpened = false;
            System.out.println("Closing connection with server");
        }
        else System.out.println("Listening to the server :");
        clientCanWrite = false;
    }

    //@Override
    public void run(){

        try {
            connectToServer(serverAddress);

            while (keepConnectionOpened){
                while(!clientCanWrite) readMessage(serverInputStream);
                sendMessage(serverOutputStream);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
