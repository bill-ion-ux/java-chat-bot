package client;

import java.io.*;
import java.net.Socket;

public class WriteToServer implements Runnable {
    private Socket socket;
    public WriteToServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            while (true){
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter outputServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                System.out.print("you: ");
                String message = userInput.readLine();
                outputServer.write(message);

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
