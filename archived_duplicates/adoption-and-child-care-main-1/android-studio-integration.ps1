# Android Studio Integration Script for Cursor
# This script sets up the project for seamless integration between Cursor and Android Studio

Write-Host "Setting up Android Studio integration for Cursor..." -ForegroundColor Green

# Check if Android SDK is installed
$androidSdkPath = "$env:LOCALAPPDATA\Android\Sdk"
if (-not (Test-Path $androidSdkPath)) {
    Write-Host "Android SDK not found at $androidSdkPath" -ForegroundColor Red
    Write-Host "Please install Android Studio and SDK first" -ForegroundColor Yellow
    exit 1
}

# Update local.properties with correct SDK path
$localPropertiesPath = "android\local.properties"
$sdkDir = "sdk.dir=$androidSdkPath"
Set-Content -Path $localPropertiesPath -Value $sdkDir -Force
Write-Host "Updated local.properties with SDK path" -ForegroundColor Green

# Create .idea directory if it doesn't exist
$ideaDir = "android\.idea"
if (-not (Test-Path $ideaDir)) {
    New-Item -ItemType Directory -Path $ideaDir -Force
    Write-Host "Created .idea directory" -ForegroundColor Green
}

# Set up Gradle wrapper permissions
$gradlewPath = "android\gradlew.bat"
if (Test-Path $gradlewPath) {
    Write-Host "Gradle wrapper found" -ForegroundColor Green
} else {
    Write-Host "Gradle wrapper not found. Please run 'gradle wrapper' in android directory" -ForegroundColor Yellow
}

# Check Java installation
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    Write-Host "Java found: $javaVersion" -ForegroundColor Green
} catch {
    Write-Host "Java not found. Please install JDK 8 or higher" -ForegroundColor Red
}

# Create workspace configuration
$workspaceConfig = @"
{
    "folders": [
        {
            "path": "."
        }
    ],
    "settings": {
        "java.configuration.updateBuildConfiguration": "automatic",
        "kotlin.languageServer.enabled": true,
        "android.sdk.path": "$androidSdkPath"
    }
}
"@

Set-Content -Path ".vscode\workspace.json" -Value $workspaceConfig -Force
Write-Host "Created workspace configuration" -ForegroundColor Green

# Install recommended extensions
Write-Host "Recommended extensions for Android development:" -ForegroundColor Yellow
Write-Host "- Kotlin Language" -ForegroundColor Cyan
Write-Host "- Extension Pack for Java" -ForegroundColor Cyan
Write-Host "- Gradle for Java" -ForegroundColor Cyan
Write-Host "- Android iOS Emulator" -ForegroundColor Cyan

Write-Host "`nIntegration setup complete!" -ForegroundColor Green
Write-Host "You can now:" -ForegroundColor Yellow
Write-Host "1. Open Android Studio and import the 'android' folder" -ForegroundColor White
Write-Host "2. Use Cursor for web development and project management" -ForegroundColor White
Write-Host "3. Use 'Ctrl+Shift+P' > 'Tasks: Run Task' to run Android build tasks" -ForegroundColor White
Write-Host "4. Use 'npx cap open android' to open Android Studio from Cursor" -ForegroundColor White

# Test Gradle build
Write-Host "`nTesting Gradle build..." -ForegroundColor Yellow
Set-Location android
try {
    .\gradlew.bat --version
    Write-Host "Gradle is working correctly" -ForegroundColor Green
} catch {
    Write-Host "Gradle test failed. Please check your setup" -ForegroundColor Red
}
Set-Location ..

Write-Host "`nSetup complete! Happy coding!" -ForegroundColor Green
