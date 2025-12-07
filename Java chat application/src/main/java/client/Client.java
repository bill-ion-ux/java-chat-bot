package client;

import java.io.*;
import java.net.Socket;

import static server.Server.PORT;

public class Client {

    public static void main(String[] args) {
        try {
            Socket clietSocket = new Socket("localhost", PORT);// connect to the server according to the server port
            System.out.println("connecting to the server...");
            BufferedWriter outputToServer = new BufferedWriter(new OutputStreamWriter(clietSocket.getOutputStream()));// output a message to the server
            System.out.println("client");
//            String message = sc.nextLine();
            outputToServer.write("Hello server\n");
            outputToServer.flush();
            System.out.println("done");
            BufferedReader readServer = new BufferedReader(new InputStreamReader(clietSocket.getInputStream()));//read a message from the server back
            // read-line are used to read an input from an input Stream
            // input-stream is a flow of bytes
            System.out.println(readServer.readLine()); // write the message from the server
            readServer.close();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //read input catch (IOException e) {


        //send to server via socket

    }

}
