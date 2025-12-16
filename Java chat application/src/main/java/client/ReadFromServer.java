package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadFromServer implements Runnable{
    private final Socket socket;
    public ReadFromServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //noinspection InfiniteLoopStatement
            do {
                String message = inServer.readLine();
                if(message != null){
                    System.out.print("client: " + message);
                }
            }
            while(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
