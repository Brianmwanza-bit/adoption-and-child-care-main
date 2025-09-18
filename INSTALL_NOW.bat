@echo off
echo.
echo ========================================
echo   INSTALLING ADOPTION APP ON YOUR PHONE
echo ========================================
echo.

echo Step 1: Building the app...
cd android
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Step 2: Installing on your phone...
    call gradlew.bat installDebug
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo Step 3: Launching the app...
        adb shell am start -n com.example.adoption_and_childcare/.MainActivity
        echo.
        echo ========================================
        echo   SUCCESS! APP IS NOW ON YOUR PHONE!
        echo ========================================
        echo.
        echo Look for "Adoption and Child Care" app icon
        echo The app should be running now!
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
