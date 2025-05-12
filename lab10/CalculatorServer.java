package lab10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Server Máy tính
 * Lắng nghe yêu cầu tính toán từ Client qua cả TCP và UDP
 */
public class CalculatorServer extends JFrame {
    private JTextArea logArea;
    private JTextField tcpPortField;
    private JTextField udpPortField;
    private JButton startButton;
    private JLabel statusLabel;
    
    private ServerSocket tcpServerSocket;
    private DatagramSocket udpServerSocket;
    private boolean running = false;
    private Thread tcpThread;
    private Thread udpThread;
    
    public CalculatorServer() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Máy tính - Server (TCP & UDP)");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel cấu hình server
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel tcpPortLabel = new JLabel("Cổng TCP:");
        tcpPortField = new JTextField("9001", 5);
        JLabel udpPortLabel = new JLabel("Cổng UDP:");
        udpPortField = new JTextField("9002", 5);
        startButton = new JButton("Bắt đầu lắng nghe");
        configPanel.add(tcpPortLabel);
        configPanel.add(tcpPortField);
        configPanel.add(udpPortLabel);
        configPanel.add(udpPortField);
        configPanel.add(startButton);
        
        // Panel hiển thị log
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Panel trạng thái
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Trạng thái: Chưa khởi động");
        statusPanel.add(statusLabel);
        
        // Thêm các panel vào panel chính
        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
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
    }
    
    /**
     * Khởi động server và bắt đầu lắng nghe yêu cầu
     */
    private void startServer() {
        try {
            int tcpPort = Integer.parseInt(tcpPortField.getText().trim());
            int udpPort = Integer.parseInt(udpPortField.getText().trim());
            
            // Tạo TCP server socket
            tcpServerSocket = new ServerSocket(tcpPort);
            
            // Tạo UDP server socket
            udpServerSocket = new DatagramSocket(udpPort);
            
            running = true;
            
            // Cập nhật giao diện
            startButton.setText("Dừng lắng nghe");
            tcpPortField.setEnabled(false);
            udpPortField.setEnabled(false);
            statusLabel.setText("Trạng thái: Đang lắng nghe (TCP: " + tcpPort + ", UDP: " + udpPort + ")");
            
            // Thêm thông báo vào logArea
            appendToLog("Server đã bắt đầu lắng nghe trên cổng TCP: " + tcpPort + ", UDP: " + udpPort);
            
            // Tạo thread để lắng nghe kết nối TCP
            tcpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForTCPConnections();
                }
            });
            tcpThread.start();
            
            // Tạo thread để lắng nghe gói tin UDP
            udpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listenForUDPPackets();
                }
            });
            udpThread.start();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cổng không hợp lệ. Vui lòng nhập một số nguyên.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
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
            
            // Đóng TCP server socket
            if (tcpServerSocket != null && !tcpServerSocket.isClosed()) {
                try {
                    tcpServerSocket.close();
                } catch (IOException e) {
                    appendToLog("Lỗi khi đóng TCP server socket: " + e.getMessage());
                }
            }
            
            // Đóng UDP server socket
            if (udpServerSocket != null && !udpServerSocket.isClosed()) {
                udpServerSocket.close();
            }
            
            // Cập nhật giao diện
            startButton.setText("Bắt đầu lắng nghe");
            tcpPortField.setEnabled(true);
            udpPortField.setEnabled(true);
            statusLabel.setText("Trạng thái: Đã dừng");
            
            // Thêm thông báo vào logArea
            appendToLog("Server đã dừng lắng nghe");
            
            // Dừng các thread
            if (tcpThread != null) {
                tcpThread.interrupt();
            }
            if (udpThread != null) {
                udpThread.interrupt();
            }
        }
    }
    
    /**
     * Lắng nghe kết nối TCP
     */
    private void listenForTCPConnections() {
        while (running) {
            try {
                // Chấp nhận kết nối từ client
                final Socket clientSocket = tcpServerSocket.accept();
                
                // Tạo thread mới để xử lý kết nối
                Thread clientThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handleTCPClient(clientSocket);
                    }
                });
                clientThread.start();
                
            } catch (IOException e) {
                if (running) {
                    final String errorMsg = "Lỗi khi chấp nhận kết nối TCP: " + e.getMessage();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            appendToLog(errorMsg);
                            statusLabel.setText("Trạng thái: Lỗi");
                        }
                    });
                }
            }
        }
    }
    
    /**
     * Xử lý client TCP
     */
    private void handleTCPClient(Socket clientSocket) {
        try {
            // Lấy thông tin client
            InetAddress clientAddress = clientSocket.getInetAddress();
            int clientPort = clientSocket.getPort();
            
            // Tạo reader và writer
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            
            // Đọc yêu cầu từ client
            String request = in.readLine();
            
            // Hiển thị yêu cầu lên giao diện
            final String clientInfo = clientAddress.getHostAddress() + ":" + clientPort;
            final String receivedRequest = request;
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendToLog("Nhận yêu cầu TCP từ " + clientInfo + ": " + receivedRequest);
                }
            });
            
            // Xử lý yêu cầu
            String result = processCalculation(request);
            
            // Gửi kết quả về client
            out.println(result);
            
            // Hiển thị kết quả lên giao diện
            final String sentResult = result;
            
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendToLog("Gửi kết quả TCP đến " + clientInfo + ": " + sentResult);
                }
            });
            
            // Đóng kết nối
            clientSocket.close();
            
        } catch (IOException e) {
            final String errorMsg = "Lỗi khi xử lý client TCP: " + e.getMessage();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    appendToLog(errorMsg);
                }
            });
        }
    }
    
    /**
     * Lắng nghe gói tin UDP
     */
    private void listenForUDPPackets() {
        byte[] buffer = new byte[1024];
        
        while (running) {
            try {
                // Tạo gói tin để nhận dữ liệu
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                
                // Nhận gói tin
                udpServerSocket.receive(packet);
                
                // Lấy thông tin client
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                
                // Lấy nội dung yêu cầu
                String request = new String(packet.getData(), 0, packet.getLength());
                
                // Hiển thị yêu cầu lên giao diện
                final String clientInfo = clientAddress.getHostAddress() + ":" + clientPort;
                final String receivedRequest = request;
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        appendToLog("Nhận yêu cầu UDP từ " + clientInfo + ": " + receivedRequest);
                    }
                });
                
                // Xử lý yêu cầu
                String result = processCalculation(request);
                
                // Gửi kết quả về client
                byte[] responseBuffer = result.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, 
                        clientAddress, clientPort);
                udpServerSocket.send(responsePacket);
                
                // Hiển thị kết quả lên giao diện
                final String sentResult = result;
                
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        appendToLog("Gửi kết quả UDP đến " + clientInfo + ": " + sentResult);
                    }
                });
                
            } catch (IOException e) {
                if (running) {
                    final String errorMsg = "Lỗi khi xử lý gói tin UDP: " + e.getMessage();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            appendToLog(errorMsg);
                        }
                    });
                }
            }
        }
    }
    
    /**
     * Xử lý yêu cầu tính toán
     * Định dạng yêu cầu: "số1;toán_tử;số2"
     */
    private String processCalculation(String request) {
        try {
            // Tách các thành phần của yêu cầu
            String[] parts = request.split(";");
            if (parts.length != 3) {
                return "Lỗi: Định dạng yêu cầu không hợp lệ. Sử dụng định dạng: số1;toán_tử;số2";
            }
            
            // Lấy các thành phần
            double num1 = Double.parseDouble(parts[0]);
            String operator = parts[1];
            double num2 = Double.parseDouble(parts[2]);
            
            // Thực hiện phép tính
            double result;
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        return "Lỗi: Không thể chia cho 0";
                    }
                    result = num1 / num2;
                    break;
                default:
                    return "Lỗi: Toán tử không hợp lệ. Sử dụng +, -, *, /";
            }
            
            // Trả về kết quả
            return String.valueOf(result);
            
        } catch (NumberFormatException e) {
            return "Lỗi: Số không hợp lệ";
        } catch (Exception e) {
            return "Lỗi: " + e.getMessage();
        }
    }
    
    /**
     * Thêm thông báo vào logArea với timestamp
     */
    private void appendToLog(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());
        logArea.append("[" + timestamp + "] " + message + "\n");
        
        // Cuộn xuống để hiển thị thông báo mới nhất
        logArea.setCaretPosition(logArea.getDocument().getLength());
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
