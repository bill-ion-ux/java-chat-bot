package com.chat.client.ui;

import com.chat.client.service.ChatService;
import com.chat.client.service.UserService;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginScreen extends Application {
    private ChatService chatService;
    private UserService userService;

    @Override
    public void start(Stage primaryStage) {
        userService = new UserService();
        chatService = new ChatService(userService);

        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("bg-secondary");

        VBox card = new VBox(16);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(380);

        Label title = new Label("JavaChat");
        title.getStyleClass().add("title-label");

        Label subtitle = new Label("Connect to a chat server");
        subtitle.getStyleClass().add("text-secondary");

        TextField userField = new TextField();
        userField.setPromptText("Username");

        TextField serverField = new TextField();
        serverField.setPromptText("localhost:5000");

        Button connectBtn = new Button("Connect");
        connectBtn.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        connectBtn.setOnAction(e -> {
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            String user = userField.getText().trim();
            String server = serverField.getText().trim();
            if (server.isEmpty()) server = "localhost:5000";

            if (user.isEmpty()) {
                errorLabel.setText("Username is required");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            String[] parts = server.split(":");
            String host = parts[0];
            int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 5000;

            try {
                chatService.setCallbacks(
                    () -> showChatWindow(primaryStage),
                    () -> System.out.println("Disconnected"),
                    status -> System.out.println(status)
                );
                chatService.connect(host, port, user);
            } catch (IOException ex) {
                errorLabel.setText("Connection failed");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });

        card.getChildren().addAll(title, subtitle, userField, serverField, connectBtn, errorLabel);
        root.getChildren().add(card);

        Scene scene = new Scene(root, 480, 320);
        scene.getStylesheets().add(getClass().getResource("/assets/styles.css").toExternalForm());

        primaryStage.setTitle("JavaChat Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showChatWindow(Stage stage) {
        ChatWindow chatWindow = new ChatWindow(chatService, userService);
        Scene scene = new Scene(chatWindow, 1024, 680);
        scene.getStylesheets().add(getClass().getResource("/assets/styles.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("JavaChat");
        stage.setMinWidth(800);
        stage.setMinHeight(500);
    }

    @Override
    public void stop() {
        if (chatService != null) {
            chatService.disconnect();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}