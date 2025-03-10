package lab2;
import javax.swing.*;
import java.awt.*;

public class Lab2 extends JFrame {
    public Lab2() {
        setTitle("52100781_NguyenThanhDat - Lab 2");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Lab 2 Exercises", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}
