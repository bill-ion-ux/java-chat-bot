package com.chat.client.ui.components;

import com.chat.shared.model.User;
import com.chat.shared.model.UserStatus;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

public class UserListPanel extends ListCell<User> {
    public UserListPanel() {
        getStyleClass().add("list-cell");
    }

    @Override
    protected void updateItem(User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty || user == null) {
            setGraphic(null);
            setText(null);
        } else {
            HBox root = new HBox(8);
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));

            Circle dot = new Circle(3);
            if (user.getStatus() == UserStatus.ONLINE) {
                dot.getStyleClass().add("status-dot-online");
            } else {
                dot.getStyleClass().add("status-dot-away");
            }

            Label name = new Label(user.getDisplayName());
            root.getChildren().addAll(dot, name);
            
            setGraphic(root);
            setText(null);
        }
    }
}