@echo off
echo Creating Runnable JAR for Exercise Launcher

rem Create build directory
if not exist build mkdir build

rem Compile all Java files
javac -encoding UTF-8 -d build *.java lab1\*.java lab2\*.java lab3\*.java lab4\*.java lab5\*.java lab6\*.java lab7\*.java lab8\*.java lab9\*.java lab10\*.java

rem Create JAR with manifest
jar cfm ExerciseLauncher.jar MANIFEST.MF -C build .

echo.
echo JAR file created: ExerciseLauncher.jar
echo.
echo You can run the application with: java -jar ExerciseLauncher.jar
