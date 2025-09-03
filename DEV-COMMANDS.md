# Development Commands Reference
# Copy and paste these commands in Cursor terminal

Write-Host "  DEVELOPMENT COMMANDS REFERENCE" -ForegroundColor Green
Write-Host "=================================" -ForegroundColor Green
Write-Host ""
Write-Host " QUICK DEPLOY (Build + Install + Push to GitHub):" -ForegroundColor Yellow
Write-Host ".\quick-deploy.ps1 `"Your commit message`"" -ForegroundColor White
Write-Host ""
Write-Host " BUILD ONLY:" -ForegroundColor Yellow
Write-Host "cd android" -ForegroundColor White
Write-Host ".\gradlew.bat assembleDebug" -ForegroundColor White
Write-Host ""
Write-Host " INSTALL ON PHONE:" -ForegroundColor Yellow
Write-Host "cd android" -ForegroundColor White
Write-Host ".\gradlew.bat installDebug" -ForegroundColor White
Write-Host ""
Write-Host " CHECK DEVICE:" -ForegroundColor Yellow
Write-Host "adb devices" -ForegroundColor White
Write-Host ""
Write-Host " GIT OPERATIONS:" -ForegroundColor Yellow
Write-Host "git status" -ForegroundColor White
Write-Host "git add ." -ForegroundColor White
Write-Host "git commit -m `"Your message`"" -ForegroundColor White
Write-Host "git push origin main" -ForegroundColor White
Write-Host ""
Write-Host " ENVIRONMENT SETUP:" -ForegroundColor Yellow
Write-Host '$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"' -ForegroundColor White
Write-Host '$env:ANDROID_HOME = "C:\Users\ADMIN\AppData\Local\Android\Sdk"' -ForegroundColor White
Write-Host '$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"' -ForegroundColor White
Write-Host ""
Write-Host " PROJECT STRUCTURE:" -ForegroundColor Yellow
Write-Host " android/app/src/main/java/com/yourdomain/adoptionchildcare/" -ForegroundColor White
Write-Host "    MainActivity.kt (Main app code)" -ForegroundColor Gray
Write-Host " android/app/src/main/res/layout/" -ForegroundColor White
Write-Host "    activity_main.xml (Main screen)" -ForegroundColor Gray
Write-Host "    login_dialog.xml (Login popup)" -ForegroundColor Gray
Write-Host "    register_dialog.xml (Register popup)" -ForegroundColor Gray
Write-Host " android/app/build/outputs/apk/debug/" -ForegroundColor White
Write-Host "    app-debug.apk (Built APK file)" -ForegroundColor Gray
Write-Host ""
Write-Host " WORKFLOW:" -ForegroundColor Magenta
Write-Host "1. Edit code in Cursor" -ForegroundColor White
Write-Host "2. Run: .\quick-deploy.ps1 `"Your changes`"" -ForegroundColor White
Write-Host "3. Test on phone" -ForegroundColor White
Write-Host "4. Repeat!" -ForegroundColor White
