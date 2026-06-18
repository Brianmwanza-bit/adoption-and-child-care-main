@echo off
echo ====================================
echo Fixing Gradle Offline Mode Issue
echo ====================================
echo.

echo Step 1: Cleaning previous build...
call gradlew clean
echo.

echo Step 2: Refreshing dependencies and building...
call gradlew --refresh-dependencies assembleDebug
echo.

echo ====================================
echo Build Complete!
echo ====================================
echo.
echo If you still see errors, please:
echo 1. Open Android Studio
echo 2. Go to File ^> Settings ^> Build, Execution, Deployment ^> Gradle
echo 3. Uncheck "Offline work"
echo 4. Click "Sync Project with Gradle Files"
echo.
pause
