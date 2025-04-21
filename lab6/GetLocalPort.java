package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

/**
 * Panel for getting local port information
 */
public class GetLocalPort extends BasePanel {
    private JTextField hostField;
    private JTextField portField;

    public GetLocalPort() {
        super();

        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JLabel portLabel = new JLabel("Remote Port:");
        portField = new JTextField(5);
        portField.setText("80");
        JButton getButton = new JButton("Get Local Port");

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

                    resultArea.setText("Connecting to " + host + ":" + port + " to get local port...\n\n");

                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(host, port), 3000); // 3 second timeout

                        int localPort = socket.getLocalPort();

                        resultArea.append("Successfully connected to " + host + ":" + port + "\n\n");
                        resultArea.append("Local Port: " + localPort + "\n");
                        resultArea.append("Local Address: " + socket.getLocalAddress() + "\n");

                        // Close the socket
                        socket.close();

                        resultArea.append("\nNote: Local port is assigned by OS from ephemeral port range (typically 49152-65535)\n");

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
        });
    }
}
