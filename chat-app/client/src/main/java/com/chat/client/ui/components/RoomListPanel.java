package com.chat.client.ui.components;

import com.chat.shared.model.Room;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class RoomListPanel extends ListCell<Room> {
    public RoomListPanel() {
        getStyleClass().add("list-cell");
    }

    @Override
    protected void updateItem(Room room, boolean empty) {
        super.updateItem(room, empty);
        if (empty || room == null) {
            setGraphic(null);
            setText(null);
            getStyleClass().remove("room-cell-active");
        } else {
            HBox root = new HBox();
            root.setAlignment(Pos.CENTER_LEFT);
            root.setPadding(new javafx.geometry.Insets(8, 12, 8, 12));

            Label name = new Label("#" + room.getName());
            root.getChildren().add(name);

            setGraphic(root);
            setText(null);
            
            if (isSelected()) {
                if (!getStyleClass().contains("room-cell-active")) {
                    getStyleClass().add("room-cell-active");
                }
            } else {
                getStyleClass().remove("room-cell-active");
            }
        }
    }
}