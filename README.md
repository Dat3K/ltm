# LTM - Java Network Programming Lab Exercises

A collection of Java applications for network programming exercises. This project includes various tools for network-related operations like DNS lookups, log analysis, and more.

## Project Overview

This project is organized as a collection of lab exercises for network programming in Java. Each lab focuses on different aspects of network programming and provides practical implementations of various network-related tools and utilities.

### Project Structure

```
Lab/
├── lab3/          
├── lab4/
├── lab5/
├── lab6/
├── lab7/
├── lab8/
├── lab9/
├── lab10/
├── build/          # Compiled class files
├── MainFrame.java  # Main launcher application
├── create_jar.bat  # Windows script to compile and create JAR
├── create_jar.sh   # Linux/Mac script to compile and create JAR
├── MANIFEST.MF     # JAR manifest file
└── build.xml       # Ant build file
```

## How It Works

### Main Launcher (MainFrame.java)

The `MainFrame.java` serves as the central entry point for all lab exercises. It:

1. Creates a graphical user interface with buttons for each lab
2. Handles launching individual lab applications when their respective buttons are clicked
3. Provides a consistent interface for accessing all lab exercises

### JAR Creation (create_jar.bat/create_jar.sh)

The project includes scripts to compile all Java files and package them into a runnable JAR:

1. `create_jar.bat` (Windows) or `create_jar.sh` (Linux/Mac) compiles all Java files in the project
2. The compiled classes are placed in the `build` directory
3. A JAR file is created using the `MANIFEST.MF` file, which specifies `MainFrame` as the main class
4. The resulting `ExerciseLauncher.jar` can be run directly to access all lab exercises

## Lab Exercises

- **Lab 3**: Network utility tools
  - **Hostname to IP Lookup**: Convert domain names to IP addresses
  - **DNS Records Lookup**: Query various DNS record types for domains (A, AAAA, MX, NS, etc.)
  - **Web Server Log Analyzer**: Analyze and extract insights from web server log files

- **Lab 4**: Basic network programming exercises
  - Placeholder for additional network programming exercises

- **Lab 5-10**: Additional network programming exercises
  - Each lab contains specific network programming implementations

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

3. **Running from source**:
   - Compile: `javac MainFrame.java`
   - Run: `java MainFrame`

## Web Server Log Analyzer (Lab 3)

The Web Server Log Analyzer in Lab 3 allows you to:

- Load and analyze web server log files (Apache Common, Apache Combined, Nginx formats)
- View logs in a structured table format
- Filter log entries by IP address, status code, URL, etc.
- View comprehensive statistics on server activity
- Export results to CSV format for further analysis

### Sample Log Files

The project includes a utility to generate sample log files for testing purposes. In the Web Server Log Analyzer, click the "Generate Sample" button to create sample log files for testing.

## Adding New Labs

To add a new lab to the project:

1. Create a new package for your lab (e.g., `lab11`)
2. Create a main class for your lab that extends `JFrame` (e.g., `Lab11.java`)
3. Add the import for your new lab class in `MainFrame.java`
4. Add a button for your lab in the `buttonPanel` in `MainFrame.java`
5. Add an action listener for the new button that creates and displays your lab class
6. Update the `create_jar.bat` and `create_jar.sh` scripts to include your new lab package

## License

This project is provided for educational purposes.

## Author

52100781_NguyenThanhDat