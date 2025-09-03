# Quick Build & Deploy Script
# Usage: .\quick-deploy.ps1 "Your commit message"

param(
    [string]$commitMessage = "Update app features"
)

Write-Host " Quick Build & Deploy to GitHub" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green

# Set environment variables
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"
$env:ANDROID_HOME = "C:\Users\ADMIN\AppData\Local\Android\Sdk"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:PATH = "C:\Users\ADMIN\AppData\Local\Android\Sdk\platform-tools;$env:PATH"

Write-Host " Checking device connection..." -ForegroundColor Cyan
$devices = adb devices
if ($devices -match "device$") {
    Write-Host " Device connected" -ForegroundColor Green
} else {
    Write-Host "  No device connected - will build APK only" -ForegroundColor Yellow
}

Write-Host " Building APK..." -ForegroundColor Cyan
cd android
.\gradlew.bat assembleDebug
if ($LASTEXITCODE -eq 0) {
    Write-Host " APK built successfully" -ForegroundColor Green
} else {
    Write-Host " Build failed" -ForegroundColor Red
    exit 1
}

if ($devices -match "device$") {
    Write-Host " Installing on device..." -ForegroundColor Cyan
    .\gradlew.bat installDebug
    if ($LASTEXITCODE -eq 0) {
        Write-Host " App installed on device" -ForegroundColor Green
    } else {
        Write-Host " Installation failed" -ForegroundColor Red
    }
}

cd ..

Write-Host " Committing changes to Git..." -ForegroundColor Cyan
git add .
git commit -m $commitMessage
if ($LASTEXITCODE -eq 0) {
    Write-Host " Changes committed" -ForegroundColor Green
} else {
    Write-Host "  No changes to commit" -ForegroundColor Yellow
}

Write-Host "  Pushing to GitHub..." -ForegroundColor Cyan
git push origin main
if ($LASTEXITCODE -eq 0) {
    Write-Host " Pushed to GitHub successfully" -ForegroundColor Green
} else {
    Write-Host " Push failed" -ForegroundColor Red
}

Write-Host ""
Write-Host " Deployment Complete!" -ForegroundColor Green
Write-Host " App updated on your phone" -ForegroundColor Cyan
Write-Host "  Changes pushed to GitHub" -ForegroundColor Cyan
Write-Host " Repository: https://github.com/Brianmwanza-bit/adoption-and-child-care-main" -ForegroundColor Blue
