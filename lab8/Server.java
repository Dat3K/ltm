package lab8;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * Server component for Lab 8
 * This server listens for client connections, receives strings,
 * converts them to uppercase, and sends them back to the client.
 */
public class Server extends JFrame {
    // UI Components
    private JTextArea logArea;
    private JTextField portField;
    private JButton startStopButton;
    private JLabel statusLabel;
    
    // Network components
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private Thread serverThread;
    
    // Default port
    private static final int DEFAULT_PORT = 8888;
    
    public Server() {
        setTitle("Lab 8 - Server");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(String.valueOf(DEFAULT_PORT), 5);
        startStopButton = new JButton("Start Server");
        statusLabel = new JLabel("Server Status: Stopped");
        statusLabel.setForeground(Color.RED);
        
        controlPanel.add(portLabel);
        controlPanel.add(portField);
        controlPanel.add(startStopButton);
        controlPanel.add(statusLabel);
        
        // Create log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Server Log"));
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        add(mainPanel);
        
        // Add action listener for start/stop button
        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isRunning) {
                    stopServer();
                } else {
                    startServer();
                }
            }
        });
        
        // Add window listener to stop server when window is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (isRunning) {
                    stopServer();
                }
            }
        });
    }
    
    /**
     * Starts the server on the specified port
     */
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            
            // Disable UI components
            portField.setEnabled(false);
            startStopButton.setText("Stop Server");
            
            // Start server in a separate thread
            serverThread = new Thread(() -> {
                try {
                    // Create server socket
                    serverSocket = new ServerSocket(port);
                    isRunning = true;
                    
                    // Update UI
                    SwingUtilities.invokeLater(() -> {
                        statusLabel.setText("Server Status: Running");
                        statusLabel.setForeground(Color.GREEN.darker());
                        logArea.append("Server started on port " + port + "\n");
                        logArea.append("Waiting for client connections...\n");
                    });
                    
                    // Accept client connections
                    while (isRunning) {
                        try {
                            // Accept client connection
                            Socket clientSocket = serverSocket.accept();
                            
                            // Handle client in a separate thread
                            new Thread(() -> handleClient(clientSocket)).start();
                        } catch (IOException e) {
                            if (isRunning) {
                                SwingUtilities.invokeLater(() -> {
                                    logArea.append("Error accepting client connection: " + e.getMessage() + "\n");
                                });
                            }
                        }
                    }
                } catch (IOException e) {
                    SwingUtilities.invokeLater(() -> {
                        logArea.append("Error starting server: " + e.getMessage() + "\n");
                        stopServer();
                    });
                }
            });
            
            serverThread.start();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid port number", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Stops the server
     */
    private void stopServer() {
        isRunning = false;
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            logArea.append("Error closing server socket: " + e.getMessage() + "\n");
        }
        
        // Update UI
        statusLabel.setText("Server Status: Stopped");
        statusLabel.setForeground(Color.RED);
        startStopButton.setText("Start Server");
        portField.setEnabled(true);
        logArea.append("Server stopped\n");
    }
    
    /**
     * Handles a client connection
     * @param clientSocket The client socket
     */
    private void handleClient(Socket clientSocket) {
        try {
            // Get client information
            String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
            
            // Log client connection
            SwingUtilities.invokeLater(() -> {
                logArea.append("Client connected: " + clientInfo + "\n");
            });
            
            // Create input/output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Read input from client
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                final String input = inputLine;
                
                // Log received message
                SwingUtilities.invokeLater(() -> {
                    logArea.append("Received from " + clientInfo + ": " + input + "\n");
                });
                
                // Process the input (convert to uppercase)
                String outputLine = input.toUpperCase();
                
                // Send response back to client
                out.println(outputLine);
                
                // Log sent message
                SwingUtilities.invokeLater(() -> {
                    logArea.append("Sent to " + clientInfo + ": " + outputLine + "\n");
                });
            }
            
            // Close resources
            in.close();
            out.close();
            clientSocket.close();
            
            // Log client disconnection
            SwingUtilities.invokeLater(() -> {
                logArea.append("Client disconnected: " + clientInfo + "\n");
            });
            
        } catch (IOException e) {
            SwingUtilities.invokeLater(() -> {
                logArea.append("Error handling client: " + e.getMessage() + "\n");
            });
        }
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Server().setVisible(true);
        });
    }
}
