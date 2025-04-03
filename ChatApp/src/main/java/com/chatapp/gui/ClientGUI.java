package com.chatapp.gui;

import com.chatapp.client.ChatClient;
import com.chatapp.client.ChatClientFactory;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.util.NetworkProtocol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client GUI component
 */
public class ClientGUI extends JFrame {
    private JTextPane chatPane;
    private JTextField messageField;
    private JButton sendButton;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JTextField serverField;
    private JTextField portField;
    private JTextField usernameField;
    private JComboBox<NetworkProtocol> protocolComboBox;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    
    private ChatClient client;
    private User user;
    private StyledDocument doc;
    
    public ClientGUI() {
        setTitle("Chat Client");
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                }
            }
        });
    }
    
    private void initComponents() {
        // Content pane with BorderLayout
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        
        // Connection panel at the top
        JPanel connectionPanel = new JPanel(new BorderLayout(5, 5));
        connectionPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        
        JPanel connectionGrid = new JPanel(new GridLayout(3, 4, 5, 5));
        
        connectionGrid.add(new JLabel("Server:"));
        serverField = new JTextField("localhost");
        connectionGrid.add(serverField);
        
        connectionGrid.add(new JLabel("Server Port:"));
        portField = new JTextField("8888");
        connectionGrid.add(portField);
        
        connectionGrid.add(new JLabel("Username:"));
        usernameField = new JTextField();
        connectionGrid.add(usernameField);
        
        connectionGrid.add(new JLabel("Protocol:"));
        protocolComboBox = new JComboBox<>(NetworkProtocol.values());
        connectionGrid.add(protocolComboBox);
        
        connectionPanel.add(connectionGrid, BorderLayout.CENTER);
        
        JPanel connectionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        connectButton = new JButton("Connect");
        disconnectButton = new JButton("Disconnect");
        disconnectButton.setEnabled(false);
        connectionButtons.add(connectButton);
        connectionButtons.add(disconnectButton);
        
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setForeground(Color.RED);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(connectionButtons, BorderLayout.EAST);
        
        connectionPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.8);
        
        // Chat panel on the left
        JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
        
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        doc = chatPane.getStyledDocument();
        
        // Add styles for different message types
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style regularStyle = chatPane.addStyle("regular", defaultStyle);
        StyleConstants.setFontFamily(regularStyle, "SansSerif");
        
        Style systemStyle = chatPane.addStyle("system", regularStyle);
        StyleConstants.setItalic(systemStyle, true);
        StyleConstants.setForeground(systemStyle, Color.BLUE);
        
        Style timestampStyle = chatPane.addStyle("timestamp", regularStyle);
        StyleConstants.setForeground(timestampStyle, Color.GRAY);
        StyleConstants.setFontSize(timestampStyle, 10);
        
        JScrollPane chatScrollPane = new JScrollPane(chatPane);
        
        // Message input panel
        JPanel messagePanel = new JPanel(new BorderLayout(5, 5));
        messageField = new JTextField();
        sendButton = new JButton("Send");
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        // Compose chat panel
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);
        
        // User list panel on the right
        JPanel userPanel = new JPanel(new BorderLayout(5, 5));
        userPanel.setBorder(BorderFactory.createTitledBorder("Online Users"));
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        JScrollPane userScrollPane = new JScrollPane(userList);
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        
        // Add components to split pane
        splitPane.setLeftComponent(chatPanel);
        splitPane.setRightComponent(userPanel);
        
        // Add all to content pane
        contentPane.add(connectionPanel, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Add action listeners
        connectButton.addActionListener(this::connectToServer);
        disconnectButton.addActionListener(e -> disconnectFromServer());
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
        
        // Initial state
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
    }
    
    private void connectToServer(ActionEvent e) {
        String serverAddress = serverField.getText().trim();
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a username", 
                "Missing Information", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int port = Integer.parseInt(portField.getText().trim());
            NetworkProtocol protocol = (NetworkProtocol) protocolComboBox.getSelectedItem();
            
            // Get local hostname
            String hostname;
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException ex) {
                hostname = "Unknown";
            }
            
            // Create user
            user = new User(username, hostname, "");
            
            // Create client using factory
            client = ChatClientFactory.createClient(protocol);
            
            // Set UI to connecting state
            connectButton.setEnabled(false);
            statusLabel.setText("Status: Connecting...");
            statusLabel.setForeground(Color.ORANGE);
            
            // Connect to server
            boolean success = client.connect(serverAddress, port, user, 
                this::onMessageReceived, 
                this::onConnected, 
                this::onDisconnected, 
                this::onError);
            
            if (!success) {
                // Reset UI if connection failed
                connectButton.setEnabled(true);
                statusLabel.setText("Status: Connection Failed");
                statusLabel.setForeground(Color.RED);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid port number", 
                "Invalid Port", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void disconnectFromServer() {
        if (client != null) {
            client.disconnect();
        }
    }
    
    private void sendMessage() {
        if (client != null && client.isConnected()) {
            String content = messageField.getText().trim();
            if (!content.isEmpty()) {
                Message message = new Message(content, user.getHostname(), user.getIpAddress(), user.getUsername());
                client.sendMessage(message);
                messageField.setText("");
            }
        }
    }
    
    private void onMessageReceived(Message message) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Format timestamp
                String timestamp = formatTimestamp(message.getTimestamp().getTime());
                
                // Add text with appropriate styles
                doc.insertString(doc.getLength(), timestamp + " ", chatPane.getStyle("timestamp"));
                
                if ("Server".equals(message.getSenderUsername())) {
                    // Server messages with different style
                    doc.insertString(doc.getLength(), message.getContent() + "\n", chatPane.getStyle("system"));
                } else {
                    // Regular user messages
                    doc.insertString(doc.getLength(), message.getSenderUsername() + ": ", chatPane.getStyle("regular"));
                    doc.insertString(doc.getLength(), message.getContent() + "\n", chatPane.getStyle("regular"));
                }
                
                // Auto-scroll to bottom
                chatPane.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
        });
    }
    
    private void onConnected() {
        SwingUtilities.invokeLater(() -> {
            // Update UI
            connectButton.setEnabled(false);
            disconnectButton.setEnabled(true);
            serverField.setEnabled(false);
            portField.setEnabled(false);
            usernameField.setEnabled(false);
            protocolComboBox.setEnabled(false);
            messageField.setEnabled(true);
            sendButton.setEnabled(true);
            statusLabel.setText("Status: Connected");
            statusLabel.setForeground(new Color(0, 128, 0));
            
            // Add welcome message
            try {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " Connected to server.\n", 
                    chatPane.getStyle("system"));
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
        });
    }
    
    private void onDisconnected() {
        SwingUtilities.invokeLater(() -> {
            // Update UI
            connectButton.setEnabled(true);
            disconnectButton.setEnabled(false);
            serverField.setEnabled(true);
            portField.setEnabled(true);
            usernameField.setEnabled(true);
            protocolComboBox.setEnabled(true);
            messageField.setEnabled(false);
            sendButton.setEnabled(false);
            statusLabel.setText("Status: Disconnected");
            statusLabel.setForeground(Color.RED);
            userListModel.clear();
            
            // Add disconnection message
            try {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " Disconnected from server.\n", 
                    chatPane.getStyle("system"));
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
        });
    }
    
    private void onError(String error) {
        SwingUtilities.invokeLater(() -> {
            try {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " ERROR: " + error + "\n", 
                    chatPane.getStyle("system"));
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
        });
    }
    
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date(timestamp)) + "]";
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
} 