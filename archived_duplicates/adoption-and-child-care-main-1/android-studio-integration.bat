@echo off
REM Android Studio Integration Script for Cursor
REM This script sets up the project for seamless integration between Cursor and Android Studio

echo Setting up Android Studio integration for Cursor...

REM Check if Android SDK is installed
set ANDROID_SDK_PATH=%LOCALAPPDATA%\Android\Sdk
if not exist "%ANDROID_SDK_PATH%" (
    echo Android SDK not found at %ANDROID_SDK_PATH%
    echo Please install Android Studio and SDK first
    pause
    exit /b 1
)

REM Update local.properties with correct SDK path
echo sdk.dir=%ANDROID_SDK_PATH% > android\local.properties
echo Updated local.properties with SDK path

REM Create .idea directory if it doesn't exist
if not exist "android\.idea" (
    mkdir android\.idea
    echo Created .idea directory
)

REM Check Java installation
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java not found. Please install JDK 8 or higher
    pause
    exit /b 1
) else (
    echo Java found
)

REM Test Gradle build
echo Testing Gradle build...
cd android
call gradlew.bat --version
if %errorlevel% neq 0 (
    echo Gradle test failed. Please check your setup
    cd ..
    pause
    exit /b 1
)
cd ..

echo.
echo Integration setup complete!
echo You can now:
echo 1. Open Android Studio and import the 'android' folder
echo 2. Use Cursor for web development and project management
echo 3. Use Ctrl+Shift+P ^> Tasks: Run Task to run Android build tasks
echo 4. Use 'npx cap open android' to open Android Studio from Cursor
echo.
echo Setup complete! Happy coding!
pause
