package com.chatapp.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Model class representing a chat user
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String hostname;
    private String ipAddress;
    private boolean isActive;

    public User() {
    }

    public User(String username, String hostname, String ipAddress) {
        this.username = username;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.isActive = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username + " (" + ipAddress + ")";
    }
} 