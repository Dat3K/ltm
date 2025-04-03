package com.chatapp.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.User;
import com.chatapp.protocol.MessageType;
import com.chatapp.protocol.Protocol;

public class ChatClient {
    private static final int PORT = 9000;
    
    private String serverHost;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private User user;
    private boolean connected;
    private ClientListener listener;
    private Thread listenerThread;
    private ChatClientListener clientListener;
    
    public ChatClient(String serverHost) {
        this.serverHost = serverHost;
        this.connected = false;
    }
    
    public boolean connect() {
        try {
            socket = new Socket(serverHost, PORT);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;
            
            // Start message listener
            listener = new ClientListener();
            listenerThread = new Thread(listener);
            listenerThread.start();
            
            return true;
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + serverHost);
            return false;
        } catch (IOException e) {
            System.err.println("Cannot connect to server: " + e.getMessage());
            return false;
        }
    }
    
    public boolean login(String username) {
        if (!connected) {
            return false;
        }
        
        try {
            // Send login request
            Protocol loginRequest = new Protocol(MessageType.LOGIN, username);
            output.writeObject(loginRequest);
            output.flush();
            return true;
        } catch (IOException e) {
            System.err.println("Login failed: " + e.getMessage());
            disconnect();
            return false;
        }
    }
    
    public void sendMessage(String messageContent) {
        if (!connected || user == null) {
            return;
        }
        
        try {
            ChatMessage message = new ChatMessage(user, messageContent);
            Protocol messageProtocol = new Protocol(MessageType.MESSAGE, message);
            output.writeObject(messageProtocol);
            output.flush();
            
            // Also notify the UI with own message
            if (clientListener != null) {
                clientListener.onMessageReceived(message);
            }
        } catch (IOException e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }
    
    public void disconnect() {
        try {
            if (connected) {
                // Send logout message
                Protocol logoutRequest = new Protocol(MessageType.LOGOUT, null);
                output.writeObject(logoutRequest);
                output.flush();
            }
        } catch (IOException e) {
            // Ignore errors on logout
        } finally {
            cleanup();
        }
    }
    
    private void cleanup() {
        connected = false;
        
        // Stop the listener thread
        if (listener != null) {
            listener.stop();
        }
        
        try {
            if (output != null) output.close();
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
    
    public void setClientListener(ChatClientListener listener) {
        this.clientListener = listener;
    }
    
    public User getUser() {
        return user;
    }
    
    public boolean isConnected() {
        return connected;
    }
    
    // Inner class to handle incoming messages
    private class ClientListener implements Runnable {
        private boolean running;
        
        public ClientListener() {
            this.running = true;
        }
        
        @Override
        public void run() {
            try {
                while (running) {
                    Protocol protocol = (Protocol) input.readObject();
                    
                    switch (protocol.getType()) {
                        case LOGIN_SUCCESS:
                            user = (User) protocol.getData();
                            if (clientListener != null) {
                                clientListener.onLoginSuccess(user);
                            }
                            break;
                        case HISTORY:
                            @SuppressWarnings("unchecked")
                            List<ChatMessage> history = (List<ChatMessage>) protocol.getData();
                            if (clientListener != null) {
                                clientListener.onHistoryReceived(history);
                            }
                            break;
                        case MESSAGE:
                            ChatMessage message = (ChatMessage) protocol.getData();
                            if (clientListener != null) {
                                clientListener.onMessageReceived(message);
                            }
                            break;
                        default:
                            // Ignore other message types
                            break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                if (running) {
                    // Only show error if not stopped voluntarily
                    System.err.println("Connection lost: " + e.getMessage());
                    cleanup();
                    
                    if (clientListener != null) {
                        clientListener.onConnectionLost();
                    }
                }
            }
        }
        
        public void stop() {
            running = false;
        }
    }
    
    // Interface for client events
    public interface ChatClientListener {
        void onLoginSuccess(User user);
        void onHistoryReceived(List<ChatMessage> history);
        void onMessageReceived(ChatMessage message);
        void onConnectionLost();
    }
} 