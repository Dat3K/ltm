package com.chatapp.gui;

import com.chatapp.client.ChatClient;
import com.chatapp.client.ChatClientFactory;
import com.chatapp.dao.MessageDAO;
import com.chatapp.model.Message;
import com.chatapp.model.User;
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
 * Client GUI component - Modern UI design with enhanced UX features
 */
public class ClientGUI extends JFrame {
    // UI Components
    private JTextPane chatPane;
    private JTextField messageField;
    private JButton sendButton;
    private JButton loadHistoryButton;
    private JButton clearChatButton;
    private JList<User> userList;
    private DefaultListModel<User> userListModel;
    private JTextField serverField;
    private JTextField portField;
    private JTextField usernameField;
    private JComboBox<NetworkProtocol> protocolComboBox;
    private JButton connectButton;
    private JButton disconnectButton;
    private JLabel statusLabel;
    private JLabel messageCountLabel;
    private JPanel userPanel;
    private JScrollPane chatScrollPane;
    
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
    private static final Font MESSAGE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TIMESTAMP_FONT = new Font("Segoe UI", Font.ITALIC, 11);
    private static final Font USERNAME_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    private static final int PADDING = 12;
    private static final int BORDER_RADIUS = 8;
    private static final int COMPONENT_SPACING = 10;
    
    // Chat data
    private ChatClient client;
    private User user;
    private StyledDocument doc;
    private MessageDAO messageDAO;
    private int messageCount = 0;
    
    // Chat bubble dimensions and appearance
    private static final int BUBBLE_RADIUS = 15;
    private static final Color MY_BUBBLE_COLOR = new Color(232, 245, 233); // Light green
    private static final Color OTHERS_BUBBLE_COLOR = new Color(232, 234, 246); // Light indigo
    
    public ClientGUI() {
        setTitle("Modern Chat Client");
        setSize(900, 700);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Set application icon if available
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
        
        // Initialize message DAO
        messageDAO = new MessageDAO();
        
        initComponents();
        setupKeyboardShortcuts();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client != null && client.isConnected()) {
                    client.disconnect();
                }
            }
        });
    }
    
    private void setupKeyboardShortcuts() {
        // Add keyboard shortcuts
        getRootPane().registerKeyboardAction(
            e -> connectToServer(null),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> disconnectFromServer(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> loadFullHistory(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        getRootPane().registerKeyboardAction(
            e -> clearChat(),
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
        
        // Connection panel at the top
        JPanel connectionPanel = createConnectionPanel();
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.8);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // Chat panel on the left
        JPanel chatPanel = createChatPanel();
        
        // User list panel on the right
        JPanel userPanel = createUserListPanel();
        
        // Add components to split pane
        splitPane.setLeftComponent(chatPanel);
        splitPane.setRightComponent(userPanel);
        
        // Create status bar at bottom
        JPanel statusBar = createStatusBar();
        
        // Add all to content pane
        contentPane.add(connectionPanel, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        
        // Initial state
        messageField.setEnabled(false);
        sendButton.setEnabled(false);
        loadHistoryButton.setEnabled(false);
        disconnectButton.setEnabled(false);
    }
    
    private JPanel createConnectionPanel() {
        JPanel connectionPanel = new JPanel(new BorderLayout(5, 5));
        connectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                new LineBorder(PRIMARY_COLOR, 1, true),
                new EmptyBorder(5, 5, 5, 5)
            ),
            "Connection Settings"
        ));
        connectionPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel connectionGrid = new JPanel(new GridLayout(2, 4, 8, 8));
        connectionGrid.setBackground(BACKGROUND_COLOR);
        
        // Server settings
        JLabel serverLabel = new JLabel("Server:");
        serverLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionGrid.add(serverLabel);
        
        serverField = new JTextField("localhost");
        serverField.setToolTipText("Enter server hostname or IP address");
        connectionGrid.add(serverField);
        
        JLabel portLabel = new JLabel("Port:");
        portLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionGrid.add(portLabel);
        
        portField = new JTextField("8888");
        portField.setToolTipText("Enter server port (default: 8888)");
        connectionGrid.add(portField);
        
        // User settings
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionGrid.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setToolTipText("Enter your username to identify yourself in the chat");
        connectionGrid.add(usernameField);
        
        JLabel protocolLabel = new JLabel("Protocol:");
        protocolLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        connectionGrid.add(protocolLabel);
        
        protocolComboBox = new JComboBox<>(NetworkProtocol.values());
        protocolComboBox.setToolTipText("Select connection protocol (TCP is recommended for reliability)");
        connectionGrid.add(protocolComboBox);
        
        connectionPanel.add(connectionGrid, BorderLayout.CENTER);
        
        // Connection buttons
        JPanel connectionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        connectionButtons.setBackground(BACKGROUND_COLOR);
        
        connectButton = createStyledButton("Connect", SUCCESS_COLOR);
        connectButton.setToolTipText("Connect to server (F5)");
        
        disconnectButton = createStyledButton("Disconnect", ERROR_COLOR);
        disconnectButton.setToolTipText("Disconnect from server (ESC)");
        disconnectButton.setEnabled(false);
        
        connectionButtons.add(connectButton);
        connectionButtons.add(disconnectButton);
        
        // Status label
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setForeground(ERROR_COLOR);
        
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(BACKGROUND_COLOR);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(connectionButtons, BorderLayout.EAST);
        
        connectionPanel.add(statusPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        connectButton.addActionListener(this::connectToServer);
        disconnectButton.addActionListener(e -> disconnectFromServer());
        
        return connectionPanel;
    }
    
    private JPanel createChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        chatPanel.setBackground(BACKGROUND_COLOR);
        chatPanel.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Create chat area with custom styling
        chatPane = new JTextPane();
        chatPane.setEditable(false);
        chatPane.setBackground(CARD_COLOR);
        chatPane.setFont(MESSAGE_FONT);
        chatPane.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        doc = chatPane.getStyledDocument();
        
        // Add styles for different message types
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        
        Style regularStyle = chatPane.addStyle("regular", defaultStyle);
        StyleConstants.setFontFamily(regularStyle, MESSAGE_FONT.getFamily());
        StyleConstants.setFontSize(regularStyle, MESSAGE_FONT.getSize());
        StyleConstants.setForeground(regularStyle, TEXT_PRIMARY);
        
        Style myMessageStyle = chatPane.addStyle("myMessage", regularStyle);
        StyleConstants.setForeground(myMessageStyle, new Color(0, 100, 0));
        
        Style otherMessageStyle = chatPane.addStyle("otherMessage", regularStyle);
        StyleConstants.setForeground(otherMessageStyle, new Color(0, 0, 120));
        
        Style usernameStyle = chatPane.addStyle("username", regularStyle);
        StyleConstants.setFontFamily(usernameStyle, USERNAME_FONT.getFamily());
        StyleConstants.setFontSize(usernameStyle, USERNAME_FONT.getSize());
        StyleConstants.setBold(usernameStyle, true);
        StyleConstants.setForeground(usernameStyle, PRIMARY_COLOR);
        
        Style systemMessageStyle = chatPane.addStyle("system", regularStyle);
        StyleConstants.setForeground(systemMessageStyle, new Color(120, 120, 120));
        StyleConstants.setItalic(systemMessageStyle, true);
        
        Style timestampStyle = chatPane.addStyle("timestamp", regularStyle);
        StyleConstants.setForeground(timestampStyle, TEXT_SECONDARY);
        StyleConstants.setFontFamily(timestampStyle, TIMESTAMP_FONT.getFamily());
        StyleConstants.setFontSize(timestampStyle, TIMESTAMP_FONT.getSize());
        StyleConstants.setItalic(timestampStyle, true);
        
        // Create scrollable chat area with custom scrollbar
        chatScrollPane = new JScrollPane(chatPane);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        chatScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Add drop shadow effect to chat pane
        chatScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        ));
        
        // Message input panel
        JPanel messagePanel = new JPanel(new BorderLayout(COMPONENT_SPACING, 0));
        messagePanel.setBackground(CARD_COLOR);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(PADDING - 2, PADDING - 2, PADDING - 2, PADDING - 2)
        ));
        
        // Message text field with rounded border
        messageField = new JTextField();
        messageField.setFont(DEFAULT_FONT);
        messageField.setEnabled(false);
        messageField.setMargin(new Insets(PADDING - 2, PADDING - 2, PADDING - 2, PADDING - 2));
        messageField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1, true),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        
        // Styling message field with custom border radius and placeholder
        messageField.setBackground(CARD_COLOR);
        messageField.setForeground(TEXT_PRIMARY);
        messageField.setCaretColor(PRIMARY_COLOR);
        
        // Send button with modern styling
        sendButton = createStyledButton("Send", PRIMARY_COLOR);
        sendButton.setToolTipText("Send message (Enter)");
        sendButton.setEnabled(false);
        
        
        // Add components to message panel
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);
        
        // Chat controls panel with history button
        JPanel chatControlsPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        chatControlsPanel.setOpaque(false);
        
        // Control buttons panel
        JPanel controlButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, COMPONENT_SPACING, 0));
        controlButtonsPanel.setOpaque(false);
        
        // Load history button
        loadHistoryButton = createStyledButton("Load History", PRIMARY_COLOR);
        loadHistoryButton.setToolTipText("Load previous messages (F3)");
        loadHistoryButton.setEnabled(false);
        
        // Clear chat button
        clearChatButton = createStyledButton("Clear Chat", WARNING_COLOR);
        clearChatButton.setToolTipText("Clear all messages from the chat window (F4)");
        clearChatButton.setEnabled(false);
        
        // Add buttons to control panel
        controlButtonsPanel.add(loadHistoryButton);
        controlButtonsPanel.add(clearChatButton);
        
        // Add components to chat controls panel
        chatControlsPanel.add(controlButtonsPanel, BorderLayout.NORTH);
        chatControlsPanel.add(messagePanel, BorderLayout.CENTER);
        
        // Add action listeners
        loadHistoryButton.addActionListener(e -> loadFullHistory());
        clearChatButton.addActionListener(e -> clearChat());
        sendButton.addActionListener(e -> sendMessage());
        
        messageField.addActionListener(e -> sendMessage());
        
        // Add components to chat panel
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);
        chatPanel.add(chatControlsPanel, BorderLayout.SOUTH);
        
        return chatPanel;
    }
    
    private JPanel createUserListPanel() {
        userPanel = new JPanel(new BorderLayout(COMPONENT_SPACING, COMPONENT_SPACING));
        userPanel.setBackground(BACKGROUND_COLOR);
        userPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING),
            BorderFactory.createTitledBorder(
            BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1, true),
                    BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
                ),
                "Online Users",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT,
                PRIMARY_COLOR
            )
        ));
        
        // Create and configure user list
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setCellRenderer(new UserListCellRenderer());
        userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userList.setBackground(CARD_COLOR);
        userList.setFont(DEFAULT_FONT);
        
        // Add custom scrollable with modern styling
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        userScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        userScrollPane.setBorder(BorderFactory.createEmptyBorder());
        userScrollPane.getViewport().setBackground(CARD_COLOR);
        
        // Add shadow effect to user list
        userScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(2, 2, 4, 4),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
            )
        ));
        
        userPanel.add(userScrollPane, BorderLayout.CENTER);
        
        return userPanel;
    }
    
    // Custom renderer for user list with avatars and online status
    class UserListCellRenderer extends DefaultListCellRenderer {
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
            JLabel avatarLabel = new JLabel(new UserAvatar(user.getUsername(), user.isActive(), AVATAR_SIZE));
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
            
            // Add online status indicator
            JPanel statusIndicator = new JPanel();
            statusIndicator.setPreferredSize(new Dimension(10, 10));
            statusIndicator.setBackground(user.isActive() ? SUCCESS_COLOR : Color.GRAY);
            statusIndicator.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            
            panel.add(statusIndicator, BorderLayout.EAST);
            
            return panel;
        }
        
        // Inner class for avatar icon
        private class UserAvatar implements Icon {
            private final Color avatarColor;
            private final String initials;
            private final boolean isActive;
            private final int size;
            
            public UserAvatar(String username, boolean isActive, int size) {
                this.isActive = isActive;
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
                
                // Draw status indicator
                if (isActive) {
                    g2d.setColor(SUCCESS_COLOR);
                    g2d.fillOval(x + size - 10, y + size - 10, 8, 8);
                    g2d.setColor(Color.WHITE);
                    g2d.drawOval(x + size - 10, y + size - 10, 8, 8);
                }
                
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
        
        // Message count label
        messageCountLabel = new JLabel("Messages: 0");
        messageCountLabel.setForeground(new Color(100, 100, 100));
        statusBar.add(messageCountLabel, BorderLayout.WEST);
        
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
        button.setBorder(new CompoundBorder(
            new LineBorder(darkenColor(color), 1, true),
            new EmptyBorder(8, 15, 8, 15)
        ));
        
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
    
    private void connectToServer(ActionEvent e) {
        String serverAddress = serverField.getText().trim();
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a username", 
                "Missing Information", JOptionPane.ERROR_MESSAGE);
            usernameField.requestFocus();
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
            statusLabel.setForeground(WARNING_COLOR);
            
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
                statusLabel.setForeground(ERROR_COLOR);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid port number", 
                "Invalid Port", JOptionPane.ERROR_MESSAGE);
            portField.requestFocus();
            portField.selectAll();
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
                messageField.requestFocus();
            }
        }
    }
    
    private void clearChat() {
        try {
            doc.remove(0, doc.getLength());
            doc.insertString(doc.getLength(), 
                formatTimestamp(System.currentTimeMillis()) + " Chat cleared.\n", 
                chatPane.getStyle("system"));
            messageCount = 0;
            updateMessageCount();
        } catch (BadLocationException e) {
            System.err.println("Error clearing chat: " + e.getMessage());
        }
    }
    
    private void loadFullHistory() {
        // Clear chat first
        clearChat();
        
        try {
            // Add header
            doc.insertString(doc.getLength(), 
                formatTimestamp(System.currentTimeMillis()) + " Loading full chat history...\n", 
                chatPane.getStyle("system"));
                
            // Show loading indicator
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
            // Get all messages from database
            List<Message> allMessages = messageDAO.getAllMessages();
            
            // Reset cursor
            setCursor(Cursor.getDefaultCursor());
            
            if (allMessages.isEmpty()) {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " No chat history found.\n", 
                    chatPane.getStyle("system"));
            } else {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " Displaying " + allMessages.size() + " messages.\n", 
                    chatPane.getStyle("system"));
                
                // Display all messages
                for (Message message : allMessages) {
                    displayMessage(message);
                }
                
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " End of chat history.\n", 
                    chatPane.getStyle("system"));
                
                // Update message count
                messageCount = allMessages.size();
                updateMessageCount();
            }
            
            // Auto-scroll to bottom
            chatPane.setCaretPosition(doc.getLength());
            
        } catch (BadLocationException e) {
            System.err.println("Error displaying history: " + e.getMessage());
        }
    }
    
    private void displayMessage(Message message) {
        try {
            // Check if this is our message or from others
            boolean isMyMessage = (user != null && message.getSenderUsername().equals(user.getUsername()));
            
            // Get the document and styles
            StyledDocument doc = chatPane.getStyledDocument();
            
            // Get timestamp as formatted string
            String timestamp = formatTimestamp(message.getTimestamp().getTime());
            
            // Format based on message type
            if (message.getSenderUsername().equals("SERVER")) {
                // System message styling
            doc.insertString(doc.getLength(), timestamp + " ", chatPane.getStyle("timestamp"));
                doc.insertString(doc.getLength(), message.getContent() + "\n", chatPane.getStyle("system"));
            } else {
                // Create chat bubble
                JPanel bubblePanel = createChatBubble(message, isMyMessage);
                
                // Insert component into text pane
                chatPane.setCaretPosition(doc.getLength());
                chatPane.insertComponent(bubblePanel);
                
                // Add a newline after the component
                doc.insertString(doc.getLength(), "\n", chatPane.getStyle("regular"));
            }
            
            // Scroll to bottom to show new message
            chatPane.setCaretPosition(doc.getLength());
            
            // Update message count
            messageCount++;
            updateMessageCount();
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to create chat bubbles
    private JPanel createChatBubble(Message message, boolean isMyMessage) {
        // Create main panel with appropriate alignment
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Create the bubble panel with rounded corners
        JPanel bubblePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Set color based on sender
                g2.setColor(isMyMessage ? MY_BUBBLE_COLOR : OTHERS_BUBBLE_COLOR);
                
                // Draw rounded rectangle
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BUBBLE_RADIUS, BUBBLE_RADIUS);
                
                // Add a subtle border
                g2.setColor(new Color(0, 0, 0, 20));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, BUBBLE_RADIUS, BUBBLE_RADIUS);
                
                g2.dispose();
            }
        };
        bubblePanel.setOpaque(false);
        bubblePanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Username label
        JLabel usernameLabel = new JLabel(message.getSenderUsername());
        usernameLabel.setForeground(isMyMessage ? 
                                  new Color(0, 100, 0) : 
                                  PRIMARY_COLOR);
        usernameLabel.setFont(USERNAME_FONT);
        
        // Tính toán chiều rộng tối đa cho tin nhắn
        int maxWidth = Math.max(300, Math.min(500, chatPane.getWidth() - 100));
        
        // Xử lý nội dung tin nhắn
        String content = message.getContent()
            .replace("\n", "<br>")
            .replace("  ", "&nbsp;&nbsp;");
        
        // Tạo label với chiều rộng tự động điều chỉnh theo nội dung
        JLabel contentLabel = new JLabel();
        contentLabel.setText("<html><div style='width: auto; max-width: " + maxWidth + "px; word-wrap: break-word;'>" + 
                            content + "</div></html>");
        contentLabel.setForeground(TEXT_PRIMARY);
        contentLabel.setFont(MESSAGE_FONT);
        
        // Timestamp label
        JLabel timestampLabel = new JLabel(formatTimestamp(message.getTimestamp().getTime()));
        timestampLabel.setForeground(TEXT_SECONDARY);
        timestampLabel.setFont(TIMESTAMP_FONT);
        
        // Arrange components in the bubble
        JPanel contentPanel = new JPanel(new BorderLayout(5, 3));
        contentPanel.setOpaque(false);
        contentPanel.add(usernameLabel, BorderLayout.NORTH);
        contentPanel.add(contentLabel, BorderLayout.CENTER);
        contentPanel.add(timestampLabel, BorderLayout.SOUTH);
        
        bubblePanel.add(contentPanel, BorderLayout.CENTER);
        
        // Align bubble based on sender
        mainPanel.add(bubblePanel, isMyMessage ? BorderLayout.EAST : BorderLayout.WEST);
        
        return mainPanel;
    }
    
    private void onMessageReceived(Message message) {
        SwingUtilities.invokeLater(() -> {
            // Kiểm tra nếu tin nhắn chứa danh sách người dùng đang online
            if (message.getOnlineUsers() != null && !message.getOnlineUsers().isEmpty()) {
                System.out.println("Received user list with " + message.getOnlineUsers().size() + " users");
                updateOnlineUsersList(message.getOnlineUsers());
            }
            
            // Hiển thị tin nhắn
            displayMessage(message);
            
            // Auto-scroll to bottom
            chatPane.setCaretPosition(doc.getLength());
            
            // Update message count
            messageCount++;
            updateMessageCount();
        });
    }
    
    // Phương thức cập nhật danh sách người dùng đang online
    private void updateOnlineUsersList(List<User> onlineUsers) {
        try {
            System.out.println("Updating online users list...");
            // Xóa danh sách hiện tại
            userListModel.clear();
            
            // Thêm người dùng mới
            for (User user : onlineUsers) {
                if (user != null) {
                    System.out.println("Adding user: " + user.getUsername() + " at " + user.getIpAddress());
                    userListModel.addElement(user);
                }
            }
            System.out.println("User list updated with " + userListModel.size() + " users");
        } catch (Exception e) {
            // Ghi log lỗi để debug
            System.err.println("Error updating user list: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateMessageCount() {
        messageCountLabel.setText("Messages: " + messageCount);
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
            loadHistoryButton.setEnabled(true);
            clearChatButton.setEnabled(true);
            statusLabel.setText("Status: Connected");
            statusLabel.setForeground(SUCCESS_COLOR);
            
            // Add welcome message
            try {
                doc.insertString(doc.getLength(), 
                    formatTimestamp(System.currentTimeMillis()) + " Connected to server.\n", 
                    chatPane.getStyle("system"));
            } catch (BadLocationException e) {
                System.err.println("Error displaying message: " + e.getMessage());
            }
            
            // Focus message field
            messageField.requestFocus();
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
            loadHistoryButton.setEnabled(false);
            clearChatButton.setEnabled(false);
            statusLabel.setText("Status: Disconnected");
            statusLabel.setForeground(ERROR_COLOR);
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
                
                // Notify user with sound if available
                Toolkit.getDefaultToolkit().beep();
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
            // Try to use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ClientGUI().setVisible(true);
        });
    }
} 