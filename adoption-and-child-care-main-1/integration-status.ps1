# Android Studio + Cursor Integration Script
# This script helps sync between Android Studio and Cursor

Write-Host " Android Studio + Cursor Integration" -ForegroundColor Green
Write-Host "=====================================" -ForegroundColor Green

# Check if both applications are running
$studio = Get-Process | Where-Object {$_.ProcessName -eq "studio64"}
$cursor = Get-Process | Where-Object {$_.ProcessName -eq "Cursor"}

if ($studio) {
    Write-Host " Android Studio: Running (PID: $($studio.Id))" -ForegroundColor Green
} else {
    Write-Host " Android Studio: Not running" -ForegroundColor Red
}

if ($cursor) {
    Write-Host " Cursor: Running (PID: $($cursor.Id))" -ForegroundColor Green
} else {
    Write-Host " Cursor: Not running" -ForegroundColor Red
}

Write-Host ""
Write-Host " Project Directory: $PWD" -ForegroundColor Cyan
Write-Host " APK Location: android\app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
Write-Host ""
Write-Host " Quick Commands:" -ForegroundColor Yellow
Write-Host "   Build APK: .\gradlew.bat assembleDebug" -ForegroundColor White
Write-Host "   Install APK: .\gradlew.bat installDebug" -ForegroundColor White
Write-Host "   Check devices: adb devices" -ForegroundColor White
Write-Host "   Git status: git status" -ForegroundColor White
Write-Host ""
Write-Host " Tips:" -ForegroundColor Magenta
Write-Host "   Use Cursor for code editing and AI assistance" -ForegroundColor White
Write-Host "   Use Android Studio for debugging and device management" -ForegroundColor White
Write-Host "   Both tools now share the same project directory" -ForegroundColor White
