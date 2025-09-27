# Database Setup Script for Adoption & Child Care System
# This script helps configure the database connection

Write-Host "Setting up database connection for Adoption & Child Care System..." -ForegroundColor Green

# Check if MySQL is running
Write-Host "Checking MySQL service..." -ForegroundColor Yellow
try {
    $mysqlService = Get-Service -Name "MySQL*" -ErrorAction SilentlyContinue
    if ($mysqlService) {
        Write-Host "MySQL service found: $($mysqlService.Name)" -ForegroundColor Green
        if ($mysqlService.Status -eq "Running") {
            Write-Host "MySQL service is running" -ForegroundColor Green
        } else {
            Write-Host "MySQL service is not running. Please start it." -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "MySQL service not found. Please ensure MySQL is installed." -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error checking MySQL service: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Create .env file for backend
$envContent = @"
# Database Configuration
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=
DB_NAME=adoption_and_childcare_tracking_system_db
DB_PORT=3306

# Server Configuration
PORT=50000
JWT_SECRET=your_super_secret_jwt_key_change_this_in_production

# Environment
NODE_ENV=development

# File Upload
MAX_FILE_SIZE=10485760
UPLOAD_PATH=./uploads
"@

Set-Content -Path "backend\.env" -Value $envContent -Force
Write-Host "Created backend\.env file" -ForegroundColor Green

# Test database connection
Write-Host "Testing database connection..." -ForegroundColor Yellow
try {
    $testConnection = mysql -u root -e "SELECT 1;" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database connection successful" -ForegroundColor Green
    } else {
        Write-Host "Database connection failed. Please check your MySQL credentials." -ForegroundColor Red
        Write-Host "You may need to set a password for the root user." -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error testing database connection: $($_.Exception.Message)" -ForegroundColor Red
}

# Check if database exists
Write-Host "Checking if database exists..." -ForegroundColor Yellow
try {
    $dbExists = mysql -u root -e "USE adoption_and_childcare_tracking_system_db; SELECT 1;" 2>$null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Database 'adoption_and_childcare_tracking_system_db' exists" -ForegroundColor Green
    } else {
        Write-Host "Database 'adoption_and_childcare_tracking_system_db' does not exist" -ForegroundColor Yellow
        Write-Host "Please create the database in phpMyAdmin or run the SQL script." -ForegroundColor Yellow
    }
} catch {
    Write-Host "Error checking database: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nDatabase setup instructions:" -ForegroundColor Cyan
Write-Host "1. Open phpMyAdmin at: http://localhost/phpmyadmin" -ForegroundColor White
Write-Host "2. Create database: adoption_and_childcare_tracking_system_db" -ForegroundColor White
Write-Host "3. Import the SQL file: database/adoption_and_childcare_tracking_system_db.sql" -ForegroundColor White
Write-Host "4. Update backend\.env with your MySQL credentials if needed" -ForegroundColor White
Write-Host "5. Start the backend server: cd backend && npm start" -ForegroundColor White

Write-Host "`nSetup complete!" -ForegroundColor Green
