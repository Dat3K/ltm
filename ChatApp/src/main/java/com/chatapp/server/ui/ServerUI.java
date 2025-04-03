package com.chatapp.server.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultCaret;

import com.chatapp.server.ChatServer;

public class ServerUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // Palette mới
    private static final Color PRIMARY_COLOR = new Color(22, 196, 127);  // Xanh lục bảo
    private static final Color SECONDARY_COLOR = new Color(255, 214, 90); // Vàng
    private static final Color WARNING_COLOR = new Color(255, 157, 35);  // Cam
    private static final Color DANGER_COLOR = new Color(249, 56, 39);    // Đỏ
    
    // Màu sắc phụ
    private static final Color PRIMARY_DARK = new Color(18, 156, 101);  // Xanh lục bảo tối
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 250);  // Màu nền xám nhạt
    private static final Color PANEL_COLOR = new Color(255, 255, 255);  // Màu panel trắng
    
    // Màu sắc chức năng
    private static final Color START_BUTTON_COLOR = PRIMARY_COLOR;  // Màu nút Start
    private static final Color STOP_BUTTON_COLOR = DANGER_COLOR;    // Màu nút Stop
    private static final Color LOG_TEXT_COLOR = new Color(240, 240, 240);  // Màu chữ log
    private static final Color LOG_BACKGROUND_COLOR = new Color(40, 40, 40);  // Màu nền log
    private static final Color SERVER_RUNNING_COLOR = PRIMARY_COLOR;  // Màu server đang chạy
    private static final Color SERVER_STOPPED_COLOR = DANGER_COLOR;  // Màu server đã dừng
    private static final Color SERVER_WARNING_COLOR = WARNING_COLOR;  // Màu cảnh báo
    
    private JTextArea logArea;
    private JButton startButton;
    private JButton stopButton;
    private JLabel statusLabel;
    
    private ChatServer server;
    private Thread serverThread;
    private boolean serverRunning;
    
    public ServerUI() {
        setTitle("Chat Server");
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 450));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        setupListeners();
        
        // Redirect System.out and System.err to the log area
        redirectSystemStreams();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopServer();
            }
        });
    }
    
    private void initComponents() {
        // Main layout with modern design
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLayout(new BorderLayout(0, 0));
        
        // Header panel with gradient
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
        headerPanel.setPreferredSize(new Dimension(800, 60));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // App title
        JLabel titleLabel = new JLabel("Chat Server");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Status label in header
        statusLabel = new JLabel("Server is stopped");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(SERVER_STOPPED_COLOR);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(PANEL_COLOR);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                new EmptyBorder(15, 15, 15, 15)));
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        
        startButton = createButton("Start Server", START_BUTTON_COLOR, PRIMARY_DARK);
        startButton.addActionListener(e -> startServer());
        
        stopButton = createButton("Stop Server", STOP_BUTTON_COLOR, new Color(200, 45, 30));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopServer());
        
        controlPanel.add(startButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(stopButton);
        controlPanel.add(Box.createHorizontalGlue());
        
        contentPanel.add(controlPanel, BorderLayout.NORTH);
        
        // Log panel
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBackground(PANEL_COLOR);
        logPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                new EmptyBorder(0, 0, 0, 0)));
        
        JLabel logLabel = new JLabel("Server Log");
        logLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logLabel.setBorder(new EmptyBorder(10, 15, 10, 15));
        logPanel.add(logLabel, BorderLayout.NORTH);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(LOG_BACKGROUND_COLOR);
        logArea.setForeground(LOG_TEXT_COLOR);
        logArea.setMargin(new Insets(10, 10, 10, 10));
        
        // Auto-scroll log area
        DefaultCaret caret = (DefaultCaret) logArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        logPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(logPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JButton createButton(String text, Color bgColor, Color borderColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)));
        
        // Add hover effect
        Color lighterBg = new Color(
                Math.min(bgColor.getRed() + 20, 255),
                Math.min(bgColor.getGreen() + 20, 255),
                Math.min(bgColor.getBlue() + 20, 255)
        );
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(lighterBg);
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor);
                }
            }
        });
        
        return button;
    }
    
    private void setupListeners() {
        // Already set up in initComponents()
    }
    
    private void startServer() {
        if (serverRunning) {
            return;
        }
        
        serverRunning = true;
        statusLabel.setText("Server is running");
        statusLabel.setForeground(SERVER_RUNNING_COLOR);
        
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        
        serverThread = new Thread(() -> {
            try {
                server = new ChatServer();
                System.out.println("Server started");
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    serverRunning = false;
                    statusLabel.setText("Server failed to start");
                    statusLabel.setForeground(SERVER_STOPPED_COLOR);
                    
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            }
        });
        
        serverThread.start();
    }
    
    private void stopServer() {
        if (!serverRunning || server == null) {
            return;
        }
        
        statusLabel.setText("Server is stopping...");
        statusLabel.setForeground(SERVER_WARNING_COLOR);
        
        Thread stopThread = new Thread(() -> {
            try {
                server.stop();
                System.out.println("Server stopped");
                
                SwingUtilities.invokeLater(() -> {
                    serverRunning = false;
                    statusLabel.setText("Server is stopped");
                    statusLabel.setForeground(SERVER_STOPPED_COLOR);
                    
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        stopThread.start();
    }
    
    // Redirect System.out and System.err to the log area
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendToLog(String.valueOf((char) b));
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                appendToLog(new String(b, off, len));
            }
        };
        
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }
    
    private void appendToLog(final String text) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(text);
        });
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            ServerUI serverUI = new ServerUI();
            serverUI.setVisible(true);
        });
    }
} 