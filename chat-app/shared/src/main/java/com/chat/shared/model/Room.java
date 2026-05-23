package com.chat.shared.model;

import com.chat.shared.util.CircularBuffer;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private final Set<String> memberIds;
    private final CircularBuffer<Message> history;

    public Room(String id, String name) {
        this.id = id;
        this.name = name;
        this.memberIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.history = new CircularBuffer<>(100);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<String> getMemberIds() { return memberIds; }
    
    public void addMember(String userId) { memberIds.add(userId); }
    public void removeMember(String userId) { memberIds.remove(userId); }
    
    public void addMessage(Message message) { history.add(message); }
    public Iterable<Message> getHistory() { return history.toList(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Room room = (Room) o;
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}