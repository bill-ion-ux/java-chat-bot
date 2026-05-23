package com.chat.shared.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageFactoryTest {

    @Test
    void testCreateBroadcast() {
        Message msg = Message.createBroadcast("alice", "hello", "general");
        assertNotNull(msg.getId());
        assertNotNull(msg.getTimestamp());
        assertEquals(MessageType.BROADCAST, msg.getType());
        assertEquals("alice", msg.getSender());
        assertEquals("hello", msg.getContent());
        assertEquals("general", msg.getTargetRoom());
        assertNull(msg.getTargetUser());
    }

    @Test
    void testCreatePrivate() {
        Message msg = Message.createPrivate("alice", "hello", "bob");
        assertNotNull(msg.getId());
        assertEquals(MessageType.PRIVATE, msg.getType());
        assertEquals("bob", msg.getTargetUser());
        assertNull(msg.getTargetRoom());
    }

    @Test
    void testCreateSystem() {
        Message msg = Message.createSystem("Welcome", "general");
        assertEquals(MessageType.SYSTEM, msg.getType());
        assertEquals("SYSTEM", msg.getSender());
        assertEquals("Welcome", msg.getContent());
        assertEquals("general", msg.getTargetRoom());
    }
}