# Database Import Script for Adoption & Child Care Tracking System
# This script imports the consolidated SQL file into a local MySQL/MariaDB server

$SQL_FILE = "adoption_and_childcare_tracking_system_db.sql"
$DB_NAME = "adoption_and_childcare_tracking_system_db"

Write-Host "--- Database Import Utility ---" -ForegroundColor Cyan
Write-Host "Target Database: $DB_NAME"
Write-Host "Source File: $SQL_FILE"

# Check if file exists
if (!(Test-Path $SQL_FILE)) {
    Write-Error "Error: $SQL_FILE not found in the current directory."
    exit 1
}

# Prompt for credentials
$Username = Read-Host -Prompt "Enter MySQL Username (default: root)"
if ([string]::IsNullOrWhiteSpace($Username)) { $Username = "root" }

$Password = Read-Host -Prompt "Enter MySQL Password (press Enter if none)" -AsSecureString

# Convert SecureString to plain text for CLI
$BSTR = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($Password)
$PlainPassword = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($BSTR)

Write-Host "`nImporting database... Please wait." -ForegroundColor Yellow

# Try to run mysql command
try {
    if ([string]::IsNullOrWhiteSpace($PlainPassword)) {
        & mysql -u $Username -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"
        Get-Content $SQL_FILE | & mysql -u $Username $DB_NAME
    } else {
        & mysql -u $Username -p$PlainPassword -e "CREATE DATABASE IF NOT EXISTS $DB_NAME;"
        Get-Content $SQL_FILE | & mysql -u $Username -p$PlainPassword $DB_NAME
    }

    if ($LASTEXITCODE -eq 0) {
        Write-Host "`nSuccess: Database imported successfully!" -ForegroundColor Green
    } else {
        Write-Host "`nError: MySQL import failed. Please ensure MySQL is in your PATH." -ForegroundColor Red
    }
} catch {
    Write-Host "`nError: Could not execute mysql command. Is XAMPP or MySQL installed and running?" -ForegroundColor Red
}

Write-Host "`nPress any key to exit..."
$null = [Console]::ReadKey()
