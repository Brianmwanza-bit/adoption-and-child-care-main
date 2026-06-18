#!/usr/bin/env pwsh

# Adoption & Child Care - Development Startup Script
# This script starts XAMPP services and the backend server

param(
    [int]$Port = 3306,
    [string]$DBHost = "127.0.0.1"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Adoption & Child Care - Dev Environment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

# 1. Start XAMPP Services (Apache & MySQL)
Write-Host "`n[1/3] Starting XAMPP Services..." -ForegroundColor Yellow
$xamppPath = "C:\xampp"

if (Test-Path "$xamppPath\mysql_start.bat") {
    Start-Process "$xamppPath\mysql_start.bat" -WindowStyle Hidden
    Start-Process "$xamppPath\apache_start.bat" -WindowStyle Hidden
    Write-Host "[OK] XAMPP MySQL and Apache start commands sent." -ForegroundColor Green
} else {
    Write-Host "[WARN] XAMPP not found at $xamppPath. Please start MySQL/Apache manually." -ForegroundColor Red
}

# 2. Network Detection (For Remote Phone Sync)
Write-Host "`n[2/3] Detecting Network for Mobile Sync..." -ForegroundColor Yellow
$ip = (Get-NetIPAddress -AddressFamily IPv4 | Where-Object { $_.InterfaceAlias -notlike "*Loopback*" -and $_.IPv4Address -notlike "169.254.*" } | Select-Object -First 1).IPv4Address

if ($ip) {
    Write-Host "MOBILE SYNC CONFIGURATION:" -ForegroundColor Cyan
    Write-Host "   On your phone/different device, set API URL to:" -ForegroundColor White
    Write-Host "   http://$ip:50000/" -ForegroundColor Green

    # Pre-set for Android Studio builds
    $gradleProps = Join-Path $PSScriptRoot "..\gradle.properties"
    if (Test-Path $gradleProps) {
        $content = Get-Content $gradleProps | Where-Object { $_ -notmatch "HOST_IP=" }
        $content += "HOST_IP=$ip"
        $content | Out-File -FilePath $gradleProps -Encoding UTF8
        Write-Host "   [OK] Pre-set HOST_IP in gradle.properties for Phone Deployment." -ForegroundColor Gray
    }
    Write-Host ""
}

# 3. Handle phpMyAdmin
$apache_port = 80
if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { $apache_port = 8080 }
$pma_url = if ($apache_port -eq 80) { "http://127.0.0.1/phpmyadmin" } else { "http://127.0.0.1:$apache_port/phpmyadmin" }
Write-Host "[OK] Opening phpMyAdmin: $pma_url" -ForegroundColor Yellow
Start-Process $pma_url

# 4. Environment Setup
Write-Host "`n[3/3] Preparing Backend Environment..." -ForegroundColor Yellow

# Kill any existing process on port 50000
$oldProcess = Get-NetTCPConnection -LocalPort 50000 -ErrorAction SilentlyContinue
if ($oldProcess) {
    Stop-Process -Id $oldProcess.OwningProcess -Force -ErrorAction SilentlyContinue
}

# Ensure .env is correct (using standard string to avoid heredoc issues)
$env_lines = "PORT=50000",
             "DB_HOST=127.0.0.1",
             "DB_PORT=3306",
             "DB_USER=root",
             "DB_PASSWORD=",
             "DB_NAME=adoption_and_childcare_tracking_system_db",
             "JWT_SECRET=your_jwt_secret_key_here",
             "NODE_ENV=development"

$env_path = Join-Path $PSScriptRoot ".env"
$env_lines -join "`n" | Out-File -FilePath $env_path -Encoding UTF8

Write-Host "[OK] Starting Backend Server..." -ForegroundColor Green
node server.js
