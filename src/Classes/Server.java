package Classes;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
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
    private ConcurrentHashMap<String, Socket>   userTable = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String>  passwordHashTable = new ConcurrentHashMap<>();
    private ServerSocketChannel 			    serverSocket;
    private String message;
    private Socket con;
    private ObjectInputStream clientInputStream;
    private ObjectOutputStream clientOutputStream;
    private PrintWriter pw;
    private boolean keepConnectionOpened = true, clientCanWrite = true;
    private String clientName;

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
            clientInputStream = new ObjectInputStream(con.getInputStream());
            clientOutputStream = new ObjectOutputStream(con.getOutputStream());
            pw = new PrintWriter(clientOutputStream, true);
            System.out.println("Connected to a new Client");
        } catch (IOException e) {
            System.err.println("Connection error");
        }
    }

    private void readMessage(ObjectInputStream is) throws IOException, ClassNotFoundException{
        System.out.println("Listening to client");
        message = (String) is.readObject();
        System.out.println("Client>" + message);
        if(message.equals("bye")){
            keepConnectionOpened = false;
            System.out.println("Closing connection with client");
        }
        else System.out.println("Communicate with the client :");
        clientCanWrite = false;
    }

    private void sendMessage(ObjectOutputStream os) throws IOException{
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        message = reader.nextLine();
        pw.write(message);
        pw.write('\n');
        pw.flush();
        if (message.equals("")) clientCanWrite = true;
    }

    public void run(){
        while(keepConnectionOpened) {
            try {
                acceptConnection();
                sleep(1);
            }
            catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }

            if(con!=null) {
                try {
                    while(!clientCanWrite) sendMessage(clientOutputStream);
                    readMessage(clientInputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
