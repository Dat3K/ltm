@echo off
REM Build script for Chat Application

REM Create directories if they don't exist
mkdir build 2>nul
mkdir lib 2>nul

REM Check if PostgreSQL JDBC driver exists
if not exist lib\postgresql-42.7.5.jar (
    echo PostgreSQL JDBC driver not found. Downloading...
    powershell -Command "Invoke-WebRequest -Uri 'https://jdbc.postgresql.org/download/postgresql-42.7.5.jar' -OutFile 'lib\postgresql-42.7.5.jar'"
)

REM Clean previous build
echo Cleaning previous build...
rmdir /s /q build\classes 2>nul
rmdir /s /q build\temp 2>nul
mkdir build\classes 2>nul
mkdir build\temp 2>nul

REM Compile Java files
echo Compiling Java files...
javac -d build\classes -cp "lib\postgresql-42.7.5.jar" src/main/java/com/chatapp/model/*.java src/main/java/com/chatapp/protocol/*.java src/main/java/com/chatapp/db/*.java src/main/java/com/chatapp/client/*.java src/main/java/com/chatapp/client/ui/*.java src/main/java/com/chatapp/server/*.java src/main/java/com/chatapp/server/ui/*.java src/main/java/com/chatapp/ui/*.java src/main/java/com/chatapp/*.java 

if %ERRORLEVEL% neq 0 (
    echo Compilation failed!
    exit /b %ERRORLEVEL%
)

REM Create manifest file
echo Manifest-Version: 1.0> build\temp\MANIFEST.MF
echo Main-Class: com.chatapp.Main>> build\temp\MANIFEST.MF
echo.>> build\temp\MANIFEST.MF

REM Extract PostgreSQL driver to temp directory
echo Extracting PostgreSQL driver...
cd build\temp
jar xf ..\..\lib\postgresql-42.7.5.jar
rmdir /s /q META-INF 2>nul
cd ..\..

REM Copy compiled classes to temp directory
echo Copying compiled classes...
xcopy /E /Y build\classes\* build\temp\

REM Create fat JAR file
echo Creating Fat JAR file...
cd build\temp
jar cfm ..\ChatApp.jar MANIFEST.MF .
cd ..\..

REM Clean up temp directory
rmdir /s /q build\temp

echo Done! Fat JAR file created at build\ChatApp.jar
echo.
echo To run: java -jar build\ChatApp.jar

REM Run the application if requested
if "%1"=="run" (
    echo Running application...
    java -jar build\ChatApp.jar %2
) 