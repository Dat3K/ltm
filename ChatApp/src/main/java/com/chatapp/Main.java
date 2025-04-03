package com.chatapp;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.chatapp.client.ui.ChatWindow;
import com.chatapp.server.ui.ServerUI;
import com.chatapp.ui.ModeSelectionDialog;

public class Main {
    
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // If command line arguments are provided, use them to decide which mode to run
        if (args.length > 0) {
            String mode = args[0].toLowerCase();
            
            if (mode.equals("server")) {
                startServer();
            } else if (mode.equals("client")) {
                startClient();
            } else {
                showModeSelectionDialog();
            }
        } else {
            showModeSelectionDialog();
        }
    }
    
    private static void showModeSelectionDialog() {
        ModeSelectionDialog dialog = new ModeSelectionDialog();
        
        // Add a listener to handle dialog closing separately
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                // Process the result after the dialog is closed
                int selectedMode = dialog.getSelectedMode();
                processModeSelection(selectedMode);
            }
        });
        
        SwingUtilities.invokeLater(() -> {
            dialog.setVisible(true);
        });
    }
    
    private static void processModeSelection(int selectedMode) {
        SwingUtilities.invokeLater(() -> {
            switch (selectedMode) {
                case 0:
                    startClient();
                    break;
                case 1:
                    startServer();
                    break;
                default:
                    System.exit(0);
                    break;
            }
        });
    }
    
    private static void startClient() {
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            chatWindow.setVisible(true);
            chatWindow.connect();
        });
    }
    
    private static void startServer() {
        SwingUtilities.invokeLater(() -> {
            ServerUI serverUI = new ServerUI();
            serverUI.setVisible(true);
        });
    }
} 