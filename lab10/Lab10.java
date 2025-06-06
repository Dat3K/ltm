package lab10;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lab 10 - Ứng dụng Java Swing Minh họa Networking
 * Màn hình chính với hai chức năng:
 * 1. Chat UDP Client/Server
 * 2. Máy tính Client/Server (TCP & UDP)
 */
public class Lab10 extends JFrame {

    public Lab10() {
        // Thiết lập cơ bản cho cửa sổ
        setTitle("Lab 10 - Ứng dụng Java Swing Minh họa Networking");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tạo panel chính với BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Tạo tiêu đề
        JLabel titleLabel = new JLabel("Ứng dụng Java Swing Minh họa Networking", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Tạo panel chứa các nút chức năng
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Tạo các nút chức năng
        JButton chatButton = new JButton("Chat UDP Client/Server");
        JButton calculatorButton = new JButton("Máy tính Client/Server (TCP & UDP)");

        // Thiết lập font cho các nút
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);
        chatButton.setFont(buttonFont);
        calculatorButton.setFont(buttonFont);

        // Thêm các nút vào panel
        buttonPanel.add(chatButton);
        buttonPanel.add(calculatorButton);

        // Thêm panel nút vào panel chính
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Thêm panel chính vào cửa sổ
        add(mainPanel);

        // Thêm action listener cho nút Chat UDP
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở cửa sổ Chat UDP
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new ChatUDP().setVisible(true);
                    }
                });
            }
        });

        // Thêm action listener cho nút Máy tính
        calculatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Mở cửa sổ Máy tính
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new Calculator().setVisible(true);
                    }
                });
            }
        });
    }
}
