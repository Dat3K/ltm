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
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Client Máy tính
 * Gửi yêu cầu tính toán đến Server qua TCP hoặc UDP
 */
public class CalculatorClient extends JFrame {
    private JTextField serverAddressField;
    private JTextField tcpPortField;
    private JTextField udpPortField;
    private JTextField num1Field;
    private JTextField num2Field;
    private JComboBox<String> operatorComboBox;
    private JRadioButton tcpRadioButton;
    private JRadioButton udpRadioButton;
    private JButton calculateButton;
    private JTextArea resultArea;
    private JLabel statusLabel;

    public CalculatorClient() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Máy tính - Client (TCP & UDP)");
        setSize(650, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel cấu hình kết nối
        JPanel configPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        configPanel.setBorder(BorderFactory.createTitledBorder("Cấu hình kết nối"));

        // Panel địa chỉ server
        JPanel serverPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel serverAddressLabel = new JLabel("Địa chỉ Server:");
        serverAddressField = new JTextField("localhost", 10);
        JLabel tcpPortLabel = new JLabel("Cổng TCP:");
        tcpPortField = new JTextField("9001", 5);
        JLabel udpPortLabel = new JLabel("Cổng UDP:");
        udpPortField = new JTextField("9002", 5);
        serverPanel.add(serverAddressLabel);
        serverPanel.add(serverAddressField);
        serverPanel.add(tcpPortLabel);
        serverPanel.add(tcpPortField);
        serverPanel.add(udpPortLabel);
        serverPanel.add(udpPortField);

        // Panel chọn giao thức
        JPanel protocolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel protocolLabel = new JLabel("Giao thức:");
        tcpRadioButton = new JRadioButton("TCP");
        udpRadioButton = new JRadioButton("UDP");
        ButtonGroup protocolGroup = new ButtonGroup();
        protocolGroup.add(tcpRadioButton);
        protocolGroup.add(udpRadioButton);
        tcpRadioButton.setSelected(true);

        // Tạo màu sắc cho các radio button
        tcpRadioButton.setForeground(new Color(0, 100, 200));
        udpRadioButton.setForeground(new Color(200, 100, 0));

        protocolPanel.add(protocolLabel);
        protocolPanel.add(tcpRadioButton);
        protocolPanel.add(udpRadioButton);

        // Thêm các panel vào panel cấu hình
        configPanel.add(serverPanel);
        configPanel.add(protocolPanel);

        // Panel nhập liệu
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Nhập phép tính"));

        // Panel chứa các thành phần nhập liệu
        JPanel calcPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JLabel num1Label = new JLabel("Số thứ nhất:");
        num1Field = new JTextField(8);
        num1Field.setFont(new Font("Arial", Font.PLAIN, 14));

        // ComboBox cho phép toán
        JLabel operatorLabel = new JLabel("Phép toán:");
        operatorComboBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        operatorComboBox.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel num2Label = new JLabel("Số thứ hai:");
        num2Field = new JTextField(8);
        num2Field.setFont(new Font("Arial", Font.PLAIN, 14));

        calculateButton = new JButton("Tính");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setBackground(new Color(70, 130, 180));
        calculateButton.setForeground(Color.WHITE);

        calcPanel.add(num1Label);
        calcPanel.add(num1Field);
        calcPanel.add(operatorLabel);
        calcPanel.add(operatorComboBox);
        calcPanel.add(num2Label);
        calcPanel.add(num2Field);
        calcPanel.add(calculateButton);

        // Thêm panel tính toán vào panel nhập liệu
        inputPanel.add(calcPanel);

        // Panel hiển thị kết quả
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Kết quả"));

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Arial", Font.PLAIN, 14));
        resultArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel trạng thái
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Trạng thái: Sẵn sàng");
        statusLabel.setForeground(new Color(100, 100, 100));
        statusPanel.add(statusLabel);

        // Thêm các panel vào panel chính
        mainPanel.add(configPanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);
        mainPanel.add(statusPanel, BorderLayout.PAGE_END);

        // Thiết lập kích thước cho panel kết quả
        resultPanel.setPreferredSize(new Dimension(650, 200));

        // Thêm panel chính vào cửa sổ
        add(mainPanel);

        // Thêm action listener cho nút Tính
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculate();
            }
        });
    }

    /**
     * Thực hiện tính toán
     */
    private void calculate() {
        try {
            // Lấy thông tin server
            String serverAddress = serverAddressField.getText().trim();
            int tcpPort = Integer.parseInt(tcpPortField.getText().trim());
            int udpPort = Integer.parseInt(udpPortField.getText().trim());

            // Lấy thông tin phép tính
            double num1 = Double.parseDouble(num1Field.getText().trim());
            String operator = (String) operatorComboBox.getSelectedItem();
            double num2 = Double.parseDouble(num2Field.getText().trim());

            // Kiểm tra phép chia cho 0
            if (operator.equals("/") && num2 == 0) {
                JOptionPane.showMessageDialog(this, "Không thể chia cho 0",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo yêu cầu
            String request = num1 + ";" + operator + ";" + num2;

            // Gửi yêu cầu và nhận kết quả
            String result;
            if (tcpRadioButton.isSelected()) {
                result = sendTCPRequest(serverAddress, tcpPort, request);
            } else {
                result = sendUDPRequest(serverAddress, udpPort, request);
            }

            // Hiển thị kết quả với định dạng đẹp hơn
            appendToResult("Yêu cầu: " + num1 + " " + operator + " " + num2 + " (qua " +
                    (tcpRadioButton.isSelected() ? "TCP" : "UDP") + ")");

            try {
                // Chuyển kết quả thành số để định dạng đẹp hơn
                double resultValue = Double.parseDouble(result);
                // Kiểm tra nếu kết quả là số nguyên
                if (resultValue == Math.floor(resultValue)) {
                    // Hiển thị kết quả dưới dạng số nguyên
                    appendToResult("Kết quả: " + (int)resultValue);
                } else {
                    // Hiển thị kết quả dưới dạng số thực
                    appendToResult("Kết quả: " + resultValue);
                }
            } catch (NumberFormatException e) {
                // Nếu không phải số (có thể là thông báo lỗi), hiển thị nguyên bản
                appendToResult("Kết quả: " + result);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Trạng thái: Lỗi nhập liệu");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Trạng thái: Lỗi");
        }
    }

    /**
     * Gửi yêu cầu qua TCP
     */
    private String sendTCPRequest(String serverAddress, int port, String request) throws IOException {
        statusLabel.setText("Trạng thái: Đang gửi yêu cầu TCP...");

        // Tạo socket
        Socket socket = new Socket(serverAddress, port);

        // Tạo reader và writer
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Gửi yêu cầu
        out.println(request);

        // Nhận kết quả
        String result = in.readLine();

        // Đóng kết nối
        socket.close();

        statusLabel.setText("Trạng thái: Đã nhận kết quả qua TCP");
        return result;
    }

    /**
     * Gửi yêu cầu qua UDP
     */
    private String sendUDPRequest(String serverAddress, int port, String request) throws IOException {
        statusLabel.setText("Trạng thái: Đang gửi yêu cầu UDP...");

        // Tạo socket
        DatagramSocket socket = new DatagramSocket();

        // Tạo gói tin để gửi yêu cầu
        InetAddress address = InetAddress.getByName(serverAddress);
        byte[] sendBuffer = request.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, port);

        // Gửi gói tin
        socket.send(sendPacket);

        // Tạo gói tin để nhận kết quả
        byte[] receiveBuffer = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

        // Nhận gói tin
        socket.receive(receivePacket);

        // Lấy kết quả
        String result = new String(receivePacket.getData(), 0, receivePacket.getLength());

        // Đóng socket
        socket.close();

        statusLabel.setText("Trạng thái: Đã nhận kết quả qua UDP");
        return result;
    }

    /**
     * Thêm thông báo vào resultArea với timestamp
     */
    private void appendToResult(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // Thêm hiệu ứng âm thanh khi nhận kết quả
        if (message.startsWith("Kết quả:")) {
            Toolkit.getDefaultToolkit().beep();

            // Hiệu ứng nhấp nháy màu nền khi nhận kết quả
            final Color originalColor = resultArea.getBackground();
            resultArea.setBackground(new Color(240, 255, 240)); // Màu xanh nhạt

            // Timer để đặt lại màu nền sau 500ms
            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resultArea.setBackground(originalColor);
                }
            });
            timer.setRepeats(false);
            timer.start();

            // Định dạng đẹp hơn cho kết quả
            String formattedMessage = message.replace("Kết quả: ", "Kết quả: ");
            resultArea.append("[" + timestamp + "] " + formattedMessage + "\n");
            resultArea.append("--------------------------------------------------\n");
        } else {
            // Định dạng thông thường cho các thông báo khác
            resultArea.append("[" + timestamp + "] " + message + "\n");
        }

        // Cuộn xuống để hiển thị thông báo mới nhất
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }
}
