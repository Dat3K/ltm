package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
                String host = hostField.getText().trim();

                if (host.isEmpty()) {
                    showMessage("Please enter a valid hostname or IP address");
                    return;
                }

                resultArea.setText("Looking up InetAddress for " + host + "...\n\n");

                try {
                    InetAddress address = InetAddress.getByName(host);

                    resultArea.append("Host Name: " + address.getHostName() + "\n");
                    resultArea.append("Host Address: " + address.getHostAddress() + "\n");

                    // Get all addresses for the host
                    InetAddress[] allAddresses = InetAddress.getAllByName(host);
                    if (allAddresses.length > 1) {
                        resultArea.append("\nAll IP addresses:\n");
                        for (InetAddress addr : allAddresses) {
                            resultArea.append("- " + addr.getHostAddress() + "\n");
                        }
                    }

                    // Check if the host is reachable
                    try {
                        boolean reachable = address.isReachable(3000); // 3 second timeout
                        resultArea.append("\nHost is reachable: " + reachable);
                    } catch (Exception ex) {
                        resultArea.append("\nCould not check if host is reachable");
                    }

                } catch (UnknownHostException ex) {
                    resultArea.setText("Error: Unknown host " + host + "\n");
                    resultArea.append("Exception: " + ex.getMessage() + "\n");
                } catch (Exception ex) {
                    resultArea.setText("Error: " + ex.getMessage() + "\n");
                }
            }
        });
    }
}
