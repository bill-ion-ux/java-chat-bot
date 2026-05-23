package com.chat.client.service;

import com.chat.shared.model.User;
import com.chat.shared.model.UserStatus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UserService {
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private String localUsername;

    public void setLocalUsername(String username) {
        this.localUsername = username;
    }

    public String getLocalUsername() {
        return localUsername;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void updateUserList(String[] userList) {
        users.clear();
        for (String name : userList) {
            if (!name.isEmpty()) {
                users.add(new User(name, name, UserStatus.ONLINE, java.time.Instant.now(), null));
            }
        }
    }
}