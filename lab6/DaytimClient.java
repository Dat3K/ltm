package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.InetSocketAddress;
import javax.swing.SwingWorker;

/**
 * Panel for daytime client
 */
public class DaytimClient extends BasePanel {
    private JTextField hostField;
    private JTextField portField;

    public DaytimClient() {
        super();

        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Daytime Server:");
        hostField = new JTextField(20);
        hostField.setText("time.nist.gov");
        JLabel portLabel = new JLabel("Port:");
        portField = new JTextField(5);
        portField.setText("13");
        JButton getButton = new JButton("Get Daytime");

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
                    showMessage("Please enter a valid daytime server hostname or IP address");
                    return;
                }

                try {
                    int port = Integer.parseInt(portText);

                    if (port < 0 || port > 65535) {
                        showMessage("Port number must be between 0 and 65535");
                        return;
                    }

                    resultArea.setText("Connecting to daytime server " + host + ":" + port + "...\n");
                    getButton.setEnabled(false);

                    // Use SwingWorker to perform the network operation in the background
                    new SwingWorker<String, Void>() {
                        @Override
                        protected String doInBackground() throws Exception {
                            StringBuilder response = new StringBuilder();
                            try {
                                // Connect to the daytime server
                                Socket socket = new Socket();
                                socket.connect(new InetSocketAddress(host, port), 5000); // 5 second timeout

                                // Read the response
                                BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));

                                String line;
                                while ((line = reader.readLine()) != null) {
                                    response.append(line).append("\n");
                                }

                                // Close the socket
                                reader.close();
                                socket.close();

                            } catch (IOException ex) {
                                throw new Exception("Error connecting to daytime server: " + ex.getMessage());
                            }
                            return response.toString();
                        }

                        @Override
                        protected void done() {
                            try {
                                String result = get();
                                if (result.isEmpty()) {
                                    resultArea.append("No response received from the server.\n");
                                } else {
                                    resultArea.append("\nResponse from daytime server:\n");
                                    resultArea.append(result);
                                    resultArea.append("\nDaytime protocol (RFC 867): Server sends current date/time and closes connection\n");
                                }
                            } catch (Exception ex) {
                                resultArea.append("\nError: " + ex.getMessage() + "\n");
                            } finally {
                                getButton.setEnabled(true);
                            }
                        }
                    }.execute();

                } catch (NumberFormatException ex) {
                    showMessage("Please enter a valid port number");
                }
            }
        });
    }
}
