package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Bt2Server extends JFrame {

    private JTextArea messageArea;
    private JTextField inputField; // Added input field
    private JButton sendButton; // Added send button
    private JButton startStopButton;
    private JTextField portField;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isServerRunning = false;
    private Thread serverThread;

    public Bt2Server() {
        setTitle("BT2 Server");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Port:"));
        portField = new JTextField("2022", 5);
        controlPanel.add(portField);
        startStopButton = new JButton("Start Server");
        controlPanel.add(startStopButton);
        add(controlPanel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Panel for message input and send button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Gửi");
        sendButton.setEnabled(false); // Disable until client is connected

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH); // Add input panel to the bottom

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerRunning) {
                    stopServer();
                } else {
                    startServer();
                }
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

    private void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText());
            if (port <= 0 || port > 65535) {
                messageArea.append("Lỗi: Số cổng không hợp lệ.\n");
                return;
            }
        } catch (NumberFormatException e) {
            messageArea.append("Lỗi: Số cổng không hợp lệ.\n");
            return;
        }

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                messageArea.append("Server đang chạy trên cổng " + port + "...\n");
                SwingUtilities.invokeLater(() -> {
                    startStopButton.setText("Stop Server");
                    portField.setEnabled(false);
                    JOptionPane.showMessageDialog(this, "Server is waiting client at port " + port + "...", "Server Status", JOptionPane.INFORMATION_MESSAGE);
                });
                isServerRunning = true;

                clientSocket = serverSocket.accept();
                messageArea.append("Client đã kết nối: " + clientSocket.getInetAddress().getHostName() + "\n");
                 SwingUtilities.invokeLater(() -> {
                    sendButton.setEnabled(true); // Enable send button when client connects
                    JOptionPane.showMessageDialog(this, "Start chatting!", "Client Connected", JOptionPane.INFORMATION_MESSAGE);
                });


                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    messageArea.append("Client: " + line + "\n");
                }

            } catch (IOException e) {
                if (isServerRunning) {
                     messageArea.append("Lỗi Server: " + e.getMessage() + "\n");
                     SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Server error: " + e.getMessage(), "Server Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            } finally {
                stopServer();
            }
        });
        serverThread.start();
    }

    private void stopServer() {
        isServerRunning = false;
        try {
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();
             messageArea.append("Server đã dừng.\n");
        } catch (IOException e) {
             messageArea.append("Lỗi đóng kết nối Server: " + e.getMessage() + "\n");
        } finally {
            SwingUtilities.invokeLater(() -> {
                startStopButton.setText("Start Server");
                portField.setEnabled(true);
                sendButton.setEnabled(false); // Disable send button when server stops
            });
            if (serverThread != null && serverThread.isAlive()) {
                serverThread.interrupt();
            }
        }
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (out != null && !message.isEmpty()) {
            out.println(message);
            messageArea.append("Server: " + message + "\n");
            inputField.setText("");
        }
    }

    // Optional: Add a main method for standalone testing
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> {
    //         new Bt2Server().setVisible(true);
    //     });
    // }
}