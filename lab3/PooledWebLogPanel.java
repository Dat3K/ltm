package lab3;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of PooledWebLog with JavaSwing UI
 * This class processes web server logs in parallel using a thread pool
 */
public class PooledWebLogPanel extends JPanel {
    private static final int DEFAULT_NUM_THREADS = 4;
    
    // UI Components
    private JTextField filePathField;
    private JButton browseButton, processButton, generateSampleButton;
    private JSpinner threadSpinner;
    private JTable logTable;
    private JLabel statusLabel;
    private JProgressBar progressBar;
    private LogTableModel tableModel;
    
    // Data model for log entries
    private static class LogEntry {
        String original;
        String resolvedEntry;
        
        LogEntry(String original, String resolvedEntry) {
            this.original = original;
            this.resolvedEntry = resolvedEntry;
        }
    }
    
    // Table model for displaying log entries
    private static class LogTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Original Entry", "Resolved Entry"};
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
                case 0: return entry.original;
                case 1: return entry.resolvedEntry;
                default: return null;
            }
        }
    }
    
    // Callable task for resolving hostnames in log entries
    private static class LookupTask implements Callable<String> {
        private final String entry;
        private static final Pattern IP_PATTERN = Pattern.compile("^(\\S+)");
        
        public LookupTask(String entry) {
            this.entry = entry;
        }
        
        @Override
        public String call() throws Exception {
            Matcher matcher = IP_PATTERN.matcher(entry);
            if (matcher.find()) {
                String ip = matcher.group(1);
                try {
                    InetAddress addr = InetAddress.getByName(ip);
                    String hostname = addr.getHostName();
                    
                    // Replace the IP with hostname in the log entry if resolved successfully
                    if (!hostname.equals(ip)) {
                        return entry.replace(ip, hostname);
                    }
                } catch (UnknownHostException e) {
                    // In case of failure, just return the original entry
                }
            }
            return entry;
        }
    }
    
    public PooledWebLogPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create description panel
        JPanel descPanel = new JPanel(new BorderLayout());
        JLabel descLabel = new JLabel("<html><b>Pooled Web Log Processor:</b> Process web server logs " +
                "in parallel using a thread pool for improved performance.</html>");
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
        
        // Add thread count spinner
        JPanel threadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel threadLabel = new JLabel("Thread Pool Size:");
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(DEFAULT_NUM_THREADS, 1, 32, 1);
        threadSpinner = new JSpinner(spinnerModel);
        threadPanel.add(threadLabel);
        threadPanel.add(threadSpinner);
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(generateSampleButton);
        buttonsPanel.add(browseButton);
        buttonsPanel.add(processButton);
        
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        topControlsPanel.add(filePathField, BorderLayout.CENTER);
        topControlsPanel.add(buttonsPanel, BorderLayout.EAST);
        
        filePanel.add(new JLabel("File:"), BorderLayout.WEST);
        filePanel.add(topControlsPanel, BorderLayout.CENTER);
        
        // Create table for displaying results
        tableModel = new LogTableModel();
        logTable = new JTable(tableModel);
        logTable.setAutoCreateRowSorter(true);
        logTable.setFillsViewportHeight(true);
        
        // Adjust column widths
        logTable.getColumnModel().getColumn(0).setPreferredWidth(400); // Original Entry
        logTable.getColumnModel().getColumn(1).setPreferredWidth(400); // Resolved Entry
        
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
        topPanel.add(threadPanel, BorderLayout.SOUTH);
        
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
        
        // Get thread count from spinner
        int numThreads = (Integer) threadSpinner.getValue();
        
        // Use SwingWorker to process file in background
        new SwingWorker<List<LogEntry>, Integer>() {
            @Override
            protected List<LogEntry> doInBackground() throws Exception {
                List<LogEntry> resultEntries = new ArrayList<>();
                File file = new File(filePath);
                long bytesRead = 0;
                
                // Create thread pool with the specified number of threads
                ExecutorService executor = Executors.newFixedThreadPool(numThreads);
                
                try {
                    // First count total lines for progress calculation
                    int totalLines = 0;
                    try (BufferedReader lineCounter = new BufferedReader(new FileReader(file))) {
                        while (lineCounter.readLine() != null) {
                            totalLines++;
                        }
                    }
                    
                    // Read and process the file
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        // Queue to store the results in order
                        Queue<Future<String>> futures = new LinkedList<>();
                        Queue<String> originalEntries = new LinkedList<>();
                        
                        String line;
                        int linesProcessed = 0;
                        
                        // Submit tasks to the executor
                        while ((line = reader.readLine()) != null) {
                            bytesRead += line.length() + 1; // +1 for newline
                            
                            // Create and submit a lookup task
                            LookupTask task = new LookupTask(line);
                            Future<String> future = executor.submit(task);
                            
                            // Store the original line and its future result
                            originalEntries.add(line);
                            futures.add(future);
                            
                            // Process results as they become available to avoid memory buildup
                            processAvailableResults(resultEntries, futures, originalEntries);
                            
                            // Update progress periodically
                            linesProcessed++;
                            if (linesProcessed % 10 == 0) {
                                int progress = (int)((linesProcessed * 100.0) / totalLines);
                                publish(progress);
                            }
                        }
                        
                        // Process any remaining results
                        while (!futures.isEmpty()) {
                            Future<String> future = futures.poll();
                            String original = originalEntries.poll();
                            
                            try {
                                String resolved = future.get();
                                resultEntries.add(new LogEntry(original, resolved));
                            } catch (Exception e) {
                                // If there's an error, use the original entry
                                resultEntries.add(new LogEntry(original, original));
                            }
                        }
                    }
                } finally {
                    // Shutdown the executor
                    executor.shutdown();
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                }
                
                return resultEntries;
            }
            
            private void processAvailableResults(List<LogEntry> resultEntries, 
                                                Queue<Future<String>> futures,
                                                Queue<String> originalEntries) {
                // Process results that are already available
                while (!futures.isEmpty()) {
                    Future<String> future = futures.peek();
                    if (future.isDone()) {
                        futures.poll();
                        String original = originalEntries.poll();
                        
                        try {
                            String resolved = future.get();
                            resultEntries.add(new LogEntry(original, resolved));
                        } catch (Exception e) {
                            // If there's an error, use the original entry
                            resultEntries.add(new LogEntry(original, original));
                        }
                    } else {
                        break;
                    }
                }
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
                    statusLabel.setText("Processed " + entries.size() + " log entries using " + threadSpinner.getValue() + " threads.");
                    progressBar.setValue(100);
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    JOptionPane.showMessageDialog(PooledWebLogPanel.this,
                        "Error processing log file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    processButton.setEnabled(true);
                }
            }
        }.execute();
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
    
    // Static method to create and show the panel in its own frame
    public static void createAndShow() {
        JFrame frame = new JFrame("Pooled Web Log Processor");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new PooledWebLogPanel());
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // Test method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PooledWebLogPanel::createAndShow);
    }
} 