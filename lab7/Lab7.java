package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Lab7 extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    public Lab7() {
        setTitle("Lab 7");
        setSize(600, 400); // Increased size for better view
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Use BorderLayout for the frame's content pane
        getContentPane().setLayout(new BorderLayout());

        // Create a panel to hold the buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Create buttons
        JButton bt1 = new JButton("Bài tập 1"); // Changed button text
        JButton bt2 = new JButton("Bài tập 2"); // Changed button text

        // Add buttons to the panel
        buttonPanel.add(bt1);
        buttonPanel.add(bt2);

        // Add the button panel to the top of the frame
        getContentPane().add(buttonPanel, BorderLayout.PAGE_START);

        // Create the content panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Create and add the initial panel with student info
        JPanel initialPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout to center the label
        JLabel infoLabel = new JLabel("<html><div style='text-align: center;'>52100781 - Nguyễn Thành Đạt</div></html>"); // Use HTML for centering
        infoLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Optional: make the text bold and larger
        initialPanel.add(infoLabel);
        contentPanel.add(initialPanel, "Initial"); // Add with a unique name

        // Add the specific panels to the content panel
        contentPanel.add(new BT1(), "BT1");
        contentPanel.add(new BT2(), "BT2");

        // Add the content panel to the center of the frame
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        // Add action listeners to the buttons
        bt1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "BT1");
            }
        });

        bt2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(contentPanel, "BT2");
            }
        });

        // Optional: Add a main method to test the frame
        // public static void main(String[] args) {
        //     SwingUtilities.invokeLater(() -> {
        //         new Lab7().setVisible(true);
        //     });
        // }
    }
}
