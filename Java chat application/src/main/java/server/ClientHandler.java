package server;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler extends Thread {
    private final Socket socket;
    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {


            try{
                while (true) {
                    String inMessage ="";
                    String fromServer ="";
                    System.out.println("server: connected");
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream())));
                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                    do {
                        inMessage = in.readLine();
                        System.out.println("client: " + inMessage);
                        System.out.print("server: ");
                        fromServer = stdIn.readLine();
                        if(fromServer != null){
                            out.write(fromServer);
                            out.flush();
                        }

                    } while (!Objects.equals(inMessage, "bye"));
                    socket.close();
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}
