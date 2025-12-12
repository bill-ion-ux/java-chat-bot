package server;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {


            try{
                while (true) {
                    String inMessage ="";
                    System.out.println("connected");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
                    do {
                        inMessage = in.readLine();
                        if(inMessage != null){
                            out.write(inMessage);
                        }

                    } while (!Objects.equals(inMessage, "close"));
                    socket.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}
