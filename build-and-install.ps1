Write-Host "Building Adoption and Child Care App..." -ForegroundColor Green
Write-Host ""

Set-Location android

Write-Host "Building APK..." -ForegroundColor Yellow
& .\gradlew.bat assembleDebug

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "Build successful! Installing on device..." -ForegroundColor Green
    Write-Host ""
    
    & .\gradlew.bat installDebug
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host ""
        Write-Host "App installed successfully!" -ForegroundColor Green
        Write-Host "Launching app..." -ForegroundColor Yellow
        Write-Host ""
        
        & adb shell am start -n com.example.adoption_and_childcare/.MainActivity
        
        Write-Host ""
        Write-Host "App launched! Check your phone." -ForegroundColor Green
    }
    else {
        Write-Host ""
        Write-Host "Installation failed. Make sure:" -ForegroundColor Red
        Write-Host "1. Phone is connected via USB" -ForegroundColor White
        Write-Host "2. USB Debugging is enabled" -ForegroundColor White
        Write-Host "3. Allow USB debugging when prompted" -ForegroundColor White
    }
}
else {
    Write-Host ""
    Write-Host "Build failed. Please check the error messages above." -ForegroundColor Red
}

Set-Location ..
Write-Host ""
Write-Host "Process complete!" -ForegroundColor Green
