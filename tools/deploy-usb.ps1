Param(
    [string]$PackageName = 'com.adoptionapp',
    [string]$MainActivity = 'com.adoptionapp.MainActivity',
    [switch]$Clean
)

$ErrorActionPreference = 'Stop'
function Resolve-AdbPath {
    $repoAdb = Join-Path (Get-Location) 'android_sdk\platform-tools\adb.exe'
    if (Test-Path $repoAdb) { return $repoAdb }
    $local = Join-Path $env:LOCALAPPDATA 'Android\Sdk\platform-tools\adb.exe'
    if (Test-Path $local) { return $local }
    try { return (Get-Command adb -ErrorAction Stop).Source } catch { }
    throw "adb not found. Install Android SDK platform-tools or add adb to PATH."
}

function Invoke-Adb([string]$ArgsLine) {
    & $Global:ADB @($ArgsLine.Split(' ')) 2>&1 | Out-String | Write-Output
}

Write-Host "[deploy-usb] Starting deployment..."
$Global:ADB = Resolve-AdbPath
Write-Host "[deploy-usb] Using adb: $ADB"

Invoke-Adb 'kill-server'
Invoke-Adb 'start-server'
$devices = & $ADB devices
Write-Host $devices
if (-not ($devices -match "\bdevice\b" -and -not ($devices -match "unauthorized"))) {
    throw "No authorized device found. Ensure USB debugging is enabled and authorize the PC."
}

# Build APK
if (Test-Path .\gradlew.bat) {
    if ($Clean) { .\gradlew.bat clean }
    .\gradlew.bat :app:assembleDebug
} elseif (Test-Path .\gradlew) {
    if ($Clean) { ./gradlew clean }
    ./gradlew :app:assembleDebug
} else {
    throw "Gradle wrapper not found in project root."
}

$apk = Join-Path (Get-Location) 'app\build\outputs\apk\debug\app-debug.apk'
if (-not (Test-Path $apk)) { throw "APK not found at $apk" }

Write-Host "[deploy-usb] Installing $apk"
Invoke-Adb "install -r `"$apk`""

Write-Host "[deploy-usb] Launching $PackageName/$MainActivity"
$launch = & $ADB shell am start -n "$PackageName/$MainActivity" 2>&1
Write-Host $launch
if ($LASTEXITCODE -ne 0 -or ($launch -match 'Error:')) {
    Write-Host "[deploy-usb] Launch failed, attempting to resolve default activity..."
    $resolved = & $ADB shell cmd package resolve-activity --brief $PackageName 2>&1
    Write-Host $resolved
    if ($resolved -match '/') {
        $comp = ($resolved | Select-String '/').ToString().Trim()
        Write-Host "[deploy-usb] Trying $comp"
        & $ADB shell am start -n $comp | Out-String | Write-Host
    } else {
        Write-Warning "Could not auto-resolve main activity. Verify package/activity names."
    }
}

Write-Host "[deploy-usb] Done."



