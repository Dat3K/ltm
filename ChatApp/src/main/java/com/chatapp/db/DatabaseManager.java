package com.chatapp.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.User;

/**
 * Singleton database manager class that handles database operations
 * using PostgreSQL JDBC driver 42.7.5
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String JDBC_URL = "jdbc:postgresql://dat3k-dat-3k.k.aivencloud.com:20132/defaultdb?ssl=require&user=avnadmin&password=AVNS__dA6FvGDyXtdkFcnDMC";
    private Connection connection;

    private DatabaseManager() {
        try {
            // Load PostgreSQL JDBC Driver (version 42.7.5)
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(JDBC_URL);
            initDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initDatabase() {
        try {
            // Read and execute the init.sql script
            String createUsersSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "hostname VARCHAR(100) NOT NULL, " +
                    "ip_address VARCHAR(45) NOT NULL, " +
                    "last_login TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP)";
            
            String createMessagesSql = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id SERIAL PRIMARY KEY, " +
                    "user_id INT REFERENCES users(id), " +
                    "content TEXT NOT NULL, " +
                    "timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP)";
            
            String createMessagesTimestampIndex = "CREATE INDEX IF NOT EXISTS idx_messages_timestamp ON messages(timestamp)";
            String createMessagesUserIdIndex = "CREATE INDEX IF NOT EXISTS idx_messages_user_id ON messages(user_id)";
            
            PreparedStatement stmt = connection.prepareStatement(createUsersSql);
            stmt.execute();
            stmt = connection.prepareStatement(createMessagesSql);
            stmt.execute();
            stmt = connection.prepareStatement(createMessagesTimestampIndex);
            stmt.execute();
            stmt = connection.prepareStatement(createMessagesUserIdIndex);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public User saveUser(String username, String hostname, String ipAddress) {
        User user = null;
        try {
            // Check if user exists
            String checkSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // User exists, update last login
                int userId = rs.getInt("id");
                String updateSql = "UPDATE users SET hostname = ?, ip_address = ?, last_login = ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setString(1, hostname);
                updateStmt.setString(2, ipAddress);
                updateStmt.setTimestamp(3, new Timestamp(new Date().getTime()));
                updateStmt.setInt(4, userId);
                updateStmt.executeUpdate();
                
                user = new User(userId, username, hostname, ipAddress);
            } else {
                // New user, insert
                String insertSql = "INSERT INTO users (username, hostname, ip_address) VALUES (?, ?, ?) RETURNING id";
                PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                insertStmt.setString(1, username);
                insertStmt.setString(2, hostname);
                insertStmt.setString(3, ipAddress);
                ResultSet insertRs = insertStmt.executeQuery();
                
                if (insertRs.next()) {
                    int newUserId = insertRs.getInt("id");
                    user = new User(newUserId, username, hostname, ipAddress);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void saveMessage(int userId, String content) {
        try {
            String sql = "INSERT INTO messages (user_id, content) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, content);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ChatMessage> getRecentMessages(int limit) {
        List<ChatMessage> messages = new ArrayList<>();
        try {
            String sql = "SELECT m.id, m.content, m.timestamp, u.id as user_id, u.username, u.hostname, u.ip_address " +
                    "FROM messages m JOIN users u ON m.user_id = u.id " +
                    "ORDER BY m.timestamp DESC LIMIT ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("hostname"),
                    rs.getString("ip_address")
                );
                
                ChatMessage message = new ChatMessage(
                    rs.getInt("id"),
                    user,
                    rs.getString("content"),
                    rs.getTimestamp("timestamp")
                );
                
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
} 