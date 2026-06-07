#!/usr/bin/env pwsh

# Adoption & Child Care - Development Startup Script
# This script prepares the database and starts the backend server

param(
    [int]$Port = 3306,
    [string]$DBHost = "localhost"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Adoption & Child Care - Dev Environment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check MySQL connectivity
Write-Host "Checking MySQL connection on $DBHost`:$Port..." -ForegroundColor Yellow
$mysql_check = Test-NetConnection -ComputerName $DBHost -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue

if ($mysql_check) {
    Write-Host "[OK] MySQL is running on port $Port" -ForegroundColor Green
} else {
    Write-Host "[ERROR] MySQL not found on port $Port" -ForegroundColor Red
    Write-Host ""
    Write-Host "Available ports:" -ForegroundColor Yellow
    netstat -ano | findstr "LISTENING" | findstr ":330" | ForEach-Object {
        $parts = $_ -split '\s+'
        $port = ($parts[1] -split ':')[-1]
        Write-Host "  Port: $port"
    }
    Write-Host ""
    Write-Host "Please ensure XAMPP MySQL is running." -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "Setting up environment variables..." -ForegroundColor Yellow

# Create or update .env file
$env_content = @"
PORT=50000
DB_HOST=$DBHost
DB_PORT=$Port
DB_USER=root
DB_PASSWORD=
DB_NAME=adoption_and_childcare_tracking_system_db
JWT_SECRET=your_jwt_secret_key_here
NODE_ENV=development
"@

$env_file = Join-Path $PSScriptRoot ".env"
$env_content | Out-File -FilePath $env_file -Encoding UTF8 -NoNewline

Write-Host "[OK] .env file created at $env_file" -ForegroundColor Green

Write-Host ""
Write-Host "Database configuration:" -ForegroundColor Cyan
Write-Host "  Host: $DBHost" -ForegroundColor White
Write-Host "  Port: $Port" -ForegroundColor White
Write-Host "  User: root" -ForegroundColor White
Write-Host "  Database: adoption_and_childcare_tracking_system_db" -ForegroundColor White
Write-Host ""

# Check if database exists (if mysql command is available)
if (Get-Command mysql -ErrorAction SilentlyContinue) {
    Write-Host "Checking database..." -ForegroundColor Yellow
    try {
        $result = mysql -h $DBHost -P $Port -u root -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='adoption_and_childcare_tracking_system_db'" 2>&1
        if ($result -match "adoption_and_childcare_tracking_system_db") {
            Write-Host "[OK] Database exists" -ForegroundColor Green
        } else {
            Write-Host "[INFO] Database not found, will import on server start" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "[WARNING] Could not check database: $_" -ForegroundColor Yellow
    }
} else {
    Write-Host "[SKIP] Skipping manual database check: 'mysql' command not in PATH. Backend server will handle it." -ForegroundColor Gray
}

Write-Host ""
Write-Host "Installing dependencies..." -ForegroundColor Yellow
if (Test-Path "package.json") {
    npm install 2>&1 | Out-Null
    Write-Host "[OK] Dependencies ready" -ForegroundColor Green
} else {
    Write-Host "[ERROR] package.json not found" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Backend Server..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Backend Server:" -ForegroundColor Green
Write-Host "  URL: http://localhost:50000" -ForegroundColor White
Write-Host "  Database: adoption_and_childcare_tracking_system_db" -ForegroundColor White
Write-Host "  MySQL: $DBHost`:$Port" -ForegroundColor White
Write-Host "  Status: Starting..." -ForegroundColor White
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Open phpMyAdmin in the default browser
$db_url = "http://localhost/phpmyadmin/index.php?route=/database/structure&db=adoption_and_childcare_tracking_system_db"
Write-Host "Opening phpMyAdmin: $db_url" -ForegroundColor Yellow
Start-Process $db_url

# Start the backend server
node server.js
