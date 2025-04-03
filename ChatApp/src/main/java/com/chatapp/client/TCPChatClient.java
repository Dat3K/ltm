package com.chatapp.client;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * TCP implementation of the ChatClient interface
 */
public class TCPChatClient implements ChatClient {
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private ExecutorService executor;
    private boolean connected;
    private Consumer<Message> onMessageReceived;
    private Runnable onConnected;
    private Runnable onDisconnected;
    private Consumer<String> onError;

    @Override
    public boolean connect(String serverAddress, int port, User user,
                          Consumer<Message> onMessageReceived,
                          Runnable onConnected,
                          Runnable onDisconnected,
                          Consumer<String> onError) {
        this.onMessageReceived = onMessageReceived;
        this.onConnected = onConnected;
        this.onDisconnected = onDisconnected;
        this.onError = onError;
        
        try {
            // Connect to server
            socket = new Socket(serverAddress, port);
            
            // Initialize streams
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
            
            // Send user information
            outputStream.writeObject(user);
            outputStream.flush();
            
            connected = true;
            
            // Start message listener
            executor = Executors.newSingleThreadExecutor();
            executor.execute(this::listenForMessages);
            
            if (onConnected != null) {
                onConnected.run();
            }
            
            return true;
        } catch (IOException e) {
            if (onError != null) {
                onError.accept("Failed to connect: " + e.getMessage());
            }
            disconnect();
            return false;
        }
    }

    private void listenForMessages() {
        try {
            while (connected) {
                try {
                    Object obj = inputStream.readObject();
                    if (obj instanceof Message) {
                        Message message = (Message) obj;
                        if (onMessageReceived != null) {
                            onMessageReceived.accept(message);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    if (connected) {
                        if (onError != null) {
                            onError.accept("Error receiving message: " + e.getMessage());
                        }
                        disconnect();
                    }
                    break;
                }
            }
        } finally {
            if (onDisconnected != null) {
                onDisconnected.run();
            }
        }
    }

    @Override
    public void disconnect() {
        connected = false;
        
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            if (onError != null) {
                onError.accept("Error disconnecting: " + e.getMessage());
            }
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    @Override
    public boolean sendMessage(Message message) {
        if (!connected || outputStream == null) {
            return false;
        }
        
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            if (onError != null) {
                onError.accept("Failed to send message: " + e.getMessage());
            }
            disconnect();
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
} 