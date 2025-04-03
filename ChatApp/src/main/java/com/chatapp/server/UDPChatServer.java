package com.chatapp.server;

import com.chatapp.dao.MessageDAO;
import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * UDP implementation of ChatServer interface
 */
public class UDPChatServer implements ChatServer {
    private final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();
    private DatagramSocket socket;
    private ExecutorService executor;
    private boolean running;
    private final MessageDAO messageDAO;
    private final int BUFFER_SIZE = 8192;
    private Consumer<Message> onMessageReceived;
    private Consumer<User> onClientConnected;
    private Consumer<User> onClientDisconnected;

    public UDPChatServer() {
        this.messageDAO = new MessageDAO();
    }

    @Override
    public void start(int port, Consumer<Message> onMessageReceived, Consumer<User> onClientConnected, Consumer<User> onClientDisconnected) {
        try {
            socket = new DatagramSocket(port);
            executor = Executors.newCachedThreadPool();
            running = true;
            this.onMessageReceived = onMessageReceived;
            this.onClientConnected = onClientConnected;
            this.onClientDisconnected = onClientDisconnected;

            System.out.println("UDP Server started on port " + port);

            // Thread for receiving packets
            executor.execute(this::receivePackets);
            
            // Thread for periodically checking client connections
            executor.execute(this::checkClientsConnection);
        } catch (SocketException e) {
            System.err.println("Failed to start UDP server: " + e.getMessage());
        }
    }

    private void receivePackets() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (running) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // Process packet in a separate thread
                executor.execute(() -> processPacket(packet));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error receiving UDP packet: " + e.getMessage());
                }
            }
        }
    }
    
    private void processPacket(DatagramPacket packet) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            
            Object obj = ois.readObject();
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            
            if (obj instanceof User) {
                // User registration/connection
                User user = (User) obj;
                user.setIpAddress(clientAddress.getHostAddress());
                
                String clientKey = clientAddress.getHostAddress() + ":" + clientPort;
                
                // Add to connected clients or update if exists
                ClientInfo clientInfo = new ClientInfo(user, clientAddress, clientPort);
                clients.put(clientKey, clientInfo);
                
                // Notify about new connection
                onClientConnected.accept(user);
                
                // Gửi danh sách người dùng cập nhật cho tất cả client
                broadcastUserList();
                
                // Send recent messages
                List<Message> recentMessages = messageDAO.getRecentMessages(50);
                for (Message message : recentMessages) {
                    sendToClient(message, clientAddress, clientPort);
                }
                
                // Welcome message với danh sách người dùng
                Message welcomeMessage = new Message(
                    "Welcome to the chat server!", 
                    "Server", 
                    "localhost", 
                    "Server",
                    getConnectedUsers()
                );
                sendToClient(welcomeMessage, clientAddress, clientPort);
                
            } else if (obj instanceof Message) {
                // Chat message
                Message message = (Message) obj;
                onMessageReceived.accept(message);
                broadcastMessage(message);
            } else if (obj instanceof String) {
                // Heartbeat or other control messages
                String control = (String) obj;
                
                if ("HEARTBEAT".equals(control)) {
                    String clientKey = clientAddress.getHostAddress() + ":" + clientPort;
                    ClientInfo clientInfo = clients.get(clientKey);
                    if (clientInfo != null) {
                        clientInfo.updateLastSeen();
                    }
                }
            }
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error processing UDP packet: " + e.getMessage());
        }
    }
    
    private void checkClientsConnection() {
        while (running) {
            try {
                // Check every 30 seconds
                Thread.sleep(30000);
                
                long currentTime = System.currentTimeMillis();
                List<String> disconnectedClients = new ArrayList<>();
                List<User> disconnectedUsers = new ArrayList<>();
                
                // Check for clients that haven't sent heartbeats recently
                for (Map.Entry<String, ClientInfo> entry : clients.entrySet()) {
                    ClientInfo clientInfo = entry.getValue();
                    
                    // If no activity for 1 minute, consider disconnected
                    if (currentTime - clientInfo.getLastSeen() > 60000) {
                        disconnectedClients.add(entry.getKey());
                        disconnectedUsers.add(clientInfo.getUser());
                    }
                }
                
                // Remove disconnected clients
                boolean hasDisconnections = false;
                for (int i = 0; i < disconnectedClients.size(); i++) {
                    String key = disconnectedClients.get(i);
                    clients.remove(key);
                    onClientDisconnected.accept(disconnectedUsers.get(i));
                    hasDisconnections = true;
                }
                
                // Nếu có người dùng nào ngắt kết nối, gửi danh sách người dùng cập nhật
                if (hasDisconnections) {
                    broadcastUserList();
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void stop() {
        running = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
        
        clients.clear();
        
        System.out.println("UDP Server stopped");
    }

    @Override
    public void broadcastMessage(Message message) {
        // Save message to database
        messageDAO.saveMessage(message);
        
        // Send to all connected clients
        for (ClientInfo clientInfo : clients.values()) {
            sendToClient(message, clientInfo.getAddress(), clientInfo.getPort());
        }
    }
    
    // Phương thức mới để gửi danh sách người dùng cập nhật cho tất cả client
    @Override
    public void broadcastUserList() {
        if (!running) return;
        
        // Tạo tin nhắn từ server chứa danh sách người dùng
        Message userListMessage = new Message(
            "User list updated", 
            "Server", 
            "localhost", 
            "Server", 
            getConnectedUsers()
        );
        
        // Gửi tin nhắn cho tất cả client không lưu vào database
        for (ClientInfo clientInfo : clients.values()) {
            sendToClient(userListMessage, clientInfo.getAddress(), clientInfo.getPort());
        }
    }
    
    private void sendToClient(Object obj, InetAddress address, int port) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            
            oos.writeObject(obj);
            oos.flush();
            
            byte[] data = bos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            
        } catch (IOException e) {
            System.err.println("Error sending UDP packet: " + e.getMessage());
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public List<User> getConnectedUsers() {
        List<User> users = new ArrayList<>();
        for (ClientInfo clientInfo : clients.values()) {
            users.add(clientInfo.getUser());
        }
        return users;
    }
    
    /**
     * Inner class to hold client information
     */
    private static class ClientInfo {
        private final User user;
        private final InetAddress address;
        private final int port;
        private long lastSeen;
        
        public ClientInfo(User user, InetAddress address, int port) {
            this.user = user;
            this.address = address;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }
        
        public User getUser() {
            return user;
        }
        
        public InetAddress getAddress() {
            return address;
        }
        
        public int getPort() {
            return port;
        }
        
        public long getLastSeen() {
            return lastSeen;
        }
        
        public void updateLastSeen() {
            this.lastSeen = System.currentTimeMillis();
        }
    }
} 