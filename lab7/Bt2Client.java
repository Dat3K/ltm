package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Bt2Client extends JFrame { // Changed to JFrame to open in a new window

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JTextField serverAddressField;
    private JTextField portField;
    private JButton startButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread clientThread;

    public Bt2Client() {
        setTitle("BT2 Client");
        setSize(700, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Top panel for connection settings
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Center alignment, 10px gaps
        connectionPanel.setBorder(BorderFactory.createTitledBorder("CLIENT")); // Add a titled border

        connectionPanel.add(new JLabel("ServerName:"));
        serverAddressField = new JTextField("localhost", 15);
        connectionPanel.add(serverAddressField);

        connectionPanel.add(new JLabel("PortNo:"));
        portField = new JTextField("2022", 5); // Default port from image
        connectionPanel.add(portField);

        startButton = new JButton("Start");
        connectionPanel.add(startButton);

        add(connectionPanel, BorderLayout.NORTH);

        // Center panel for messages
        messageArea = new JTextArea();
        messageArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for message input
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0)); // 10px horizontal gap
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add some padding

        inputField = new JTextField();
        inputPanel.add(inputField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setEnabled(false); // Disable until connected
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Action listeners
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer();
            }
        });

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    private void connectToServer() {
        String serverAddress = serverAddressField.getText();
        int port;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            messageArea.append("Lỗi: Số cổng không hợp lệ.\n");
            return;
        }

        if (serverAddress.isEmpty()) {
            messageArea.append("Vui lòng nhập địa chỉ Server.\n");
            return;
        }

        // Disable connection components while connecting
        startButton.setEnabled(false);
        serverAddressField.setEditable(false);
        portField.setEditable(false);

        clientThread = new Thread(() -> {
            try {
                messageArea.append("Đang kết nối đến " + serverAddress + ":" + port + "...\n");
                socket = new Socket(serverAddress, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                messageArea.append("Đã kết nối thành công.\n");
                SwingUtilities.invokeLater(() -> {
                    sendButton.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Start chatting!", "Connected to Server", JOptionPane.INFORMATION_MESSAGE);
                });


                String line;
                while ((line = in.readLine()) != null) {
                    messageArea.append("Server: " + line + "\n");
                }

            } catch (IOException e) {
                messageArea.append("Lỗi kết nối: " + e.getMessage() + "\n");
                 SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Connection error: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    messageArea.append("Lỗi đóng kết nối: " + e.getMessage() + "\n");
                }
                SwingUtilities.invokeLater(() -> {
                    sendButton.setEnabled(false);
                    startButton.setEnabled(true);
                    serverAddressField.setEditable(true);
                    portField.setEditable(true);
                    messageArea.append("Kết nối đã đóng.\n");
                });
            }
        });
        clientThread.start();
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (out != null && !message.isEmpty()) {
            out.println(message);
            messageArea.append("Client: " + message + "\n");
            inputField.setText("");
        }
    }

    // Optional: Add a main method for standalone testing
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new Bt2Client().setVisible(true);
    //     });
    // }
}