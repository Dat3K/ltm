package lab34;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A simple web server log analyzer that reads log files,
 * resolves IP addresses to hostnames, and displays results.
 * This is an all-in-one implementation with minimal external dependencies.
 */
public class WebLogAnalyzer extends JPanel {
    // UI Components
    private JTextField filePathField;
    private JButton browseButton, processButton, generateSampleButton;
    private JTable logTable;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private LogTableModel tableModel;
    
    // Cache for hostname lookups
    private Map<String, String> hostnameCache = new ConcurrentHashMap<>();
    
    // Data model for log entries
    private static class LogEntry {
        String ipAddress;
        String hostname;
        String timestamp;
        String request;
        int statusCode;
        long bytesSent;
        
        public LogEntry(String ipAddress, String timestamp, String request, 
                        int statusCode, long bytesSent) {
            this.ipAddress = ipAddress;
            this.timestamp = timestamp;
            this.request = request;
            this.statusCode = statusCode;
            this.bytesSent = bytesSent;
        }
    }
    
    // Table model for displaying log entries
    private static class LogTableModel extends AbstractTableModel {
        private final String[] columnNames = {
            "IP Address", "Hostname", "Timestamp", "Request", "Status", "Bytes"
        };
        private List<LogEntry> entries = new ArrayList<>();
        
        public void setEntries(List<LogEntry> entries) {
            this.entries = entries;
            fireTableDataChanged();
        }
        
        public void clearEntries() {
            this.entries.clear();
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() { return entries.size(); }
        
        @Override
        public int getColumnCount() { return columnNames.length; }
        
        @Override
        public String getColumnName(int column) { return columnNames[column]; }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            LogEntry entry = entries.get(rowIndex);
            switch (columnIndex) {
                case 0: return entry.ipAddress;
                case 1: return entry.hostname != null ? entry.hostname : "unresolved";
                case 2: return entry.timestamp;
                case 3: return entry.request;
                case 4: return entry.statusCode;
                case 5: return entry.bytesSent;
                default: return null;
            }
        }
    }
    
    public WebLogAnalyzer() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        JLabel descLabel = new JLabel("<html><b>Web Server Log Analyzer:</b> Load web server logs, " +
                "resolve IP addresses to hostnames, and view log entries.</html>");
        descLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        descPanel.add(descLabel, BorderLayout.CENTER);
        add(descPanel, BorderLayout.NORTH);
        
        // Create file selection panel
        JPanel filePanel = new JPanel(new BorderLayout(5, 0));
        filePanel.setBorder(BorderFactory.createTitledBorder("Log File"));
        
        filePathField = new JTextField();
        filePathField.setEditable(false);
        browseButton = new JButton("Browse...");
        processButton = new JButton("Process Log");
        processButton.setEnabled(false);
        generateSampleButton = new JButton("Generate Sample");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(generateSampleButton);
        buttonPanel.add(browseButton);
        buttonPanel.add(processButton);
        
        filePanel.add(new JLabel("File:"), BorderLayout.WEST);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create table for displaying results
        tableModel = new LogTableModel();
        logTable = new JTable(tableModel);
        logTable.setAutoCreateRowSorter(true);
        logTable.setFillsViewportHeight(true);
        
        // Adjust column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(100); // IP
        logTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Hostname
        logTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Timestamp
        logTable.getColumnModel().getColumn(3).setPreferredWidth(300); // Request
        logTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Status
        logTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Bytes
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        
        // Create status panel
        JPanel statusPanel = new JPanel(new BorderLayout(5, 0));
        statusLabel = new JLabel("Ready");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);
        
        // Add main components to the panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(filePanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        browseButton.addActionListener(e -> browseForLogFile());
        processButton.addActionListener(e -> processLogFile());
        generateSampleButton.addActionListener(e -> generateSampleLog());
    }
    
    private void browseForLogFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Web Server Log File");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePathField.setText(selectedFile.getAbsolutePath());
            processButton.setEnabled(true);
        }
    }
    
    private void processLogFile() {
        String filePath = filePathField.getText();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a log file first.");
            return;
        }
        
        statusLabel.setText("Processing log file...");
        progressBar.setValue(0);
        processButton.setEnabled(false);
        tableModel.clearEntries();
        
        // Use SwingWorker to process file in background
        new SwingWorker<List<LogEntry>, Integer>() {
            @Override
            protected List<LogEntry> doInBackground() throws Exception {
                File file = new File(filePath);
                long fileSize = file.length();
                long bytesRead = 0;
                List<LogEntry> entries = new ArrayList<>();
                
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bytesRead += line.length() + 1; // +1 for newline
                        LogEntry entry = parseLine(line);
                        if (entry != null) {
                            // Resolve hostname
                            entry.hostname = resolveHostname(entry.ipAddress);
                            entries.add(entry);
                        }
                        
                        // Update progress
                        int progress = (int)((bytesRead * 100) / fileSize);
                        publish(progress);
                    }
                }
                
                return entries;
            }
            
            @Override
            protected void process(List<Integer> chunks) {
                // Update progress bar with the latest value
                if (!chunks.isEmpty()) {
                    int latestProgress = chunks.get(chunks.size() - 1);
                    progressBar.setValue(latestProgress);
                    statusLabel.setText("Processing log file... " + latestProgress + "%");
                }
            }
            
            @Override
            protected void done() {
                try {
                    List<LogEntry> entries = get();
                    tableModel.setEntries(entries);
                    statusLabel.setText("Processed " + entries.size() + " log entries.");
                    progressBar.setValue(100);
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(WebLogAnalyzer.this,
                        "Error processing log file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    processButton.setEnabled(true);
                }
            }
        }.execute();
    }
    
    private LogEntry parseLine(String line) {
        // Common Log Format pattern:
        // 127.0.0.1 - frank [10/Oct/2000:13:55:36 -0700] "GET /apache_pb.gif HTTP/1.0" 200 2326
        Pattern pattern = Pattern.compile(
            "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+|-)$"
        );
        
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String ip = matcher.group(1);
            String timestamp = matcher.group(2);
            String request = matcher.group(3);
            int status = Integer.parseInt(matcher.group(4));
            
            // Handle "-" in bytes field
            long bytes = 0;
            String bytesStr = matcher.group(5);
            if (!"-".equals(bytesStr)) {
                bytes = Long.parseLong(bytesStr);
            }
            
            return new LogEntry(ip, timestamp, request, status, bytes);
        }
        
        return null;
    }
    
    private String resolveHostname(String ipAddress) {
        // Check cache first
        if (hostnameCache.containsKey(ipAddress)) {
            return hostnameCache.get(ipAddress);
        }
        
        try {
            InetAddress addr = InetAddress.getByName(ipAddress);
            String hostname = addr.getHostName();
            
            // If the hostname is the same as the IP, DNS couldn't resolve it
            if (!hostname.equals(ipAddress)) {
                hostnameCache.put(ipAddress, hostname);
                return hostname;
            } else {
                hostnameCache.put(ipAddress, "unresolved");
                return "unresolved";
            }
        } catch (UnknownHostException e) {
            hostnameCache.put(ipAddress, "unresolved");
            return "unresolved";
        }
    }
    
    private void generateSampleLog() {
        // Create a simple dialog to configure the sample log generation
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Generate Sample Log", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Entry count selection
        JPanel countPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel countLabel = new JLabel("Number of Entries:");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(10, 10, 1000, 10);
        JSpinner countSpinner = new JSpinner(spinnerModel);
        
        countPanel.add(countLabel);
        countPanel.add(countSpinner);
        
        // Output file panel
        JPanel outputPanel = new JPanel(new BorderLayout(5, 0));
        JLabel outputLabel = new JLabel("Output File:");
        // Use the current directory as default
        String defaultPath = System.getProperty("user.dir") + File.separator + "sample_log.log";
        JTextField outputField = new JTextField(defaultPath);
        JButton outputBrowseButton = new JButton("...");
        outputPanel.add(outputLabel, BorderLayout.WEST);
        outputPanel.add(outputField, BorderLayout.CENTER);
        outputPanel.add(outputBrowseButton, BorderLayout.EAST);
        
        outputBrowseButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Sample Log File");
            fileChooser.setSelectedFile(new File(outputField.getText()));
            
            int result = fileChooser.showSaveDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                outputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton generateButton = new JButton("Generate");
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(generateButton);
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        generateButton.addActionListener(e -> {
            try {
                String outputPath = outputField.getText();
                File outputFile = new File(outputPath);
                
                // Ensure parent directory exists
                File parentDir = outputFile.getParentFile();
                if (parentDir != null && !parentDir.exists()) {
                    boolean created = parentDir.mkdirs();
                    if (!created) {
                        throw new IOException("Failed to create directory: " + parentDir.getAbsolutePath());
                    }
                }
                
                int count = (Integer)countSpinner.getValue();
                
                statusLabel.setText("Generating sample log file...");
                generateSampleLogFile(outputFile, count);
                
                JOptionPane.showMessageDialog(dialog,
                    "Sample log file generated successfully:\n" + outputFile.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                filePathField.setText(outputFile.getAbsolutePath());
                processButton.setEnabled(true);
                statusLabel.setText("Sample log file generated: " + outputFile.getName());
                dialog.dispose();
                
            } catch (Exception ex) {
                String errorMessage = "Error generating sample log: " + ex.getMessage();
                JOptionPane.showMessageDialog(dialog,
                    errorMessage,
                    "Error", JOptionPane.ERROR_MESSAGE);
                statusLabel.setText(errorMessage);
                ex.printStackTrace(); // Print the stack trace for debugging
            }
        });
        
        mainPanel.add(countPanel);
        mainPanel.add(outputPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void generateSampleLogFile(File outputFile, int numEntries) throws IOException {
        // Sample data for log generation
        String[] ipAddresses = {
            "192.168.1.1", "10.0.0.1", "172.16.0.1", "127.0.0.1",
            "8.8.8.8", "1.1.1.1", "74.125.24.100", "157.240.22.35"
        };
        
        String[] methods = {"GET", "POST", "PUT", "DELETE", "HEAD"};
        
        String[] resources = {
            "/index.html", "/about.html", "/contact.html", "/products/list",
            "/api/users", "/api/data", "/images/logo.png", "/css/style.css"
        };
        
        String[] protocols = {"HTTP/1.0", "HTTP/1.1", "HTTP/2.0"};
        
        int[] statusCodes = {200, 301, 302, 404, 500};
        
        Random random = new Random();
        
        // Use a hard-coded timezone offset to avoid locale issues
        String[] timeZones = {"-0800", "+0000", "+0100", "+0200"};
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (int i = 0; i < numEntries; i++) {
                String ip = ipAddresses[random.nextInt(ipAddresses.length)];
                String identd = "-";
                String userId = random.nextBoolean() ? "user" : "-";
                
                // Generate timestamp manually to avoid locale/formatting issues
                int day = 1 + random.nextInt(28);
                String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
                String month = months[random.nextInt(months.length)];
                int year = 2020 + random.nextInt(4);
                int hour = random.nextInt(24);
                int minute = random.nextInt(60);
                int second = random.nextInt(60);
                String timezone = timeZones[random.nextInt(timeZones.length)];
                
                String dateStr = String.format("%02d/%s/%d:%02d:%02d:%02d %s", 
                                              day, month, year, hour, minute, second, timezone);
                
                String method = methods[random.nextInt(methods.length)];
                String resource = resources[random.nextInt(resources.length)];
                String protocol = protocols[random.nextInt(protocols.length)];
                
                int statusCode = statusCodes[random.nextInt(statusCodes.length)];
                int bytesSent = random.nextInt(10000);
                
                String logLine = String.format("%s %s %s [%s] \"%s %s %s\" %d %d",
                        ip, identd, userId, dateStr, method, resource, protocol, statusCode, bytesSent);
                
                writer.write(logLine);
                writer.newLine();
                
                // Update progress periodically
                if (i % 10 == 0) {
                    final int progress = (i * 100) / numEntries;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(progress);
                        statusLabel.setText("Generating sample log file... " + progress + "%");
                    });
                }
            }
            
            // Set progress to 100% when done
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(100);
            });
        }
    }
    
    // Static method to create and show the simple log analyzer
    public static void createAndShow() {
        JFrame frame = new JFrame("Web Server Log Analyzer");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new WebLogAnalyzer());
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(WebLogAnalyzer::createAndShow);
    }
}
