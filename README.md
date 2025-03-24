# LTM - Java Network Programming Lab Exercises

A collection of Java applications for network programming exercises. This project includes various tools for network-related operations like DNS lookups, log analysis, and more.

## Features

- **Main Launcher**: Central application to access all lab exercises
- **Lab 1**: Basic network programming exercises
- **Lab 2**: Socket programming exercises
- **Lab 3**: Network utility tools
  - Hostname to IP Lookup: Convert domain names to IP addresses
  - DNS Records Lookup: Query various DNS record types for domains
  - Web Server Log Analyzer: Analyze and extract insights from web server log files
- **Lab 4-10**: Additional network programming exercises

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- Swing support (included in standard JDK)

### Running the Application

You can run the application in several ways:

1. **Using the JAR file**:
   ```
   java -jar ExerciseLauncher.jar
   ```

2. **Building from source**:
   - On Windows: `create_jar.bat`
   - On Linux/Mac: `./create_jar.sh`
   - Using Ant: `ant jar`

## Web Server Log Analyzer

The Web Server Log Analyzer in Lab 3 allows you to:

- Load and analyze web server log files (Apache Common, Apache Combined, Nginx formats)
- View logs in a structured table format
- Filter log entries by IP address, status code, URL, etc.
- View comprehensive statistics on server activity
- Export results to CSV format for further analysis

### Sample Log Files

The project includes a utility to generate sample log files for testing purposes. Run the SampleLogGenerator class to create these files.

## License

This project is provided for educational purposes.

## Author

52100781_NguyenThanhDat