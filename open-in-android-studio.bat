@echo off
echo Opening Adoption and Child Care App in Android Studio...
echo.
echo Project Path: %~dp0
echo GitHub Repository: https://github.com/Brianmwanza-bit/adoption-and-child-care-main.git
echo.

REM Try to find Android Studio installation
set "ANDROID_STUDIO_PATH="
if exist "C:\Program Files\Android\Android Studio\bin\studio64.exe" (
    set "ANDROID_STUDIO_PATH=C:\Program Files\Android\Android Studio\bin\studio64.exe"
) else if exist "C:\Users\%USERNAME%\AppData\Local\Android\Sdk\tools\android.bat" (
    set "ANDROID_STUDIO_PATH=C:\Users\%USERNAME%\AppData\Local\Android\Sdk\tools\android.bat"
) else (
    echo Android Studio not found in default locations.
    echo Please install Android Studio or update the path in this script.
    echo.
    echo Default installation paths checked:
    echo - C:\Program Files\Android\Android Studio\bin\studio64.exe
    echo - C:\Users\%USERNAME%\AppData\Local\Android\Sdk\tools\android.bat
    echo.
    pause
    exit /b 1
)

echo Found Android Studio at: %ANDROID_STUDIO_PATH%
echo.

REM Open the project in Android Studio
echo Starting Android Studio with project...
start "" "%ANDROID_STUDIO_PATH%" "%~dp0"

echo.
echo Android Studio should now be opening with your project.
echo If it doesn't open automatically, you can:
echo 1. Open Android Studio manually
echo 2. Choose "Open an existing project"
echo 3. Navigate to: %~dp0
echo.
pause
