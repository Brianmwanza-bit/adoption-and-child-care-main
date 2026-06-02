@echo off
REM Adoption & Child Care - Development Quick Start Batch File
REM Run from backend directory: dev.bat

cd /d "%~dp0"

echo.
echo ========================================
echo Adoption ^& Child Care - Dev Environment
echo ========================================
echo.

REM Check if Node.js is installed
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Node.js is not installed
    echo Please install Node.js from https://nodejs.org/
    exit /b 1
)

REM Install dependencies
echo Installing dependencies...
call npm install

REM Start the backend server
echo.
echo ========================================
echo Starting Backend Server...
echo ========================================
echo.
echo Backend Server:
echo   URL: http://localhost:50000
echo   Database: adoption_and_childcare_tracking_system_db
echo   Status: Starting...
echo.
echo Press Ctrl+C to stop the server
echo.

node server.js
