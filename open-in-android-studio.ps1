# PowerShell script to open the project in Android Studio
Write-Host "Opening Adoption and Child Care App in Android Studio..." -ForegroundColor Green
Write-Host ""
Write-Host "Project Path: $PSScriptRoot" -ForegroundColor Yellow
Write-Host "GitHub Repository: https://github.com/Brianmwanza-bit/adoption-and-child-care-main.git" -ForegroundColor Yellow
Write-Host ""

# Try to find Android Studio installation
$androidStudioPaths = @(
    "C:\Program Files\Android\Android Studio\bin\studio64.exe",
    "C:\Users\$env:USERNAME\AppData\Local\JetBrains\Toolbox\apps\AndroidStudio\ch-0\*\bin\studio64.exe",
    "C:\Program Files\JetBrains\JetBrains Toolbox\apps\AndroidStudio\ch-0\*\bin\studio64.exe"
)

$androidStudioPath = $null
foreach ($path in $androidStudioPaths) {
    $resolvedPaths = Get-ChildItem -Path $path -ErrorAction SilentlyContinue
    if ($resolvedPaths) {
        $androidStudioPath = $resolvedPaths[0].FullName
        break
    }
}

if (-not $androidStudioPath) {
    Write-Host "Android Studio not found in default locations." -ForegroundColor Red
    Write-Host "Please install Android Studio or update the path in this script." -ForegroundColor Red
    Write-Host ""
    Write-Host "Default installation paths checked:" -ForegroundColor Yellow
    foreach ($path in $androidStudioPaths) {
        Write-Host "- $path" -ForegroundColor Gray
    }
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Found Android Studio at: $androidStudioPath" -ForegroundColor Green
Write-Host ""

# Open the project in Android Studio
Write-Host "Starting Android Studio with project..." -ForegroundColor Green
Start-Process -FilePath $androidStudioPath -ArgumentList $PSScriptRoot

Write-Host ""
Write-Host "Android Studio should now be opening with your project." -ForegroundColor Green
Write-Host "If it doesn't open automatically, you can:" -ForegroundColor Yellow
Write-Host "1. Open Android Studio manually" -ForegroundColor Gray
Write-Host "2. Choose 'Open an existing project'" -ForegroundColor Gray
Write-Host "3. Navigate to: $PSScriptRoot" -ForegroundColor Gray
Write-Host ""
Read-Host "Press Enter to continue"
