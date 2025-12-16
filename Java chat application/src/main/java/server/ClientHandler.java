package server;

import java.io.*;
import java.net.Socket;
import java.util.Vector;


public class ClientHandler implements Runnable {
    public String userName;
    public Socket socket;
    public Vector<ClientHandler> clients;
    private BufferedWriter out;

    public ClientHandler(Socket socket, Vector<ClientHandler> clients,String userName){
        this.socket = socket;
        this.clients = clients;
        this.userName = userName;
    }
    @Override
    public void run() {


        try{
                // I need to use Vector to read and write from another socket
                // do I need a thread?
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
                clients.add(this);
            //noinspection InfiniteLoopStatement
                while (true) {
                    System.out.println("server: connected");
                    String inMessage = in.readLine();
                    sendToClient(inMessage);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
    public void sendToClient(String message) throws IOException {
        for(ClientHandler client:clients){
            if(!client.userName.equals(this.userName)){
                client.out.write(message);
                client.out.newLine();
                client.out.flush();//flush() forces buffered data to be sent immediately instead of waiting in memory.
            }
        }
    }

}
