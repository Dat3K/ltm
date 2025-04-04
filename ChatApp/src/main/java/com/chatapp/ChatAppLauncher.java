package com.chatapp;

import com.chatapp.gui.ClientGUI;
import com.chatapp.gui.ServerGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main launcher class for the Chat Application
 * Uses a modern splash screen to let the user choose between starting a client or server
 */
public class ChatAppLauncher {
    
    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(25, 118, 210); // Material blue
    private static final Color HOVER_COLOR = new Color(21, 101, 192);   // Darker blue
    private static final Color TEXT_COLOR = new Color(255, 255, 255);   // White
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    
    public static void main(String[] args) {
        try {
            // Set system look and feel with additional tweaks
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("Button.arc", 15);
            UIManager.put("Button.margin", new Insets(10, 20, 10, 20));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and display the splash screen
        showModernSplashScreen();
    }
    
    private static void showModernSplashScreen() {
        // Create main frame
        JFrame splashFrame = new JFrame("Chat Application");
        splashFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        splashFrame.setSize(500, 400);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setResizable(false);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(41, 128, 185),
                    0, getHeight(), new Color(44, 62, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Application title
        JLabel titleLabel = new JLabel("Chat Application", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Choose an option to start:", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout(0, 10));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        buttonPanel.setOpaque(false);
        
        // Client button
        JButton clientButton = createStyledButton("Start Client", "Connect to an existing chat server");
        clientButton.addActionListener(e -> {
            splashFrame.dispose();
            SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
        });
        
        // Server button
        JButton serverButton = createStyledButton("Start Server", "Host your own chat server");
        serverButton.addActionListener(e -> {
            splashFrame.dispose();
            SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
        });
        
        // Exit button
        JButton exitButton = createStyledButton("Exit", "Close the application");
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add components
        buttonPanel.add(clientButton);
        buttonPanel.add(serverButton);
        buttonPanel.add(exitButton);
        
        // Add version info
        JLabel versionLabel = new JLabel("Version 1.0.0", JLabel.CENTER);
        versionLabel.setForeground(new Color(200, 200, 200));
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Add components to main panel
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(versionLabel, BorderLayout.SOUTH);
        
        // Set content and display
        splashFrame.setContentPane(mainPanel);
        splashFrame.setVisible(true);
    }
    
    private static JButton createStyledButton(String text, String tooltip) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Determine button state color
                Color background = getModel().isPressed() ? HOVER_COLOR.darker() :
                                   getModel().isRollover() ? HOVER_COLOR : PRIMARY_COLOR;
                
                // Draw rounded rectangle background
                g2d.setColor(background);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Draw button text
                g2d.setColor(TEXT_COLOR);
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(getText(), textX, textY);
                
                g2d.dispose();
            }
        };
        
        // Configure button properties
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT_COLOR);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setToolTipText(tooltip);
        button.setPreferredSize(new Dimension(200, 50));
        
        return button;
    }
} 