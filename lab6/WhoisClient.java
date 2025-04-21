package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel for whois client
 */
public class WhoisClient extends BasePanel {
    private JTextField serverField;
    private JTextField domainField;
    
    public WhoisClient() {
        super();
        
        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel serverLabel = new JLabel("Whois Server:");
        serverField = new JTextField(20);
        serverField.setText("whois.internic.net");
        JLabel domainLabel = new JLabel("Domain:");
        domainField = new JTextField(20);
        domainField.setText("example.com");
        JButton lookupButton = new JButton("Lookup");
        
        inputPanel.add(serverLabel);
        inputPanel.add(serverField);
        inputPanel.add(domainLabel);
        inputPanel.add(domainField);
        inputPanel.add(lookupButton);
        
        // Create result area
        JTextArea resultArea = createResultArea();
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Add components to panel
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add action listener for lookup button
        lookupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("Whois client functionality will be implemented in the future.\n");
                resultArea.append("This will query the whois server at " + serverField.getText() + 
                               " for information about " + domainField.getText() + ".");
            }
        });
    }
}
