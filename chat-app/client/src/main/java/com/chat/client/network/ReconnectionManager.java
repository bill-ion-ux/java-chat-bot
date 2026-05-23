package com.chat.client.network;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReconnectionManager {
    private static final Logger log = LoggerFactory.getLogger(ReconnectionManager.class);

    private int attempt = 0;
    private final int maxAttempts = 6;
    private final Runnable reconnectAction;
    private final Runnable onFailureAction;

    public ReconnectionManager(Runnable reconnectAction, Runnable onFailureAction) {
        this.reconnectAction = reconnectAction;
        this.onFailureAction = onFailureAction;
    }

    public void scheduleReconnect() {
        if (attempt >= maxAttempts) {
            log.error("Max reconnection attempts reached.");
            Platform.runLater(onFailureAction);
            return;
        }

        attempt++;
        int delaySeconds = Math.min((int) Math.pow(2, attempt - 1), 32);
        
        log.info("Scheduling reconnect attempt {} in {} seconds", attempt, delaySeconds);
        
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000L);
                Platform.runLater(reconnectAction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    public void reset() {
        attempt = 0;
    }
    
    public int getAttempt() {
        return attempt;
    }
}