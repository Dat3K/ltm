import javax.swing.*;
import java.awt.*;

public class Lab3 extends JFrame {
    public Lab3() {
        setTitle("52100781_NguyenThanhDat - Lab 3");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Lab 3 Exercises", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}
