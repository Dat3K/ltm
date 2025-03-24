package lab34;
import javax.swing.*;
import java.awt.*;

public class Lab34 extends JFrame {
    
    public Lab34() {
        setTitle("52100781_NguyenThanhDat - Lab 3");
        setSize(1000, 700);  // Increased size for better display of the log analyzer
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create a tabbed pane to organize exercises
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add Exercise 3.1 tab (Hostname to IP Address) with renamed class
        HostnameToIPPanel hostnameToIPPanel = new HostnameToIPPanel();
        tabbedPane.addTab("Hostname to IP Lookup", null, hostnameToIPPanel, "Convert hostname to IP address");
        
        // Add Exercise 3.2 tab with renamed class
        DNSLookupPanel dnsLookupPanel = new DNSLookupPanel();
        tabbedPane.addTab("DNS Records Lookup", null, dnsLookupPanel, "Query DNS records for domains");
        
        // Add Exercise 3.3 tab - Simple Web Log Analyzer
        WebLogAnalyzer webLogAnalyzer = new WebLogAnalyzer();
        tabbedPane.addTab("Web Server Log Analyzer", null, webLogAnalyzer, "Process and analyze web server logs");
        
        // Add the tabbed pane to the frame
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add a title at the top
        JLabel titleLabel = new JLabel("52100781_NguyenThanhDat - Lab 3 Exercises", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);
    }
}
