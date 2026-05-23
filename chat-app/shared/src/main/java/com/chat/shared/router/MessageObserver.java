package com.chat.shared.router;

import com.chat.shared.model.Message;

public interface MessageObserver {
    void onMessage(Message message);
    String getObserverId();
}