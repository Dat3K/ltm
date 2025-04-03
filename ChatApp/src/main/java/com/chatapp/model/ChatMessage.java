package com.chatapp.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private User user;
    private String content;
    private Timestamp timestamp;
    
    public ChatMessage(User user, String content) {
        this.user = user;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
    
    public ChatMessage(int id, User user, String content, Timestamp timestamp) {
        this.id = id;
        this.user = user;
        this.content = content;
        this.timestamp = timestamp;
    }
    
    public int getId() {
        return id;
    }
    
    public User getUser() {
        return user;
    }
    
    public String getContent() {
        return content;
    }
    
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
                getFormattedTimestamp(), 
                user.getUsername(), 
                content);
    }
} 