# Quick Android Studio Setup Script
# This script helps you set up Android Studio for app installation

Write-Host "Quick Android Studio Setup for Adoption & Child Care App" -ForegroundColor Green
Write-Host "=========================================================" -ForegroundColor Green

Write-Host "`nStep 1: Download Android Studio" -ForegroundColor Yellow
Write-Host "Visit: https://developer.android.com/studio" -ForegroundColor White
Write-Host "Download and install Android Studio" -ForegroundColor White

Write-Host "`nStep 2: Open Project in Android Studio" -ForegroundColor Yellow
Write-Host "1. Open Android Studio" -ForegroundColor White
Write-Host "2. Click 'Open an existing project'" -ForegroundColor White
Write-Host "3. Navigate to: $PWD\android" -ForegroundColor White
Write-Host "4. Select the android folder and click 'OK'" -ForegroundColor White

Write-Host "`nStep 3: Enable USB Debugging on Your Phone" -ForegroundColor Yellow
Write-Host "1. Go to Settings > About Phone" -ForegroundColor White
Write-Host "2. Tap 'Build Number' 7 times" -ForegroundColor White
Write-Host "3. Go to Settings > Developer Options" -ForegroundColor White
Write-Host "4. Enable 'USB Debugging'" -ForegroundColor White
Write-Host "5. Connect phone via USB cable" -ForegroundColor White

Write-Host "`nStep 4: Install App" -ForegroundColor Yellow
Write-Host "1. Wait for Gradle sync to complete" -ForegroundColor White
Write-Host "2. Click the green 'Run' button (play icon)" -ForegroundColor White
Write-Host "3. Select your phone from the device list" -ForegroundColor White
Write-Host "4. Click 'OK' to install" -ForegroundColor White

Write-Host "`nStep 5: Start Backend Server" -ForegroundColor Yellow
Write-Host "Open a new terminal and run:" -ForegroundColor White
Write-Host "cd backend" -ForegroundColor Cyan
Write-Host "npm start" -ForegroundColor Cyan

Write-Host "`nApp Features After Installation:" -ForegroundColor Green
Write-Host "✅ No login required - dashboard appears immediately" -ForegroundColor White
Write-Host "✅ Connected to PC database" -ForegroundColor White
Write-Host "✅ All functions accessible" -ForegroundColor White
Write-Host "✅ Real data from PC database" -ForegroundColor White

Write-Host "`nTroubleshooting:" -ForegroundColor Yellow
Write-Host "If build fails:" -ForegroundColor White
Write-Host "- Check internet connection for Gradle downloads" -ForegroundColor White
Write-Host "- Ensure Java JDK is installed" -ForegroundColor White
Write-Host "- Try 'File > Invalidate Caches and Restart'" -ForegroundColor White

Write-Host "`nIf phone not detected:" -ForegroundColor White
Write-Host "- Check USB cable quality" -ForegroundColor White
Write-Host "- Enable 'Install from unknown sources'" -ForegroundColor White
Write-Host "- Allow USB debugging when prompted" -ForegroundColor White

Write-Host "`nYour app is ready to be installed!" -ForegroundColor Green
Write-Host "Follow the steps above to get it running on your phone." -ForegroundColor White
