package com.chatapp.client;

import com.chatapp.model.Message;
import com.chatapp.model.User;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * UDP implementation of the ChatClient interface
 */
public class UDPChatClient implements ChatClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private ExecutorService executor;
    private ScheduledExecutorService scheduledExecutor;
    private boolean connected;
    private Consumer<Message> onMessageReceived;
    private Runnable onConnected;
    private Runnable onDisconnected;
    private Consumer<String> onError;
    private User user;
    private final int BUFFER_SIZE = 8192;

    @Override
    public boolean connect(String serverAddressStr, int port, User user,
                          Consumer<Message> onMessageReceived,
                          Runnable onConnected,
                          Runnable onDisconnected,
                          Consumer<String> onError) {
        this.onMessageReceived = onMessageReceived;
        this.onConnected = onConnected;
        this.onDisconnected = onDisconnected;
        this.onError = onError;
        this.user = user;
        
        try {
            // Create socket
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(serverAddressStr);
            serverPort = port;
            
            // Send user information
            sendObject(user);
            
            connected = true;
            
            // Start message listener
            executor = Executors.newSingleThreadExecutor();
            executor.execute(this::listenForMessages);
            
            // Start heartbeat sender
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutor.scheduleAtFixedRate(this::sendHeartbeat, 20, 20, TimeUnit.SECONDS);
            
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
    
    private void sendHeartbeat() {
        try {
            if (connected) {
                sendObject("HEARTBEAT");
            }
        } catch (Exception e) {
            if (onError != null) {
                onError.accept("Failed to send heartbeat: " + e.getMessage());
            }
        }
    }
    
    private void sendObject(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            
            oos.writeObject(obj);
            oos.flush();
            
            byte[] data = bos.toByteArray();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, serverPort);
            socket.send(packet);
            
        } catch (IOException e) {
            if (onError != null) {
                onError.accept("Failed to send object: " + e.getMessage());
            }
            disconnect();
        }
    }

    private void listenForMessages() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (connected) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // Create a new buffer with only the data received
                byte[] data = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
                
                try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
                     ObjectInputStream ois = new ObjectInputStream(bis)) {
                    
                    Object obj = ois.readObject();
                    
                    if (obj instanceof Message) {
                        Message message = (Message) obj;
                        if (onMessageReceived != null) {
                            onMessageReceived.accept(message);
                        }
                    }
                } catch (ClassNotFoundException e) {
                    if (onError != null) {
                        onError.accept("Error processing received data: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                if (connected) {
                    if (onError != null) {
                        onError.accept("Error receiving message: " + e.getMessage());
                    }
                    disconnect();
                }
                break;
            }
        }
        
        if (onDisconnected != null) {
            onDisconnected.run();
        }
    }

    @Override
    public void disconnect() {
        connected = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        if (executor != null) {
            executor.shutdownNow();
        }
        
        if (scheduledExecutor != null) {
            scheduledExecutor.shutdownNow();
        }
    }

    @Override
    public boolean sendMessage(Message message) {
        if (!connected || socket == null || socket.isClosed()) {
            return false;
        }
        
        try {
            sendObject(message);
            return true;
        } catch (Exception e) {
            if (onError != null) {
                onError.accept("Failed to send message: " + e.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
} 