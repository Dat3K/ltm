package com.chatapp.protocol;

import java.io.Serializable;

public class Protocol implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private MessageType type;
    private Object data;
    
    public Protocol(MessageType type, Object data) {
        this.type = type;
        this.data = data;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
} 