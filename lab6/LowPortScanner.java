package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for scanning low ports
 */
public class LowPortScanner extends BasePanel {
    private JTextField startPortField;
    private JTextField endPortField;
    
    public LowPortScanner() {
        super();
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel startPortLabel = new JLabel("Start Port:");
        startPortField = new JTextField(5);
        startPortField.setText("1");
        JLabel endPortLabel = new JLabel("End Port:");
        endPortField = new JTextField(5);
        endPortField.setText("1024");
        JButton scanButton = new JButton("Scan Low Ports");
        
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
                resultArea.setText("Low Port Scanner functionality will be implemented in the future.\n");
                resultArea.append("This will scan ports " + startPortField.getText() + 
                               " to " + endPortField.getText() + " on localhost.");
            }
        });
    }
}
