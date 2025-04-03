package com.chatapp.server;

import com.chatapp.dao.MessageDAO;
import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * TCP implementation of ChatServer interface
 */
public class TCPChatServer implements ChatServer {
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private final List<User> connectedUsers = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private boolean running;
    private final MessageDAO messageDAO;

    public TCPChatServer() {
        this.messageDAO = new MessageDAO();
    }

    @Override
    public void start(int port, Consumer<Message> onMessageReceived, Consumer<User> onClientConnected, Consumer<User> onClientDisconnected) {
        try {
            serverSocket = new ServerSocket(port);
            executor = Executors.newCachedThreadPool();
            running = true;

            System.out.println("TCP Server started on port " + port);

            // Thread for accepting client connections
            executor.execute(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler clientHandler = new ClientHandler(socket, onMessageReceived, onClientConnected, onClientDisconnected);
                        clients.add(clientHandler);
                        executor.execute(clientHandler);
                    } catch (IOException e) {
                        if (running) {
                            System.err.println("Error accepting client connection: " + e.getMessage());
                        }
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to start TCP server: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        running = false;
        
        // Close all client connections
        for (ClientHandler client : clients) {
            client.close();
        }
        clients.clear();
        connectedUsers.clear();
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing server socket: " + e.getMessage());
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
        
        System.out.println("TCP Server stopped");
    }

    @Override
    public void broadcastMessage(Message message) {
        // Save message to database
        messageDAO.saveMessage(message);
        
        // Send to all connected clients
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public List<User> getConnectedUsers() {
        return new ArrayList<>(connectedUsers);
    }

    /**
     * Inner class to handle client connections
     */
    private class ClientHandler implements Runnable {
        private final Socket socket;
        private final Consumer<Message> onMessageReceived;
        private final Consumer<User> onClientConnected;
        private final Consumer<User> onClientDisconnected;
        private ObjectOutputStream outputStream;
        private ObjectInputStream inputStream;
        private User user;

        public ClientHandler(Socket socket, Consumer<Message> onMessageReceived, 
                             Consumer<User> onClientConnected, Consumer<User> onClientDisconnected) {
            this.socket = socket;
            this.onMessageReceived = onMessageReceived;
            this.onClientConnected = onClientConnected;
            this.onClientDisconnected = onClientDisconnected;
        }

        @Override
        public void run() {
            try {
                // Set up streams
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());

                // Read user information
                user = (User) inputStream.readObject();
                user.setIpAddress(socket.getInetAddress().getHostAddress());
                
                // Add to connected users
                connectedUsers.add(user);
                onClientConnected.accept(user);

                // Send recent messages to the client
                List<Message> recentMessages = messageDAO.getRecentMessages(20);
                for (Message message : recentMessages) {
                    outputStream.writeObject(message);
                }
                outputStream.writeObject(new Message("Welcome to the chat server!", "Server", "localhost", "Server"));
                outputStream.flush();

                // Process client messages
                while (running) {
                    try {
                        Message message = (Message) inputStream.readObject();
                        onMessageReceived.accept(message);
                        broadcastMessage(message);
                    } catch (IOException | ClassNotFoundException e) {
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                close();
            }
        }

        public void sendMessage(Message message) {
            try {
                if (outputStream != null) {
                    outputStream.writeObject(message);
                    outputStream.flush();
                }
            } catch (IOException e) {
                System.err.println("Error sending message to client: " + e.getMessage());
                close();
            }
        }

        public void close() {
            try {
                if (user != null) {
                    connectedUsers.remove(user);
                    onClientDisconnected.accept(user);
                }
                
                clients.remove(this);
                
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
                System.err.println("Error closing client handler: " + e.getMessage());
            }
        }
    }
} 