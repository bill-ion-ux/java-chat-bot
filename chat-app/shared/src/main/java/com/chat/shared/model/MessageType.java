package com.chat.shared.model;

public enum MessageType {
    CONNECT, DISCONNECT, BROADCAST, PRIVATE,
    SYSTEM, HEARTBEAT, HEARTBEAT_ACK, USER_LIST,
    ROOM_LIST, ERROR, TYPING
}