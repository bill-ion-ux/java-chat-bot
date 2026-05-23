package com.chat.client.service;

import com.chat.client.network.ClientConnection;
import com.chat.client.network.ReconnectionManager;
import com.chat.shared.model.Message;
import com.chat.shared.model.Room;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ChatService {
    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private ClientConnection connection;
    private final UserService userService;
    private final ObservableList<Room> rooms = FXCollections.observableArrayList();
    private final ObservableList<Message> currentMessages = FXCollections.observableArrayList();
    
    private ReconnectionManager reconnectionManager;
    private String host;
    private int port;
    private String username;

    private Runnable onConnectedCallback;
    private Runnable onDisconnectedCallback;
    private java.util.function.Consumer<String> onReconnectAttempt;

    public ChatService(UserService userService) {
        this.userService = userService;
        this.reconnectionManager = new ReconnectionManager(
            this::attemptReconnect,
            () -> { if (onDisconnectedCallback != null) onDisconnectedCallback.run(); }
        );
    }

    public void setCallbacks(Runnable onConnected, Runnable onDisconnected, java.util.function.Consumer<String> onReconnectAttempt) {
        this.onConnectedCallback = onConnected;
        this.onDisconnectedCallback = onDisconnected;
        this.onReconnectAttempt = onReconnectAttempt;
    }

    public void connect(String host, int port, String username) throws IOException {
        this.host = host;
        this.port = port;
        this.username = username;
        userService.setLocalUsername(username);

        connection = new ClientConnection(host, port, username, this);
        connection.connect();
        
        reconnectionManager.reset();
        if (onConnectedCallback != null) {
            Platform.runLater(onConnectedCallback);
        }
    }

    private void attemptReconnect() {
        if (onReconnectAttempt != null) {
            onReconnectAttempt.accept("Reconnecting (attempt " + reconnectionManager.getAttempt() + ")…");
        }
        try {
            connect(host, port, username);
        } catch (IOException e) {
            reconnectionManager.scheduleReconnect();
        }
    }

    public void onDisconnected() {
        reconnectionManager.scheduleReconnect();
    }

    public void sendMessage(String content, String roomId) {
        if (connection != null) {
            connection.send(Message.createBroadcast(userService.getLocalUsername(), content, roomId));
        }
    }

    public void disconnect() {
        if (connection != null) {
            connection.send(Message.createDisconnect());
            connection.disconnect();
        }
    }

    public void handleIncomingMessage(Message msg) {
        Platform.runLater(() -> {
            switch (msg.getType()) {
                case USER_LIST -> {
                    String[] users = msg.getContent() != null ? msg.getContent().split(",") : new String[0];
                    userService.updateUserList(users);
                }
                case ROOM_LIST -> {
                    rooms.clear();
                    String[] roomNames = msg.getContent() != null ? msg.getContent().split(",") : new String[0];
                    for (String name : roomNames) {
                        if (!name.isEmpty()) {
                            rooms.add(new Room(name, name));
                        }
                    }
                }
                case BROADCAST, SYSTEM, PRIVATE, ERROR -> {
                    currentMessages.add(msg);
                }
                default -> {}
            }
        });
    }

    public ObservableList<Room> getRooms() {
        return rooms;
    }

    public ObservableList<Message> getCurrentMessages() {
        return currentMessages;
    }
}