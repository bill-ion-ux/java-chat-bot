package com.chat.shared.command;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandRegistry {
    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    public void register(String name, Command command) {
        commands.put(name.toLowerCase(), command);
    }

    public Command get(String name) {
        return commands.get(name.toLowerCase());
    }

    public boolean isCommand(String text) {
        return text != null && text.startsWith("/");
    }
}