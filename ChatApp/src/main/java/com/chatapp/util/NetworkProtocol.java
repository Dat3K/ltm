package com.chatapp.util;

/**
 * Enum representing network protocol types
 */
public enum NetworkProtocol {
    TCP,
    UDP;
    
    @Override
    public String toString() {
        return name();
    }
    
    public static NetworkProtocol fromString(String protocol) {
        try {
            return valueOf(protocol.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TCP; // Default to TCP if invalid
        }
    }
} 