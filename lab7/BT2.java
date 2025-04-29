package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BT2 extends JPanel {

    public BT2() {
        setLayout(new GridBagLayout()); // Use GridBagLayout to center the button panel

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout for buttons

        JButton openServerButton = new JButton("Mở Giao diện Server");
        JButton openClientButton = new JButton("Mở Giao diện Client");

        buttonPanel.add(openServerButton);
        buttonPanel.add(openClientButton);

        add(buttonPanel); // Add the button panel to the center of BT2

        // Action listeners
        openServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Bt2Server serverFrame = new Bt2Server();
                    serverFrame.setVisible(true);
                });
            }
        });

        openClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 SwingUtilities.invokeLater(() -> {
                    Bt2Client clientFrame = new Bt2Client();
                    clientFrame.setVisible(true);
                });
            }
        });
    }
}