package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for scanning high ports
 */
public class HighPortScanner extends BasePanel {
    private JTextField hostField;
    private JTextField startPortField;
    private JTextField endPortField;
    
    public HighPortScanner() {
        super();
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JLabel startPortLabel = new JLabel("Start Port:");
        startPortField = new JTextField(5);
        startPortField.setText("1025");
        JLabel endPortLabel = new JLabel("End Port:");
        endPortField = new JTextField(5);
        endPortField.setText("65535");
        JButton scanButton = new JButton("Scan High Ports");
        
        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
        inputPanel.add(startPortLabel);
        inputPanel.add(startPortField);
        inputPanel.add(endPortLabel);
        inputPanel.add(endPortField);
        inputPanel.add(scanButton);
        
        // Create result area
        JTextArea resultArea = createResultArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Add components to panel
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listener for scan button
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("High Port Scanner functionality will be implemented in the future.\n");
                resultArea.append("This will scan ports " + startPortField.getText() + 
                               " to " + endPortField.getText() + " on " + hostField.getText() + ".");
            }
        });
    }
}
