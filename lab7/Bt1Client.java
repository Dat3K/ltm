package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class Bt1Client extends JFrame { // Changed to JFrame

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;
    private JTextField serverAddressField;
    private JButton connectButton;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Bt1Client() { // Changed constructor name
        setTitle("BT1 Client"); // Set frame title
        setSize(400, 300); // Set frame size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose on close
        setLocationRelativeTo(null); // Center the frame

        setLayout(new BorderLayout()); // Use BorderLayout for the frame

        JPanel connectionPanel = new JPanel(new FlowLayout());
        serverAddressField = new JTextField("localhost", 15); // Default to localhost
        connectButton = new JButton("Kết nối");
        connectionPanel.add(new JLabel("Địa chỉ Server:"));
        connectionPanel.add(serverAddressField);
        connectionPanel.add(connectButton);

        add(connectionPanel, BorderLayout.NORTH);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Gửi");
        sendButton.setEnabled(false); // Disable until connected

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        connectButton.addActionListener(new ActionListener() {
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
        if (serverAddress.isEmpty()) {
            messageArea.append("Vui lòng nhập địa chỉ Server.\n");
            return;
        }

        new Thread(() -> {
            try {
                socket = new Socket(serverAddress, 12345); // Use the same fixed port
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                messageArea.append("Đã kết nối đến Server.\n");
                SwingUtilities.invokeLater(() -> sendButton.setEnabled(true));

                String line;
                while ((line = in.readLine()) != null) {
                    messageArea.append("Server: " + line + "\n");
                }

            } catch (IOException e) {
                messageArea.append("Lỗi Client: " + e.getMessage() + "\n");
            } finally {
                try {
                    if (socket != null) socket.close();
                } catch (IOException e) {
                    messageArea.append("Lỗi đóng kết nối Client: " + e.getMessage() + "\n");
                }
                SwingUtilities.invokeLater(() -> sendButton.setEnabled(false));
            }
        }).start();
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
    //         new Bt1Client().setVisible(true);
    //     });
    // }
}