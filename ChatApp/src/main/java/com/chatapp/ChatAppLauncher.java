package com.chatapp;

import com.chatapp.gui.ClientGUI;
import com.chatapp.gui.ServerGUI;

import javax.swing.*;
import java.awt.*;

/**
 * Main launcher class for the Chat Application
 * Uses a simple dialog to let the user choose between starting a client or server
 */
public class ChatAppLauncher {
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create a dialog to choose between client and server
        String[] options = {"Start Client", "Start Server", "Exit"};
        int choice = JOptionPane.showOptionDialog(
            null, 
            "Welcome to Chat Application\nPlease select an option:",
            "Chat Application Launcher",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        // Handle user choice
        switch (choice) {
            case 0:
                SwingUtilities.invokeLater(() -> new ClientGUI().setVisible(true));
                break;
            case 1:
                SwingUtilities.invokeLater(() -> new ServerGUI().setVisible(true));
                break;
            default:
                System.exit(0);
        }
    }
} 