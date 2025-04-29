package lab6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.SwingWorker;

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
                String server = serverField.getText().trim();
                String domain = domainField.getText().trim();

                if (server.isEmpty()) {
                    showMessage("Please enter a valid whois server");
                    return;
                }

                if (domain.isEmpty()) {
                    showMessage("Please enter a domain name to lookup");
                    return;
                }

                resultArea.setText("Querying whois server " + server + " for domain " + domain + "...\n");
                lookupButton.setEnabled(false);

                // Use SwingWorker to perform the whois query in the background
                new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        StringBuilder response = new StringBuilder();
                        try {
                            // Standard whois port is 43
                            Socket socket = new Socket(server, 43);

                            // Send the query
                            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                            out.println(domain);

                            // Read the response
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String line;
                            while ((line = in.readLine()) != null) {
                                response.append(line).append("\n");
                            }

                            // Close everything
                            in.close();
                            out.close();
                            socket.close();

                        } catch (IOException ex) {
                            throw new Exception("Error querying whois server: " + ex.getMessage());
                        }
                        return response.toString();
                    }

                    @Override
                    protected void done() {
                        try {
                            String result = get();
                            if (result.isEmpty()) {
                                resultArea.append("No response received from the whois server.\n");
                            } else {
                                resultArea.append("\nWhois information for " + domain + ":\n");
                                // Limit the output to avoid overwhelming the user
                                if (result.length() > 2000) {
                                    resultArea.append(result.substring(0, 2000) + "...\n\n(Output truncated - full response is " +
                                                 result.length() + " characters)\n");
                                } else {
                                    resultArea.append(result);
                                }
                            }
                        } catch (Exception ex) {
                            resultArea.append("\nError: " + ex.getMessage() + "\n");
                        } finally {
                            lookupButton.setEnabled(true);
                        }
                    }
                }.execute();
            }
        });
    }
}
