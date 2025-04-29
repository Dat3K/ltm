package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Panel for getting local address information
 */
public class GetLocalAddress extends BasePanel {
    private JTextField hostField;
    private JTextField portField;

    public GetLocalAddress() {
        super();

        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JLabel portLabel = new JLabel("Remote Port:");
        portField = new JTextField(5);
        portField.setText("80");
        JButton getButton = new JButton("Get Local Address");

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
                String host = hostField.getText().trim();
                String portText = portField.getText().trim();

                if (host.isEmpty()) {
                    showMessage("Please enter a valid hostname or IP address");
                    return;
                }

                try {
                    int port = Integer.parseInt(portText);

                    if (port < 0 || port > 65535) {
                        showMessage("Port number must be between 0 and 65535");
                        return;
                    }

                    resultArea.setText("Connecting to " + host + ":" + port + " to get local address...\n\n");

                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(host, port), 3000); // 3 second timeout

                        InetAddress localAddress = socket.getLocalAddress();

                        resultArea.append("Successfully connected to " + host + ":" + port + "\n\n");
                        resultArea.append("Local Address: " + localAddress.getHostAddress() + "\n");
                        resultArea.append("Local Port: " + socket.getLocalPort() + "\n");

                        // Close the socket
                        socket.close();

                        // Show active network interfaces
                        resultArea.append("\nActive Network Interfaces:\n");
                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                        while (interfaces.hasMoreElements()) {
                            NetworkInterface ni = interfaces.nextElement();
                            if (ni.isUp() && !ni.isLoopback()) {
                                resultArea.append("- " + ni.getName() + ": ");

                                // Get MAC address
                                String mac = getMacAddress(ni);
                                if (!mac.equals("Not Available")) {
                                    resultArea.append("MAC=" + mac + ", ");
                                }

                                // Get IP addresses
                                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                                boolean hasAddresses = false;
                                while (addresses.hasMoreElements()) {
                                    InetAddress addr = addresses.nextElement();
                                    // Skip IPv6 addresses for simplicity
                                    if (addr.getHostAddress().contains(":")) continue;

                                    resultArea.append("IP=" + addr.getHostAddress());
                                    if (addr.equals(localAddress)) {
                                        resultArea.append(" (used for connection)");
                                    }
                                    hasAddresses = true;
                                }

                                if (!hasAddresses) {
                                    resultArea.append("No IPv4 addresses");
                                }

                                resultArea.append("\n");
                            }
                        }

                    } catch (IOException ex) {
                        resultArea.setText("Could not connect to " + host + ":" + port + "\n");
                        resultArea.append("The port is either closed, filtered, or the host is unreachable.\n");
                        resultArea.append("Error: " + ex.getMessage() + "\n");
                    }

                } catch (NumberFormatException ex) {
                    showMessage("Please enter a valid port number");
                } catch (Exception ex) {
                    resultArea.setText("Error: " + ex.getMessage() + "\n");
                }
            }

            // Helper method to get MAC address as a string
            private String getMacAddress(NetworkInterface ni) {
                try {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac == null) {
                        return "Not Available";
                    }

                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "Error: " + e.getMessage();
                }
            }
        });
    }
}
