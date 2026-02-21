package com.example.tictactoe.services;

import android.util.Log;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

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
        final String url = "http://" + baseIp + ":80/gamehub";

        if (connection != null) {
            try {
                connection.stop();
            } catch (Exception ignored) {
            }
            connection = null;
        }

        connection = HubConnectionBuilder.create(url).build();

        connection.on("ReceiveMessage", (user, message) -> {
            Log.d(TAG, "ReceiveMessage: " + user + ": " + message);
            if (listener != null) {
                listener.onReceiveMessage(user, message);
            }
        }, String.class, String.class);

        connection.on("ReceiveKey", (key) -> {
            Log.d(TAG, "ReceiveKey: " + key);
            if (listener != null) {
                listener.onReceiveKey(key);
            }
        }, String.class);

        new Thread(() -> {
            try {
                connection.start().blockingAwait();
                Log.d(TAG, "Connected to " + url);
                if (listener != null) {
                    listener.onConnected();
                }

                try {
                    connection.send("SendMessage", "Jojo", "Hello everybody");
                } catch (Throwable t) {
                    Log.w(TAG, "SendMessage failed: " + t.getMessage());
                }
            } catch (Throwable t) {
                Log.e(TAG, "Hub exception: " + t.getMessage());
                if (listener != null) {
                    listener.onDisconnected(t);
                }
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

    public boolean isConnected() {
        return connection != null && connection.getConnectionState() == HubConnectionState.CONNECTED;
    }

    public void sendMove(int row, int col, String player) {
        String key = row + "," + col + "," + player;
        sendKey(key);
    }

    public void sendKey(String key) {
        if (connection == null) {
            Log.w(TAG, "sendKey called but connection is null");
            return;
        }
        try {
            connection.send("SendKey", key);
        } catch (Throwable t) {
            Log.e(TAG, "Failed to send key: " + t.getMessage());
        }
    }
}
