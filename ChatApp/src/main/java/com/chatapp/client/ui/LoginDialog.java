package com.chatapp.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.chatapp.ui.AppColors;

public class LoginDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField txtUsername;
    private JTextField txtServerHost;
    private JButton btnLogin;
    private JButton btnCancel;
    private boolean loginSuccessful;
    
    public LoginDialog(JFrame parent) {
        super(parent, "Chat Login", true);
        initComponents();
        
        // Default values
        txtServerHost.setText("localhost");
        
        // Center on screen
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(AppColors.BACKGROUND_COLOR);
        
        // Header Panel with gradient
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
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        headerPanel.setPreferredSize(new Dimension(400, 80));
        
        JLabel lblHeader = new JLabel("Chat Login", SwingConstants.CENTER);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(AppColors.TEXT_LIGHT);
        headerPanel.add(lblHeader, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(AppColors.BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(25, 30, 20, 30));
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(AppColors.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Server host
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblServerHost = new JLabel("Server Host:");
        lblServerHost.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblServerHost.setForeground(AppColors.PRIMARY_COLOR);
        inputPanel.add(lblServerHost, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        txtServerHost = new JTextField(20);
        txtServerHost.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtServerHost.setMargin(new Insets(8, 8, 8, 8));
        txtServerHost.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        inputPanel.add(txtServerHost, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        gbc.insets = new Insets(20, 5, 8, 5);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsername.setForeground(AppColors.PRIMARY_COLOR);
        inputPanel.add(lblUsername, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(8, 5, 8, 5);
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setMargin(new Insets(8, 8, 8, 8));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        inputPanel.add(txtUsername, gbc);
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(AppColors.BACKGROUND_COLOR);
        buttonsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        btnCancel = new JButton("Cancel");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setForeground(AppColors.TEXT_DARK);
        btnCancel.setBackground(AppColors.PANEL_COLOR);
        btnCancel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
        btnCancel.setFocusPainted(false);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginSuccessful = false;
                dispose();
            }
        });
        
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setForeground(AppColors.SECONDARY_COLOR);
        btnLogin.setBackground(AppColors.SECONDARY_COLOR);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppColors.SECONDARY_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 25, 8, 25)));
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Add hover effects
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(AppColors.SECONDARY_HOVER);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogin.setBackground(AppColors.SECONDARY_COLOR);
            }
        });
        
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(AppColors.ACCENT_HOVER);
                btnCancel.setForeground(AppColors.ATTENTION_COLOR);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(AppColors.PANEL_COLOR);
                btnCancel.setForeground(AppColors.TEXT_DARK);
            }
        });
        
        buttonsPanel.add(btnCancel);
        buttonsPanel.add(btnLogin);
        
        formPanel.add(buttonsPanel, BorderLayout.SOUTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Final dialog setup
        setContentPane(mainPanel);
        getRootPane().setDefaultButton(btnLogin);
        pack();
        setMinimumSize(new Dimension(400, 380));
    }
    
    private void login() {
        String username = txtUsername.getText().trim();
        String serverHost = txtServerHost.getText().trim();
        
        if (username.isEmpty()) {
            showErrorMessage("Please enter a username", "Input Error");
            txtUsername.requestFocus();
            return;
        }
        
        if (serverHost.isEmpty()) {
            showErrorMessage("Please enter a server host", "Input Error");
            txtServerHost.requestFocus();
            return;
        }
        
        loginSuccessful = true;
        dispose();
    }
    
    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(
                this,
                message,
                title,
                JOptionPane.ERROR_MESSAGE);
    }
    
    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
    
    public String getUsername() {
        return txtUsername.getText().trim();
    }
    
    public String getServerHost() {
        return txtServerHost.getText().trim();
    }
} 