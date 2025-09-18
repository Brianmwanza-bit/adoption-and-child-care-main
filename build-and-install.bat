@echo off
echo Building Adoption and Child Care App...
echo.

cd android
echo Building APK...
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build successful! Installing on device...
    echo.
    call gradlew.bat installDebug
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo App installed successfully!
        echo Launching app...
        echo.
        adb shell am start -n com.example.adoption_and_childcare/.MainActivity
        echo.
        echo App launched! Check your phone.
    ) else (
        echo.
        echo Installation failed. Make sure:
        echo 1. Phone is connected via USB
        echo 2. USB Debugging is enabled
        echo 3. Allow USB debugging when prompted
    )
) else (
    echo.
    echo Build failed. Please check the error messages above.
)

echo.
echo Press any key to exit...
pause > nul
