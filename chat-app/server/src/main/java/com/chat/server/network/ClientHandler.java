package com.chat.server.network;

import com.chat.server.config.ServerConfig;
import com.chat.server.service.ChatService;
import com.chat.server.service.UserService;
import com.chat.shared.model.Message;
import com.chat.shared.model.MessageType;
import com.chat.shared.router.MessageObserver;
import com.chat.shared.router.MessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientHandler implements Runnable, MessageObserver {
    private static final Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private final Socket socket;
    private final ConnectionManager connectionManager;
    private final MessageRouter messageRouter;
    private final ChatService chatService;
    private final UserService userService;

    private ObjectInputStream objectIn;
    private ObjectOutputStream objectOut;
    private final LinkedBlockingQueue<Message> sendQueue;

    private volatile boolean running = true;
    private volatile Instant lastHeartbeatAck = Instant.now();
    
    private String clientId;
    private String username;
    
    private final Runnable senderTask;

    public ClientHandler(Socket socket, ConnectionManager connectionManager, MessageRouter messageRouter, ChatService chatService, UserService userService) {
        this.socket = socket;
        this.connectionManager = connectionManager;
        this.messageRouter = messageRouter;
        this.chatService = chatService;
        this.userService = userService;
        this.clientId = UUID.randomUUID().toString();
        
        int queueCapacity = ServerConfig.getInt("client.queue.capacity", 100);
        this.sendQueue = new LinkedBlockingQueue<>(queueCapacity);
        
        this.senderTask = () -> {
            while (running) {
                try {
                    Message msg = sendQueue.take();
                    sendInternal(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        };
        
        try {
            this.objectOut = new ObjectOutputStream(socket.getOutputStream());
            this.objectOut.flush();
            this.objectIn = new ObjectInputStream(socket.getInputStream());
        } catch (EOFException e) {
            log.debug("Connection closed by client during handshake (likely healthcheck): {}", socket.getRemoteSocketAddress());
            disconnect();
        } catch (IOException e) {
            log.error("Error setting up streams", e);
            disconnect();
        }
    }

    public Runnable getSenderTask() {
        return senderTask;
    }

    @Override
    public void run() {
        try {
            while (running) {
                Object obj = objectIn.readObject();
                if (obj instanceof Message msg) {
                    handleIncomingMessage(msg);
                }
            }
        } catch (SocketException | EOFException e) {
            log.info("Client disconnected: {}", clientId);
        } catch (ClassNotFoundException e) {
            log.warn("Protocol mismatch: {}", clientId, e);
        } catch (InvalidClassException e) {
            log.error("serialVersionUID mismatch for {}", clientId, e);
        } catch (IOException e) {
            log.error("Unexpected IOException for {}", clientId, e);
        } finally {
            cleanup();
        }
    }

    private void handleIncomingMessage(Message msg) {
        switch (msg.getType()) {
            case CONNECT -> {
                this.username = msg.getSender();
                this.clientId = this.username;
                connectionManager.addClient(clientId, this);
                messageRouter.subscribe(this);
                userService.registerUser(clientId, username);
                
                log.info("Client connected: {} ({})", clientId, socket.getRemoteSocketAddress());
                
                send(Message.createSystem("Welcome, " + username, null));
                send(Message.createUserList(userService.getOnlineUsersAsString()));
                send(Message.createRoomList(chatService.getRoomListAsString()));
                
                chatService.joinRoom(clientId, "general");
            }
            case HEARTBEAT_ACK -> {
                this.lastHeartbeatAck = Instant.now();
            }
            case BROADCAST -> {
                chatService.handleBroadcast(msg, this);
            }
            case PRIVATE -> {
                chatService.handlePrivateMessage(msg, this);
            }
            case DISCONNECT -> {
                running = false;
            }
            default -> log.warn("Unhandled message type: {}", msg.getType());
        }
    }

    @Override
    public void onMessage(Message message) {
        if (!sendQueue.offer(message)) {
            log.warn("DROP: queue full for {} (msg type: {})", clientId, message.getType());
        }
    }

    @Override
    public String getObserverId() {
        return clientId;
    }

    public void send(Message msg) {
        if (!sendQueue.offer(msg)) {
            log.warn("DROP: queue full for {} (msg type: {})", clientId, msg.getType());
        }
    }

    private synchronized void sendInternal(Message msg) {
        if (!running) return;
        try {
            objectOut.writeObject(msg);
            objectOut.flush();
            objectOut.reset();
        } catch (IOException e) {
            disconnect();
        }
    }

    public Instant getLastHeartbeatAck() {
        return lastHeartbeatAck;
    }

    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }

    private void cleanup() {
        running = false;
        connectionManager.removeClient(clientId);
        messageRouter.unsubscribe(this);
        chatService.leaveAllRooms(clientId);
        userService.removeUser(clientId);
        
        try {
            if (objectIn != null) objectIn.close();
            if (objectOut != null) objectOut.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException ignored) {}
    }
}