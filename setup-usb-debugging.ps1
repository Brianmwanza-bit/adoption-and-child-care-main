# USB Debugging Setup Script for Android Development
Write-Host "Setting up USB debugging for Android development..." -ForegroundColor Green

# Check if ADB is available
$adbPath = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adbPath) {
    Write-Host "ADB found at: $adbPath" -ForegroundColor Green
    
    # Start ADB server
    Write-Host "Starting ADB server..." -ForegroundColor Yellow
    & $adbPath start-server
    
    # Check for connected devices
    Write-Host "Checking for connected devices..." -ForegroundColor Yellow
    $devices = & $adbPath devices
    Write-Host $devices -ForegroundColor Cyan
    
    if ($devices -match "device$") {
        Write-Host "Device found and ready for debugging!" -ForegroundColor Green
        Write-Host "You can now run: npx cap run android" -ForegroundColor Yellow
    } else {
        Write-Host "No devices found. Please:" -ForegroundColor Red
        Write-Host "1. Enable Developer Options on your Android device" -ForegroundColor White
        Write-Host "2. Enable USB Debugging in Developer Options" -ForegroundColor White
        Write-Host "3. Connect your device via USB" -ForegroundColor White
        Write-Host "4. Allow USB debugging when prompted" -ForegroundColor White
    }
} else {
    Write-Host "ADB not found. Please install Android SDK first." -ForegroundColor Red
    Write-Host "Download Android Studio from: https://developer.android.com/studio" -ForegroundColor Yellow
}

Write-Host "`nUSB Debugging setup complete!" -ForegroundColor Green
