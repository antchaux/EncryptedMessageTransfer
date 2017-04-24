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
    private ConcurrentHashMap<String, ObjectInputStream>   clientIS = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, ObjectOutputStream>   clientOS = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String>  passwordHashTable = new ConcurrentHashMap<>();
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

    private void init(int port){
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
        } catch (IOException e){
            System.err.println("Could not bind socket to port");
            System.exit(1);
        }
    }

    private void acceptConnection() throws IOException {
        SocketChannel channel = serverSocket.accept();

        if (channel == null)
            return;

        con = channel.socket();
        try {
            clientInputStream = new ObjectInputStream(con.getInputStream());
            clientOutputStream = new ObjectOutputStream(con.getOutputStream());
            pw = new PrintWriter(clientOutputStream, true);
            sendMessage(clientOutputStream, "Client connected to Server");
        } catch (IOException e) {
            System.err.println("Connection error");
        }
    }

    private void authenticateClient(ObjectInputStream is, ObjectOutputStream os) throws IOException, ClassNotFoundException{
        sendMessage(clientOutputStream, "Authentication");
        String answer = readMessage(is);
        if (answer.equals("N")){
            sendMessage(os, "Login");
            String name="", pwd="";
            while(name.equals("")) name = readMessage(is);
            sendMessage(os, "Password");
            while(pwd.equals("")) pwd = readMessage(is);
            passwordHashTable.put(name, pwd);

            System.out.println("Connected to the client : " + name + " with password : " + pwd);
        }
        else if (answer.equals("Y")) {
            sendMessage(os, "Login");
            String name = readMessage(is);
            do{
                sendMessage(os, "Password");
            }while (readMessage(is).equals(passwordHashTable.get(name)));

            System.out.println("Connected to the client : " + clientName);
        }
    }

    private String readMessage(ObjectInputStream is) throws IOException, ClassNotFoundException {
        message = (String) is.readObject();
        return message;
    }

    private void displayMessage(String message){
        System.out.println(clientName + "> " + message);
        if(message.equals("bye")){
            keepConnectionOpened = false;
            System.out.println("Closing connection with client");
        }
        clientCanWrite = false;
    }

    private String getMessageFromUser(){
        System.out.print(">");
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        return reader.nextLine();
    }

    private void sendMessage(ObjectOutputStream os, String message) throws IOException{
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
                    authenticateClient(clientInputStream, clientOutputStream);
                    while(!clientCanWrite){
                        message = getMessageFromUser();
                        sendMessage(clientOutputStream, message);
                    }
                    message = readMessage(clientInputStream);
                    displayMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
