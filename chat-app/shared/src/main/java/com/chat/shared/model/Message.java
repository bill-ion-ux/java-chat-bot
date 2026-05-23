package com.chat.shared.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String sender;
    private final String content;
    private final Instant timestamp;
    private final MessageType type;
    private final String targetRoom;
    private final String targetUser;

    public Message(MessageType type, String sender, String content, String targetRoom, String targetUser) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.type = type;
        this.sender = sender;
        this.content = content;
        this.targetRoom = targetRoom;
        this.targetUser = targetUser;
    }

    public static Message createBroadcast(String sender, String content, String targetRoom) {
        return new Message(MessageType.BROADCAST, sender, content, targetRoom, null);
    }

    public static Message createPrivate(String sender, String content, String targetUser) {
        return new Message(MessageType.PRIVATE, sender, content, null, targetUser);
    }

    public static Message createSystem(String content, String targetRoom) {
        return new Message(MessageType.SYSTEM, "SYSTEM", content, targetRoom, null);
    }

    public static Message createConnect(String username) {
        return new Message(MessageType.CONNECT, username, null, null, null);
    }

    public static Message createDisconnect() {
        return new Message(MessageType.DISCONNECT, null, null, null, null);
    }

    public static Message createHeartbeat() {
        return new Message(MessageType.HEARTBEAT, null, null, null, null);
    }

    public static Message createHeartbeatAck() {
        return new Message(MessageType.HEARTBEAT_ACK, null, null, null, null);
    }
    
    public static Message createUserList(String content) {
        return new Message(MessageType.USER_LIST, "SYSTEM", content, null, null);
    }
    
    public static Message createRoomList(String content) {
        return new Message(MessageType.ROOM_LIST, "SYSTEM", content, null, null);
    }
    
    public static Message createError(String content) {
        return new Message(MessageType.ERROR, "SYSTEM", content, null, null);
    }

    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }
    public MessageType getType() { return type; }
    public String getTargetRoom() { return targetRoom; }
    public String getTargetUser() { return targetUser; }

    @Override
    public String toString() {
        return String.format("Message{type=%s, sender='%s', content='%s'}", type, sender, content);
    }
}