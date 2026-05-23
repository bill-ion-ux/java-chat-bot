package com.chat.shared.router;

import com.chat.shared.model.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class MessageRouterTest {
    private MessageRouter router;
    private MessageObserver observer1;
    private MessageObserver observer2;
    
    @BeforeEach
    void setUp() {
        router = new MessageRouter();
        observer1 = mock(MessageObserver.class);
        when(observer1.getObserverId()).thenReturn("user1");
        
        observer2 = mock(MessageObserver.class);
        when(observer2.getObserverId()).thenReturn("user2");
        
        router.subscribe(observer1);
        router.subscribe(observer2);
    }
    
    @Test
    void testBroadcastReachesAllObservers() {
        Message msg = Message.createBroadcast("system", "hello", "general");
        router.broadcast(msg, null);
        
        verify(observer1, times(1)).onMessage(msg);
        verify(observer2, times(1)).onMessage(msg);
    }
    
    @Test
    void testSenderIsExcluded() {
        Message msg = Message.createBroadcast("user1", "hello", "general");
        router.broadcast(msg, observer1);
        
        verify(observer1, never()).onMessage(msg);
        verify(observer2, times(1)).onMessage(msg);
    }
    
    @Test
    void testUnsubscribedObserverNotCalled() {
        router.unsubscribe(observer2);
        
        Message msg = Message.createBroadcast("system", "hello", "general");
        router.broadcast(msg, null);
        
        verify(observer1, times(1)).onMessage(msg);
        verify(observer2, never()).onMessage(msg);
    }
}