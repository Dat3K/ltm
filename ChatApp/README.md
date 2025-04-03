# Chat Application

A simple client-server chat application with TCP/UDP protocol support.

## Features

- Multi-client chat server
- Support for both TCP and UDP protocols
- Message persistence in PostgreSQL database
- User-friendly GUI for both client and server
- Real-time message transmission

## Design Patterns

This application uses several design patterns:

1. **Singleton Pattern**: For database connection management
2. **Factory Pattern**: For creating client and server instances based on protocol selection
3. **Strategy Pattern**: For implementing different communication protocols (TCP/UDP)

## Requirements

- Java 8 or higher
- Network connectivity between clients and server

## How to Run

### Using the Batch File (Windows)

1. Simply run the `build_jar.bat` file
2. The batch file will:
   - Download the PostgreSQL JDBC driver (v42.7.5)
   - Compile the application
   - Create a single JAR file with all dependencies included
   - Create a run script

3. Run the application using:
   ```
   java -jar ChatApp.jar
   ```
   or execute the `run.bat` file

### Manual Compilation

If you prefer to compile and run manually:

1. Download the PostgreSQL JDBC driver:
   ```
   mkdir lib
   # Download postgresql-42.7.5.jar to lib/
   ```

2. Compile the application:
   ```
   mkdir build
   javac -d build -cp "lib\postgresql-42.7.5.jar;src\main\java" src\main\java\com\chatapp\*.java src\main\java\com\chatapp\model\*.java src\main\java\com\chatapp\util\*.java src\main\java\com\chatapp\client\*.java src\main\java\com\chatapp\server\*.java src\main\java\com\chatapp\dao\*.java src\main\java\com\chatapp\gui\*.java
   ```

3. Create a fat JAR file:
   ```
   cd build
   jar xf ..\lib\postgresql-42.7.5.jar
   jar cfm ..\ChatApp.jar ..\manifest.txt com\chatapp\*.class com\chatapp\model\*.class com\chatapp\util\*.class com\chatapp\client\*.class com\chatapp\server\*.class com\chatapp\dao\*.class com\chatapp\gui\*.class org\postgresql\**\*.class
   jar uf ..\ChatApp.jar META-INF\**\*
   cd ..
   ```

4. Run the application:
   ```
   java -jar ChatApp.jar
   ```

## Usage

1. Start the application by running it
2. Choose "Start Server" to create a chat server or "Start Client" to connect to a server
3. For server:
   - Select TCP or UDP protocol
   - Enter a port number (default 8888)
   - Click "Start Server"
4. For client:
   - Enter server address/IP
   - Enter port number
   - Enter your username
   - Select the same protocol as the server
   - Click "Connect"
5. Start chatting!

## Database

The application uses a PostgreSQL database to store all messages with the following information:
- Message content
- Sender's username
- Sender's hostname
- Sender's IP address
- Timestamp 