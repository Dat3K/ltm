package lab3;
import javax.swing.*;
import java.awt.*;

public class Lab3 extends JFrame {
    
    public Lab3() {
        setTitle("52100781_NguyenThanhDat - Lab 3");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create a tabbed pane to organize exercises
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Exercise 3.1 tab (Hostname to IP Address)
        Exercise31Panel exercise31Panel = new Exercise31Panel();
        tabbedPane.addTab("Exercise 3.1: Hostname to IP", null, exercise31Panel, "Convert hostname to IP address");
        
        // Add Exercise 3.2 tab
        Exercise32Panel exercise32Panel = new Exercise32Panel();
        tabbedPane.addTab("Exercise 3.2: IP Information", null, exercise32Panel, "Display detailed IP information");
        
        // Add the tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add a title at the top
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Lab 3 Exercises", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
    }
}
