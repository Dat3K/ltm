package lab4;
import javax.swing.*;
import java.awt.*;

public class Lab4 extends JFrame {
    public Lab4() {
        setTitle("Lab 4");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create a label with placeholder text
        JLabel placeholderLabel = new JLabel("Lab 4 - Placeholder");
        placeholderLabel.setFont(new Font("Arial", Font.BOLD, 20));
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add the label to the center of the frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(placeholderLabel, BorderLayout.CENTER);
    }
}
