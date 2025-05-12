package lab8;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Lab 8: Simple Client/Server Application
 * This lab demonstrates a basic client/server model using Java Sockets
 * where the client sends a string to the server, the server converts it
 * to uppercase, and sends it back to the client.
 */
public class Lab8 extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public Lab8() {
        setTitle("Lab 8 - Client/Server Application");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with card layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create welcome panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Lab 8", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(titleLabel, BorderLayout.NORTH);

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton serverButton = new JButton("Start Server");
        JButton clientButton = new JButton("Start Client");

        buttonPanel.add(serverButton);
        buttonPanel.add(clientButton);
        welcomePanel.add(buttonPanel, BorderLayout.CENTER);

        // Add action listeners
        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open server in a new window
                SwingUtilities.invokeLater(() -> {
                    new Server().setVisible(true);
                });
            }
        });

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open client in a new window
                SwingUtilities.invokeLater(() -> {
                    new Client().setVisible(true);
                });
            }
        });

        // Add panels to main panel
        mainPanel.add(welcomePanel, "welcome");

        // Add main panel to frame
        add(mainPanel);
    }
}
