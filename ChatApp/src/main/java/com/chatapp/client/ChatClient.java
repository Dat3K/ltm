package com.chatapp.client;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.function.Consumer;

/**
 * Interface for chat client implementations (Strategy pattern)
 */
public interface ChatClient {
    /**
     * Connect to a chat server
     * @param serverAddress The server address to connect to
     * @param port The server port to connect to
     * @param user The user information for this client
     * @param onMessageReceived Callback for when a message is received
     * @param onConnected Callback for when connection is established
     * @param onDisconnected Callback for when disconnected from server
     * @param onError Callback for when an error occurs
     * @return true if connected successfully, false otherwise
     */
    boolean connect(String serverAddress, int port, User user,
                   Consumer<Message> onMessageReceived,
                   Runnable onConnected,
                   Runnable onDisconnected,
                   Consumer<String> onError);
    
    /**
     * Disconnect from the server
     */
    void disconnect();
    
    /**
     * Send a message to the server
     * @param message The message to send
     * @return true if sent successfully, false otherwise
     */
    boolean sendMessage(Message message);
    
    /**
     * Check if connected to server
     * @return true if connected, false otherwise
     */
    boolean isConnected();
} 