@echo off
echo Building Chat Application JAR...

:: Create build directory
mkdir build 2>nul
mkdir lib 2>nul

:: Download PostgreSQL JDBC driver if not present
if not exist "lib\postgresql-42.7.5.jar" (
  echo Downloading PostgreSQL JDBC driver...
  powershell -Command "Invoke-WebRequest -Uri 'https://jdbc.postgresql.org/download/postgresql-42.7.5.jar' -OutFile 'lib\postgresql-42.7.5.jar'"
)

:: Compile the application
echo Compiling Java classes...
javac -d build -cp "lib\postgresql-42.7.5.jar;src\main\java" src\main\java\com\chatapp\*.java src\main\java\com\chatapp\model\*.java src\main\java\com\chatapp\util\*.java src\main\java\com\chatapp\client\*.java src\main\java\com\chatapp\server\*.java src\main\java\com\chatapp\dao\*.java src\main\java\com\chatapp\gui\*.java

if %ERRORLEVEL% NEQ 0 (
  echo Compilation failed.
  exit /b 1
)

:: Create a temporary directory for extraction
echo Creating temporary directory...
mkdir temp 2>nul

:: Extract the PostgreSQL driver JAR
echo Extracting PostgreSQL driver to include in the JAR...
cd temp
jar xf ..\lib\postgresql-42.7.5.jar
cd ..

:: Copy compiled classes to temp directory
echo Copying compiled classes to temp directory...
xcopy /E /Y build\com temp\com\

:: Create manifest file for fat JAR (no Class-Path needed)
echo Creating manifest file...
echo Main-Class: com.chatapp.ChatAppLauncher> manifest.txt

:: Create fat JAR file that includes all dependencies
echo Building fat JAR file...
cd temp
jar cfm ..\ChatApp.jar ..\manifest.txt .

:: Return to main directory
cd ..

:: Clean up
echo Cleaning up temporary files...
rmdir /S /Q temp

echo JAR build completed successfully!
echo The JAR file is located at ChatApp.jar

:: Create dist directory
if not exist "dist" mkdir dist
copy ChatApp.jar dist\

echo.
echo To run the application, use run.bat or execute "java -jar ChatApp.jar"
pause 