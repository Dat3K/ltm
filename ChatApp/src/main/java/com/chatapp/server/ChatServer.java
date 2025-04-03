package com.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.chatapp.db.DatabaseManager;
import com.chatapp.model.ChatMessage;
import com.chatapp.model.User;

public class ChatServer {
    private static final int PORT = 9000;
    private static final int MAX_HISTORY_MESSAGES = 50;
    
    private ServerSocket serverSocket;
    private List<ClientHandler> clients;
    private ExecutorService executor;
    private DatabaseManager dbManager;
    
    public ChatServer() {
        clients = new ArrayList<>();
        executor = Executors.newCachedThreadPool();
        dbManager = DatabaseManager.getInstance();
    }
    
    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Chat Server started on port " + PORT);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());
                
                ClientHandler handler = new ClientHandler(this, clientSocket);
                clients.add(handler);
                executor.execute(handler);
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            stop();
        }
    }
    
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            
            for (ClientHandler client : clients) {
                client.close();
            }
            
            executor.shutdown();
            dbManager.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void broadcast(ChatMessage message, ClientHandler sender) {
        // Save message to database
        dbManager.saveMessage(message.getUser().getId(), message.getContent());
        
        // Broadcast to all clients
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
    
    public User registerUser(String username, String hostname, String ipAddress) {
        return dbManager.saveUser(username, hostname, ipAddress);
    }
    
    public List<ChatMessage> getChatHistory() {
        return dbManager.getRecentMessages(MAX_HISTORY_MESSAGES);
    }
    
    public void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Client disconnected: " + client.getClientAddress());
    }
    
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start();
    }
} 