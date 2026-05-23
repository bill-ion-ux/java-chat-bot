package com.chat.server.network;

import com.chat.server.config.ServerConfig;
import com.chat.server.service.ChatService;
import com.chat.server.service.UserService;
import com.chat.shared.model.Message;
import com.chat.shared.router.MessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ConnectionManager.class);
    
    private final ExecutorService pool;
    private final Map<String, ClientHandler> activeClients = new ConcurrentHashMap<>();
    private final MessageRouter messageRouter;
    private final ChatService chatService;
    private final UserService userService;
    private final HeartbeatScheduler heartbeatScheduler;

    public ConnectionManager(int poolSize, MessageRouter messageRouter, ChatService chatService, UserService userService) {
        this.pool = Executors.newFixedThreadPool(poolSize);
        this.messageRouter = messageRouter;
        this.chatService = chatService;
        this.userService = userService;
        this.heartbeatScheduler = new HeartbeatScheduler(this);
    }

    public void handleNewConnection(Socket socket) {
        ClientHandler handler = new ClientHandler(socket, this, messageRouter, chatService, userService);
        pool.submit(handler);
        pool.submit(handler.getSenderTask());
    }

    public void addClient(String id, ClientHandler handler) {
        activeClients.put(id, handler);
        heartbeatScheduler.register(id, handler);
    }

    public void removeClient(String id) {
        ClientHandler handler = activeClients.remove(id);
        if (handler != null) {
            heartbeatScheduler.unregister(id);
        }
    }

    public Map<String, ClientHandler> getActiveClients() {
        return activeClients;
    }

    public void shutdown() {
        log.info("Shutting down connections...");
        heartbeatScheduler.shutdown();
        
        Message shutdownMsg = Message.createSystem("SERVER_SHUTDOWN", null);
        for (ClientHandler handler : activeClients.values()) {
            handler.send(shutdownMsg);
            handler.disconnect();
        }
        
        pool.shutdown();
        try {
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}