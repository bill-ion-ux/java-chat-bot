package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static final int PORT = 333;
    public static void main(String[] args) {

        try{
            ServerSocket serverSocket;
            serverSocket = new ServerSocket(PORT);// bind itself to the local address and port number
            System.out.println("Connection is establishing......");
            int i = 1;
            while(true){
                Socket connectSocket = serverSocket.accept();// accept a request from client
                System.out.println(i);

                //read a request from connectSocket
                //@getInputStream is used to receive a raw bytes of data from the client
                //@InputStreamReader wraps the raw bytes and converts it to character
                BufferedReader inClient = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));
                String message = inClient.readLine();
                if (message != null){
                    System.out.println("connection is established");
                }
                System.out.println(message + " received from client");
                BufferedWriter toClient = new BufferedWriter(new OutputStreamWriter(connectSocket.getOutputStream()));
                toClient.write("connection is established with the server\n");
                toClient.flush();
                connectSocket.close();
                i++;
            }

        }catch(IOException e){
            e.getCause();
        }
    }

}
