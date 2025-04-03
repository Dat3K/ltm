package com.chatapp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton class for managing database connections
 */
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    
    private static final String URL = "jdbc:postgresql://dat3k-dat-3k.k.aivencloud.com:20132/defaultdb?ssl=require";
    private static final String USER = "avnadmin";
    private static final String PASSWORD = "AVNS__dA6FvGDyXtdkFcnDMC";
    
    private DatabaseConnection() {
        try {
            // Explicitly load the PostgreSQL JDBC driver (not strictly necessary with modern JDBC)
            Class.forName("org.postgresql.Driver");
            
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            initializeDatabase();
            System.out.println("Database connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found: " + e.getMessage());
        }
    }
    
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create messages table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS messages (" +
                    "id SERIAL PRIMARY KEY," +
                    "content TEXT NOT NULL," +
                    "sender_name VARCHAR(255) NOT NULL," +
                    "sender_ip VARCHAR(50) NOT NULL," +
                    "sender_username VARCHAR(255) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createTableSQL);
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing database tables: " + e.getMessage());
        }
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
} 