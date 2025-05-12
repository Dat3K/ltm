package lab10;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Màn hình chọn chế độ Chat UDP (Client hoặc Server)
 */
public class ChatUDP extends JFrame {
    
    public ChatUDP() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Chat UDP - Chọn chế độ");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Tạo tiêu đề
        JLabel titleLabel = new JLabel("Chat UDP Client/Server", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tạo panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        
        // Tạo các nút chức năng
        JButton serverButton = new JButton("Chạy ở chế độ Server");
        JButton clientButton = new JButton("Chạy ở chế độ Client");
        
        // Thiết lập font cho các nút
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        serverButton.setFont(buttonFont);
        clientButton.setFont(buttonFont);
        
        // Thêm các nút vào panel
        buttonPanel.add(serverButton);
        buttonPanel.add(clientButton);
        
        // Thêm panel nút vào panel chính
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Thêm panel chính vào cửa sổ
        add(mainPanel);
        
        // Thêm action listener cho nút Server
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở cửa sổ Server
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ChatUDPServer().setVisible(true);
                    }
                });
            }
        });
        
        // Thêm action listener cho nút Client
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở cửa sổ Client
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ChatUDPClient().setVisible(true);
                    }
                });
            }
        });
    }
}
