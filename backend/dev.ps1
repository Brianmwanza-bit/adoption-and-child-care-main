#!/usr/bin/env pwsh

# Adoption & Child Care - Development Startup Script
# This script prepares the database and starts the backend server

param(
    [int]$Port = 3307,
    [string]$Host = "localhost"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Adoption & Child Care - Dev Environment" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check MySQL connectivity
Write-Host "Checking MySQL connection on $Host`:$Port..." -ForegroundColor Yellow
$mysql_check = Test-NetConnection -ComputerName $Host -Port $Port -InformationLevel Quiet -WarningAction SilentlyContinue

if ($mysql_check) {
    Write-Host "✓ MySQL is running on port $Port" -ForegroundColor Green
} else {
    Write-Host "✗ MySQL not found on port $Port" -ForegroundColor Red
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
DB_HOST=$Host
DB_PORT=$Port
DB_USER=root
DB_PASSWORD=
DB_NAME=adoption_and_childcare_tracking_system_db
JWT_SECRET=your_jwt_secret_key_here
NODE_ENV=development
"@

$env_file = Join-Path $PSScriptRoot ".env"
$env_content | Out-File -FilePath $env_file -Encoding UTF8 -NoNewline

Write-Host "✓ .env file created at $env_file" -ForegroundColor Green

Write-Host ""
Write-Host "Database configuration:" -ForegroundColor Cyan
Write-Host "  Host: $Host" -ForegroundColor White
Write-Host "  Port: $Port" -ForegroundColor White
Write-Host "  User: root" -ForegroundColor White
Write-Host "  Database: adoption_and_childcare_tracking_system_db" -ForegroundColor White
Write-Host ""

# Check if database exists and import if needed
Write-Host "Checking database..." -ForegroundColor Yellow

$mysql_check_db = & {
    $output = mysql -h $Host -P $Port -u root -e "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='adoption_and_childcare_tracking_system_db'" 2>&1
    if ($output -match "adoption_and_childcare_tracking_system_db") {
        return $true
    }
    return $false
} -ErrorAction SilentlyContinue

if ($mysql_check_db) {
    Write-Host "✓ Database exists" -ForegroundColor Green
} else {
    Write-Host "Database not found, importing..." -ForegroundColor Yellow
    
    # Run the database import script
    $db_script = Join-Path (Split-Path $PSScriptRoot) "database\import-db.ps1"
    if (Test-Path $db_script) {
        try {
            & $db_script -MySQLPort $Port
            Write-Host "✓ Database imported successfully" -ForegroundColor Green
        } catch {
            Write-Host "⚠ Database import may have encountered issues" -ForegroundColor Yellow
            Write-Host "Error: $_" -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠ Database import script not found at $db_script" -ForegroundColor Yellow
        Write-Host "Attempting manual import..." -ForegroundColor Yellow
        
        $sql_file = Join-Path (Split-Path $PSScriptRoot) "database\adoption_and_childcare_tracking_system_db.sql"
        if (Test-Path $sql_file) {
            try {
                mysql -h $Host -P $Port -u root < $sql_file
                Write-Host "✓ Database imported successfully" -ForegroundColor Green
            } catch {
                Write-Host "✗ Database import failed" -ForegroundColor Red
                Write-Host "Error: $_" -ForegroundColor Red
            }
        }
    }
}

Write-Host ""
Write-Host "Installing dependencies..." -ForegroundColor Yellow
npm install 2>&1 | Out-Null
Write-Host "✓ Dependencies ready" -ForegroundColor Green

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Starting Backend Server..." -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Backend Server:" -ForegroundColor Green
Write-Host "  URL: http://localhost:50000" -ForegroundColor White
Write-Host "  Database: adoption_and_childcare_tracking_system_db" -ForegroundColor White
Write-Host "  Status: Starting..." -ForegroundColor White
Write-Host ""
Write-Host "Press Ctrl+C to stop the server" -ForegroundColor Yellow
Write-Host ""

# Start the backend server
node server.js
