package client;

import java.io.*;
import java.net.Socket;

import static server.Server.PORT;

public class Client {

    public static void main(String[] args) {
        try {
            Socket clietSocket = new Socket("localhost", PORT);// connect to the server according to the server port
            System.out.println("connecting to the server...");
            new Thread(new WriteToServer(clietSocket)).start();
            new Thread(new ReadFromServer(clietSocket)).start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //read input catch (IOException e) {


        //send to server via socket

    }

}
