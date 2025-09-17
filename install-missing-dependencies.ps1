# Install Missing Dependencies for Adoption & Child Care Project
# This script helps install Node.js and Java if they're missing

Write-Host "Installing Missing Dependencies for Adoption & Child Care Project" -ForegroundColor Green
Write-Host "=================================================================" -ForegroundColor Green

# Check if Node.js is installed
$nodeInstalled = $false
try {
    $nodeVersion = node --version 2>$null
    if ($nodeVersion) {
        Write-Host "✅ Node.js is already installed: $nodeVersion" -ForegroundColor Green
        $nodeInstalled = $true
    }
}
catch {
    Write-Host "❌ Node.js not found" -ForegroundColor Red
}

# Check if Java is installed
$javaInstalled = $false
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "✅ Java is already installed: $javaVersion" -ForegroundColor Green
        $javaInstalled = $true
    }
}
catch {
    Write-Host "❌ Java not found" -ForegroundColor Red
}

# Check if npm is available
$npmInstalled = $false
try {
    $npmVersion = npm --version 2>$null
    if ($npmVersion) {
        Write-Host "✅ npm is already installed: $npmVersion" -ForegroundColor Green
        $npmInstalled = $true
    }
}
catch {
    Write-Host "❌ npm not found" -ForegroundColor Red
}

Write-Host "`nInstallation Instructions:" -ForegroundColor Yellow

if (-not $nodeInstalled) {
    Write-Host "`n📦 Install Node.js:" -ForegroundColor Cyan
    Write-Host "1. Visit: https://nodejs.org/" -ForegroundColor White
    Write-Host "2. Download the LTS version (recommended)" -ForegroundColor White
    Write-Host "3. Run the installer and follow the setup wizard" -ForegroundColor White
    Write-Host "4. Restart your terminal after installation" -ForegroundColor White
}

if (-not $javaInstalled) {
    Write-Host "`n☕ Install Java JDK:" -ForegroundColor Cyan
    Write-Host "1. Visit: https://adoptium.net/" -ForegroundColor White
    Write-Host "2. Download JDK 8 or higher" -ForegroundColor White
    Write-Host "3. Run the installer and follow the setup wizard" -ForegroundColor White
    Write-Host "4. Restart your terminal after installation" -ForegroundColor White
    Write-Host "`n   Alternative: Use the Java bundled with Android Studio" -ForegroundColor Gray
    Write-Host "   Location: C:\Users\BRIAN MWANZA\AppData\Local\Android\Sdk\jbr" -ForegroundColor Gray
}

Write-Host "`n🚀 After Installation:" -ForegroundColor Yellow
Write-Host "1. Restart your terminal/command prompt" -ForegroundColor White
Write-Host "2. Run this script again to verify installation" -ForegroundColor White
Write-Host "3. Open Cursor and install recommended extensions" -ForegroundColor White
Write-Host "4. Start developing your adoption app!" -ForegroundColor White

Write-Host "`n📋 Quick Commands After Installation:" -ForegroundColor Yellow
Write-Host "npm install                    # Install project dependencies" -ForegroundColor Cyan
Write-Host "npx cap sync android          # Sync web changes to Android" -ForegroundColor Cyan
Write-Host "npx cap open android          # Open Android Studio" -ForegroundColor Cyan
Write-Host "gradlew.bat assembleDebug     # Build Android APK" -ForegroundColor Cyan

Write-Host "`n🎯 Your project is ready for development!" -ForegroundColor Green
Write-Host "Follow the CURSOR_ANDROID_SETUP_COMPLETE.md guide for detailed instructions." -ForegroundColor White
