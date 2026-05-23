package com.chat.server;

import com.chat.server.config.ServerConfig;
import com.chat.server.network.ConnectionManager;
import com.chat.server.service.ChatService;
import com.chat.server.service.UserService;
import com.chat.shared.router.MessageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private final int port;
    private final ConnectionManager connectionManager;
    private final MessageRouter messageRouter;
    private final ChatService chatService;
    private final UserService userService;
    private boolean running = true;

    public Server() {
        this.port = ServerConfig.getInt("server.port", 5000);
        int poolSize = ServerConfig.getInt("server.pool.size", 50);
        
        this.messageRouter = new MessageRouter();
        this.userService = new UserService();
        this.chatService = new ChatService(messageRouter, userService);
        this.connectionManager = new ConnectionManager(poolSize, messageRouter, chatService, userService);
    }

    public void start() {
        log.info("Starting chat server on port {}", port);
        
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    connectionManager.handleNewConnection(clientSocket);
                } catch (IOException e) {
                    if (running) {
                        log.error("Error accepting connection", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Server exception", e);
        }
    }

    public void shutdown() {
        log.info("Server shutdown initiated");
        running = false;
        connectionManager.shutdown();
    }

    public static void main(String[] args) {
        new Server().start();
    }
}