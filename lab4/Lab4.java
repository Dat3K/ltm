package lab4;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.util.Date;

public class Lab4 extends JFrame {
    private JPanel mainPanel;
    private JPanel containerPanel;
    private JLabel nameLabel;
    private CardLayout cardLayout;

    // Panels for different functionalities
    private JPanel welcomePanel;
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel4;
    private JPanel panel5;
    private JPanel panel6;

    // Text areas for displaying content
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JTextArea textArea3;
    private JTextArea textArea4;
    private JTextArea textArea5;
    private JTextArea textArea6;

    // Text fields for input
    private JTextField baseUrlField;
    private JTextField relativeUrlField;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField urlField;
    private JTextField urlInputField;
    private JTextArea dataOutputArea;

    public Lab4() {
        setTitle("52100781_NguyenThanhDat - Lab 4");
        setSize(700, 500);
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
        JLabel welcomeLabel = new JLabel("Select a menu option to begin");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Create Panel 1 - Constructing relative URLs
        panel1 = createRelativeUrlPanel();

        // Create Panel 2 - Constructing a URL from a String
        panel2 = createPanel("Constructing a URL from a String", textField2 = new JTextField(30), textArea2 = new JTextArea(20, 50));

        // Create Panel 3 - Constructing a URL from its component part1
        panel3 = createPanel("Constructing a URL from its component part1", textField3 = new JTextField(30), textArea3 = new JTextArea(20, 50));

        // Create Panel 4 - Constructing a URL from its component part2
        panel4 = createPanel("Constructing a URL from its component part2", textField4 = new JTextField(30), textArea4 = new JTextArea(20, 50));

        // Create Panel 5 - Splitting a URL into Pieces
        panel5 = createUrlSplittingPanel();

        // Create Panel 6 - Retrieving Data from a URL
        panel6 = createDataRetrievalPanel();

        // Create a card layout panel to switch between views
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(welcomePanel, "welcome");
        containerPanel.add(panel1, "panel1");
        containerPanel.add(panel2, "panel2");
        containerPanel.add(panel3, "panel3");
        containerPanel.add(panel4, "panel4");
        containerPanel.add(panel5, "panel5");
        containerPanel.add(panel6, "panel6");

        // Show welcome panel by default
        cardLayout.show(containerPanel, "welcome");

        // Add container panel to main panel
        mainPanel.add(containerPanel, BorderLayout.CENTER);

        // Add main panel to frame
        getContentPane().add(mainPanel);

        // Create and set up the menu bar
        JMenuBar menuBar = new JMenuBar();

        // Menu 1: Creating New URLs
        JMenu creatingUrlsMenu = new JMenu("Creating New URLs");
        JMenuItem relativeUrlsItem = new JMenuItem("Constructing relative URLs");
        JMenuItem fromStringItem = new JMenuItem("Constructing a URL from a String");
        JMenuItem fromComponentPart1Item = new JMenuItem("Constructing a URL from its component part1");
        JMenuItem fromComponentPart2Item = new JMenuItem("Constructing a URL from its component part2");

        creatingUrlsMenu.add(relativeUrlsItem);
        creatingUrlsMenu.add(fromStringItem);
        creatingUrlsMenu.add(fromComponentPart1Item);
        creatingUrlsMenu.add(fromComponentPart2Item);
        menuBar.add(creatingUrlsMenu);

        // Menu 2: Splitting a URL into Pieces
        JMenu splittingUrlMenu = new JMenu("Splitting a URL into Pieces");
        JMenuItem splittingUrlItem = new JMenuItem("Split URL");

        splittingUrlMenu.add(splittingUrlItem);
        menuBar.add(splittingUrlMenu);

        // Menu 3: Retrieving Data from a URL
        JMenu retrievingDataMenu = new JMenu("Retrieving Data from a URL");
        JMenuItem downloadWebPageItem = new JMenuItem("Download a web page");
        JMenuItem downloadObjectItem = new JMenuItem("Download an object");
        JMenuItem sameFilesItem = new JMenuItem("Are the sameFiles?");

        retrievingDataMenu.add(downloadWebPageItem);
        retrievingDataMenu.add(downloadObjectItem);
        retrievingDataMenu.add(sameFilesItem);
        menuBar.add(retrievingDataMenu);

        // Add action listeners for menu items in Creating New URLs menu
        relativeUrlsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel1");
                baseUrlField.requestFocus();

                // Automatically show the result when the panel is displayed
                try {
                    URL base = new URL(baseUrlField.getText().trim());
                    URL resolved = new URL(base, relativeUrlField.getText().trim());
                    textArea1.setText(resolved.toString());
                } catch (MalformedURLException ex) {
                    // Ignore exceptions on initial display
                }
            }
        });

        fromStringItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel2");
                textField2.requestFocus();
            }
        });

        fromComponentPart1Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel3");
                textField3.requestFocus();
            }
        });

        fromComponentPart2Item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel4");
                textField4.requestFocus();
            }
        });

        // Add action listener for Splitting a URL menu item
        splittingUrlItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel5");
                urlField.requestFocus();
            }
        });

        // Add action listeners for Retrieving Data menu items
        downloadWebPageItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel6");
                // Set the method type to use
                panel6.putClientProperty("methodType", "openStream");
                panel6.putClientProperty("operationType", "downloadWebPage");
            }
        });

        downloadObjectItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel6");
                // Set the method type to use
                panel6.putClientProperty("methodType", "getContent");
                panel6.putClientProperty("operationType", "downloadObject");
            }
        });

        sameFilesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "panel6");
                // Set the method type to use
                panel6.putClientProperty("methodType", "openConnection");
                panel6.putClientProperty("operationType", "sameFiles");
            }
        });

        setJMenuBar(menuBar);
    }

    /**
     * Creates a panel with a text field and text area
     * @param panelName The name of the panel
     * @param textField The text field for input
     * @param textArea The text area for output
     * @return The created panel
     */
    private JPanel createPanel(String panelName, JTextField textField, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout());

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(panelName);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel inputLabel = new JLabel("URL:");
        JButton actionButton = new JButton("Process");

        inputPanel.add(inputLabel);
        inputPanel.add(textField);
        inputPanel.add(actionButton);

        // Create output area
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // Create a panel to hold the input panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        // Add components to panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener for button
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = textField.getText().trim();
                if (!input.isEmpty()) {
                    // Add http:// prefix if not present
                    if (!input.startsWith("http://") && !input.startsWith("https://")) {
                        input = "http://" + input;
                        textField.setText(input);
                    }

                    try {
                        URL url = new URL(input);

                        // Process URL based on panel name
                        if (panelName.equals("Splitting a URL into Pieces")) {
                            processUrlSplitting(url, textArea);
                        } else if (panelName.equals("Retrieving Data from a URL")) {
                            textArea.setText("URL: " + input + "\n\n");
                            textArea.append("Please use the dedicated Retrieving Data panel for this functionality.");
                        } else if (panelName.equals("Constructing a URL from a String")) {
                            textArea.setText("URL: " + input + "\n\n");
                            textArea.append("URL successfully constructed from string: " + url + "\n");
                            textArea.append("Protocol: " + url.getProtocol() + "\n");
                            textArea.append("Host: " + url.getHost() + "\n");
                            textArea.append("Port: " + (url.getPort() == -1 ? "default" : url.getPort()) + "\n");
                            textArea.append("Path: " + url.getPath() + "\n");
                        } else if (panelName.equals("Constructing a URL from its component part1")) {
                            textArea.setText("URL: " + input + "\n\n");
                            textArea.append("This panel will demonstrate constructing a URL from its components (part 1).\n");
                            textArea.append("You can implement the actual functionality later.");
                        } else if (panelName.equals("Constructing a URL from its component part2")) {
                            textArea.setText("URL: " + input + "\n\n");
                            textArea.append("This panel will demonstrate constructing a URL from its components (part 2).\n");
                            textArea.append("You can implement the actual functionality later.");
                        } else {
                            textArea.setText("URL: " + input + "\n\n");
                            textArea.append("This is a placeholder for " + panelName + " functionality.\n");
                            textArea.append("You can implement the actual functionality later.");
                        }
                    } catch (MalformedURLException ex) {
                        textArea.setText("Error: Invalid URL format\n");
                        textArea.append(ex.getMessage());
                    } catch (Exception ex) {
                        textArea.setText("Error: " + ex.getMessage() + "\n");
                        ex.printStackTrace();
                    }
                } else {
                    showMessage("Please enter a URL");
                }
            }
        });

        return panel;
    }

    /**
     * Creates a panel for splitting a URL into its components
     * @return The created panel
     */
    private JPanel createUrlSplittingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Splitting a URL into Pieces");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout());

        // URL input row
        JPanel urlRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Enter URL:");
        urlField = new JTextField("ftp://mp3:mp3@138.247.121.61:21000/c%3a/", 40);

        urlRow.add(urlLabel);
        urlRow.add(urlField);

        // Button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton clickMeButton = new JButton("Click Me!");
        buttonRow.add(clickMeButton);

        // Add rows to input panel
        inputPanel.add(urlRow, BorderLayout.NORTH);
        inputPanel.add(buttonRow, BorderLayout.CENTER);

        // Create components panel to display all URL components
        JPanel componentsPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        componentsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create text areas for each component
        JPanel protocolPanel = createComponentPanel("Protocol:");
        JPanel hostPanel = createComponentPanel("Host:");
        JPanel portPanel = createComponentPanel("Port:");
        JPanel defaultPortPanel = createComponentPanel("DefaultPort:");
        JPanel pathPanel = createComponentPanel("Path:");
        JPanel filePanel = createComponentPanel("File:");
        JPanel queryPanel = createComponentPanel("Query:");
        JPanel refPanel = createComponentPanel("Ref:");
        JPanel userInfoPanel = createComponentPanel("UserInfo:");
        JPanel authorityPanel = createComponentPanel("Authority:");

        // Add component panels to components panel
        componentsPanel.add(protocolPanel);
        componentsPanel.add(hostPanel);
        componentsPanel.add(portPanel);
        componentsPanel.add(defaultPortPanel);
        componentsPanel.add(pathPanel);
        componentsPanel.add(filePanel);
        componentsPanel.add(queryPanel);
        componentsPanel.add(refPanel);
        componentsPanel.add(userInfoPanel);
        componentsPanel.add(authorityPanel);

        // Create a scroll pane for components panel
        JScrollPane componentsScrollPane = new JScrollPane(componentsPanel);
        componentsScrollPane.setPreferredSize(new Dimension(600, 300));

        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(componentsScrollPane, BorderLayout.CENTER);

        // Add a "Back" button at the bottom
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButtonPanel.add(backButton);
        panel.add(backButtonPanel, BorderLayout.SOUTH);

        // Add action listener for Click Me button
        clickMeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlString = urlField.getText().trim();

                if (urlString.isEmpty()) {
                    showMessage("Please enter a URL");
                    return;
                }

                try {
                    URL url = new URL(urlString);

                    // Display all URL components
                    displayAllUrlComponents(url, componentsPanel);
                } catch (MalformedURLException ex) {
                    showMessage("Error: Invalid URL format - " + ex.getMessage());
                }
            }
        });

        // Add action listener for Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "welcome");
            }
        });

        return panel;
    }

    /**
     * Creates a panel for a URL component with a label and text area
     * @param labelText The label text
     * @return The created panel
     */
    private JPanel createComponentPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, 20));
        JTextArea textArea = new JTextArea(1, 40);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        textArea.setName(labelText.toLowerCase().replace(":", ""));

        panel.add(label, BorderLayout.WEST);
        panel.add(textArea, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Displays all URL components in the components panel
     * @param url The URL to display components for
     * @param componentsPanel The panel containing the component text areas
     */
    private void displayAllUrlComponents(URL url, JPanel componentsPanel) {
        // Get all components from the URL
        String protocol = url.getProtocol();
        String host = url.getHost();
        String port = url.getPort() == -1 ? "default" : String.valueOf(url.getPort());
        String defaultPort = String.valueOf(url.getDefaultPort());
        String path = url.getPath();
        String file = url.getFile();
        String query = url.getQuery();
        String ref = url.getRef();
        String userInfo = url.getUserInfo();
        String authority = url.getAuthority();

        // Update all component text areas
        for (Component component : componentsPanel.getComponents()) {
            if (component instanceof JPanel) {
                JPanel componentPanel = (JPanel) component;
                for (Component subComponent : componentPanel.getComponents()) {
                    if (subComponent instanceof JTextArea) {
                        JTextArea textArea = (JTextArea) subComponent;
                        String componentType = textArea.getName();

                        // Set the appropriate value based on component type
                        switch (componentType) {
                            case "protocol":
                                textArea.setText(protocol != null ? protocol : "");
                                break;
                            case "host":
                                textArea.setText(host != null ? host : "");
                                break;
                            case "port":
                                textArea.setText(port);
                                break;
                            case "defaultport":
                                textArea.setText(defaultPort);
                                break;
                            case "path":
                                textArea.setText(path != null ? path : "");
                                break;
                            case "file":
                                textArea.setText(file != null ? file : "");
                                break;
                            case "query":
                                textArea.setText(query != null ? query : "");
                                break;
                            case "ref":
                                textArea.setText(ref != null ? ref : "");
                                break;
                            case "userinfo":
                                textArea.setText(userInfo != null ? userInfo : "");
                                break;
                            case "authority":
                                textArea.setText(authority != null ? authority : "");
                                break;
                        }
                    }
                }
            }
        }
    }



    /**
     * Process URL splitting operations based on the current menu selection
     * @param url The URL to process
     * @param textArea The text area to display results
     */
    private void processUrlSplitting(URL url, JTextArea textArea) {
        // Get the title of the current panel's parent component (JInternalFrame)
        String currentOperation = textArea.getText();

        textArea.setText("URL: " + url.toString() + "\n\n");

        if (currentOperation.contains("authority")) {
            textArea.append("Authority: " + url.getAuthority() + "\n");
        } else if (currentOperation.contains("default port")) {
            textArea.append("Default Port: " + url.getDefaultPort() + "\n");
        } else if (currentOperation.contains("port")) {
            textArea.append("Port: " + (url.getPort() == -1 ? "default" : url.getPort()) + "\n");
        } else if (currentOperation.contains("file")) {
            textArea.append("File: " + url.getFile() + "\n");
        } else if (currentOperation.contains("host")) {
            textArea.append("Host: " + url.getHost() + "\n");
        } else if (currentOperation.contains("path")) {
            textArea.append("Path: " + url.getPath() + "\n");
        } else if (currentOperation.contains("protocol")) {
            textArea.append("Protocol: " + url.getProtocol() + "\n");
        } else if (currentOperation.contains("query")) {
            textArea.append("Query: " + url.getQuery() + "\n");
        } else if (currentOperation.contains("reference")) {
            textArea.append("Reference: " + url.getRef() + "\n");
        } else if (currentOperation.contains("user info")) {
            textArea.append("User Info: " + url.getUserInfo() + "\n");
        } else {
            // Show all components if no specific operation is selected
            textArea.append("Authority: " + url.getAuthority() + "\n");
            textArea.append("Default Port: " + url.getDefaultPort() + "\n");
            textArea.append("Port: " + (url.getPort() == -1 ? "default" : url.getPort()) + "\n");
            textArea.append("File: " + url.getFile() + "\n");
            textArea.append("Host: " + url.getHost() + "\n");
            textArea.append("Path: " + url.getPath() + "\n");
            textArea.append("Protocol: " + url.getProtocol() + "\n");
            textArea.append("Query: " + url.getQuery() + "\n");
            textArea.append("Reference: " + url.getRef() + "\n");
            textArea.append("User Info: " + url.getUserInfo() + "\n");
        }
    }

    /**
     * Creates a panel for retrieving data from a URL
     * @return The created panel
     */
    private JPanel createDataRetrievalPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Retrieving Data from a URL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create method label panel
        JPanel methodPanel = new JPanel(new BorderLayout());
        final JLabel methodLabel = new JLabel("Using openStream()");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        methodLabel.setHorizontalAlignment(SwingConstants.CENTER);
        methodLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        methodPanel.add(methodLabel, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout());

        // URL input row
        JPanel urlRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel urlLabel = new JLabel("Enter URL:");
        JTextField urlField = new JTextField("https://www.example.com", 40);

        urlRow.add(urlLabel);
        urlRow.add(urlField);

        // Button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton retrieveButton = new JButton("Retrieve Data");
        buttonRow.add(retrieveButton);

        // Add rows to input panel
        inputPanel.add(urlRow, BorderLayout.NORTH);
        inputPanel.add(buttonRow, BorderLayout.CENTER);

        // Create output panel
        JPanel outputPanel = new JPanel(new BorderLayout());
        JLabel outputLabel = new JLabel("Data Output:");
        outputLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

        JTextArea outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        // Add components to panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(methodPanel, BorderLayout.CENTER);
        topPanel.add(inputPanel, BorderLayout.SOUTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(outputPanel, BorderLayout.CENTER);

        // Add a "Back" button at the bottom
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButtonPanel.add(backButton);
        panel.add(backButtonPanel, BorderLayout.SOUTH);

        // Set default method type and operation type
        panel.putClientProperty("methodType", "openStream");
        panel.putClientProperty("operationType", "downloadWebPage");

        // Add property change listener to update method label and operation type
        panel.addPropertyChangeListener("operationType", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String operationType = (String) evt.getNewValue();
                String methodType = (String) panel.getClientProperty("methodType");

                switch (operationType) {
                    case "downloadWebPage":
                        methodLabel.setText("Download a web page");
                        outputArea.setText("This panel will download and display a web page using " + methodType + "() method.\n");
                        outputArea.append("Enter a URL and click Retrieve Data to download the web page.");
                        break;
                    case "downloadObject":
                        methodLabel.setText("Download an object");
                        outputArea.setText("This panel will download an object from a URL using " + methodType + "() method.\n");
                        outputArea.append("Enter a URL and click Retrieve Data to download the object.");
                        break;
                    case "sameFiles":
                        methodLabel.setText("Are the sameFiles?");
                        outputArea.setText("This panel will check if two URLs refer to the same file using " + methodType + "() method.\n");
                        outputArea.append("Enter a URL and click Retrieve Data to check if it's the same as another file.");
                        break;
                    default:
                        methodLabel.setText("Using " + methodType + "()");
                        outputArea.setText("This panel will retrieve data using " + methodType + "() method.\n");
                        outputArea.append("Enter a URL and click Retrieve Data to retrieve data.");
                        break;
                }
            }
        });

        // Add action listener for Retrieve Data button
        retrieveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String urlString = urlField.getText().trim();

                if (urlString.isEmpty()) {
                    showMessage("Please enter a URL");
                    return;
                }

                // Get the method type and operation type to use
                String methodType = (String) panel.getClientProperty("methodType");
                String operationType = (String) panel.getClientProperty("operationType");

                // Retrieve data using the selected method and operation type
                retrieveData(urlString, methodType, operationType, outputArea);
            }
        });

        // Add action listener for Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "welcome");
            }
        });

        // Initialize output area with default operation type
        methodLabel.setText("Download a web page");
        outputArea.setText("This panel will download and display a web page using openStream() method.\n");
        outputArea.append("Enter a URL and click Retrieve Data to download the web page.");

        return panel;
    }

    /**
     * Retrieves data from a URL using the specified method and operation type
     * @param urlString The URL to retrieve data from
     * @param methodType The method to use (openStream, openConnection, getContent)
     * @param operationType The operation type (downloadWebPage, downloadObject, sameFiles)
     * @param outputArea The text area to display results
     */
    private void retrieveData(String urlString, String methodType, String operationType, JTextArea outputArea) {
        try {
            URL url = new URL(urlString);
            outputArea.setText("URL: " + url.toString() + "\n\n");

            // Add operation type information
            if (operationType != null) {
                switch (operationType) {
                    case "downloadWebPage":
                        outputArea.append("Operation: Download a web page\n");
                        outputArea.append("Method: " + methodType + "()\n\n");
                        break;
                    case "downloadObject":
                        outputArea.append("Operation: Download an object\n");
                        outputArea.append("Method: " + methodType + "()\n\n");
                        break;
                    case "sameFiles":
                        outputArea.append("Operation: Check if two URLs refer to the same file\n");
                        outputArea.append("Method: " + methodType + "()\n\n");
                        break;
                    default:
                        outputArea.append("Method: " + methodType + "()\n\n");
                        break;
                }
            }

            // Retrieve data using the specified method
            switch (methodType) {
                case "openStream":
                    retrieveDataUsingOpenStream(url, outputArea);
                    break;
                case "openConnection":
                    retrieveDataUsingOpenConnection(url, outputArea);
                    break;
                case "getContent":
                    retrieveDataUsingGetContent(url, outputArea);
                    break;
                default:
                    outputArea.append("Unknown method type: " + methodType);
                    break;
            }
        } catch (MalformedURLException ex) {
            outputArea.setText("Error: Invalid URL format - " + ex.getMessage());
        } catch (Exception ex) {
            outputArea.setText("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves data from a URL using openStream() method
     * @param url The URL to retrieve data from
     * @param outputArea The text area to display results
     */
    private void retrieveDataUsingOpenStream(URL url, JTextArea outputArea) {
        try (InputStream in = url.openStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            outputArea.append("Data retrieved using openStream() method:\n\n");

            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 50) {
                outputArea.append(line + "\n");
                lineCount++;
            }

            if (lineCount >= 50) {
                outputArea.append("\n... (output truncated, showing first 50 lines) ...");
            }
        } catch (IOException ex) {
            outputArea.append("Error reading from URL: " + ex.getMessage());
        }
    }

    /**
     * Retrieves data from a URL using openConnection() method
     * @param url The URL to retrieve data from
     * @param outputArea The text area to display results
     */
    private void retrieveDataUsingOpenConnection(URL url, JTextArea outputArea) {
        try {
            URLConnection connection = url.openConnection();

            // Display connection information
            outputArea.append("Connection Information:\n");
            outputArea.append("Content Type: " + connection.getContentType() + "\n");
            outputArea.append("Content Length: " + connection.getContentLength() + " bytes\n");
            outputArea.append("Last Modified: " + new Date(connection.getLastModified()) + "\n\n");

            // Display headers
            outputArea.append("Headers:\n");
            for (int i = 1; ; i++) {
                String headerName = connection.getHeaderFieldKey(i);
                String headerValue = connection.getHeaderField(i);

                if (headerName == null && headerValue == null) {
                    break;
                }

                if (headerName != null && headerValue != null) {
                    outputArea.append(headerName + ": " + headerValue + "\n");
                }
            }

            // Display content (first 20 lines)
            outputArea.append("\nContent (first 20 lines):\n");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 20) {
                    outputArea.append(line + "\n");
                    lineCount++;
                }

                if (lineCount >= 20) {
                    outputArea.append("\n... (output truncated, showing first 20 lines) ...");
                }
            }
        } catch (IOException ex) {
            outputArea.append("Error opening connection: " + ex.getMessage());
        }
    }

    /**
     * Retrieves data from a URL using getContent() method
     * @param url The URL to retrieve data from
     * @param outputArea The text area to display results
     */
    private void retrieveDataUsingGetContent(URL url, JTextArea outputArea) {
        try {
            Object content = url.getContent();

            outputArea.append("Content retrieved using getContent() method:\n\n");
            outputArea.append("Content Type: " + content.getClass().getName() + "\n\n");

            if (content instanceof InputStream) {
                outputArea.append("Content is an InputStream. Reading first 20 lines:\n\n");

                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) content))) {
                    String line;
                    int lineCount = 0;
                    while ((line = reader.readLine()) != null && lineCount < 20) {
                        outputArea.append(line + "\n");
                        lineCount++;
                    }

                    if (lineCount >= 20) {
                        outputArea.append("\n... (output truncated, showing first 20 lines) ...");
                    }
                }
            } else {
                outputArea.append("Content: " + content.toString());
            }
        } catch (IOException ex) {
            outputArea.append("Error getting content: " + ex.getMessage());
        }
    }

    /**
     * Creates a panel for constructing relative URLs
     * @return The created panel
     */
    private JPanel createRelativeUrlPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Constructing relative URLs");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Create input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // Base URL input row
        JPanel baseUrlRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel baseUrlLabel = new JLabel("Enter Data1:");
        baseUrlField = new JTextField("http://www.ibiblio.org/javafaq/index.html", 40);
        baseUrlRow.add(baseUrlLabel);
        baseUrlRow.add(baseUrlField);

        // Relative URL input row
        JPanel relativeUrlRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel relativeUrlLabel = new JLabel("Enter Data2:");
        relativeUrlField = new JTextField("mailinglists.html", 40);
        relativeUrlRow.add(relativeUrlLabel);
        relativeUrlRow.add(relativeUrlField);

        // Button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton clickMeButton = new JButton("Click Me!");
        buttonRow.add(clickMeButton);

        // Add rows to input panel
        inputPanel.add(baseUrlRow);
        inputPanel.add(relativeUrlRow);
        inputPanel.add(buttonRow);

        // Create result panel
        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("Result:");
        resultLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 0));

        textArea1 = new JTextArea(1, 50);
        textArea1.setEditable(false);
        textArea1.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(textArea1, BorderLayout.CENTER);

        // Create a panel to hold the input panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(inputPanel, BorderLayout.CENTER);
        topPanel.add(resultPanel, BorderLayout.SOUTH);

        // Add components to panel
        panel.add(topPanel, BorderLayout.NORTH);

        // Add a "Back" button at the bottom
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back");
        backButtonPanel.add(backButton);
        panel.add(backButtonPanel, BorderLayout.SOUTH);

        // Add action listener for Click Me button
        clickMeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String baseUrl = baseUrlField.getText().trim();
                String relativeUrl = relativeUrlField.getText().trim();

                if (baseUrl.isEmpty() || relativeUrl.isEmpty()) {
                    showMessage("Please enter both URLs");
                    return;
                }

                try {
                    URL base = new URL(baseUrl);
                    URL resolved = new URL(base, relativeUrl);
                    textArea1.setText(resolved.toString());
                } catch (MalformedURLException ex) {
                    showMessage("Error: " + ex.getMessage());
                }
            }
        });

        // Add action listener for Back button
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(containerPanel, "welcome");
            }
        });

        return panel;
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
            Lab4 app = new Lab4();
            app.setVisible(true);
        });
    }
}
