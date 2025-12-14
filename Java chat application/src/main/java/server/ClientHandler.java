package server;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Vector;


public class ClientHandler extends Thread {
    public String userName;
    public Socket socket;
    public Vector<ClientHandler> clients;
    private BufferedReader in;
    private BufferedWriter out;

    public ClientHandler(Socket socket, Vector<ClientHandler> clients,String userName){
        this.socket = socket;
        this.clients = clients;
        this.userName = userName;

    }
    @Override
    public void run() {


            try{

                // i need to use Vector to read and write from another socket
                // do i need a thread?
                clients.add(this);
                while (true) {
                    String inMessage ="";
                    String fromServer ="";
                    System.out.println("server: connected");
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    readFromClient();
                    out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
                    do {
                        inMessage = in.readLine();
                        System.out.println("you: " + inMessage);
                        if(inMessage != null){
                            sendToClient(inMessage);
                        }

                        System.out.print("server: ");


                    } while (!Objects.equals(inMessage, "bye"));
                    socket.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
    public void sendToClient(String message) throws IOException {
        for(ClientHandler client:clients){
            if(!client.userName.equals(this.userName)){
                client.out.write(message);
            }
        }
    }
    public void readFromClient() throws IOException {
        for(ClientHandler client:clients){
            if(!client.userName.equals(this.userName)){
                out.write(client.in.readLine());
            }
        }
    }
}
