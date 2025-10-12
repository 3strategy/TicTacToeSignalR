package com.example.tictactoe;

import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

public class SignalRService {
    private static final String TAG = "SignalRService";

    private HubConnection connection;

    public interface Listener {
        void onConnected();
        void onDisconnected(Throwable error);
        void onReceiveMessage(String user, String message);
        void onReceiveKey(String key);
    }

    public void connect(String baseIp, Listener listener) {
        // Build hub URL: http://<ip>:8081/gamehub
        final String url = "http://" + baseIp + ":8081/gamehub";

        // Dispose previous connection if exists
        if (connection != null) {
            try { connection.stop(); } catch (Exception ignored) {}
            connection = null;
        }

        connection = HubConnectionBuilder.create(url).build();

        // Subscriptions
        connection.on("ReceiveMessage", (user, message) -> {
            Log.d(TAG, "ReceiveMessage: " + user + ": " + message);
            if (listener != null) listener.onReceiveMessage(user, message);
        }, String.class, String.class);

        connection.on("ReceiveKey", (key) -> {
            Log.d(TAG, "ReceiveKey: " + key);
            if (listener != null) listener.onReceiveKey(key);
        }, String.class);

        // Connect on background thread
        new Thread(() -> {
            try {
                connection.start().blockingAwait();
                Log.d(TAG, "Connected to " + url);
                if (listener != null) listener.onConnected();

                // Debug: send a test message like the C# sample
                try {
                    connection.send("SendMessage", "Jojo", "Hello everybody");
                } catch (Throwable t) {
                    Log.w(TAG, "SendMessage failed: " + t.getMessage());
                }
            } catch (Throwable t) {
                Log.e(TAG, "Hub exception: " + t.getMessage());
                if (listener != null) listener.onDisconnected(t);
            }
        }).start();
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.stop().blockingAwait();
            } catch (Throwable ignored) {
            } finally {
                connection = null;
            }
        }
    }
}

