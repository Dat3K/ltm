package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;

/**
 * Panel for scanning high ports
 */
public class HighPortScanner extends BasePanel {
    private JTextField hostField;
    private JTextField startPortField;
    private JTextField endPortField;

    public HighPortScanner() {
        super();

        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel hostLabel = new JLabel("Host:");
        hostField = new JTextField(20);
        hostField.setText("localhost");
        JLabel startPortLabel = new JLabel("Start Port:");
        startPortField = new JTextField(5);
        startPortField.setText("1025");
        JLabel endPortLabel = new JLabel("End Port:");
        endPortField = new JTextField(5);
        endPortField.setText("65535");
        JButton scanButton = new JButton("Scan High Ports");

        inputPanel.add(hostLabel);
        inputPanel.add(hostField);
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
                try {
                    String host = hostField.getText().trim();
                    int startPort = Integer.parseInt(startPortField.getText());
                    int endPort = Integer.parseInt(endPortField.getText());

                    if (host.isEmpty()) {
                        showMessage("Please enter a valid hostname or IP address");
                        return;
                    }

                    if (startPort < 0 || startPort > 65535 || endPort < 0 || endPort > 65535) {
                        showMessage("Port numbers must be between 0 and 65535");
                        return;
                    }

                    if (startPort > endPort) {
                        showMessage("Start port must be less than or equal to end port");
                        return;
                    }

                    // Limit the range to avoid scanning too many ports
                    if (endPort - startPort > 1000) {
                        int response = JOptionPane.showConfirmDialog(
                            HighPortScanner.this,
                            "You are about to scan " + (endPort - startPort + 1) + " ports.\n" +
                            "This may take a long time. Continue?",
                            "Confirm Scan",
                            JOptionPane.YES_NO_OPTION
                        );

                        if (response != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }

                    // Disable the scan button while scanning
                    scanButton.setEnabled(false);
                    resultArea.setText("Scanning ports " + startPort + " to " + endPort + " on " + host + "...\n");

                    // Use SwingWorker to perform the scan in the background
                    new SwingWorker<List<Integer>, Integer>() {
                        @Override
                        protected List<Integer> doInBackground() throws Exception {
                            List<Integer> openPorts = new ArrayList<>();
                            for (int port = startPort; port <= endPort; port++) {
                                try {
                                    // Try to connect to the port
                                    Socket socket = new Socket();
                                    socket.connect(new InetSocketAddress(host, port), 200); // 200ms timeout
                                    socket.close();
                                    openPorts.add(port);
                                    publish(port); // Publish the open port to process method
                                } catch (IOException ex) {
                                    // Port is closed or filtered
                                }
                            }
                            return openPorts;
                        }

                        @Override
                        protected void process(List<Integer> ports) {
                            for (int port : ports) {
                                resultArea.append(port + ", ");
                            }
                        }

                        @Override
                        protected void done() {
                            try {
                                List<Integer> openPorts = get();
                                if (resultArea.getText().endsWith(", ")) {
                                    // Remove the trailing comma and space
                                    String text = resultArea.getText();
                                    resultArea.setText(text.substring(0, text.length() - 2));
                                }
                                resultArea.append("\n\nScan complete. Found " + openPorts.size() + " open port(s).");
                                if (openPorts.isEmpty()) {
                                    resultArea.append("\nNo open ports found in the specified range.");
                                }
                            } catch (Exception ex) {
                                resultArea.append("\nError during scan: " + ex.getMessage() + "\n");
                            } finally {
                                scanButton.setEnabled(true);
                            }
                        }
                    }.execute();
                } catch (NumberFormatException ex) {
                    showMessage("Please enter valid port numbers");
                }
            }
        });
    }
}
