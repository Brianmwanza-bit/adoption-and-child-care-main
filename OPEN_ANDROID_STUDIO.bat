@echo off
echo Opening Android Studio with your project...
echo.

REM Try to find Android Studio installation
set "STUDIO_PATH="

REM Common Android Studio installation paths
if exist "C:\Program Files\Android\Android Studio\bin\studio64.exe" (
    set "STUDIO_PATH=C:\Program Files\Android\Android Studio\bin\studio64.exe"
)
if exist "C:\Program Files (x86)\Android\Android Studio\bin\studio64.exe" (
    set "STUDIO_PATH=C:\Program Files (x86)\Android\Android Studio\bin\studio64.exe"
)
if exist "%LOCALAPPDATA%\Android\Studio\bin\studio64.exe" (
    set "STUDIO_PATH=%LOCALAPPDATA%\Android\Studio\bin\studio64.exe"
)

if "%STUDIO_PATH%"=="" (
    echo Android Studio not found in common locations.
    echo Please open Android Studio manually and:
    echo 1. Click "Open an existing project"
    echo 2. Navigate to: %CD%\android
    echo 3. Click OK
    echo.
    echo Then:
    echo 1. Connect your phone via USB
    echo 2. Enable USB Debugging
    echo 3. Click the green Run button
    pause
    exit /b 1
)

echo Found Android Studio at: %STUDIO_PATH%
echo Opening project: %CD%\android
echo.

start "" "%STUDIO_PATH%" "%CD%\android"

echo Android Studio is opening with your project!
echo.
echo Next steps:
echo 1. Wait for Gradle sync to complete
echo 2. Connect your phone via USB
echo 3. Enable USB Debugging on your phone
echo 4. Click the green Run button (▶️)
echo 5. Select your phone and click OK
echo.
echo Your app will be installed and launched automatically!
echo.
pause
