# Build and Install Android App Script
# This script builds the updated app and installs it on connected devices

Write-Host "Building and Installing Adoption & Child Care App..." -ForegroundColor Green

# Check if Android SDK is available
Write-Host "Checking Android SDK..." -ForegroundColor Yellow
try {
    $adbPath = Get-Command adb -ErrorAction SilentlyContinue
    if ($adbPath) {
        Write-Host "ADB found at: $($adbPath.Source)" -ForegroundColor Green
    } else {
        Write-Host "ADB not found. Please ensure Android SDK is installed and in PATH." -ForegroundColor Red
        Write-Host "You can install Android Studio or set ANDROID_HOME environment variable." -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Error checking ADB: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Check for connected devices
Write-Host "Checking for connected devices..." -ForegroundColor Yellow
try {
    $devices = adb devices
    $connectedDevices = $devices | Where-Object { $_ -match "device$" }
    
    if ($connectedDevices.Count -eq 0) {
        Write-Host "No devices connected. Please connect your phone via USB and enable USB debugging." -ForegroundColor Red
        Write-Host "To enable USB debugging:" -ForegroundColor Yellow
        Write-Host "1. Go to Settings > About Phone > Tap Build Number 7 times" -ForegroundColor White
        Write-Host "2. Go to Settings > Developer Options > Enable USB Debugging" -ForegroundColor White
        Write-Host "3. Connect phone via USB and allow debugging when prompted" -ForegroundColor White
        exit 1
    } else {
        Write-Host "Found $($connectedDevices.Count) connected device(s):" -ForegroundColor Green
        foreach ($device in $connectedDevices) {
            Write-Host "  $device" -ForegroundColor White
        }
    }
} catch {
    Write-Host "Error checking devices: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Build the app
Write-Host "Building the app..." -ForegroundColor Yellow
try {
    Set-Location android
    Write-Host "Running Gradle build..." -ForegroundColor White
    
    # Clean and build
    ./gradlew clean
    ./gradlew assembleDebug
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Build successful!" -ForegroundColor Green
    } else {
        Write-Host "Build failed. Please check the error messages above." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error building app: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Install the app
Write-Host "Installing the app on connected devices..." -ForegroundColor Yellow
try {
    $apkPath = "app/build/outputs/apk/debug/app-debug.apk"
    
    if (Test-Path $apkPath) {
        Write-Host "APK found at: $apkPath" -ForegroundColor Green
        
        # Install on all connected devices
        adb install -r $apkPath
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "App installed successfully!" -ForegroundColor Green
            Write-Host "The app should now appear on your phone." -ForegroundColor White
            Write-Host "Look for 'AdoptionChildCare' in your app list." -ForegroundColor White
        } else {
            Write-Host "Installation failed. Please check the error messages above." -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "APK not found at: $apkPath" -ForegroundColor Red
        Write-Host "Build may have failed. Please check the build output above." -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "Error installing app: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Return to original directory
Set-Location ..

Write-Host "`nApp Installation Complete!" -ForegroundColor Green
Write-Host "Your phone app is now connected to the PC database." -ForegroundColor White
Write-Host "The app will show the dashboard without requiring login." -ForegroundColor White
Write-Host "All data will be synchronized with your PC database." -ForegroundColor White

Write-Host "`nNext Steps:" -ForegroundColor Cyan
Write-Host "1. Open the app on your phone" -ForegroundColor White
Write-Host "2. You should see the dashboard immediately" -ForegroundColor White
Write-Host "3. Test the 'Users Management' button to see data from PC" -ForegroundColor White
Write-Host "4. Make sure your PC backend server is running" -ForegroundColor White

Write-Host "`nTo start the backend server:" -ForegroundColor Yellow
Write-Host "cd backend && npm start" -ForegroundColor White
