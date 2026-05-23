package com.chat.server.network;

import com.chat.server.config.ServerConfig;
import com.chat.shared.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HeartbeatScheduler {
    private static final Logger log = LoggerFactory.getLogger(HeartbeatScheduler.class);

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConnectionManager connectionManager;
    private final int interval;
    private final int timeout;

    public HeartbeatScheduler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.interval = ServerConfig.getInt("heartbeat.interval.seconds", 15);
        this.timeout = ServerConfig.getInt("heartbeat.timeout.seconds", 20);

        scheduler.scheduleAtFixedRate(this::checkHeartbeats, interval, interval, TimeUnit.SECONDS);
    }

    public void register(String id, ClientHandler handler) {
    }

    public void unregister(String id) {
    }

    private void checkHeartbeats() {
        Instant now = Instant.now();
        Message heartbeatMsg = Message.createHeartbeat();

        for (var entry : connectionManager.getActiveClients().entrySet()) {
            String clientId = entry.getKey();
            ClientHandler handler = entry.getValue();

            if (now.minusSeconds(timeout).isAfter(handler.getLastHeartbeatAck())) {
                log.warn("Heartbeat timeout for client {}. Disconnecting.", clientId);
                handler.disconnect();
            } else {
                handler.send(heartbeatMsg);
            }
        }
    }

    public void shutdown() {
        scheduler.shutdownNow();
    }
}