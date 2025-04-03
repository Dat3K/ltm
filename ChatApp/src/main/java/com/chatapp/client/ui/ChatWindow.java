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
import com.chatapp.ui.AppColors;

public class ChatWindow extends JFrame implements ChatClientListener {
    private static final long serialVersionUID = 1L;
    
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
        getContentPane().setBackground(AppColors.BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
        
        // Top panel (header)
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, AppColors.PRIMARY_COLOR, getWidth(), getHeight(), AppColors.SECONDARY_COLOR);
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
        titleLabel.setForeground(AppColors.TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Status panel in header
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setOpaque(false);
        
        statusLabel = new JLabel("Not connected");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(new Color(0xff, 0xcc, 0xcc));
        statusPanel.add(statusLabel, BorderLayout.WEST);
        
        userInfoLabel = new JLabel();
        userInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userInfoLabel.setForeground(AppColors.TEXT_LIGHT);
        statusPanel.add(userInfoLabel, BorderLayout.EAST);
        
        headerPanel.add(statusPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 0));
        contentPanel.setBackground(AppColors.BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Chat area
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBackground(AppColors.PANEL_COLOR);
        chatPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chatArea.setBackground(AppColors.PANEL_COLOR);
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
        bottomPanel.setBackground(AppColors.PANEL_COLOR);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, AppColors.BORDER_COLOR),
                new EmptyBorder(10, 10, 10, 10)));
        
        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        bottomPanel.add(messageField, BorderLayout.CENTER);
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sendButton.setBackground(AppColors.SECONDARY_COLOR);
        sendButton.setForeground(AppColors.PRIMARY_COLOR);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.SECONDARY_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        
        // Add hover effect
        sendButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(AppColors.SECONDARY_HOVER);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sendButton.setBackground(AppColors.SECONDARY_COLOR);
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
                            statusLabel.setForeground(AppColors.ERROR_COLOR);
                            showErrorMessage("Failed to log in to the server");
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Connection failed");
                        statusLabel.setForeground(AppColors.ERROR_COLOR);
                        showErrorMessage("Failed to connect to the server at " + serverHost);
                    });
                }
            });
            connectionThread.start();
        }
    }
    
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
    
    private void appendToChatArea(String message, Color color) {
        try {
            Document doc = chatArea.getDocument();
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            StyleConstants.setForeground(attrs, color);
            
            doc.insertString(doc.getLength(), message + "\n", attrs);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onLoginSuccess(User user) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Connected");
            statusLabel.setForeground(AppColors.SUCCESS_COLOR);
            userInfoLabel.setText("Logged in as: " + user.getUsername());
            appendToChatArea("Connected to server as " + user.getUsername(), AppColors.SECONDARY_COLOR);
        });
    }
    
    @Override
    public void onHistoryReceived(List<ChatMessage> history) {
        SwingUtilities.invokeLater(() -> {
            if (history.isEmpty()) {
                appendToChatArea("No chat history", AppColors.TEXT_MUTED);
            } else {
                appendToChatArea("--- Chat History ---", AppColors.TEXT_MUTED);
                for (ChatMessage message : history) {
                    String formattedMessage = String.format("[%s] %s: %s", 
                            message.getFormattedTimestamp(), message.getUser().getUsername(), message.getContent());
                    appendToChatArea(formattedMessage, AppColors.PRIMARY_COLOR);
                }
                appendToChatArea("--- End of History ---", AppColors.TEXT_MUTED);
            }
        });
    }
    
    @Override
    public void onMessageReceived(ChatMessage message) {
        SwingUtilities.invokeLater(() -> {
            String formattedMessage = String.format("[%s] %s: %s", 
                    message.getFormattedTimestamp(), message.getUser().getUsername(), message.getContent());
            appendToChatArea(formattedMessage, AppColors.HIGHLIGHT_COLOR);
        });
    }
    
    @Override
    public void onConnectionLost() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Disconnected");
            statusLabel.setForeground(AppColors.ERROR_COLOR);
            appendToChatArea("Connection to server lost", AppColors.ERROR_COLOR);
            
            int option = JOptionPane.showConfirmDialog(
                    this,
                    "Connection to the server was lost. Do you want to reconnect?",
                    "Connection Lost",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            
            if (option == JOptionPane.YES_OPTION) {
                connect();
            }
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatWindow window = new ChatWindow();
            window.setVisible(true);
            window.connect();
        });
    }
} 