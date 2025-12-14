package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ReadFromServer implements Runnable{
    private Socket socket;
    public ReadFromServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String message = "";
            do {
                BufferedReader inServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                message = inServer.readLine();
                System.out.println("client: " + message);
                if(message.equals("close")){
                    break;
                }
            }
            while(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
