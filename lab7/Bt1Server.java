package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Bt1Server extends JFrame { // Changed to JFrame

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public Bt1Server() { // Changed constructor name
        setTitle("BT1 Server"); // Set frame title
        setSize(400, 300); // Set frame size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        setLocationRelativeTo(null); // Center the frame

        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Gửi");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

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

        // Start the server in a new thread
        new Thread(this::startServer).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(12345); // Use a fixed port
            messageArea.append("Server đang chờ kết nối...\n");
            clientSocket = serverSocket.accept();
            messageArea.append("Client đã kết nối: " + clientSocket.getInetAddress().getHostName() + "\n");

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                messageArea.append("Client: " + line + "\n");
            }

        } catch (IOException e) {
            messageArea.append("Lỗi Server: " + e.getMessage() + "\n");
        } finally {
            try {
                if (clientSocket != null) clientSocket.close();
                if (serverSocket != null) serverSocket.close();
            } catch (IOException e) {
                messageArea.append("Lỗi đóng kết nối Server: " + e.getMessage() + "\n");
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
    //         new Bt1Server().setVisible(true);
    //     });
    // }
}