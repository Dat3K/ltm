package lab34;

import javax.swing.*;
import javax.naming.directory.*;
import javax.naming.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class DNSLookupPanel extends JPanel {
    private JTextField domainField;
    private JComboBox<String> recordTypeComboBox;
    private JButton lookupButton;
    private JTextArea resultArea;
    
    // DNS record types that we support
    private final String[] recordTypes = {
        "A", "AAAA", "MX", "NS", "CNAME", "TXT", "SOA", "PTR", "SRV", "All"
    };
    
    public DNSLookupPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create title panel
        JLabel titleLabel = new JLabel("<html><h2>DNS Records Lookup Tool</h2></html>");
        add(titleLabel, BorderLayout.NORTH);
        
        // Create the main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        // Input panel (domain and record type)
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        
        // Domain name input
        JPanel domainPanel = new JPanel(new BorderLayout(5, 0));
        JLabel domainLabel = new JLabel("Domain/IP: ");
        domainField = new JTextField(20);
        domainPanel.add(domainLabel, BorderLayout.WEST);
        domainPanel.add(domainField, BorderLayout.CENTER);
        
        // Record type selection
        JPanel typePanel = new JPanel(new BorderLayout(5, 0));
        JLabel typeLabel = new JLabel("Record Type: ");
        recordTypeComboBox = new JComboBox<>(recordTypes);
        typePanel.add(typeLabel, BorderLayout.WEST);
        typePanel.add(recordTypeComboBox, BorderLayout.CENTER);
        
        // Create a panel to hold domain and record type inputs side by side
        JPanel topInputsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topInputsPanel.add(domainPanel);
        topInputsPanel.add(typePanel);
        
        // Add lookup button
        lookupButton = new JButton("Lookup");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(lookupButton);
        
        // Combined input panel
        inputPanel.add(topInputsPanel, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Quick test panel
        JPanel quickTestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quickTestPanel.setBorder(BorderFactory.createTitledBorder("Quick Test"));
        
        String[] commonDomains = {"google.com", "facebook.com", "github.com", "example.com", "microsoft.com"};
        
        for (final String domain : commonDomains) {
            JButton testButton = new JButton(domain);
            testButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    domainField.setText(domain);
                    lookupDnsRecords();
                }
            });
            quickTestPanel.add(testButton);
        }
        
        // Result area
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Description
        JLabel descriptionLabel = new JLabel(
            "<html><b>NSLookup Tool:</b> Enter a domain name and select a record type to query DNS information.</html>"
        );
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Add components to content panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.add(descriptionLabel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(quickTestPanel, BorderLayout.SOUTH);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Add event listeners
        lookupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lookupDnsRecords();
            }
        });
        
        // Add event listener for Enter key in domain field
        domainField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    lookupDnsRecords();
                }
            }
        });
    }
    
    private void lookupDnsRecords() {
        String domain = domainField.getText().trim();
        String recordType = (String) recordTypeComboBox.getSelectedItem();
        
        if (domain.isEmpty()) {
            resultArea.setText("Please enter a domain name or IP address");
            return;
        }
        
        resultArea.setText("Looking up " + recordType + " records for " + domain + "...\n\n");
        resultArea.update(resultArea.getGraphics());
        
        try {
            // Setup environment for creating the initial context
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            env.put("java.naming.provider.url", "dns:");
            
            // Create the initial context
            DirContext ctx = new InitialDirContext(env);
            
            if ("All".equals(recordType)) {
                // Lookup all record types
                for (String type : recordTypes) {
                    if (!"All".equals(type)) {
                        lookupAndDisplayRecords(ctx, domain, type);
                    }
                }
            } else {
                // Lookup specific record type
                lookupAndDisplayRecords(ctx, domain, recordType);
            }
            
            // Close the context when done
            ctx.close();
            
        } catch (NamingException e) {
            resultArea.append("Error: " + e.getMessage() + "\n");
            
            // Try to provide helpful information if lookup fails
            if (e instanceof NameNotFoundException) {
                resultArea.append("\nThe domain name could not be found. Please check for typos.\n");
            } else {
                resultArea.append("\nTry another record type - the selected type might not exist for this domain.\n");
            }
        } catch (Exception e) {
            resultArea.append("An unexpected error occurred: " + e.getMessage() + "\n");
            e.printStackTrace();
        }
        
        // Force UI to update
        resultArea.revalidate();
        resultArea.repaint();
    }
    
    private void lookupAndDisplayRecords(DirContext ctx, String domain, String recordType) {
        try {
            Attributes attrs = ctx.getAttributes(domain, new String[] { recordType });
            Attribute attr = attrs.get(recordType);
            
            if (attr != null) {
                resultArea.append("===== " + recordType + " Records =====\n");
                
                for (int i = 0; i < attr.size(); i++) {
                    Object record = attr.get(i);
                    resultArea.append((i+1) + ". " + record.toString() + "\n");
                }
                resultArea.append("\n");
            } else {
                resultArea.append("No " + recordType + " records found for " + domain + "\n\n");
            }
        } catch (NamingException e) {
            // Skip errors for individual record types when querying "All"
            if (!"All".equals(recordType)) {
                resultArea.append("Error looking up " + recordType + " records: " + e.getMessage() + "\n\n");
            }
        }
    }
}