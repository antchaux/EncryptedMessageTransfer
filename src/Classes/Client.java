package Classes;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    protected InetSocketAddress serverAddress;
    private ObjectInputStream serverInputStream;
    private ObjectOutputStream serverOutputStream;
    private String message = "";
    private boolean keepConnectionOpened = true, clientCanWrite = true;
    private String clientName = "TestClient";
    private String clientPassword = "Password1234";

    public Client(InetSocketAddress address) {
        serverAddress = address;
    }

    private void connectToServer(InetSocketAddress address)
            throws IOException, ClassNotFoundException, InterruptedException {
        Socket server = new Socket();
        server.setKeepAlive(true);
        server.connect(address);
        serverOutputStream = new ObjectOutputStream(server.getOutputStream());
        serverInputStream = new ObjectInputStream(server.getInputStream());
        //sendMessage(serverOutputStream, clientName);
    }

    private String readMessage(ObjectInputStream is) throws ClassNotFoundException, IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        message = in.readLine();
        if (message.equals("")) clientCanWrite = true;
        return message;
    }

    private void displayMessage(String message) {
        System.out.println("Server> " + message);
    }

    private String getMessageFromUser() {
        Scanner reader = new Scanner(System.in);  // Reading from System.in
        return reader.nextLine();
    }

    private void sendMessage(ObjectOutputStream os, String message) throws IOException, ClassNotFoundException {
        os.writeObject(message);
        os.flush();
    }

    private void handleServerMessage(ObjectInputStream is) throws ClassNotFoundException, IOException{
        message = readMessage(is);
        switch(message){
            case "Authentication":
                displayMessage("------------- Authentication -----------");
                displayMessage("Did you already register ?(Y/N)");
                sendMessage(serverOutputStream, "N");
                break;
            case "Login":
                displayMessage("Login :");
                clientName = getMessageFromUser();
                sendMessage(serverOutputStream, clientName);
                break;
            case "Password":
                displayMessage("Password :");
                clientPassword = getMessageFromUser();
                sendMessage(serverOutputStream, clientPassword);
                break;
            default:
                displayMessage(message);
                break;
        }
    }

    private void closeConnection() {
        System.out.println("Closing connection with server");
    }

    //@Override
    public void run() {
        try {
            connectToServer(serverAddress);
            while(keepConnectionOpened) handleServerMessage(serverInputStream);
            closeConnection();
        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
