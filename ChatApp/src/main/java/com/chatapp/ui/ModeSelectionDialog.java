package com.chatapp.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ModeSelectionDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    // Palette mới
    private static final Color PRIMARY_COLOR = new Color(22, 196, 127);  // Xanh lục bảo
    private static final Color SECONDARY_COLOR = new Color(255, 214, 90); // Vàng
    private static final Color WARNING_COLOR = new Color(255, 157, 35);  // Cam
    private static final Color DANGER_COLOR = new Color(249, 56, 39);    // Đỏ
    
    // Màu sắc phụ
    private static final Color PRIMARY_DARK = new Color(18, 156, 101);  // Xanh lục bảo tối
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);  // Màu nền xám nhạt
    private static final Color TEXT_COLOR = new Color(60, 60, 60);  // Màu chữ xám đậm
    
    private int selectedMode = -1; // -1: none, 0: client, 1: server
    
    public ModeSelectionDialog() {
        super((JFrame) null, "Chat Application - Mode Selection", true);
        initComponents();
        setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel with gradient background
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
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("Chat Application", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Select Operation Mode", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(240, 240, 240));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(BACKGROUND_COLOR);
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Client button
        JButton clientButton = createModeButton("Client Mode", "Connect to a chat server and communicate with other users.");
        clientButton.addActionListener(e -> {
            selectedMode = 0;
            dispose();
        });
        
        // Server button
        JButton serverButton = createModeButton("Server Mode", "Start a chat server that clients can connect to.");
        serverButton.addActionListener(e -> {
            selectedMode = 1;
            dispose();
        });
        
        optionsPanel.add(clientButton);
        optionsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        optionsPanel.add(serverButton);
        
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(BACKGROUND_COLOR);
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(DANGER_COLOR);
        exitButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 45, 30), 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        exitButton.setFocusPainted(false);
        exitButton.addActionListener(e -> {
            selectedMode = -1;
            dispose();
        });
        
        // Hover effect for exit button
        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(220, 50, 35));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(DANGER_COLOR);
            }
        });
        
        footerPanel.add(exitButton);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        pack();
        setMinimumSize(new Dimension(500, 400));
    }
    
    private JButton createModeButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        button.setFocusPainted(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JLabel descLabel = new JLabel("<html><body width='300px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(TEXT_COLOR);
        
        button.add(titleLabel, BorderLayout.NORTH);
        button.add(descLabel, BorderLayout.CENTER);
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(245, 245, 255));
                titleLabel.setForeground(PRIMARY_DARK);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                titleLabel.setForeground(PRIMARY_COLOR);
            }
        });
        
        return button;
    }
    
    public int getSelectedMode() {
        return selectedMode;
    }
} 