package com.chat.shared.command;

import com.chat.shared.model.Message;

public interface Command {
    void execute(Message message, CommandContext context);
}