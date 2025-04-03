#!/bin/bash
# Build script for Chat Application

# Create directories if they don't exist
mkdir -p build
mkdir -p lib

# Check if PostgreSQL JDBC driver exists
if [ ! -f "lib/postgresql-42.7.5.jar" ]; then
    echo "PostgreSQL JDBC driver not found. Downloading..."
    if command -v wget &> /dev/null; then
        wget -O lib/postgresql-42.7.5.jar https://jdbc.postgresql.org/download/postgresql-42.7.5.jar
    elif command -v curl &> /dev/null; then
        curl -o lib/postgresql-42.7.5.jar https://jdbc.postgresql.org/download/postgresql-42.7.5.jar
    else
        echo "Neither wget nor curl found. Please download the PostgreSQL JDBC driver manually."
        exit 1
    fi
fi

# Clean previous build
echo "Cleaning previous build..."
rm -rf build/classes
rm -rf build/temp
mkdir -p build/classes
mkdir -p build/temp

# Compile Java files
echo "Compiling Java files..."
javac -d build/classes -cp "lib/postgresql-42.7.5.jar" src/main/java/com/chatapp/model/*.java src/main/java/com/chatapp/protocol/*.java src/main/java/com/chatapp/db/*.java src/main/java/com/chatapp/client/*.java src/main/java/com/chatapp/client/ui/*.java src/main/java/com/chatapp/server/*.java src/main/java/com/chatapp/server/ui/*.java src/main/java/com/chatapp/ui/*.java src/main/java/com/chatapp/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Create manifest file
echo "Manifest-Version: 1.0" > build/temp/MANIFEST.MF
echo "Main-Class: com.chatapp.Main" >> build/temp/MANIFEST.MF
echo "" >> build/temp/MANIFEST.MF

# Extract PostgreSQL driver to temp directory
echo "Extracting PostgreSQL driver..."
cd build/temp
jar xf ../../lib/postgresql-42.7.5.jar
rm -rf META-INF
cd ../..

# Copy compiled classes to temp directory
echo "Copying compiled classes..."
cp -r build/classes/* build/temp/

# Create fat JAR file
echo "Creating Fat JAR file..."
cd build/temp
jar cfm ../ChatApp.jar MANIFEST.MF .
cd ../..

# Clean up temp directory
rm -rf build/temp

echo "Done! Fat JAR file created at build/ChatApp.jar"
echo ""
echo "To run: java -jar build/ChatApp.jar"

# Run the application if requested
if [ "$1" = "run" ]; then
    echo "Running application..."
    java -jar build/ChatApp.jar $2
fi 