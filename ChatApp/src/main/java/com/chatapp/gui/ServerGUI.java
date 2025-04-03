package com.chatapp.gui;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.server.ChatServer;
import com.chatapp.server.ChatServerFactory;
import com.chatapp.util.NetworkProtocol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Server GUI component
 */
public class ServerGUI extends JFrame {
    private JTextArea logArea;
    private JList<User> clientList;
    private DefaultListModel<User> clientListModel;
    private JButton startButton;
    private JButton stopButton;
    private JTextField portField;
    private JComboBox<NetworkProtocol> protocolComboBox;
    private JLabel statusLabel;
    
    private ChatServer server;
    
    public ServerGUI() {
        setTitle("Chat Server");
        setSize(800, 600);
        setMinimumSize(new Dimension(640, 480));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null && server.isRunning()) {
                    server.stop();
                }
            }
        });
    }
    
    private void initComponents() {
        // Content pane with BorderLayout
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(contentPane);
        
        // Control panel at the top
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Server Control"));
        
        JPanel controlsGrid = new JPanel(new GridLayout(2, 3, 5, 5));
        
        controlsGrid.add(new JLabel("Protocol:"));
        protocolComboBox = new JComboBox<>(NetworkProtocol.values());
        controlsGrid.add(protocolComboBox);
        
        controlsGrid.add(new JLabel("Server Port:"));
        portField = new JTextField("8888");
        controlsGrid.add(portField);
        
        statusLabel = new JLabel("Server Status: Stopped");
        statusLabel.setForeground(Color.RED);
        controlsGrid.add(statusLabel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        startButton = new JButton("Start Server");
        stopButton = new JButton("Stop Server");
        stopButton.setEnabled(false);
        
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        
        controlPanel.add(controlsGrid, BorderLayout.CENTER);
        controlPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        logArea.setMargin(new Insets(5, 5, 5, 5));
        
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Client list panel
        JPanel clientPanel = new JPanel(new BorderLayout(5, 5));
        clientPanel.setBorder(BorderFactory.createTitledBorder("Connected Clients"));
        
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        
        // Add components to split pane
        splitPane.setLeftComponent(logPanel);
        splitPane.setRightComponent(clientPanel);
        
        // Add all to content pane
        contentPane.add(controlPanel, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
        
        // Add action listeners
        startButton.addActionListener(e -> startServer());
        stopButton.addActionListener(e -> stopServer());
    }
    
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            NetworkProtocol protocol = (NetworkProtocol) protocolComboBox.getSelectedItem();
            
            // Create server instance using factory
            server = ChatServerFactory.createServer(protocol);
            
            // Start server with callbacks
            server.start(port, 
                this::onMessageReceived, 
                this::onClientConnected, 
                this::onClientDisconnected);
            
            // Update UI
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
            protocolComboBox.setEnabled(false);
            statusLabel.setText("Server Status: Running");
            statusLabel.setForeground(new Color(0, 128, 0));
            
            // Log
            logMessage("Server started using " + protocol + " protocol on port " + port);
            
            try {
                String hostname = InetAddress.getLocalHost().getHostName();
                String ip = InetAddress.getLocalHost().getHostAddress();
                logMessage("Server hostname: " + hostname);
                logMessage("Server IP address: " + ip);
            } catch (UnknownHostException e) {
                logMessage("Could not determine server host information");
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid port number", 
                "Invalid Port", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error starting server: " + e.getMessage(), 
                "Server Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void stopServer() {
        if (server != null) {
            server.stop();
        }
        
        // Update UI
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        portField.setEnabled(true);
        protocolComboBox.setEnabled(true);
        statusLabel.setText("Server Status: Stopped");
        statusLabel.setForeground(Color.RED);
        
        // Clear client list
        clientListModel.clear();
        
        // Log
        logMessage("Server stopped");
    }
    
    private void onMessageReceived(Message message) {
        String logEntry = String.format("%s [%s] %s: %s", 
            formatTimestamp(message.getTimestamp().getTime()),
            message.getSenderIp(),
            message.getSenderUsername(),
            message.getContent());
        
        logMessage(logEntry);
    }
    
    private void onClientConnected(User user) {
        SwingUtilities.invokeLater(() -> {
            clientListModel.addElement(user);
            logMessage("Client connected: " + user.getUsername() + " (" + user.getIpAddress() + ")");
        });
    }
    
    private void onClientDisconnected(User user) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < clientListModel.size(); i++) {
                if (clientListModel.get(i).getUsername().equals(user.getUsername())) {
                    clientListModel.remove(i);
                    break;
                }
            }
            logMessage("Client disconnected: " + user.getUsername() + " (" + user.getIpAddress() + ")");
        });
    }
    
    private void logMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(formatTimestamp(System.currentTimeMillis()) + " " + message + "\n");
            // Auto-scroll to bottom
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return "[" + sdf.format(new Date(timestamp)) + "]";
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }
} 