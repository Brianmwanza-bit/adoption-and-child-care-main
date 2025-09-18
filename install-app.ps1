Write-Host "🚀 Installing Adoption & Child Care App on Your Phone" -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Green

# Check if ADB is available
$adbPath = "C:\Users\BRIAN MWANZA\AppData\Local\Android\Sdk\platform-tools\adb.exe"
if (Test-Path $adbPath) {
    Write-Host "✅ ADB found at: $adbPath" -ForegroundColor Green
}
else {
    Write-Host "❌ ADB not found. Please ensure Android SDK is installed." -ForegroundColor Red
    exit 1
}

# Check for connected devices
Write-Host "`n📱 Checking for connected devices..." -ForegroundColor Yellow
$devices = & $adbPath devices
Write-Host $devices

if ($devices -match "device$") {
    Write-Host "✅ Device connected!" -ForegroundColor Green
    
    # Install the APK
    Write-Host "`n📦 Installing APK..." -ForegroundColor Yellow
    $apkPath = "android\app\build\outputs\apk\debug\app-debug.apk"
    
    if (Test-Path $apkPath) {
        & $adbPath install -r $apkPath
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ App installed successfully!" -ForegroundColor Green
            
            # Launch the app
            Write-Host "`n🚀 Launching app..." -ForegroundColor Yellow
            & $adbPath shell am start -n com.example.adoption_and_childcare/.MainActivity
            
            Write-Host "`n🎉 App launched! Check your phone!" -ForegroundColor Green
            Write-Host "Look for 'Adoption and Child Care' app icon." -ForegroundColor White
        }
        else {
            Write-Host "❌ Installation failed. Check your phone for prompts." -ForegroundColor Red
        }
    }
    else {
        Write-Host "❌ APK not found. Building first..." -ForegroundColor Yellow
        Write-Host "Please run the build process first." -ForegroundColor White
    }
}
else {
    Write-Host "❌ No device connected or USB debugging not enabled." -ForegroundColor Red
    Write-Host "`nPlease:" -ForegroundColor Yellow
    Write-Host "1. Connect your phone via USB" -ForegroundColor White
    Write-Host "2. Enable USB Debugging in Developer Options" -ForegroundColor White
    Write-Host "3. Allow USB debugging when prompted" -ForegroundColor White
}

Write-Host "`nPress any key to continue..." -ForegroundColor Gray
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
