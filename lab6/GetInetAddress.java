package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for getting InetAddress information
 */
public class GetInetAddress extends BasePanel {
    private JTextField hostField;
    
    public GetInetAddress() {
        super();
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JButton getButton = new JButton("Get InetAddress");
        
        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
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
                resultArea.setText("GetInetAddress functionality will be implemented in the future.\n");
                resultArea.append("This will display the InetAddress for " + hostField.getText() + ".");
            }
        });
    }
}
