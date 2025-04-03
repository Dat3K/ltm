# Chat Application

A Java-based client-server chat application with a PostgreSQL database backend. This application allows multiple clients to connect to a central server and exchange messages, which are stored in the database for persistence.

## Features

- TCP-based client-server communication
- Multiple client support
- Message persistence using PostgreSQL database
- User information tracking (username, hostname, IP address)
- Timestamp recording for all messages
- Modern Java Swing UI for both client and server

## Architecture

The application follows a client-server architecture using the following design patterns:
- **Singleton Pattern**: Used for database connection management
- **Observer Pattern**: Used for client-server communication events
- **MVC Pattern**: Separation of UI, logic, and data models

## Prerequisites

- Java JDK 8 or later
- PostgreSQL JDBC Driver 42.7.5 (automatically downloaded and embedded in the JAR)

## Building the Project

### On Windows

Run the build script:

```batch
build.bat
```

### On Linux/macOS

Make the script executable and run it:

```bash
chmod +x build.sh
./build.sh
```

The script will:
1. Download the PostgreSQL JDBC driver 42.7.5 if needed
2. Compile the Java source files
3. Create a "fat JAR" file at `build/ChatApp.jar` with the driver embedded

## Running the Application

### Using the run scripts (Recommended)

```bash
# On Windows
run.bat

# On Linux/macOS
./run.sh
```

You can also specify the mode:

```bash
# On Windows
run.bat server

# On Linux/macOS
./run.sh server
```

### Running directly

```bash
# Run the JAR
java -jar build/ChatApp.jar

# Or with parameters
java -jar build/ChatApp.jar server
java -jar build/ChatApp.jar client
```

## Usage

1. Start the server application
2. Start one or more client applications
3. Enter the server host (default: localhost) and username in the login dialog
4. Start chatting!

## Database Configuration

The application is pre-configured to use the PostgreSQL database at:
```
jdbc:postgresql://dat3k-dat-3k.k.aivencloud.com:20132/defaultdb
```

Tables will be automatically created on first run. 