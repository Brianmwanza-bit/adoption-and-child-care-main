# Build APK Script for Adoption & Child Care App
# Builds debug APK for device installation

Write-Host "🔨 Building APK for Adoption & Child Care App" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

# Check if gradlew exists
$gradlew = ".\gradlew.bat"
if (-Not (Test-Path $gradlew)) {
    $gradlew = "gradlew.bat"
}

if (-Not (Test-Path $gradlew)) {
    Write-Host "❌ Error: gradlew.bat not found!" -ForegroundColor Red
    Write-Host "Please run this script from the project root directory." -ForegroundColor Yellow
    exit 1
}

Write-Host "`n📋 Build Configuration:" -ForegroundColor Cyan
Write-Host "Build Type: Debug" -ForegroundColor White
Write-Host "Output: app/build/outputs/apk/debug/" -ForegroundColor White

Write-Host "`n⚠️  This will take 2-5 minutes..." -ForegroundColor Yellow
Write-Host "Press any key to start build..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")

# Clean previous builds
Write-Host "`n🧹 Cleaning previous builds..." -ForegroundColor Cyan
& cmd /c "$gradlew clean" 2>&1 | Out-Host

# Build debug APK
Write-Host "`n🔨 Building debug APK..." -ForegroundColor Cyan
& cmd /c "$gradlew assembleDebug" 2>&1 | Out-Host

# Check if build was successful
$apkPath = "app\build\outputs\apk\debug\app-debug.apk"
if (Test-Path $apkPath) {
    $apkSize = (Get-Item $apkPath).Length / 1MB
    Write-Host "`n==============================================" -ForegroundColor Cyan
    Write-Host "✅ Build Successful!" -ForegroundColor Green
    Write-Host "==============================================" -ForegroundColor Cyan
    Write-Host "APK Location: $apkPath" -ForegroundColor White
    Write-Host "APK Size: $([math]::Round($apkSize, 2)) MB" -ForegroundColor White
    Write-Host "`n📱 To install on connected device:" -ForegroundColor Cyan
    Write-Host "  adb install -r $apkPath" -ForegroundColor White
    Write-Host "`n📱 To install on specific device:" -ForegroundColor Cyan
    Write-Host "  adb -s <device-id> install -r $apkPath" -ForegroundColor White
    
    # Ask if user wants to install
    Write-Host "`nWould you like to install on connected device? (Y/N)" -ForegroundColor Yellow
    $response = Read-Host
    
    if ($response -eq "Y" -or $response -eq "y") {
        Write-Host "`n📲 Installing APK..." -ForegroundColor Cyan
        & adb install -r $apkPath 2>&1 | Out-Host
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "`n✅ Installation successful!" -ForegroundColor Green
            Write-Host "Look for 'Adoption & Child Care' app on your device." -ForegroundColor White
        } else {
            Write-Host "`n❌ Installation failed." -ForegroundColor Red
            Write-Host "Make sure your device is connected and USB debugging is enabled." -ForegroundColor Yellow
        }
    }
} else {
    Write-Host "`n❌ Build failed! APK not found at: $apkPath" -ForegroundColor Red
    Write-Host "Check the build output above for errors." -ForegroundColor Yellow
}

Write-Host "`nPress any key to exit..." -ForegroundColor Yellow
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
