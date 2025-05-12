package lab10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client Chat UDP
 * Gửi tin nhắn đến Server và nhận tin nhắn từ Server
 */
public class ChatUDPClient extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField serverAddressField;
    private JTextField serverPortField;
    private JButton connectButton;
    private JButton sendButton;
    private JLabel statusLabel;

    private DatagramSocket socket;
    private boolean connected = false;
    private InetAddress serverAddress;
    private int serverPort;
    private Thread receiveThread;

    public ChatUDPClient() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Chat UDP - Client");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel cấu hình kết nối
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình kết nối"));

        JLabel serverAddressLabel = new JLabel("Địa chỉ Server:");
        serverAddressField = new JTextField("localhost", 10);
        JLabel serverPortLabel = new JLabel("Cổng Server:");
        serverPortField = new JTextField("9000", 5);

        connectButton = new JButton("Kết nối");
        connectButton.setPreferredSize(new Dimension(100, 30));
        connectButton.setBackground(new Color(100, 180, 100));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFont(new Font("Arial", Font.BOLD, 12));

        configPanel.add(serverAddressLabel);
        configPanel.add(serverAddressField);
        configPanel.add(serverPortLabel);
        configPanel.add(serverPortField);
        configPanel.add(connectButton);

        // Panel hiển thị tin nhắn
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatPanel.setBorder(BorderFactory.createTitledBorder("Tin nhắn"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setBackground(new Color(250, 250, 250));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel nhập và gửi tin nhắn
        JPanel messagePanel = new JPanel(new BorderLayout(5, 0));
        messagePanel.setBorder(BorderFactory.createTitledBorder("Nhập tin nhắn"));
        messagePanel.setPreferredSize(new Dimension(600, 60)); // Đặt kích thước cố định

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));

        sendButton = new JButton("Gửi");
        sendButton.setEnabled(false);
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(80, 30));

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Panel trạng thái
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Trạng thái: Chưa kết nối");
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel);

        // Tạo panel chứa khu vực chat và nhập tin nhắn
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.add(chatPanel, BorderLayout.CENTER);
        centerPanel.add(messagePanel, BorderLayout.SOUTH);

        // Thêm các panel vào panel chính
        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        // Thêm panel chính vào cửa sổ
        add(mainPanel);

        // Thêm action listener cho nút Kết nối
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!connected) {
                    connectToServer();
                } else {
                    disconnectFromServer();
                }
            }
        });

        // Thêm action listener cho nút Gửi
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Thêm action listener cho phím Enter trong ô nhập tin nhắn
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }

    /**
     * Kết nối đến server
     */
    private void connectToServer() {
        try {
            // Lấy thông tin server
            String serverAddressStr = serverAddressField.getText().trim();
            serverPort = Integer.parseInt(serverPortField.getText().trim());
            serverAddress = InetAddress.getByName(serverAddressStr);

            // Tạo socket
            socket = new DatagramSocket();
            connected = true;

            // Cập nhật giao diện
            connectButton.setText("Ngắt kết nối");
            serverAddressField.setEnabled(false);
            serverPortField.setEnabled(false);
            sendButton.setEnabled(true);
            statusLabel.setText("Trạng thái: Đã kết nối tới " + serverAddressStr + ":" + serverPort);

            // Thêm thông báo vào chatArea
            appendToChatArea("Đã kết nối tới server " + serverAddressStr + ":" + serverPort);

            // Tạo thread để lắng nghe tin nhắn từ server
            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForMessages();
                }
            });
            receiveThread.start();

            // Gửi tin nhắn chào server
            String helloMessage = "Xin chào Server!";
            byte[] buffer = helloMessage.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(packet);

            // Hiển thị tin nhắn đã gửi
            appendToChatArea("Gửi đến Server: " + helloMessage);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cổng không hợp lệ. Vui lòng nhập một số nguyên.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(this, "Không thể phân giải địa chỉ server: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tạo socket: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi gửi tin nhắn chào: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ngắt kết nối từ server
     */
    private void disconnectFromServer() {
        if (connected) {
            connected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // Cập nhật giao diện
            connectButton.setText("Kết nối");
            serverAddressField.setEnabled(true);
            serverPortField.setEnabled(true);
            sendButton.setEnabled(false);
            statusLabel.setText("Trạng thái: Đã ngắt kết nối");

            // Thêm thông báo vào chatArea
            appendToChatArea("Đã ngắt kết nối từ server");

            // Dừng thread lắng nghe
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
        }
    }

    /**
     * Lắng nghe tin nhắn từ server
     */
    private void listenForMessages() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (connected) {
            try {
                socket.receive(packet);

                // Lấy nội dung tin nhắn
                String message = new String(packet.getData(), 0, packet.getLength());

                // Hiển thị tin nhắn lên giao diện
                final String receivedMessage = message;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        appendToChatArea("Nhận từ Server: " + receivedMessage);
                        statusLabel.setText("Trạng thái: Đã nhận tin nhắn từ Server");

                        // Hiệu ứng nhấp nháy màu nền khi nhận tin nhắn mới
                        final Color originalColor = chatArea.getBackground();
                        chatArea.setBackground(new Color(230, 240, 255));

                        // Timer để đặt lại màu nền sau 500ms
                        Timer timer = new Timer(500, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                chatArea.setBackground(originalColor);
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                });

            } catch (IOException e) {
                if (connected) {
                    final String errorMsg = "Lỗi khi nhận tin nhắn: " + e.getMessage();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            appendToChatArea(errorMsg);
                            statusLabel.setText("Trạng thái: Lỗi");
                        }
                    });
                }
            }
        }
    }

    /**
     * Gửi tin nhắn đến server
     */
    private void sendMessage() {
        if (connected) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    // Tạo và gửi gói tin
                    byte[] buffer = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                    socket.send(packet);

                    // Hiển thị tin nhắn đã gửi
                    appendToChatArea("Gửi đến Server: " + message);
                    statusLabel.setText("Trạng thái: Đã gửi tin nhắn");

                    // Xóa nội dung ô nhập tin nhắn
                    messageField.setText("");
                    messageField.requestFocus();

                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi gửi tin nhắn: " + e.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    statusLabel.setText("Trạng thái: Lỗi khi gửi tin nhắn");
                }
            }
        }
    }

    /**
     * Thêm tin nhắn vào chatArea với timestamp
     */
    private void appendToChatArea(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Thêm hiệu ứng âm thanh khi nhận tin nhắn mới (chỉ khi tin nhắn bắt đầu bằng "Nhận từ")
        if (message.startsWith("Nhận từ")) {
            Toolkit.getDefaultToolkit().beep();
        }

        // Thêm tin nhắn với định dạng đẹp hơn
        chatArea.append("[" + timestamp + "] " + message + "\n");

        // Cuộn xuống để hiển thị tin nhắn mới nhất
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }

    /**
     * Xử lý khi đóng cửa sổ
     */
    @Override
    public void dispose() {
        disconnectFromServer();
        super.dispose();
    }
}
