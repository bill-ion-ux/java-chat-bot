package com.chat.client.ui.components;

import com.chat.shared.model.Message;
import com.chat.shared.model.MessageType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageCell extends ListCell<Message> {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private final String localUser;

    public MessageCell(String localUser) {
        this.localUser = localUser;
        getStyleClass().add("list-cell");
    }

    @Override
    protected void updateItem(Message msg, boolean empty) {
        super.updateItem(msg, empty);
        if (empty || msg == null) {
            setGraphic(null);
            setText(null);
        } else {
            VBox root = new VBox(4);
            root.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));

            if (msg.getType() == MessageType.SYSTEM) {
                Label sysLabel = new Label(msg.getContent());
                sysLabel.setStyle("-fx-font-style: italic; -fx-text-fill: var(--text-secondary); -fx-font-size: 12px;");
                HBox box = new HBox(sysLabel);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            } else {
                HBox header = new HBox(8);
                header.setAlignment(Pos.CENTER_LEFT);
                
                String senderName = msg.getType() == MessageType.PRIVATE ? "\u2192 " + msg.getSender() : msg.getSender();
                Label sender = new Label(senderName);
                sender.setStyle("-fx-font-weight: 600; -fx-text-fill: var(--accent);");
                
                Label time = new Label(TIME_FORMATTER.format(msg.getTimestamp()));
                time.getStyleClass().add("text-secondary");
                
                header.getChildren().addAll(sender, time);
                
                Label content = new Label(msg.getContent());
                content.setWrapText(true);
                
                root.getChildren().addAll(header, content);
                
                if (msg.getType() == MessageType.PRIVATE) {
                    root.setStyle("-fx-background-color: #EEF4FC; -fx-background-radius: 4px;");
                }
                setGraphic(root);
            }
            setText(null);
        }
    }
}