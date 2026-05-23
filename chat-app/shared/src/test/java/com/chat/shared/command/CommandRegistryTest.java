package com.chat.shared.command;

import com.chat.shared.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandRegistryTest {
    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
    }

    @Test
    void testCommandRegistration() {
        Command whisperCmd = mock(Command.class);
        registry.register("/whisper", whisperCmd);
        
        assertEquals(whisperCmd, registry.get("/whisper"));
        assertNull(registry.get("/unknown"));
    }

    @Test
    void testIsCommand() {
        assertTrue(registry.isCommand("/whisper alice hello"));
        assertFalse(registry.isCommand("hello world"));
        assertFalse(registry.isCommand(null));
    }
}