package lab5;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.List;

public class Lab5 extends JFrame {
    private JPanel mainPanel;
    private JPanel urlPanel;
    private JPanel readDataPanel;
    private JPanel downloadPanel;
    private JPanel encodingViewerPanel;
    private JPanel headerViewerPanel;
    private JPanel headerFieldPanel;
    private JPanel allHeadersPanel;
    private JPanel containerPanel;
    private JTextField urlField;
    private JTextField connectionField;
    private JTextField readUrlField;
    private JTextField downloadUrlField;
    private JTextField fileNameField;
    private JTextField encodingUrlField;
    private JTextField headerViewerUrlField;
    private JTextField headerFieldUrlField;
    private JTextField headerNameField;
    private JTextField allHeadersUrlField;
    private JTextArea htmlContentArea;
    private JTextArea downloadStatusArea;
    private JTextArea encodingContentArea;
    private JTextArea headerViewerArea;
    private JTextArea headerFieldArea;
    private JTextArea allHeadersArea;
    private JLabel nameLabel;
    private CardLayout cardLayout;
    private URL currentUrl;
    private URLConnection currentConnection;
    private JComboBox<String> encodingComboBox;

    public Lab5() {
        setTitle("Lab 5");
        setSize(700, 400);
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

        // Create a panel for reading data from a server
        readDataPanel = new JPanel(new BorderLayout());

        // Create input panel with URL field and Click Me button
        JPanel readInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel readUrlLabel = new JLabel("Enter URL:");
        readUrlField = new JTextField(30);
        JButton clickMeButton = new JButton("Click Me");

        readInputPanel.add(readUrlLabel);
        readInputPanel.add(readUrlField);
        readInputPanel.add(clickMeButton);

        // Create HTML content display area
        htmlContentArea = new JTextArea(20, 50);
        htmlContentArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(htmlContentArea);

        // Add components to read data panel
        readDataPanel.add(readInputPanel, BorderLayout.NORTH);
        readDataPanel.add(scrollPane, BorderLayout.CENTER);

        // Add action listener for Click Me button
        clickMeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readDataFromServer();
            }
        });

        // Create a welcome panel (shown by default)
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Select a menu option to begin");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Create a panel for downloading HTML files
        downloadPanel = new JPanel(new BorderLayout());

        // Create input panel with URL field, filename field, and Download button
        JPanel downloadInputPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        // URL input row
        JPanel urlInputRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel downloadUrlLabel = new JLabel("Enter URL:");
        downloadUrlField = new JTextField(30);
        urlInputRow.add(downloadUrlLabel);
        urlInputRow.add(downloadUrlField);

        // Filename input row
        JPanel fileNameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fileNameLabel = new JLabel("File Name:");
        fileNameField = new JTextField(20);
        fileNameField.setText("downloaded.htm");
        fileNameRow.add(fileNameLabel);
        fileNameRow.add(fileNameField);

        // Button row
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton downloadButton = new JButton("Download");
        buttonRow.add(downloadButton);

        // Add rows to download input panel
        downloadInputPanel.add(urlInputRow);
        downloadInputPanel.add(fileNameRow);
        downloadInputPanel.add(buttonRow);

        // Create download status area
        downloadStatusArea = new JTextArea(15, 50);
        downloadStatusArea.setEditable(false);
        JScrollPane downloadScrollPane = new JScrollPane(downloadStatusArea);

        // Add components to download panel
        downloadPanel.add(downloadInputPanel, BorderLayout.NORTH);
        downloadPanel.add(downloadScrollPane, BorderLayout.CENTER);

        // Add action listener for Download button
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downloadHtmlFile();
            }
        });

        // Create EncodingAwareSourceViewer panel
        encodingViewerPanel = new JPanel(new BorderLayout());

        // Create input panel for encoding viewer
        JPanel encodingInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel encodingUrlLabel = new JLabel("Enter URL:");
        encodingUrlField = new JTextField(30);
        JLabel encodingLabel = new JLabel("Encoding:");
        encodingComboBox = new JComboBox<>(new String[] {
            "UTF-8", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE"
        });
        JButton viewEncodedButton = new JButton("View");

        encodingInputPanel.add(encodingUrlLabel);
        encodingInputPanel.add(encodingUrlField);
        encodingInputPanel.add(encodingLabel);
        encodingInputPanel.add(encodingComboBox);
        encodingInputPanel.add(viewEncodedButton);

        // Create content area for encoding viewer
        encodingContentArea = new JTextArea(20, 50);
        encodingContentArea.setEditable(false);
        JScrollPane encodingScrollPane = new JScrollPane(encodingContentArea);

        // Add components to encoding viewer panel
        encodingViewerPanel.add(encodingInputPanel, BorderLayout.NORTH);
        encodingViewerPanel.add(encodingScrollPane, BorderLayout.CENTER);

        // Add action listener for View button
        viewEncodedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewEncodedContent();
            }
        });

        // Create HeaderViewer panel
        headerViewerPanel = new JPanel(new BorderLayout());

        // Create input panel for header viewer
        JPanel headerViewerInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerViewerUrlLabel = new JLabel("Enter URL:");
        headerViewerUrlField = new JTextField(30);
        JButton viewHeadersButton = new JButton("View Headers");

        headerViewerInputPanel.add(headerViewerUrlLabel);
        headerViewerInputPanel.add(headerViewerUrlField);
        headerViewerInputPanel.add(viewHeadersButton);

        // Create content area for header viewer
        headerViewerArea = new JTextArea(20, 50);
        headerViewerArea.setEditable(false);
        JScrollPane headerViewerScrollPane = new JScrollPane(headerViewerArea);

        // Add components to header viewer panel
        headerViewerPanel.add(headerViewerInputPanel, BorderLayout.NORTH);
        headerViewerPanel.add(headerViewerScrollPane, BorderLayout.CENTER);

        // Add action listener for View Headers button
        viewHeadersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewHeaders();
            }
        });

        // Create Get Header Field panel
        headerFieldPanel = new JPanel(new BorderLayout());

        // Create input panel for header field
        JPanel headerFieldInputPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // URL input row
        JPanel headerFieldUrlRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerFieldUrlLabel = new JLabel("Enter URL:");
        headerFieldUrlField = new JTextField(30);
        headerFieldUrlRow.add(headerFieldUrlLabel);
        headerFieldUrlRow.add(headerFieldUrlField);

        // Header name row
        JPanel headerNameRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerNameLabel = new JLabel("Header Name:");
        headerNameField = new JTextField(20);
        JButton getHeaderButton = new JButton("Get Header");
        headerNameRow.add(headerNameLabel);
        headerNameRow.add(headerNameField);
        headerNameRow.add(getHeaderButton);

        // Add rows to header field input panel
        headerFieldInputPanel.add(headerFieldUrlRow);
        headerFieldInputPanel.add(headerNameRow);

        // Create content area for header field
        headerFieldArea = new JTextArea(20, 50);
        headerFieldArea.setEditable(false);
        JScrollPane headerFieldScrollPane = new JScrollPane(headerFieldArea);

        // Add components to header field panel
        headerFieldPanel.add(headerFieldInputPanel, BorderLayout.NORTH);
        headerFieldPanel.add(headerFieldScrollPane, BorderLayout.CENTER);

        // Add action listener for Get Header button
        getHeaderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getHeaderField();
            }
        });

        // Create Get All Headers panel
        allHeadersPanel = new JPanel(new BorderLayout());

        // Create input panel for all headers
        JPanel allHeadersInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel allHeadersUrlLabel = new JLabel("Enter URL:");
        allHeadersUrlField = new JTextField(30);
        JButton getAllHeadersButton = new JButton("Get All Headers");

        allHeadersInputPanel.add(allHeadersUrlLabel);
        allHeadersInputPanel.add(allHeadersUrlField);
        allHeadersInputPanel.add(getAllHeadersButton);

        // Create content area for all headers
        allHeadersArea = new JTextArea(20, 50);
        allHeadersArea.setEditable(false);
        JScrollPane allHeadersScrollPane = new JScrollPane(allHeadersArea);

        // Add components to all headers panel
        allHeadersPanel.add(allHeadersInputPanel, BorderLayout.NORTH);
        allHeadersPanel.add(allHeadersScrollPane, BorderLayout.CENTER);

        // Add action listener for Get All Headers button
        getAllHeadersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getAllHeaders();
            }
        });

        // Create a card layout panel to switch between views
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.add(welcomePanel, "welcome");
        containerPanel.add(urlConnectionInterface, "urlConnection");
        containerPanel.add(readDataPanel, "readData");
        containerPanel.add(downloadPanel, "download");
        containerPanel.add(encodingViewerPanel, "encodingViewer");
        containerPanel.add(headerViewerPanel, "headerViewer");
        containerPanel.add(headerFieldPanel, "headerField");
        containerPanel.add(allHeadersPanel, "allHeaders");

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
                // Show the read data interface
                cardLayout.show(containerPanel, "readData");
                readUrlField.requestFocus();
            }
        });

        getOutputMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the download interface
                cardLayout.show(containerPanel, "download");
                downloadUrlField.requestFocus();
            }
        });

        encodingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the encoding viewer interface
                cardLayout.show(containerPanel, "encodingViewer");
                encodingUrlField.requestFocus();
            }
        });

        headerViewerMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the header viewer interface
                cardLayout.show(containerPanel, "headerViewer");
                headerViewerUrlField.requestFocus();
            }
        });

        getHeaderFieldMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the header field interface
                cardLayout.show(containerPanel, "headerField");
                headerFieldUrlField.requestFocus();
            }
        });

        getAllHeadersMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show the all headers interface
                cardLayout.show(containerPanel, "allHeaders");
                allHeadersUrlField.requestFocus();
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
     * Reads HTML data from the server at the specified URL
     * and displays it in the HTML content area
     */
    private void readDataFromServer() {
        String urlString = readUrlField.getText().trim();
        htmlContentArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            readUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Read data from the server
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {

                String line;
                StringBuilder content = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // Display the HTML content
                htmlContentArea.setText(content.toString());
            }

        } catch (URISyntaxException e) {
            showMessage("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            showMessage("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            showMessage("Error reading data from server: " + e.getMessage());
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

    /**
     * Downloads an HTML file from the specified URL and saves it to the lab5 directory
     */
    private void downloadHtmlFile() {
        String urlString = downloadUrlField.getText().trim();
        String fileName = fileNameField.getText().trim();
        downloadStatusArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        if (fileName.isEmpty()) {
            showMessage("Please enter a file name");
            return;
        }

        // Add .htm extension if not present
        if (!fileName.toLowerCase().endsWith(".htm") && !fileName.toLowerCase().endsWith(".html")) {
            fileName += ".htm";
            fileNameField.setText(fileName);
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            downloadUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Create the lab5 directory if it doesn't exist
            File lab5Dir = new File("lab5");
            if (!lab5Dir.exists()) {
                lab5Dir.mkdir();
                downloadStatusArea.append("Created lab5 directory\n");
            }

            // Create the output file
            File outputFile = new File(lab5Dir, fileName);

            // Download the file
            downloadStatusArea.append("Downloading from: " + url.toString() + "\n");
            downloadStatusArea.append("Saving to: " + outputFile.getAbsolutePath() + "\n\n");

            try (InputStream in = connection.getInputStream();
                 FileOutputStream out = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                    downloadStatusArea.append("Downloaded " + totalBytes + " bytes\r");
                }

                downloadStatusArea.append("\n\nDownload complete! Total size: " + totalBytes + " bytes\n");
                showMessage("File downloaded successfully to: " + outputFile.getAbsolutePath());
            }

        } catch (URISyntaxException e) {
            downloadStatusArea.append("Error: Invalid URI syntax - " + e.getMessage() + "\n");
        } catch (MalformedURLException e) {
            downloadStatusArea.append("Error: Invalid URL format - " + e.getMessage() + "\n");
        } catch (IOException e) {
            downloadStatusArea.append("Error: IO Exception - " + e.getMessage() + "\n");
        } catch (Exception e) {
            downloadStatusArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    /**
     * Views the content of a URL with the specified encoding
     */
    private void viewEncodedContent() {
        String urlString = encodingUrlField.getText().trim();
        String encoding = (String) encodingComboBox.getSelectedItem();
        encodingContentArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            encodingUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Read data from the server with specified encoding
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), encoding))) {

                String line;
                StringBuilder content = new StringBuilder();

                encodingContentArea.append("Using encoding: " + encoding + "\n\n");

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // Display the content
                encodingContentArea.append(content.toString());
            }

        } catch (URISyntaxException e) {
            encodingContentArea.setText("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            encodingContentArea.setText("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            encodingContentArea.setText("Error reading data from server: " + e.getMessage());
        } catch (Exception e) {
            encodingContentArea.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Views the headers of a URL connection
     */
    private void viewHeaders() {
        String urlString = headerViewerUrlField.getText().trim();
        headerViewerArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            headerViewerUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Get headers
            headerViewerArea.append("Headers for: " + url.toString() + "\n\n");

            // Display content type
            String contentType = connection.getContentType();
            headerViewerArea.append("Content-Type: " + contentType + "\n");

            // Display content length
            int contentLength = connection.getContentLength();
            headerViewerArea.append("Content-Length: " + contentLength + " bytes\n");

            // Display content encoding
            String contentEncoding = connection.getContentEncoding();
            headerViewerArea.append("Content-Encoding: " + contentEncoding + "\n");

            // Display date
            long date = connection.getDate();
            headerViewerArea.append("Date: " + (date == 0 ? "Not available" : new java.util.Date(date)) + "\n");

            // Display last modified
            long lastModified = connection.getLastModified();
            headerViewerArea.append("Last-Modified: " + (lastModified == 0 ? "Not available" : new java.util.Date(lastModified)) + "\n");

            // Display expiration
            long expiration = connection.getExpiration();
            headerViewerArea.append("Expiration: " + (expiration == 0 ? "Not available" : new java.util.Date(expiration)) + "\n");

        } catch (URISyntaxException e) {
            headerViewerArea.setText("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            headerViewerArea.setText("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            headerViewerArea.setText("Error connecting to URL: " + e.getMessage());
        } catch (Exception e) {
            headerViewerArea.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Gets a specific header field from a URL connection
     */
    private void getHeaderField() {
        String urlString = headerFieldUrlField.getText().trim();
        String headerName = headerNameField.getText().trim();
        headerFieldArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        if (headerName.isEmpty()) {
            showMessage("Please enter a header name");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            headerFieldUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Get header field
            String headerValue = connection.getHeaderField(headerName);

            headerFieldArea.append("URL: " + url.toString() + "\n\n");
            headerFieldArea.append("Header Name: " + headerName + "\n");
            headerFieldArea.append("Header Value: " + (headerValue != null ? headerValue : "Not found") + "\n");

        } catch (URISyntaxException e) {
            headerFieldArea.setText("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            headerFieldArea.setText("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            headerFieldArea.setText("Error connecting to URL: " + e.getMessage());
        } catch (Exception e) {
            headerFieldArea.setText("Error: " + e.getMessage());
        }
    }

    /**
     * Gets all headers from a URL connection
     */
    private void getAllHeaders() {
        String urlString = allHeadersUrlField.getText().trim();
        allHeadersArea.setText("");

        if (urlString.isEmpty()) {
            showMessage("Please enter a URL");
            return;
        }

        // Add http:// prefix if not present
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
            allHeadersUrlField.setText(urlString);
        }

        try {
            // Create URL object using URI to handle encoding properly
            URI uri = new URI(urlString);
            URL url = uri.toURL();

            // Open connection
            URLConnection connection = url.openConnection();

            // Get all headers
            allHeadersArea.append("All Headers for: " + url.toString() + "\n\n");

            // Get header fields
            Map<String, List<String>> headerFields = connection.getHeaderFields();

            if (headerFields.isEmpty()) {
                allHeadersArea.append("No headers found.\n");
            } else {
                for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
                    String key = entry.getKey();
                    List<String> values = entry.getValue();

                    if (key == null) {
                        allHeadersArea.append("Status: " + values.get(0) + "\n");
                    } else {
                        allHeadersArea.append(key + ": " + String.join(", ", values) + "\n");
                    }
                }
            }

        } catch (URISyntaxException e) {
            allHeadersArea.setText("Invalid URI syntax: " + e.getMessage());
        } catch (MalformedURLException e) {
            allHeadersArea.setText("Invalid URL format: " + e.getMessage());
        } catch (IOException e) {
            allHeadersArea.setText("Error connecting to URL: " + e.getMessage());
        } catch (Exception e) {
            allHeadersArea.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Lab5 app = new Lab5();
            app.setVisible(true);
        });
    }
}
