package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for getting port information
 */
public class GetPort extends BasePanel {
    private JTextField hostField;
    private JTextField portField;
    
    public GetPort() {
        super();
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(5);
        portField.setText("80");
        JButton getButton = new JButton("Get Port");
        
        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);
        inputPanel.add(getButton);
        
        // Create result area
        JTextArea resultArea = createResultArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Add components to panel
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listener for get button
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("GetPort functionality will be implemented in the future.\n");
                resultArea.append("This will display the port information for " + 
                               hostField.getText() + ":" + portField.getText() + ".");
            }
        });
    }
}
