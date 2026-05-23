package com.chat.server;

import com.chat.server.network.ConnectionManager;
import com.chat.server.service.ChatService;
import com.chat.server.service.UserService;
import com.chat.shared.model.Message;
import com.chat.shared.router.MessageRouter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ServerIntegrationTest {

    private ServerSocket serverSocket;
    private Thread acceptThread;
    private ConnectionManager connectionManager;
    private MessageRouter router;

    @BeforeEach
    void setUp() throws Exception {
        serverSocket = new ServerSocket(0); // ephemeral port
        int port = serverSocket.getLocalPort();
        
        router = new MessageRouter();
        UserService userService = new UserService();
        ChatService chatService = new ChatService(router, userService);
        connectionManager = new ConnectionManager(10, router, chatService, userService);
        
        acceptThread = new Thread(() -> {
            try {
                while (!serverSocket.isClosed()) {
                    Socket client = serverSocket.accept();
                    connectionManager.handleNewConnection(client);
                }
            } catch (Exception ignored) {}
        });
        acceptThread.start();
    }

    @AfterEach
    void tearDown() throws Exception {
        connectionManager.shutdown();
        serverSocket.close();
        acceptThread.interrupt();
    }

    @Test
    void testClientConnectionAndBroadcast() throws Exception {
        int port = serverSocket.getLocalPort();
        
        // Client 1
        Socket socket1 = new Socket("localhost", port);
        ObjectOutputStream out1 = new ObjectOutputStream(socket1.getOutputStream());
        out1.flush();
        ObjectInputStream in1 = new ObjectInputStream(socket1.getInputStream());
        
        out1.writeObject(Message.createConnect("alice"));
        out1.flush();
        
        // Read initial messages (system, user list, room list)
        assertTrue(in1.readObject() instanceof Message);
        assertTrue(in1.readObject() instanceof Message);
        assertTrue(in1.readObject() instanceof Message);

        // Client 2
        Socket socket2 = new Socket("localhost", port);
        ObjectOutputStream out2 = new ObjectOutputStream(socket2.getOutputStream());
        out2.flush();
        ObjectInputStream in2 = new ObjectInputStream(socket2.getInputStream());
        
        out2.writeObject(Message.createConnect("bob"));
        out2.flush();
        
        assertTrue(in2.readObject() instanceof Message);
        assertTrue(in2.readObject() instanceof Message);
        assertTrue(in2.readObject() instanceof Message);
        
        // Alice sends a broadcast
        out1.writeObject(Message.createBroadcast("alice", "hello everyone", "general"));
        out1.flush();
        
        // Bob should receive a system message that Alice joined, then the broadcast
        Message msgBob1 = (Message) in2.readObject();
        if (msgBob1.getType() == com.chat.shared.model.MessageType.SYSTEM) {
            Message msgBob2 = (Message) in2.readObject();
            assertEquals("hello everyone", msgBob2.getContent());
        } else {
            assertEquals("hello everyone", msgBob1.getContent());
        }

        socket1.close();
        socket2.close();
    }
}