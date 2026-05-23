package com.chat.shared.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String displayName;
    private UserStatus status;
    private final Instant connectedAt;
    private String currentRoom;

    public User(String displayName) {
        this(UUID.randomUUID().toString(), displayName, UserStatus.ONLINE, Instant.now(), null);
    }

    public User(String id, String displayName, UserStatus status, Instant connectedAt, String currentRoom) {
        this.id = id;
        this.displayName = displayName;
        this.status = status;
        this.connectedAt = connectedAt;
        this.currentRoom = currentRoom;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    public Instant getConnectedAt() { return connectedAt; }
    public String getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(String currentRoom) { this.currentRoom = currentRoom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}