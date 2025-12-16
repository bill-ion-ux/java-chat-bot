package client;

import java.io.*;
import java.net.Socket;

public class WriteToServer implements Runnable {
    private final Socket socket;
    public WriteToServer(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try{
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter outputServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            //noinspection InfiniteLoopStatement
            while (true){
                String message = userInput.readLine();
                System.out.println("you: " + message);
                outputServer.write(message);
                outputServer.newLine();
                outputServer.flush();

            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
