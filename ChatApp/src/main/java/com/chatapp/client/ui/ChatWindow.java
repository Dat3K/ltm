package com.chatapp.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.chatapp.client.ChatClient;
import com.chatapp.client.ChatClient.ChatClientListener;
import com.chatapp.model.ChatMessage;
import com.chatapp.model.User;

public class ChatWindow extends JFrame implements ChatClientListener {
    private static final long serialVersionUID = 1L;
    
    // Palette mới
    private static final Color PRIMARY_COLOR = new Color(22, 196, 127);  // Xanh lục bảo
    private static final Color SECONDARY_COLOR = new Color(255, 214, 90); // Vàng
    private static final Color WARNING_COLOR = new Color(255, 157, 35);  // Cam
    private static final Color DANGER_COLOR = new Color(249, 56, 39);    // Đỏ
    
    // Màu sắc phụ
    private static final Color PRIMARY_DARK = new Color(18, 156, 101);   // Xanh lục bảo tối
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);  // Màu nền xám nhạt
    private static final Color PANEL_COLOR = new Color(255, 255, 255);   // Màu panel trắng
    
    // Message colors with better contrast
    private static final Color MY_MESSAGE_COLOR = new Color(0, 120, 80);  // Màu tin nhắn của tôi
    private static final Color OTHER_MESSAGE_COLOR = new Color(70, 70, 70);  // Màu tin nhắn người khác
    private static final Color SYSTEM_MESSAGE_COLOR = DANGER_COLOR;  // Màu thông báo hệ thống
    private static final Color HISTORY_MESSAGE_COLOR = new Color(100, 100, 100);  // Màu lịch sử
    
    // Status colors with better contrast
    private static final Color CONNECTED_COLOR = PRIMARY_COLOR;  // Màu kết nối
    private static final Color DISCONNECTED_COLOR = DANGER_COLOR;  // Màu ngắt kết nối
    private static final Color CONNECTING_COLOR = WARNING_COLOR;  // Màu đang kết nối
    
    private JTextPane chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JLabel statusLabel;
    private JLabel userInfoLabel;
    
    private ChatClient client;
    
    public ChatWindow() {
        setTitle("Chat Application");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setupListeners();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null) {
                    client.disconnect();
                }
            }
        });
    }
    
    private void initComponents() {
        // Main layout with modern design
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
        
        // Top panel (header)
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_COLOR, getWidth(), getHeight(), PRIMARY_DARK);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(800, 60));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // App title
        JLabel titleLabel = new JLabel("Chat Application");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Status panel in header
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setOpaque(false);
        
        statusLabel = new JLabel("Not connected");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(DISCONNECTED_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userInfoLabel.setForeground(Color.WHITE);
        statusPanel.add(userInfoLabel, BorderLayout.EAST);
        
        headerPanel.add(statusPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Chat area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(PANEL_COLOR);
        chatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(PANEL_COLOR);
        chatArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        
        // Auto-scroll chat area
        DefaultCaret caret = (DefaultCaret) chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(chatPanel, BorderLayout.CENTER);
        
        // Bottom panel (input area)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setBackground(PANEL_COLOR);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)));
        
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(PRIMARY_COLOR);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_DARK, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        
        // Add hover effect
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(PRIMARY_DARK);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(PRIMARY_COLOR);
            }
        });
        
        bottomPanel.add(sendButton, BorderLayout.EAST);
        
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void setupListeners() {
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }
    
    private void sendMessage() {
        if (client == null || !client.isConnected()) {
            showErrorMessage("Not connected to the server");
            return;
        }
        
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            client.sendMessage(message);
            messageField.setText("");
        }
    }
    
    public void connect() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);
        
        if (loginDialog.isLoginSuccessful()) {
            String username = loginDialog.getUsername();
            String serverHost = loginDialog.getServerHost();
            
            statusLabel.setText("Connecting to " + serverHost + "...");
            statusLabel.setForeground(CONNECTING_COLOR);
            
            // Connect in background thread to avoid UI freezing
            Thread connectionThread = new Thread(() -> {
                client = new ChatClient(serverHost);
                client.setClientListener(this);
                
                boolean connected = client.connect();
                if (connected) {
                    boolean loggedIn = client.login(username);
                    if (!loggedIn) {
                        SwingUtilities.invokeLater(() -> {
                            statusLabel.setText("Login failed");
                            statusLabel.setForeground(DISCONNECTED_COLOR);
                            showErrorMessage("Failed to log in to the server");
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Connection failed");
                        statusLabel.setForeground(DISCONNECTED_COLOR);
                        showErrorMessage("Failed to connect to the server");
                    });
                }
            });
            
            connectionThread.start();
        } else {
            System.exit(0); // Exit if login canceled
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, 
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void appendToChatArea(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            Document doc = chatArea.getDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            StyleConstants.setFontFamily(attrs, "Segoe UI");
            
            try {
                doc.insertString(doc.getLength(), message + "\n", attrs);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }
    
    @Override
    public void onLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Connected");
            statusLabel.setForeground(CONNECTED_COLOR);
            userInfoLabel.setText("Logged in as: " + user.getUsername());
            setTitle("Chat Application - " + user.getUsername());
        });
    }
    
    @Override
    public void onHistoryReceived(List<ChatMessage> history) {
        SwingUtilities.invokeLater(() -> {
            if (!history.isEmpty()) {
                appendToChatArea("--- Chat History ---", HISTORY_MESSAGE_COLOR);
                
                // Display messages in chronological order (most recent last)
                for (int i = history.size() - 1; i >= 0; i--) {
                    ChatMessage message = history.get(i);
                    appendToChatArea(message.toString(), HISTORY_MESSAGE_COLOR);
                }
                
                appendToChatArea("--- End of History ---", HISTORY_MESSAGE_COLOR);
            }
        });
    }
    
    @Override
    public void onMessageReceived(ChatMessage message) {
        Color messageColor = OTHER_MESSAGE_COLOR;
        
        // Show own messages in a different color
        if (client.getUser() != null && 
                message.getUser().getId() == client.getUser().getId()) {
            messageColor = MY_MESSAGE_COLOR;
        }
        
        appendToChatArea(message.toString(), messageColor);
    }
    
    @Override
    public void onConnectionLost() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Disconnected");
            statusLabel.setForeground(DISCONNECTED_COLOR);
            appendToChatArea("--- Connection to server lost ---", SYSTEM_MESSAGE_COLOR);
            
            // Offer reconnection
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Connection to server lost. Would you like to reconnect?",
                    "Connection Lost",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                connect();
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ChatWindow chatWindow = new ChatWindow();
            chatWindow.setVisible(true);
            chatWindow.connect();
        });
    }
} 