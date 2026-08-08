@echo off
echo Cleaning build directory...
rmdir /s /q build\classes\ecoride 2>nul
echo Building all Java files...
cd src
javac -d ..\build\classes ecoride\*.java
if %ERRORLEVEL% EQU 0 (
    echo Build successful!
    cd ..
    echo Running program...
    java -cp build\classes ecoride.Main
) else (
    echo Build failed!
    cd ..
)
pause
