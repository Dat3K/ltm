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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Server Chat UDP
 * Lắng nghe tin nhắn từ Client và gửi tin nhắn đến Client
 */
public class ChatUDPServer extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JTextField portField;
    private JButton startButton;
    private JButton sendButton;
    private JLabel statusLabel;
    private JComboBox<String> clientComboBox;
    private JLabel clientCountLabel;

    private DatagramSocket socket;
    private boolean running = false;
    private InetAddress clientAddress;
    private int clientPort;
    private Thread receiveThread;

    // Lưu trữ thông tin các client đã kết nối
    private Map<String, ClientInfo> connectedClients = new HashMap<>();

    // Lớp lưu trữ thông tin client
    private static class ClientInfo {
        InetAddress address;
        int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }
    }

    public ChatUDPServer() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Chat UDP - Server");
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel cấu hình server
        JPanel configPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Panel cấu hình cổng
        JPanel portPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel portLabel = new JLabel("Cổng lắng nghe:");
        portField = new JTextField("9000", 5);
        startButton = new JButton("Bắt đầu lắng nghe");
        startButton.setBackground(new Color(100, 180, 100));
        startButton.setForeground(Color.WHITE);
        portPanel.add(portLabel);
        portPanel.add(portField);
        portPanel.add(startButton);

        // Panel hiển thị client
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel clientLabel = new JLabel("Client đã kết nối:");
        clientComboBox = new JComboBox<>();
        clientComboBox.setPreferredSize(new Dimension(200, 25));
        clientCountLabel = new JLabel("(0 client)");
        clientPanel.add(clientLabel);
        clientPanel.add(clientComboBox);
        clientPanel.add(clientCountLabel);

        // Thêm các panel vào panel cấu hình
        configPanel.add(portPanel);
        configPanel.add(clientPanel);

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
        messagePanel.setPreferredSize(new Dimension(700, 60)); // Đặt kích thước cố định

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));

        sendButton = new JButton("Gửi");
        sendButton.setEnabled(false);
        sendButton.setBackground(new Color(70, 130, 180));
        sendButton.setForeground(Color.WHITE);
        sendButton.setPreferredSize(new Dimension(80, 30));
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        // Panel trạng thái
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Trạng thái: Chưa khởi động");
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

        // Thêm action listener cho nút Bắt đầu lắng nghe
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    startServer();
                } else {
                    stopServer();
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

        // Thêm action listener cho combobox client
        clientComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Khi chọn client, cập nhật trạng thái
                String selectedClient = (String) clientComboBox.getSelectedItem();
                if (selectedClient != null) {
                    statusLabel.setText("Trạng thái: Đã chọn client " + selectedClient);
                }
            }
        });
    }

    /**
     * Khởi động server và bắt đầu lắng nghe tin nhắn
     */
    private void startServer() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            socket = new DatagramSocket(port);
            running = true;

            // Cập nhật giao diện
            startButton.setText("Dừng lắng nghe");
            portField.setEnabled(false);
            statusLabel.setText("Trạng thái: Đang lắng nghe trên cổng " + port);

            // Thêm thông báo vào chatArea
            appendToChatArea("Server đã bắt đầu lắng nghe trên cổng " + port);

            // Tạo thread để lắng nghe tin nhắn
            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForMessages();
                }
            });
            receiveThread.start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cổng không hợp lệ. Vui lòng nhập một số nguyên.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SocketException e) {
            JOptionPane.showMessageDialog(this, "Không thể mở cổng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Trạng thái: Lỗi khi khởi động server");
        }
    }

    /**
     * Dừng server
     */
    private void stopServer() {
        if (running) {
            running = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }

            // Cập nhật giao diện
            startButton.setText("Bắt đầu lắng nghe");
            portField.setEnabled(true);
            sendButton.setEnabled(false);
            statusLabel.setText("Trạng thái: Đã dừng");

            // Thêm thông báo vào chatArea
            appendToChatArea("Server đã dừng lắng nghe");

            // Dừng thread lắng nghe
            if (receiveThread != null) {
                receiveThread.interrupt();
            }
        }
    }

    /**
     * Lắng nghe tin nhắn từ client
     */
    private void listenForMessages() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (running) {
            try {
                socket.receive(packet);

                // Lấy thông tin client
                final InetAddress clientAddr = packet.getAddress();
                final int clientP = packet.getPort();

                // Lưu thông tin client hiện tại để gửi tin nhắn
                clientAddress = clientAddr;
                clientPort = clientP;

                // Lấy nội dung tin nhắn
                String message = new String(packet.getData(), 0, packet.getLength());

                // Tạo key cho client
                final String clientKey = clientAddr.getHostAddress() + ":" + clientP;

                // Thêm client vào danh sách nếu chưa có
                if (!connectedClients.containsKey(clientKey)) {
                    connectedClients.put(clientKey, new ClientInfo(clientAddr, clientP));

                    // Cập nhật UI với client mới
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            clientComboBox.addItem(clientKey);
                            clientCountLabel.setText("(" + connectedClients.size() + " client)");
                            appendToChatArea("Client mới kết nối: " + clientKey);
                        }
                    });
                }

                // Hiển thị tin nhắn lên giao diện
                final String receivedMessage = message;

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        // Chọn client hiện tại trong combobox
                        clientComboBox.setSelectedItem(clientKey);

                        // Hiển thị tin nhắn với định dạng đẹp hơn
                        appendToChatArea("📩 " + clientKey + ": " + receivedMessage);
                        statusLabel.setText("Trạng thái: Đã nhận tin nhắn từ " + clientKey);
                        sendButton.setEnabled(true);
                    }
                });

            } catch (IOException e) {
                if (running) {
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
     * Gửi tin nhắn đến client
     */
    private void sendMessage() {
        if (running) {
            String message = messageField.getText().trim();
            if (!message.isEmpty()) {
                try {
                    // Lấy client được chọn từ combobox
                    String selectedClientKey = (String) clientComboBox.getSelectedItem();

                    if (selectedClientKey != null) {
                        // Lấy thông tin client từ map
                        ClientInfo clientInfo = connectedClients.get(selectedClientKey);

                        if (clientInfo != null) {
                            // Tạo và gửi gói tin
                            byte[] buffer = message.getBytes();
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
                                    clientInfo.address, clientInfo.port);
                            socket.send(packet);

                            // Hiển thị tin nhắn đã gửi với định dạng đẹp hơn
                            appendToChatArea("📤 Server → " + selectedClientKey + ": " + message);
                            statusLabel.setText("Trạng thái: Đã gửi tin nhắn đến " + selectedClientKey);

                            // Xóa nội dung ô nhập tin nhắn
                            messageField.setText("");
                            messageField.requestFocus();
                        } else {
                            // Trường hợp không tìm thấy client trong map (hiếm khi xảy ra)
                            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin client",
                                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (clientAddress != null) {
                        // Trường hợp không có client nào trong combobox nhưng có client gần nhất
                        byte[] buffer = message.getBytes();
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                        socket.send(packet);

                        // Hiển thị tin nhắn đã gửi
                        String clientKey = clientAddress.getHostAddress() + ":" + clientPort;
                        appendToChatArea("📤 Server → " + clientKey + ": " + message);
                        statusLabel.setText("Trạng thái: Đã gửi tin nhắn đến " + clientKey);

                        // Xóa nội dung ô nhập tin nhắn
                        messageField.setText("");
                        messageField.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Chưa có client nào kết nối",
                                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    }
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
        stopServer();
        super.dispose();
    }
}
