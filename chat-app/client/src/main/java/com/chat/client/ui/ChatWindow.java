package com.chat.client.ui;

import com.chat.client.service.ChatService;
import com.chat.client.service.UserService;
import com.chat.client.ui.components.MessageCell;
import com.chat.client.ui.components.RoomListPanel;
import com.chat.client.ui.components.UserListPanel;
import com.chat.shared.model.Message;
import com.chat.shared.model.Room;
import com.chat.shared.model.User;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.SVGPath;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

public class ChatWindow extends HBox {
    private final ChatService chatService;
    private final UserService userService;

    private ListView<Room> roomListView;
    private ListView<Message> messageListView;
    private ListView<User> userListView;
    private TextField inputField;
    private Label statusLabel;
    private javafx.scene.shape.Circle statusDot;
    private Label topBarRoomLabel;
    private String currentRoomId = "general";

    public ChatWindow(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
        initUI();
        bindData();
        
        chatService.setCallbacks(
            () -> Platform.runLater(() -> updateStatus("Connected", true)),
            () -> Platform.runLater(() -> updateStatus("Disconnected", false)),
            status -> Platform.runLater(() -> updateStatus(status, false))
        );
    }

    private void initUI() {
        VBox leftPanel = new VBox();
        leftPanel.setPrefWidth(220);
        leftPanel.getStyleClass().addAll("bg-secondary", "border-right");
        
        Label roomsLabel = new Label("ROOMS");
        roomsLabel.getStyleClass().add("text-secondary");
        roomsLabel.setStyle("-fx-font-weight: 700;");
        roomsLabel.setPadding(new Insets(16, 12, 8, 12));
        
        roomListView = new ListView<>();
        roomListView.setCellFactory(lv -> new RoomListPanel());
        VBox.setVgrow(roomListView, Priority.ALWAYS);
        
        leftPanel.getChildren().addAll(roomsLabel, roomListView);

        VBox centerPanel = new VBox();
        HBox.setHgrow(centerPanel, Priority.ALWAYS);
        
        HBox topBar = new HBox(8);
        topBar.setMinHeight(48);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(0, 16, 0, 16));
        topBar.getStyleClass().add("border-bottom");
        
        topBarRoomLabel = new Label("#general");
        topBarRoomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");
        topBar.getChildren().add(topBarRoomLabel);
        
        messageListView = new ListView<>();
        messageListView.getStyleClass().add("bg-primary");
        messageListView.setCellFactory(lv -> new MessageCell(userService.getLocalUsername()));
        VBox.setVgrow(messageListView, Priority.ALWAYS);
        
        HBox inputBar = new HBox(8);
        inputBar.setMinHeight(52);
        inputBar.setAlignment(Pos.CENTER);
        inputBar.setPadding(new Insets(6, 16, 6, 16));
        inputBar.getStyleClass().addAll("bg-primary", "border-top");
        
        inputField = new TextField();
        inputField.setPromptText("Message #general");
        inputField.getStyleClass().add("borderless-input");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnAction(e -> sendMessage());
        
        Button sendBtn = new Button();
        sendBtn.getStyleClass().add("icon-button");
        SVGPath planeIcon = new SVGPath();
        planeIcon.setContent("M2.01 21L23 12 2.01 3 2 10l15 2-15 2z");
        sendBtn.setGraphic(planeIcon);
        sendBtn.setOnAction(e -> sendMessage());
        
        inputBar.getChildren().addAll(inputField, sendBtn);
        
        HBox statusBar = new HBox();
        statusBar.setMinHeight(24);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(0, 12, 0, 12));
        statusBar.getStyleClass().addAll("bg-secondary", "border-top");
        
        statusLabel = new Label();
        statusLabel.getStyleClass().add("text-secondary");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        statusDot = new javafx.scene.shape.Circle(4);
        statusDot.getStyleClass().add("status-dot-online");
        
        statusBar.getChildren().addAll(statusLabel, spacer, statusDot);
        updateStatus("Connected as " + userService.getLocalUsername() + " \u00B7 #general", true);
        
        centerPanel.getChildren().addAll(topBar, messageListView, inputBar, statusBar);

        VBox rightPanel = new VBox();
        rightPanel.setPrefWidth(200);
        rightPanel.getStyleClass().addAll("bg-secondary", "border-left");
        
        Label onlineLabel = new Label("ONLINE");
        onlineLabel.getStyleClass().add("text-secondary");
        onlineLabel.setStyle("-fx-font-weight: 700;");
        onlineLabel.setPadding(new Insets(16, 12, 8, 12));
        
        userListView = new ListView<>();
        userListView.setCellFactory(lv -> new UserListPanel());
        VBox.setVgrow(userListView, Priority.ALWAYS);
        
        rightPanel.getChildren().addAll(onlineLabel, userListView);

        this.getChildren().addAll(leftPanel, centerPanel, rightPanel);
        
        roomListView.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                currentRoomId = newV.getId();
                topBarRoomLabel.setText("#" + newV.getName());
                inputField.setPromptText("Message #" + newV.getName());
            }
        });
    }

    private void bindData() {
        roomListView.setItems(chatService.getRooms());
        userListView.setItems(userService.getUsers());
        messageListView.setItems(chatService.getCurrentMessages());
        
        chatService.getCurrentMessages().addListener((javafx.collections.ListChangeListener.Change<? extends Message> c) -> {
            Platform.runLater(() -> messageListView.scrollTo(messageListView.getItems().size() - 1));
        });
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            chatService.sendMessage(text, currentRoomId);
            
            // Local Echo: Add the message to our own list immediately
            Message selfMsg = Message.createBroadcast(userService.getLocalUsername(), text, currentRoomId);
            chatService.getCurrentMessages().add(selfMsg);
            
            inputField.clear();
        }
    }

    private void updateStatus(String text, boolean isOnline) {
        statusLabel.setText(text);
        if (isOnline) {
            statusDot.getStyleClass().setAll("status-dot-online");
        } else {
            statusDot.getStyleClass().setAll("status-dot-away");
        }
    }
}