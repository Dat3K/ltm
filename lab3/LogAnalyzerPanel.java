package lab3;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import java.text.SimpleDateFormat;

public class LogAnalyzerPanel extends JPanel {
    private JTextArea logContentArea;
    private JTable statsTable;
    private JTextField searchField;
    private JComboBox<String> logFormatCombo;
    private JButton openFileButton, analyzeButton;
    private JLabel statusLabel, fileNameLabel;
    private JPanel statsPanel;
    private File selectedFile;
    
    // Data model
    private DefaultTableModel tableModel;
    private Map<String, Integer> ipHits = new HashMap<>();
    private Map<String, Integer> pageHits = new HashMap<>();
    private Map<String, Integer> statusCodes = new HashMap<>();
    private Map<String, Integer> userAgents = new HashMap<>();
    
    // Regular expressions for different log formats
    private static final String APACHE_COMMON_PATTERN = 
            "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+|-)";
    private static final String APACHE_COMBINED_PATTERN = 
            "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\"";
    private static final String NGINX_PATTERN = 
            "^(\\S+) \\S+ \\S+ \\[([^\\]]+)\\] \"([^\"]+)\" (\\d+) (\\d+) \"([^\"]*)\" \"([^\"]*)\"";

    public LogAnalyzerPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title and description
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("<html><h2>Web Server Log Analyzer</h2></html>");
        JLabel descriptionLabel = new JLabel("<html><p>Load and analyze web server log files (Apache, Nginx, etc). " +
                                          "View statistics on requests, IP addresses, response codes and more.</p></html>");
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(descriptionLabel, BorderLayout.CENTER);
        
        // File selection panel
        JPanel filePanel = new JPanel(new BorderLayout(10, 10));
        fileNameLabel = new JLabel("No file selected");
        openFileButton = new JButton("Open Log File");
        
        filePanel.add(fileNameLabel, BorderLayout.CENTER);
        filePanel.add(openFileButton, BorderLayout.EAST);
        
        // Log format selection
        JPanel formatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        JLabel formatLabel = new JLabel("Log Format:");
        logFormatCombo = new JComboBox<>(new String[] {"Apache Common", "Apache Combined", "Nginx"});
        
        formatPanel.add(formatLabel);
        formatPanel.add(logFormatCombo);
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchField = new JTextField();
        analyzeButton = new JButton("Analyze");
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(analyzeButton, BorderLayout.EAST);
        
        // Log content area
        logContentArea = new JTextArea(15, 50);
        logContentArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logContentArea);
        
        // Status label
        statusLabel = new JLabel("Status: Ready");
        
        // Stats table
        tableModel = new DefaultTableModel(new Object[] {"Metric", "Value"}, 0);
        statsTable = new JTable(tableModel);
        JScrollPane statsScrollPane = new JScrollPane(statsTable);
        
        // Stats panel
        statsPanel = new JPanel(new BorderLayout(10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.add(statsScrollPane, BorderLayout.CENTER);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(filePanel, BorderLayout.WEST);
        add(formatPanel, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);
        add(logScrollPane, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
        add(statsPanel, BorderLayout.SOUTH);
        
        // Event listeners
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLogFile();
            }
        });
        
        analyzeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                analyzeLogFile();
            }
        });
    }
    
    private void openLogFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Log Files", "log", "txt"));
        
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileNameLabel.setText(selectedFile.getName());
            loadLogFile();
        }
    }
    
    private void loadLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            logContentArea.setText("");
            String line;
            while ((line = reader.readLine()) != null) {
                logContentArea.append(line + "\n");
            }
            statusLabel.setText("Status: File loaded successfully");
        } catch (IOException e) {
            statusLabel.setText("Status: Error loading file");
            e.printStackTrace();
        }
    }
    
    private void analyzeLogFile() {
        String logFormat = (String) logFormatCombo.getSelectedItem();
        String pattern = "";
        
        switch (logFormat) {
            case "Apache Common":
                pattern = APACHE_COMMON_PATTERN;
                break;
            case "Apache Combined":
                pattern = APACHE_COMBINED_PATTERN;
                break;
            case "Nginx":
                pattern = NGINX_PATTERN;
                break;
        }
        
        Pattern logPattern = Pattern.compile(pattern);
        Matcher matcher;
        
        ipHits.clear();
        pageHits.clear();
        statusCodes.clear();
        userAgents.clear();
        
        String[] lines = logContentArea.getText().split("\n");
        for (String line : lines) {
            matcher = logPattern.matcher(line);
            if (matcher.find()) {
                String ip = matcher.group(1);
                String request = matcher.group(3);
                String statusCode = matcher.group(4);
                String userAgent = matcher.groupCount() > 6 ? matcher.group(7) : "Unknown";
                
                ipHits.put(ip, ipHits.getOrDefault(ip, 0) + 1);
                pageHits.put(request, pageHits.getOrDefault(request, 0) + 1);
                statusCodes.put(statusCode, statusCodes.getOrDefault(statusCode, 0) + 1);
                userAgents.put(userAgent, userAgents.getOrDefault(userAgent, 0) + 1);
            }
        }
        
        updateStatsTable();
        statusLabel.setText("Status: Analysis complete");
    }
    
    private void updateStatsTable() {
        tableModel.setRowCount(0);
        
        tableModel.addRow(new Object[] {"Total Requests", logContentArea.getLineCount()});
        tableModel.addRow(new Object[] {"Unique IPs", ipHits.size()});
        tableModel.addRow(new Object[] {"Unique Pages", pageHits.size()});
        tableModel.addRow(new Object[] {"Unique Status Codes", statusCodes.size()});
        tableModel.addRow(new Object[] {"Unique User Agents", userAgents.size()});
        
        for (Map.Entry<String, Integer> entry : ipHits.entrySet()) {
            tableModel.addRow(new Object[] {"IP: " + entry.getKey(), entry.getValue()});
        }
        
        for (Map.Entry<String, Integer> entry : pageHits.entrySet()) {
            tableModel.addRow(new Object[] {"Page: " + entry.getKey(), entry.getValue()});
        }
        
        for (Map.Entry<String, Integer> entry : statusCodes.entrySet()) {
            tableModel.addRow(new Object[] {"Status Code: " + entry.getKey(), entry.getValue()});
        }
        
        for (Map.Entry<String, Integer> entry : userAgents.entrySet()) {
            tableModel.addRow(new Object[] {"User Agent: " + entry.getKey(), entry.getValue()});
        }
    }
}