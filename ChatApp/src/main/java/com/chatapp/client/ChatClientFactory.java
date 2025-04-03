package com.chatapp.client;

import com.chatapp.util.NetworkProtocol;

/**
 * Factory class for creating ChatClient instances (Factory pattern)
 */
public class ChatClientFactory {
    
    /**
     * Create a ChatClient instance based on the specified protocol
     * @param protocol The network protocol to use
     * @return A ChatClient implementation
     */
    public static ChatClient createClient(NetworkProtocol protocol) {
        switch (protocol) {
            case UDP:
                return new UDPChatClient();
            case TCP:
            default:
                return new TCPChatClient();
        }
    }
} 