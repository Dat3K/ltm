package lab5;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Lab5 extends JFrame {
    private JPanel mainPanel;
    private JPanel urlPanel;
    private JPanel containerPanel;
    private JTextField urlField;
    private JTextField connectionField;
    private JLabel nameLabel;
    private CardLayout cardLayout;
    private URL currentUrl;
    private URLConnection currentConnection;

    public Lab5() {
        setTitle("Lab 5");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());

        // Create a label with the student information
        nameLabel = new JLabel("52100781 - Nguyễn Thành Đạt");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(nameLabel, BorderLayout.NORTH);

        // Create URL input panel
        urlPanel = new JPanel(new BorderLayout());

        // Create input row with URL field and Check button
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Enter URL:");
        urlField = new JTextField(30);
        JButton checkButton = new JButton("Check");

        inputRow.add(urlLabel);
        inputRow.add(urlField);
        inputRow.add(checkButton);

        // Create connection display row with connection field
        JPanel connectionRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel connectionLabel = new JLabel("Connection String:");
        connectionField = new JTextField(40);
        connectionField.setEditable(false);

        connectionRow.add(connectionLabel);
        connectionRow.add(connectionField);

        // Add rows to URL panel
        JPanel rowsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        rowsPanel.add(inputRow);
        rowsPanel.add(connectionRow);
        urlPanel.add(rowsPanel, BorderLayout.CENTER);

        // Create a panel for the URL connection interface
        JPanel urlConnectionInterface = new JPanel(new BorderLayout());
        urlConnectionInterface.add(urlPanel, BorderLayout.NORTH);

        // Create a welcome panel (shown by default)
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Select a menu option to begin");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Create a card layout panel to switch between views
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(welcomePanel, "welcome");
        containerPanel.add(urlConnectionInterface, "urlConnection");

        // Show welcome panel by default
        cardLayout.show(containerPanel, "welcome");

        // Add container panel to main panel
        mainPanel.add(containerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        getContentPane().add(mainPanel);

        // Create and set up the menu bar
        JMenuBar menuBar = new JMenuBar();

        // 1. URLConnection menu
        JMenu urlConnectionMenu = new JMenu("URLConnection");
        JMenuItem openConnMenuItem = new JMenuItem("Opening URLConnections");
        JMenuItem readDataMenuItem = new JMenuItem("Reading data from a server");
        JMenuItem getOutputMenuItem = new JMenuItem("GetOutputStream");

        urlConnectionMenu.add(openConnMenuItem);
        urlConnectionMenu.add(readDataMenuItem);
        urlConnectionMenu.add(getOutputMenuItem);
        menuBar.add(urlConnectionMenu);

        // 2. Correct Character Set menu
        JMenu characterSetMenu = new JMenu("Correct Character Set");
        JMenuItem encodingMenuItem = new JMenuItem("EndcodingAwareSourceViewer");
        JMenuItem headerViewerMenuItem = new JMenuItem("HeaderViewer");
        JMenuItem getHeaderFieldMenuItem = new JMenuItem("Get Header Field With String Name");
        JMenuItem getAllHeadersMenuItem = new JMenuItem("Get All Headers");

        characterSetMenu.add(encodingMenuItem);
        characterSetMenu.add(headerViewerMenuItem);
        characterSetMenu.add(getHeaderFieldMenuItem);
        characterSetMenu.add(getAllHeadersMenuItem);
        menuBar.add(characterSetMenu);

        // 3. Configuring The Connection menu
        JMenu configConnectionMenu = new JMenu("Configuring The Connection");
        JMenuItem configMenuItem = new JMenuItem("Configure Connection");
        configConnectionMenu.add(configMenuItem);
        menuBar.add(configConnectionMenu);

        // Add action listeners for other menu items
        readDataMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Reading data from a server functionality will be implemented in the future.");
            }
        });

        getOutputMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("GetOutputStream functionality will be implemented in the future.");
            }
        });

        encodingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("EndcodingAwareSourceViewer functionality will be implemented in the future.");
            }
        });

        headerViewerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("HeaderViewer functionality will be implemented in the future.");
            }
        });

        getHeaderFieldMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Get Header Field With String Name functionality will be implemented in the future.");
            }
        });

        getAllHeadersMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Get All Headers functionality will be implemented in the future.");
            }
        });

        configMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMessage("Configure Connection functionality will be implemented in the future.");
            }
        });

        setJMenuBar(menuBar);

        // Add action listener for Check button
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkConnection();
            }
        });

        openConnMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the URL connection interface
                cardLayout.show(containerPanel, "urlConnection");
                urlField.requestFocus();
            }
        });
    }

    /**
     * Checks the connection to the URL entered in the text field
     * and displays the string representation of the URLConnection object
     */
    private void checkConnection() {
        String urlString = urlField.getText().trim();
        connectionField.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            urlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            currentUrl = uri.toURL();

            // Open connection
            currentConnection = currentUrl.openConnection();

            // Display the string representation of the URLConnection object
            connectionField.setText(currentConnection.toString());

            // Show success message
            showMessage("Connection established successfully!");

        } catch (URISyntaxException e) {
            showMessage("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            showMessage("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            showMessage("Error connecting to URL: " + e.getMessage());
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    /**
     * Shows a message dialog with the given message
     * @param message The message to display
     */
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Lab5 app = new Lab5();
            app.setVisible(true);
        });
    }
}
