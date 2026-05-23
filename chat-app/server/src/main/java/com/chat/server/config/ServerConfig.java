package com.chat.server.config;

import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = ServerConfig.class.getResourceAsStream("/chat.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Failed to load chat.properties");
        }
    }

    public static int getInt(String key, int def) {
        String val = props.getProperty(key);
        return val != null ? Integer.parseInt(val) : def;
    }
}