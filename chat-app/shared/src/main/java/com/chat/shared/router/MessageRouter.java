package com.chat.shared.router;

import com.chat.shared.model.Message;
import java.util.concurrent.CopyOnWriteArrayList;

public class MessageRouter {
    private final CopyOnWriteArrayList<MessageObserver> observers = new CopyOnWriteArrayList<>();

    public void subscribe(MessageObserver observer) {
        observers.addIfAbsent(observer);
    }

    public void unsubscribe(MessageObserver observer) {
        observers.remove(observer);
    }

    public void broadcast(Message message, MessageObserver sender) {
        for (MessageObserver observer : observers) {
            if (sender != null && observer.getObserverId().equals(sender.getObserverId())) {
                continue;
            }
            observer.onMessage(message);
        }
    }
}