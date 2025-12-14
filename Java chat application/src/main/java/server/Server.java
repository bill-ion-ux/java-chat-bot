package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    public static final int PORT = 333;
    public static void main(String[] args) {
        String[] userName = {"alice", "ali"};
        int i = 0;
        try{
            ServerSocket serverSocket;
            Vector<ClientHandler> clients = new Vector<>();
            serverSocket = new ServerSocket(PORT);// bind itself to the local address and port number
            System.out.println("Connection is establishing......");
            while(true){
                Socket connectSocket = serverSocket.accept();// accept a request from client
                new Thread(new ClientHandler(connectSocket, clients,userName[i])).start();
                i++;
            }

        }catch(IOException e){
            e.getCause();
        }
    }

}
