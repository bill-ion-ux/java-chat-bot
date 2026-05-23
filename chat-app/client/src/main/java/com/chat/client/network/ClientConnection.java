package com.chat.client.network;

import com.chat.client.service.ChatService;
import com.chat.shared.model.Message;
import com.chat.shared.model.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientConnection {
    private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);

    private final String host;
    private final int port;
    private final String username;
    private final ChatService chatService;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private volatile boolean connected = false;

    private final LinkedBlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>(100);
    private Thread receiverThread;
    private Thread senderThread;

    public ClientConnection(String host, int port, String username, ChatService chatService) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.chatService = chatService;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        connected = true;

        receiverThread = new Thread(this::receiveLoop, "ClientReceiver");
        senderThread = new Thread(this::sendLoop, "ClientSender");
        
        receiverThread.start();
        senderThread.start();

        send(Message.createConnect(username));
    }

    public void send(Message msg) {
        if (connected) {
            sendQueue.offer(msg);
        }
    }

    private void sendLoop() {
        try {
            while (connected) {
                Message msg = sendQueue.take();
                out.writeObject(msg);
                out.flush();
                out.reset();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            handleDisconnect(e);
        }
    }

    private void receiveLoop() {
        try {
            while (connected) {
                Object obj = in.readObject();
                if (obj instanceof Message msg) {
                    if (msg.getType() == MessageType.HEARTBEAT) {
                        send(Message.createHeartbeatAck());
                    } else {
                        chatService.handleIncomingMessage(msg);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            handleDisconnect(e);
        }
    }

    private void handleDisconnect(Exception e) {
        if (connected) {
            log.warn("Disconnected from server", e);
            disconnect();
            chatService.onDisconnected();
        }
    }

    public void disconnect() {
        connected = false;
        try {
            if (receiverThread != null) receiverThread.interrupt();
            if (senderThread != null) senderThread.interrupt();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}