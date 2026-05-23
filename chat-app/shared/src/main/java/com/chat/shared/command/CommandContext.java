package com.chat.shared.command;

import com.chat.shared.model.Message;

public interface CommandContext {
    void sendToSender(Message message);
    void broadcastToRoom(Message message, String roomId);
    void routeMessage(Message message);
}