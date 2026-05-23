package com.chat.server.service;

import com.chat.shared.model.User;
import com.chat.shared.model.UserStatus;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void registerUser(String id, String displayName) {
        users.put(id, new User(id, displayName, UserStatus.ONLINE, java.time.Instant.now(), null));
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public String getOnlineUsersAsString() {
        return users.values().stream()
                .map(User::getDisplayName)
                .collect(Collectors.joining(","));
    }
}