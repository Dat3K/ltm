package com.chatapp.server;

import com.chatapp.util.NetworkProtocol;

/**
 * Factory class for creating ChatServer instances (Factory pattern)
 */
public class ChatServerFactory {
    
    /**
     * Create a ChatServer instance based on the specified protocol
     * @param protocol The network protocol to use
     * @return A ChatServer implementation
     */
    public static ChatServer createServer(NetworkProtocol protocol) {
        switch (protocol) {
            case UDP:
                return new UDPChatServer();
            case TCP:
            default:
                return new TCPChatServer();
        }
    }
} 