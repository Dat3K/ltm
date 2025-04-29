package lab7;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BT1 extends JPanel {

    public BT1() {
        setLayout(new GridBagLayout()); // Use GridBagLayout to center the button panel

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // FlowLayout for buttons

        JButton openServerButton = new JButton("Mở Giao diện Server");
        JButton openClientButton = new JButton("Mở Giao diện Client");

        buttonPanel.add(openServerButton);
        buttonPanel.add(openClientButton);

        add(buttonPanel); // Add the button panel to the center of BT1

        // Action listeners
        openServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    Bt1Server serverFrame = new Bt1Server();
                    serverFrame.setVisible(true);
                });
            }
        });

        openClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 SwingUtilities.invokeLater(() -> {
                    Bt1Client clientFrame = new Bt1Client();
                    clientFrame.setVisible(true);
                });
            }
        });
    }
}