package com.chat.server.service;

import com.chat.server.network.ClientHandler;
import com.chat.shared.model.Message;
import com.chat.shared.model.Room;
import com.chat.shared.model.User;
import com.chat.shared.router.MessageRouter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatService {
    private final MessageRouter messageRouter;
    private final UserService userService;
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public ChatService(MessageRouter messageRouter, UserService userService) {
        this.messageRouter = messageRouter;
        this.userService = userService;
        rooms.put("general", new Room("general", "general"));
    }

    public void joinRoom(String clientId, String roomId) {
        Room room = rooms.computeIfAbsent(roomId, k -> new Room(k, k));
        room.addMember(clientId);
        User user = userService.getUser(clientId);
        if (user != null) {
            user.setCurrentRoom(roomId);
            Message sysMsg = Message.createSystem(user.getDisplayName() + " joined #" + roomId, roomId);
            room.addMessage(sysMsg);
            messageRouter.broadcast(sysMsg, null);
        }
    }

    public void leaveAllRooms(String clientId) {
        User user = userService.getUser(clientId);
        String name = user != null ? user.getDisplayName() : clientId;
        for (Room room : rooms.values()) {
            if (room.getMemberIds().remove(clientId)) {
                Message sysMsg = Message.createSystem(name + " left #" + room.getName(), room.getId());
                room.addMessage(sysMsg);
                messageRouter.broadcast(sysMsg, null);
            }
        }
    }

    public void handleBroadcast(Message msg, ClientHandler senderHandler) {
        String roomId = msg.getTargetRoom();
        Room room = rooms.get(roomId);
        if (room != null && room.getMemberIds().contains(msg.getSender())) {
            room.addMessage(msg);
            messageRouter.broadcast(msg, senderHandler);
        } else {
            senderHandler.send(Message.createError("You are not in room: " + roomId));
        }
    }

    public void handlePrivateMessage(Message msg, ClientHandler senderHandler) {
        messageRouter.broadcast(msg, null);
    }

    public String getRoomListAsString() {
        return String.join(",", rooms.keySet());
    }
}