package lab6;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Lab6 extends JFrame {
    private JPanel mainPanel;
    private JPanel welcomePanel;
    private JPanel containerPanel;
    private JLabel nameLabel;
    private CardLayout cardLayout;

    // Panels for each functionality
    private LowPortScanner lowPortScannerPanel;
    private HighPortScanner highPortScannerPanel;
    private GetInetAddress getInetAddressPanel;
    private GetPort getPortPanel;
    private GetLocalPort getLocalPortPanel;
    private GetLocalAddress getLocalAddressPanel;
    private DaytimClient daytimClientPanel;
    private WhoisClient whoisClientPanel;

    public Lab6() {
        setTitle("Lab 6");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        // Create a label with the student information
        nameLabel = new JLabel("52100781 - Nguyễn Thành Đạt");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(nameLabel, BorderLayout.NORTH);

        // Create a welcome panel (shown by default)
        welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("52100781 - Nguyễn Thành Đạt");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Initialize panels for each functionality
        lowPortScannerPanel = new LowPortScanner();
        highPortScannerPanel = new HighPortScanner();
        getInetAddressPanel = new GetInetAddress();
        getPortPanel = new GetPort();
        getLocalPortPanel = new GetLocalPort();
        getLocalAddressPanel = new GetLocalAddress();
        daytimClientPanel = new DaytimClient();
        whoisClientPanel = new WhoisClient();

        // Create a card layout panel to switch between views
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(welcomePanel, "welcome");

        // Add The Constructors panels
        containerPanel.add(lowPortScannerPanel, "lowPortScanner");
        containerPanel.add(highPortScannerPanel, "highPortScanner");

        // Add Information about socket panels
        containerPanel.add(getInetAddressPanel, "getInetAddress");
        containerPanel.add(getPortPanel, "getPort");
        containerPanel.add(getLocalPortPanel, "getLocalPort");
        containerPanel.add(getLocalAddressPanel, "getLocalAddress");
        containerPanel.add(daytimClientPanel, "daytimClient");

        // Add whois client panel
        containerPanel.add(whoisClientPanel, "whoisClient");

        // Show welcome panel by default
        cardLayout.show(containerPanel, "welcome");

        // Add container panel to main panel
        mainPanel.add(containerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        getContentPane().add(mainPanel);

        // Create and set up the menu bar
        JMenuBar menuBar = new JMenuBar();

        // The Constructors menu
        JMenu constructorsMenu = new JMenu("The Constructors");
        JMenuItem lowPortScannerMenuItem = new JMenuItem("LowPortScanner");
        JMenuItem highPortScannerMenuItem = new JMenuItem("HighPortScanner");

        constructorsMenu.add(lowPortScannerMenuItem);
        constructorsMenu.add(highPortScannerMenuItem);
        menuBar.add(constructorsMenu);

        // Information about socket menu
        JMenu socketInfoMenu = new JMenu("Information about socket");
        JMenuItem getInetAddressMenuItem = new JMenuItem("GetInetAddress");
        JMenuItem getPortMenuItem = new JMenuItem("GetPort");
        JMenuItem getLocalPortMenuItem = new JMenuItem("GetLocalPort");
        JMenuItem getLocalAddressMenuItem = new JMenuItem("GetLocalAddress");
        JMenuItem daytimClientMenuItem = new JMenuItem("DaytimClient");

        socketInfoMenu.add(getInetAddressMenuItem);
        socketInfoMenu.add(getPortMenuItem);
        socketInfoMenu.add(getLocalPortMenuItem);
        socketInfoMenu.add(getLocalAddressMenuItem);
        socketInfoMenu.add(daytimClientMenuItem);
        menuBar.add(socketInfoMenu);

        // Whois client menu
        JMenu whoisMenu = new JMenu("Whois Client");
        JMenuItem whoisClientMenuItem = new JMenuItem("Command-line whois client");

        whoisMenu.add(whoisClientMenuItem);
        menuBar.add(whoisMenu);

        // Add action listeners for menu items

        // Add action listeners for The Constructors menu items
        lowPortScannerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "lowPortScanner");
            }
        });

        highPortScannerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "highPortScanner");
            }
        });

        // Add action listeners for Information about socket menu items
        getInetAddressMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "getInetAddress");
            }
        });

        getPortMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "getPort");
            }
        });

        getLocalPortMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "getLocalPort");
            }
        });

        getLocalAddressMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "getLocalAddress");
            }
        });

        daytimClientMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "daytimClient");
            }
        });

        // Add action listener for Whois client menu item
        whoisClientMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "whoisClient");
            }
        });

        setJMenuBar(menuBar);
    }

    /**
     * Shows a message dialog with the given message
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Lab6 app = new Lab6();
            app.setVisible(true);
        });
    }
}
