package lab8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Client component for Lab 8
 * This client connects to the server, sends strings,
 * and displays the uppercase response from the server.
 */
public class Client extends JFrame {
    // UI Components
    private JTextField serverField;
    private JTextField portField;
    private JTextField inputField;
    private JTextArea resultArea;
    private JButton connectButton;
    private JButton sendButton;
    private JLabel statusLabel;
    
    // Network components
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;
    
    // Default values
    private static final String DEFAULT_SERVER = "localhost";
    private static final int DEFAULT_PORT = 8888;
    
    public Client() {
        setTitle("Lab 8 - Client");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create connection panel
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel serverLabel = new JLabel("Server:");
        serverField = new JTextField(DEFAULT_SERVER, 10);
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(String.valueOf(DEFAULT_PORT), 5);
        connectButton = new JButton("Connect");
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setForeground(Color.RED);
        
        connectionPanel.add(serverLabel);
        connectionPanel.add(serverField);
        connectionPanel.add(portLabel);
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        connectionPanel.add(statusLabel);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        
        JLabel inputLabel = new JLabel("Enter text:");
        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setEnabled(false);
        
        JPanel inputControlPanel = new JPanel(new BorderLayout());
        inputControlPanel.add(inputLabel, BorderLayout.WEST);
        inputControlPanel.add(inputField, BorderLayout.CENTER);
        inputControlPanel.add(sendButton, BorderLayout.EAST);
        
        inputPanel.add(inputControlPanel, BorderLayout.NORTH);
        
        // Create result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Result from Server"));
        
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(connectionPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);
        
        // Set preferred sizes
        resultArea.setPreferredSize(new Dimension(480, 150));
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listeners
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isConnected) {
                    disconnect();
                } else {
                    connect();
                }
            }
        });
        
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Add key listener to input field
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        // Add window listener to disconnect when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isConnected) {
                    disconnect();
                }
            }
        });
    }
    
    /**
     * Connects to the server
     */
    private void connect() {
        try {
            String server = serverField.getText().trim();
            int port = Integer.parseInt(portField.getText().trim());
            
            // Disable UI components
            serverField.setEnabled(false);
            portField.setEnabled(false);
            connectButton.setText("Disconnect");
            
            // Connect to server in a separate thread
            new Thread(() -> {
                try {
                    // Create socket
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(server, port), 5000); // 5 second timeout
                    
                    // Create input/output streams
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    
                    // Update UI
                    SwingUtilities.invokeLater(() -> {
                        isConnected = true;
                        statusLabel.setText("Status: Connected");
                        statusLabel.setForeground(Color.GREEN.darker());
                        sendButton.setEnabled(true);
                        resultArea.append("Connected to " + server + ":" + port + "\n");
                    });
                    
                    // Start listening for server responses
                    String response;
                    while ((response = in.readLine()) != null) {
                        final String result = response;
                        SwingUtilities.invokeLater(() -> {
                            resultArea.append("Server response: " + result + "\n");
                        });
                    }
                    
                    // If we get here, the server closed the connection
                    SwingUtilities.invokeLater(() -> {
                        disconnect();
                        resultArea.append("Server closed the connection\n");
                    });
                    
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        resultArea.append("Error connecting to server: " + e.getMessage() + "\n");
                        disconnect();
                    });
                }
            }).start();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Disconnects from the server
     */
    private void disconnect() {
        isConnected = false;
        
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            resultArea.append("Error disconnecting: " + e.getMessage() + "\n");
        }
        
        // Update UI
        statusLabel.setText("Status: Disconnected");
        statusLabel.setForeground(Color.RED);
        connectButton.setText("Connect");
        serverField.setEnabled(true);
        portField.setEnabled(true);
        sendButton.setEnabled(false);
        resultArea.append("Disconnected from server\n");
    }
    
    /**
     * Sends a message to the server
     */
    private void sendMessage() {
        if (isConnected && out != null) {
            String message = inputField.getText().trim();
            
            if (!message.isEmpty()) {
                // Send message to server
                out.println(message);
                
                // Log sent message
                resultArea.append("Sent: " + message + "\n");
                
                // Clear input field
                inputField.setText("");
            }
        }
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Client().setVisible(true);
        });
    }
}
