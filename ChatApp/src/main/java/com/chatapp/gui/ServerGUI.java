package com.chatapp.gui;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.server.ChatServer;
import com.chatapp.server.ChatServerFactory;
import com.chatapp.util.NetworkProtocol;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Server GUI component - Modern UI design with enhanced UX features
 */
public class ServerGUI extends JFrame {
    // UI Components
    private JTextPane logPane;
    private JTextField broadcastField;
    private JButton broadcastButton;
    private JList<User> clientList;
    private DefaultListModel<User> clientListModel;
    private JTextField portField;
    private JComboBox<NetworkProtocol> protocolComboBox;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    private JLabel clientCountLabel;
    private JPanel clientPanel;
    private JScrollPane logScrollPane;
    
    // Colors and UI constants - Modern Material palette
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);     // Indigo 500
    private static final Color PRIMARY_DARK_COLOR = new Color(48, 63, 159); // Indigo 700
    private static final Color ACCENT_COLOR = new Color(255, 64, 129);     // Pink A400
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);     // Green 500
    private static final Color ERROR_COLOR = new Color(244, 67, 54);       // Red 500
    private static final Color WARNING_COLOR = new Color(255, 152, 0);     // Orange 500
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250); // Grey 50
    private static final Color CARD_COLOR = new Color(255, 255, 255);      // White
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);       // Grey 900
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);  // Grey 600
    private static final Color DIVIDER_COLOR = new Color(224, 224, 224);   // Grey 300
    
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font DEFAULT_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font LOG_FONT = new Font("Consolas", Font.PLAIN, 13);
    private static final Font TIMESTAMP_FONT = new Font("Segoe UI", Font.ITALIC, 11);
    private static final Font USERNAME_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    private static final int PADDING = 12;
    private static final int BORDER_RADIUS = 8;
    private static final int COMPONENT_SPACING = 10;
    
    // Server data
    private ChatServer server;
    private StyledDocument doc;
    private int messageCount = 0;
    
    public ServerGUI() {
        setTitle("Modern Chat Server");
        setSize(900, 700);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon if available
        try {
            setIconImage(new ImageIcon(getClass().getResource("/server_icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        initComponents();
        setupKeyboardShortcuts();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (server != null && server.isRunning()) {
                    server.stop();
                }
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        // Add keyboard shortcuts
        getRootPane().registerKeyboardAction(
            e -> startServer(null),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> stopServer(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> clearLog(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void initComponents() {
        // Set UI Font
        setUIFont(DEFAULT_FONT);
        
        // Content pane with BorderLayout
        JPanel contentPane = new JPanel(new BorderLayout(PADDING, PADDING));
        contentPane.setBorder(new EmptyBorder(PADDING, PADDING, PADDING, PADDING));
        contentPane.setBackground(BACKGROUND_COLOR);
        setContentPane(contentPane);
        
        // Server control panel at the top
        JPanel controlPanel = createControlPanel();
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.8);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // Log panel on the left
        JPanel logPanel = createLogPanel();
        
        // Client list panel on the right
        JPanel clientPanel = createClientListPanel();
        
        // Add components to split pane
        splitPane.setLeftComponent(logPanel);
        splitPane.setRightComponent(clientPanel);
        
        // Create status bar at bottom
        JPanel statusBar = createStatusBar();
        
        // Add all to content pane
        contentPane.add(controlPanel, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        
        // Initial state
        broadcastField.setEnabled(false);
        broadcastButton.setEnabled(false);
        stopButton.setEnabled(false);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1, true),
                new EmptyBorder(5, 5, 5, 5)
            ),
            "Server Controls"
        ));
        controlPanel.setBackground(BACKGROUND_COLOR);
        
        // Server settings
        JPanel settingsGrid = new JPanel(new GridLayout(1, 4, 8, 8));
        settingsGrid.setBackground(BACKGROUND_COLOR);
        
        JLabel portLabel = new JLabel("Port:");
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        settingsGrid.add(portLabel);
        
        portField = new JTextField("8888");
        portField.setToolTipText("Enter port number to listen on (default: 8888)");
        settingsGrid.add(portField);
        
        JLabel protocolLabel = new JLabel("Protocol:");
        protocolLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        settingsGrid.add(protocolLabel);
        
        protocolComboBox = new JComboBox<>(NetworkProtocol.values());
        protocolComboBox.setToolTipText("Select protocol for server communication");
        settingsGrid.add(protocolComboBox);
        
        controlPanel.add(settingsGrid, BorderLayout.CENTER);
        
        // Server control buttons
        JPanel controlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlButtons.setBackground(BACKGROUND_COLOR);
        
        startButton = createStyledButton("Start Server", SUCCESS_COLOR);
        startButton.setToolTipText("Start chat server (F5)");
        
        stopButton = createStyledButton("Stop Server", ERROR_COLOR);
        stopButton.setToolTipText("Stop chat server (ESC)");
        stopButton.setEnabled(false);
        
        controlButtons.add(startButton);
        controlButtons.add(stopButton);
        
        // Status label
        statusLabel = new JLabel("Status: Stopped");
        statusLabel.setForeground(ERROR_COLOR);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(controlButtons, BorderLayout.EAST);
        
        controlPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        startButton.addActionListener(this::startServer);
        stopButton.addActionListener(e -> stopServer());
        
        return controlPanel;
    }
    
    private JPanel createLogPanel() {
        JPanel logPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        logPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING),
            BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
                ),
                "Server Log",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT,
                PRIMARY_COLOR
            )
        ));
        logPanel.setBackground(BACKGROUND_COLOR);
        
        // Create log text area with custom styling
        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setBackground(CARD_COLOR);
        logPane.setFont(LOG_FONT);
        logPane.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        doc = logPane.getStyledDocument();
        
        // Add styles for different log entry types
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style regularStyle = logPane.addStyle("regular", defaultStyle);
        StyleConstants.setFontFamily(regularStyle, LOG_FONT.getFamily());
        StyleConstants.setFontSize(regularStyle, LOG_FONT.getSize());
        StyleConstants.setForeground(regularStyle, TEXT_PRIMARY);
        
        Style infoStyle = logPane.addStyle("info", regularStyle);
        StyleConstants.setForeground(infoStyle, new Color(33, 150, 243)); // Blue 500
        
        Style successStyle = logPane.addStyle("success", regularStyle);
        StyleConstants.setForeground(successStyle, SUCCESS_COLOR);
        
        Style errorStyle = logPane.addStyle("error", regularStyle);
        StyleConstants.setForeground(errorStyle, ERROR_COLOR);
        
        Style warningStyle = logPane.addStyle("warning", regularStyle);
        StyleConstants.setForeground(warningStyle, WARNING_COLOR);
        
        Style timestampStyle = logPane.addStyle("timestamp", regularStyle);
        StyleConstants.setForeground(timestampStyle, TEXT_SECONDARY);
        StyleConstants.setFontFamily(timestampStyle, TIMESTAMP_FONT.getFamily());
        StyleConstants.setFontSize(timestampStyle, TIMESTAMP_FONT.getSize());
        StyleConstants.setItalic(timestampStyle, true);
        
        // Create scrollable log area with custom scrollbar
        logScrollPane = new JScrollPane(logPane);
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        logScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Add shadow effect to log panel
        logScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        ));
        
        // Add broadcast input panel
        JPanel broadcastPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, 0));
        broadcastPanel.setBackground(CARD_COLOR);
        broadcastPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(PADDING - 2, PADDING - 2, PADDING - 2, PADDING - 2)
        ));
        
        // Broadcast text field with modern styling
        broadcastField = new JTextField();
        broadcastField.setFont(DEFAULT_FONT);
        broadcastField.setEnabled(false);
        broadcastField.setMargin(new Insets(PADDING - 2, PADDING - 2, PADDING - 2, PADDING - 2));
        broadcastField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        // Styling broadcast field
        broadcastField.setBackground(CARD_COLOR);
        broadcastField.setForeground(TEXT_PRIMARY);
        broadcastField.setCaretColor(PRIMARY_COLOR);
        
        // Create a placeholder effect using focus listeners
        broadcastField.putClientProperty("JTextField.placeholderText", "Type announcement for all clients...");
        
        // Broadcast button with modern styling
        broadcastButton = createStyledButton("Broadcast", ACCENT_COLOR);
        broadcastButton.setToolTipText("Send announcement to all connected clients");
        broadcastButton.setEnabled(false);
        
        // Add components to broadcast panel
        broadcastPanel.add(broadcastField, BorderLayout.CENTER);
        broadcastPanel.add(broadcastButton, BorderLayout.EAST);
        
        // Add action listeners
        broadcastButton.addActionListener(e -> broadcastMessage());
        broadcastField.addActionListener(e -> broadcastMessage());
        
        // Add components to log panel
        logPanel.add(logScrollPane, BorderLayout.CENTER);
        logPanel.add(broadcastPanel, BorderLayout.SOUTH);
        
        // Add button to clear the log
        JButton clearLogButton = createStyledButton("Clear Log", WARNING_COLOR);
        clearLogButton.setToolTipText("Clear all log entries (F4)");
        clearLogButton.addActionListener(e -> clearLog());
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setOpaque(false);
        controlPanel.add(clearLogButton);
        
        logPanel.add(controlPanel, BorderLayout.NORTH);
        
        return logPanel;
    }
    
    private JPanel createClientListPanel() {
        clientPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        clientPanel.setBackground(BACKGROUND_COLOR);
        clientPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING),
            BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
                ),
                "Connected Clients",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT,
                PRIMARY_COLOR
            )
        ));
        
        // Create and configure client list
        clientListModel = new DefaultListModel<>();
        clientList = new JList<>(clientListModel);
        clientList.setCellRenderer(new ClientListCellRenderer());
        clientList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        clientList.setBackground(CARD_COLOR);
        clientList.setFont(DEFAULT_FONT);
        
        // Add custom scrollable with modern styling
        JScrollPane clientScrollPane = new JScrollPane(clientList);
        clientScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        clientScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        clientScrollPane.setBorder(BorderFactory.createEmptyBorder());
        clientScrollPane.getViewport().setBackground(CARD_COLOR);
        
        // Add shadow effect to client list
        clientScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        ));
        
        clientPanel.add(clientScrollPane, BorderLayout.CENTER);
        
        return clientPanel;
    }
    
    // Custom renderer for client list with avatars
    class ClientListCellRenderer extends DefaultListCellRenderer {
        private final int AVATAR_SIZE = 32;
        
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JPanel panel = new JPanel(new BorderLayout(10, 0));
            panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            
            if (isSelected) {
                panel.setBackground(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 40));
            } else {
                panel.setBackground(CARD_COLOR);
            }
            
            User user = (User) value;
            
            // Create avatar
            JLabel avatarLabel = new JLabel(new ClientAvatar(user.getUsername(), AVATAR_SIZE));
            avatarLabel.setPreferredSize(new Dimension(AVATAR_SIZE, AVATAR_SIZE));
            
            // Create username label
            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setForeground(TEXT_PRIMARY);
            usernameLabel.setFont(DEFAULT_FONT);
            
            // Create info label (showing IP)
            JLabel infoLabel = new JLabel(user.getIpAddress());
            infoLabel.setForeground(TEXT_SECONDARY);
            infoLabel.setFont(new Font(DEFAULT_FONT.getName(), Font.PLAIN, DEFAULT_FONT.getSize() - 2));
            
            // Create a panel for username and info
            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            textPanel.add(usernameLabel);
            textPanel.add(infoLabel);
            
            // Add components to panel
            panel.add(avatarLabel, BorderLayout.WEST);
            panel.add(textPanel, BorderLayout.CENTER);
            
            return panel;
        }
        
        // Inner class for avatar icon
        private class ClientAvatar implements Icon {
            private final Color avatarColor;
            private final String initials;
            private final int size;
            
            public ClientAvatar(String username, int size) {
                this.size = size;
                
                // Generate color based on username hash
                int hash = username.hashCode();
                float hue = (hash % 360) / 360.0f;
                this.avatarColor = Color.getHSBColor(hue, 0.5f, 0.8f);
                
                // Get user initials (up to 2 characters)
                if (username.length() > 0) {
                    if (username.contains(" ")) {
                        String[] parts = username.split(" ");
                        this.initials = (parts[0].substring(0, 1) + parts[1].substring(0, 1)).toUpperCase();
                    } else {
                        this.initials = username.substring(0, Math.min(2, username.length())).toUpperCase();
                    }
                } else {
                    this.initials = "?";
                }
            }
            
            @Override
            public int getIconWidth() {
                return size;
            }
            
            @Override
            public int getIconHeight() {
                return size;
            }
            
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular background
                g2d.setColor(avatarColor);
                g2d.fillOval(x, y, size, size);
                
                // Draw user initials
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                
                FontMetrics metrics = g2d.getFontMetrics();
                int textX = x + (size - metrics.stringWidth(initials)) / 2;
                int textY = y + ((size - metrics.getHeight()) / 2) + metrics.getAscent();
                
                g2d.drawString(initials, textX, textY);
                g2d.dispose();
            }
        }
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        statusBar.setBackground(BACKGROUND_COLOR);
        
        // Client count label
        clientCountLabel = new JLabel("Connected clients: 0");
        clientCountLabel.setForeground(new Color(100, 100, 100));
        statusBar.add(clientCountLabel, BorderLayout.WEST);
        
        // Version label
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setForeground(new Color(150, 150, 150));
        statusBar.add(versionLabel, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(DEFAULT_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(8, 16, 8, 16));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(darkenColor(color));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        // Create rounded button with drop shadow
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
            )
        ));
        
        return button;
    }
    
    private Color darkenColor(Color color) {
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        return Color.getHSBColor(hsb[0], hsb[1], Math.max(hsb[2] - 0.1f, 0));
    }
    
    private void setUIFont(Font font) {
        // Set default font for all UI components
        UIManager.put("Button.font", font);
        UIManager.put("ToggleButton.font", font);
        UIManager.put("RadioButton.font", font);
        UIManager.put("CheckBox.font", font);
        UIManager.put("ComboBox.font", font);
        UIManager.put("Label.font", font);
        UIManager.put("List.font", font);
        UIManager.put("TextField.font", font);
        UIManager.put("TextArea.font", font);
        UIManager.put("TextPane.font", font);
        UIManager.put("EditorPane.font", font);
        UIManager.put("TabbedPane.font", font);
        UIManager.put("TitledBorder.font", font);
    }
    
    private void startServer(ActionEvent e) {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            NetworkProtocol protocol = (NetworkProtocol) protocolComboBox.getSelectedItem();
            
            // Check if port is already in use before starting the server
            if (isPortInUse(port)) {
                logError("Port " + port + " is already in use. Please choose a different port.");
                JOptionPane.showMessageDialog(this, 
                    "Port " + port + " is already in use.\nPlease choose a different port.", 
                    "Port Already In Use", JOptionPane.ERROR_MESSAGE);
                portField.requestFocus();
                portField.selectAll();
                return;
            }
            
            // Create server using factory
            server = ChatServerFactory.createServer(protocol);
            
            // Update UI before starting server
            startButton.setEnabled(false);
            portField.setEnabled(false);
            protocolComboBox.setEnabled(false);
            statusLabel.setText("Status: Starting...");
            statusLabel.setForeground(WARNING_COLOR);
            
            // Log start attempt
            logInfo("Starting " + protocol + " server on port " + port + "...");
            
            // Start server
            server.start(port, this::onMessageReceived, this::onClientConnected, this::onClientDisconnected);
            
            // Update UI after server started
            stopButton.setEnabled(true);
            broadcastField.setEnabled(true);
            broadcastButton.setEnabled(true);
            statusLabel.setText("Status: Running");
            statusLabel.setForeground(SUCCESS_COLOR);
            
            // Log success
            logSuccess("Server started successfully.");
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid port number", 
                "Invalid Port", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
        } catch (Exception ex) {
            logError("Error starting server: " + ex.getMessage());
            statusLabel.setText("Status: Error");
            statusLabel.setForeground(ERROR_COLOR);
            startButton.setEnabled(true);
        }
    }
    
    /**
     * Checks if a specific port is already in use
     * @param port The port to check
     * @return true if the port is already in use, false otherwise
     */
    private boolean isPortInUse(int port) {
        try (java.net.ServerSocket serverSocket = new java.net.ServerSocket(port)) {
            // Port is available
            return false;
        } catch (java.net.BindException e) {
            // Port is in use
            return true;
        } catch (java.io.IOException e) {
            // Other IO issue occurred
            logWarning("Error checking port availability: " + e.getMessage());
            return false;
        }
    }
    
    private void stopServer() {
        if (server != null && server.isRunning()) {
            // Log stop attempt
            logInfo("Stopping server...");
            
            // Stop server
            server.stop();
        
        // Update UI
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        portField.setEnabled(true);
        protocolComboBox.setEnabled(true);
            broadcastField.setEnabled(false);
            broadcastButton.setEnabled(false);
            statusLabel.setText("Status: Stopped");
            statusLabel.setForeground(ERROR_COLOR);
        
        // Clear client list
        clientListModel.clear();
            updateClientCount();
            
            // Log success
            logSuccess("Server stopped successfully.");
        }
    }
    
    private void clearLog() {
        try {
            doc.remove(0, doc.getLength());
            logInfo("Log cleared.");
            messageCount = 0;
            updateMessageCount();
        } catch (BadLocationException e) {
            System.err.println("Error clearing log: " + e.getMessage());
        }
    }
    
    private void broadcastMessage() {
        if (server != null && server.isRunning()) {
            String content = broadcastField.getText().trim();
            if (!content.isEmpty()) {
                // Create server message
                Message message = new Message(content, "Server", "localhost", "Server");
                
                // Broadcast message
                server.broadcastMessage(message);
                
                // Log the broadcast
                logInfo("Broadcast: " + content);
                
                // Clear field
                broadcastField.setText("");
                broadcastField.requestFocus();
            }
        }
    }
    
    private void onMessageReceived(Message message) {
        SwingUtilities.invokeLater(() -> {
            // Log message
            String sender = message.getSenderUsername();
            String content = message.getContent();
            logInfo("Message from " + sender + ": " + content);
            
            // Update message count
            messageCount++;
            updateMessageCount();
        });
    }
    
    private void onClientConnected(User user) {
        SwingUtilities.invokeLater(() -> {
            // Thay đổi từ toString() thành thêm trực tiếp đối tượng User
            System.out.println("Client connected: " + user.getUsername() + " from " + user.getIpAddress());
            clientListModel.addElement(user);
            
            // Log connection
            logSuccess("Client connected: " + user);
            
            // Update client count
            updateClientCount();
        });
    }
    
    private void onClientDisconnected(User user) {
        SwingUtilities.invokeLater(() -> {
            if (user == null) {
                return; // Ngăn xử lý nếu user là null
            }

            // Kiểm tra xem user có thực sự tồn tại trong danh sách không
            boolean userFound = false;
            for (int i = 0; i < clientListModel.getSize(); i++) {
                User listUser = clientListModel.getElementAt(i);
                if (listUser.getUsername().equals(user.getUsername())) {
                    clientListModel.remove(i);
                    userFound = true;
                    // Log disconnection - chỉ log khi thực sự tìm thấy và xóa user
                    logWarning("Client disconnected: " + user.getUsername() + " (" + user.getIpAddress() + ")");
                    break;
                }
            }
            
            // Chỉ cập nhật số lượng client khi có sự thay đổi thực sự trong danh sách
            if (userFound) {
                // Debug log to console
                System.out.println("Client disconnected and removed: " + user.getUsername() + " from " + user.getIpAddress());
                // Update client count
                updateClientCount();
            }
        });
    }
    
    private void updateClientCount() {
        clientCountLabel.setText("Connected clients: " + clientListModel.getSize());
    }
    
    private void updateMessageCount() {
        // Could be used in status bar if needed
    }
    
    private void logEntry(String message, String style) {
        try {
            // Format timestamp
            String timestamp = formatTimestamp(System.currentTimeMillis());
            
            // Add log entry with timestamp
            doc.insertString(doc.getLength(), timestamp + " ", logPane.getStyle("timestamp"));
            doc.insertString(doc.getLength(), message + "\n", logPane.getStyle(style));
            
            // Auto-scroll to bottom
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            System.err.println("Error adding log entry: " + e.getMessage());
        }
    }
    
    private void logInfo(String message) {
        logEntry(message, "info");
    }
    
    private void logSuccess(String message) {
        logEntry(message, "success");
    }
    
    private void logError(String message) {
        logEntry(message, "error");
        
        // Notify user with sound if available
        Toolkit.getDefaultToolkit().beep();
    }
    
    private void logWarning(String message) {
        logEntry(message, "warning");
    }
    
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date(timestamp)) + "]";
    }
    
    public static void main(String[] args) {
        try {
            // Try to use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ServerGUI().setVisible(true);
        });
    }
} 