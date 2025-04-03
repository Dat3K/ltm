package com.chatapp.dao;

import com.chatapp.model.Message;
import com.chatapp.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Message operations
 */
public class MessageDAO {
    private Connection connection;
    
    public MessageDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Save a message to the database
     */
    public boolean saveMessage(Message message) {
        String sql = "INSERT INTO messages (content, sender_name, sender_ip, sender_username, timestamp) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, message.getContent());
            pstmt.setString(2, message.getSenderName());
            pstmt.setString(3, message.getSenderIp());
            pstmt.setString(4, message.getSenderUsername());
            pstmt.setTimestamp(5, message.getTimestamp());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        message.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving message: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Get all messages from the database
     */
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY timestamp ASC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Message message = new Message();
                message.setId(rs.getInt("id"));
                message.setContent(rs.getString("content"));
                message.setSenderName(rs.getString("sender_name"));
                message.setSenderIp(rs.getString("sender_ip"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setTimestamp(rs.getTimestamp("timestamp"));
                
                messages.add(message);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving messages: " + e.getMessage());
        }
        
        return messages;
    }
    
    /**
     * Get recent messages (limit by count)
     */
    public List<Message> getRecentMessages(int limit) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages ORDER BY timestamp DESC LIMIT ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Message message = new Message();
                    message.setId(rs.getInt("id"));
                    message.setContent(rs.getString("content"));
                    message.setSenderName(rs.getString("sender_name"));
                    message.setSenderIp(rs.getString("sender_ip"));
                    message.setSenderUsername(rs.getString("sender_username"));
                    message.setTimestamp(rs.getTimestamp("timestamp"));
                    
                    messages.add(message);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving recent messages: " + e.getMessage());
        }
        
        return messages;
    }
} 