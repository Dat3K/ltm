package com.chatapp.server;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.util.List;
import java.util.function.Consumer;

/**
 * Interface for chat server implementations (Strategy pattern)
 */
public interface ChatServer {
    /**
     * Start the server on the specified port
     * @param port Port number to run server on
     * @param onMessageReceived Callback for when a message is received
     * @param onClientConnected Callback for when a client connects
     * @param onClientDisconnected Callback for when a client disconnects
     */
    void start(int port, 
               Consumer<Message> onMessageReceived, 
               Consumer<User> onClientConnected, 
               Consumer<User> onClientDisconnected);
    
    /**
     * Stop the server
     */
    void stop();
    
    /**
     * Send a message to all connected clients
     * @param message The message to broadcast
     */
    void broadcastMessage(Message message);
    
    /**
     * Broadcast user list to all connected clients
     */
    void broadcastUserList();
    
    /**
     * Check if the server is running
     * @return true if server is running, false otherwise
     */
    boolean isRunning();
    
    /**
     * Get all connected users
     * @return List of connected users
     */
    List<User> getConnectedUsers();
} 