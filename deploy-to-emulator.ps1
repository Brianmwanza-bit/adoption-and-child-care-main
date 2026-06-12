# Deploy to Emulator - One-Click Script
# This script builds and installs the app on a running emulator

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  EMULATOR DEPLOYMENT SCRIPT" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# Check if emulator is running
Write-Host "Checking for running emulators..." -ForegroundColor Yellow
$emulators = adb devices | Select-String "emulator"

if ($emulators.Count -eq 0) {
    Write-Host "ERROR: No running emulator detected!" -ForegroundColor Red
    Write-Host "`nPlease start an emulator first:" -ForegroundColor Yellow
    Write-Host "1. Open Android Studio" -ForegroundColor White
    Write-Host "2. Go to Tools > Device Manager" -ForegroundColor White
    Write-Host "3. Click Play on your emulator" -ForegroundColor White
    Write-Host "`nThen run this script again.`n" -ForegroundColor White
    exit 1
}

Write-Host "✅ Emulator detected: $($emulators[0])`n" -ForegroundColor Green

# Check if backend is running
Write-Host "Checking backend server..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:50000/" -TimeoutSec 2 -ErrorAction Stop
    Write-Host "✅ Backend server is running on port 50000`n" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Backend server is NOT running on port 50000" -ForegroundColor Yellow
    Write-Host "`nYou can:" -ForegroundColor Yellow
    Write-Host "1. Start backend: cd backend && node server.js" -ForegroundColor White
    Write-Host "2. Or deploy app anyway (will use local DB only)`n" -ForegroundColor White
    
    $continue = Read-Host "Continue deployment anyway? (y/n)"
    if ($continue -ne "y") {
        Write-Host "Deployment cancelled.`n" -ForegroundColor Yellow
        exit 0
    }
}

# Clean and build
Write-Host "Building app..." -ForegroundColor Yellow
Set-Location "$PSScriptRoot\android"

Write-Host "Cleaning previous build..." -ForegroundColor Gray
.\gradlew clean --quiet

Write-Host "Compiling debug APK..." -ForegroundColor Gray
$buildResult = .\gradlew assembleDebug 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Build failed!" -ForegroundColor Red
    Write-Host "Check the errors above and fix them before deploying.`n" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Build successful!`n" -ForegroundColor Green

# Install on emulator
Write-Host "Installing app on emulator..." -ForegroundColor Yellow
$installResult = .\gradlew installDebug 2>&1

if ($LASTEXITCODE -ne 0) {
    Write-Host "`n❌ Installation failed!" -ForegroundColor Red
    Write-Host "Make sure emulator is running and try again.`n" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ App installed successfully!`n" -ForegroundColor Green

# Launch app
Write-Host "Launching app..." -ForegroundColor Yellow
adb shell am start -n "com.example.adoption_and_childcare/com.example.adoption_and_childcare.MainActivity" --activity-clear-task

Write-Host "`n========================================" -ForegroundColor Green
Write-Host "  DEPLOYMENT COMPLETE!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Green

Write-Host "Configuration Summary:" -ForegroundColor Cyan
Write-Host "  API URL: http://10.0.2.2:50000/" -ForegroundColor White
Write-Host "  Target: Emulator" -ForegroundColor White
Write-Host "  Status: Ready to use`n" -ForegroundColor White

Write-Host "Next Steps:" -ForegroundColor Cyan
Write-Host "  1. Check app loads on emulator" -ForegroundColor White
Write-Host "  2. Verify backend connectivity in Settings" -ForegroundColor White
Write-Host "  3. Test all screens`n" -ForegroundColor White

Write-Host "Troubleshooting:" -ForegroundColor Cyan
Write-Host "  - View logs: adb logcat | Select-String 'Retrofit|API'" -ForegroundColor White
Write-Host "  - Check connection: Test API URL in emulator browser" -ForegroundColor White
Write-Host "  - Restart app: adb shell am force-stop com.example.adoption_and_childcare`n" -ForegroundColor White

# Return to project root
Set-Location $PSScriptRoot
