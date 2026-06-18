Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Fixing Gradle Offline Mode Issue" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 1: Cleaning previous build..." -ForegroundColor Yellow
.\gradlew clean
Write-Host ""

Write-Host "Step 2: Refreshing dependencies and building..." -ForegroundColor Yellow
.\gradlew --refresh-dependencies assembleDebug
Write-Host ""

Write-Host "====================================" -ForegroundColor Green
Write-Host "Build Complete!" -ForegroundColor Green
Write-Host "====================================" -ForegroundColor Green
Write-Host ""
Write-Host "If you still see errors, please:" -ForegroundColor Yellow
Write-Host "1. Open Android Studio"
Write-Host "2. Go to File > Settings > Build, Execution, Deployment > Gradle"
Write-Host "3. Uncheck 'Offline work'"
Write-Host "4. Click 'Sync Project with Gradle Files'"
Write-Host ""
Read-Host "Press Enter to exit"
