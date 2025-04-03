package com.chatapp.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.User;
import com.chatapp.protocol.MessageType;
import com.chatapp.protocol.Protocol;

public class ClientHandler implements Runnable {
    private ChatServer server;
    private Socket clientSocket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private User user;
    private boolean running;
    
    public ClientHandler(ChatServer server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
        this.running = true;
        
        try {
            // Important: Create output stream first to avoid deadlock
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            input = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        try {
            // Handle user registration
            Protocol loginMessage = (Protocol) input.readObject();
            
            if (loginMessage.getType() == MessageType.LOGIN) {
                String username = (String) loginMessage.getData();
                String hostname = clientSocket.getInetAddress().getHostName();
                String ipAddress = clientSocket.getInetAddress().getHostAddress();
                
                user = server.registerUser(username, hostname, ipAddress);
                
                // Send login success response
                sendProtocol(new Protocol(MessageType.LOGIN_SUCCESS, user));
                
                // Send chat history
                List<ChatMessage> history = server.getChatHistory();
                sendProtocol(new Protocol(MessageType.HISTORY, history));
            }
            
            // Handle messages
            while (running) {
                Protocol message = (Protocol) input.readObject();
                
                switch (message.getType()) {
                    case MESSAGE:
                        ChatMessage chatMessage = (ChatMessage) message.getData();
                        server.broadcast(chatMessage, this);
                        break;
                    case LOGOUT:
                        running = false;
                        break;
                    default:
                        // Ignore other message types
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Client disconnected or error occurred
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            close();
            server.removeClient(this);
        }
    }
    
    public void sendMessage(ChatMessage message) {
        sendProtocol(new Protocol(MessageType.MESSAGE, message));
    }
    
    private void sendProtocol(Protocol protocol) {
        try {
            output.writeObject(protocol);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        running = false;
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getClientAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }
}