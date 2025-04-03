package com.chatapp.model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String hostname;
    private String ipAddress;
    
    public User(int id, String username, String hostname, String ipAddress) {
        this.id = id;
        this.username = username;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
    }
    
    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    @Override
    public String toString() {
        return username;
    }
} 