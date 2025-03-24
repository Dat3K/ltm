package lab34;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;

public class HostnameToIPPanel extends JPanel {
    private JTextField hostnameField;
    private JTextArea resultArea;
    private JButton lookupButton;

    public HostnameToIPPanel() {
        setLayout(new BorderLayout());
        
        // Create content panel with simple BorderLayout
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Input panel with hostname field and lookup button
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel hostnameLabel = new JLabel("Enter Hostname: ");
        hostnameField = new JTextField(20);
        lookupButton = new JButton("Lookup IP Address");
        
        inputPanel.add(hostnameLabel, BorderLayout.WEST);
        inputPanel.add(hostnameField, BorderLayout.CENTER);
        inputPanel.add(lookupButton, BorderLayout.EAST);
        
        // Quick test buttons panel
        JPanel quickTestPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        quickTestPanel.setBorder(BorderFactory.createTitledBorder("Quick Test"));
        
        String[] commonHostnames = {"google.com", "facebook.com", "github.com", "localhost", "amazon.com"};
        
        for (final String hostname : commonHostnames) {
            JButton testButton = new JButton(hostname);
            testButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    hostnameField.setText(hostname);
                    lookupHostname();
                }
            });
            quickTestPanel.add(testButton);
        }
        
        // Create a wrapper panel to hold both input panel and quick test panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(inputPanel, BorderLayout.NORTH);
        topPanel.add(quickTestPanel, BorderLayout.CENTER);
        
        // Result area - MODIFIED THIS SECTION
        resultArea = new JTextArea(10, 40); // Set explicit rows and columns
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(500, 200)); // Set preferred size
        
        // Add description label
        JLabel descriptionLabel = new JLabel("<html><b>Exercise 3.1:</b> Enter a hostname to find its IP address and IP class.</html>");
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Add panels to content panel - MODIFIED THIS SECTION
        JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.add(descriptionLabel, BorderLayout.NORTH);
        mainContentPanel.add(topPanel, BorderLayout.CENTER);
        
        contentPanel.add(mainContentPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER); // Changed from SOUTH to CENTER to give it more space
        
        // Add event listener to button
        lookupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lookupHostname();
            }
        });
        
        // Add event listener for Enter key in hostname field
        hostnameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    lookupHostname();
                }
            }
        });
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void lookupHostname() {
        String hostname = hostnameField.getText().trim();
        if (hostname.isEmpty()) {
            resultArea.setText("Please enter a hostname");
            return;
        }
        
        try {
            resultArea.setText("Looking up hostname: " + hostname + "...\n"); // Add immediate feedback
            resultArea.update(resultArea.getGraphics()); // Force update of display
            
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            resultArea.setText("");
            resultArea.append("Results for hostname: " + hostname + "\n\n");
            
            for (int i = 0; i < addresses.length; i++) {
                String ipAddress = addresses[i].getHostAddress();
                String ipClass = getIpAddressClass(ipAddress);  // Changed from IPUtils.getIpAddressClass
                resultArea.append((i + 1) + ". " + ipAddress + " - Class " + ipClass + "\n");
            }
        } catch (UnknownHostException ex) {
            resultArea.setText("Could not find IP address for hostname: " + hostname + "\n");
            resultArea.append("Error: " + ex.getMessage());
        } catch (Exception ex) {
            resultArea.setText("An error occurred: " + ex.getMessage());
            ex.printStackTrace(); // Print stack trace for debugging
        }
        
        // Force the UI to update
        resultArea.revalidate();
        resultArea.repaint();
    }
    
    /**
     * Determines the class of an IPv4 address
     * @param ipAddress the IP address as a string
     * @return the IP address class (A, B, C, D, E, or "Unknown" for special cases)
     */
    private String getIpAddressClass(String ipAddress) {
        // For IPv6 addresses, return special classification
        if (ipAddress.contains(":")) {
            return "IPv6";
        }
        
        try {
            // Parse the first octet of the IP address
            String[] octets = ipAddress.split("\\.");
            int firstOctet = Integer.parseInt(octets[0]);
            
            // Classify based on first octet
            if (firstOctet >= 1 && firstOctet <= 126) {
                if (firstOctet == 10) {
                    return "A (Private)";
                }
                return "A";
            } else if (firstOctet == 127) {
                return "Loopback";
            } else if (firstOctet >= 128 && firstOctet <= 191) {
                if (firstOctet == 172 && (Integer.parseInt(octets[1]) >= 16 && Integer.parseInt(octets[1]) <= 31)) {
                    return "B (Private)";
                }
                return "B";
            } else if (firstOctet >= 192 && firstOctet <= 223) {
                if (firstOctet == 192 && Integer.parseInt(octets[1]) == 168) {
                    return "C (Private)";
                }
                return "C";
            } else if (firstOctet >= 224 && firstOctet <= 239) {
                return "D (Multicast)";
            } else if (firstOctet >= 240 && firstOctet <= 255) {
                return "E (Reserved)";
            } else if (firstOctet == 169 && Integer.parseInt(octets[1]) == 254) {
                return "Link-Local";
            }
        } catch (Exception e) {
            // Handle any parsing errors
            return "Unknown format";
        }
        
        return "Unknown";
    }
}