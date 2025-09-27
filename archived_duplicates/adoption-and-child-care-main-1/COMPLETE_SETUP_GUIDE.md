# Complete Setup Guide for Adoption & Child Care System

This guide will help you set up the entire system from scratch, including all necessary software installations.

## Prerequisites

### Required Software
1. **XAMPP** (for MySQL and phpMyAdmin)
2. **Node.js** (for backend server)
3. **Git** (for version control)

## Step 1: Install XAMPP

### Download and Install XAMPP
1. Go to: https://www.apachefriends.org/download.html
2. Download XAMPP for Windows
3. Run the installer with default settings
4. Start XAMPP Control Panel
5. Start Apache and MySQL services

### Verify Installation
1. Open browser and go to: `http://localhost/phpmyadmin`
2. You should see phpMyAdmin login page
3. Default credentials:
   - Username: `root`
   - Password: `` (empty)

## Step 2: Install Node.js

### Download and Install Node.js
1. Go to: https://nodejs.org/
2. Download LTS version (recommended)
3. Run the installer with default settings
4. Restart your terminal/PowerShell

### Verify Installation
```bash
node --version
npm --version
```

## Step 3: Set Up Database

### Create Database
1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Click "New" to create a new database
3. Enter database name: `adoption_and_childcare_tracking_system_db`
4. Select collation: `utf8mb4_general_ci`
5. Click "Create"

### Import Database Schema
1. Select the `adoption_and_childcare_tracking_system_db` database
2. Click "Import" tab
3. Choose file: `database/adoption_and_childcare_tracking_system_db.sql`
4. Click "Go" to import

## Step 4: Configure Backend

### Install Dependencies
```bash
cd backend
npm install
```

### Create Environment File
Create `backend/.env`:
```env
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
```

### Start Backend Server
```bash
cd backend
npm start
```

## Step 5: Test the System

### Test Database Connection
```bash
# Test MySQL connection
mysql -u root -h localhost -e "USE adoption_and_childcare_tracking_system_db; SELECT COUNT(*) FROM users;"
```

### Test Backend API
```bash
# Test API endpoint
curl http://localhost:50000/users
```

### Test Frontend
1. Open the web application in your browser
2. Try logging in with default credentials:
   - Username: `admin`
   - Password: `admin`

## Alternative Installation Methods

### Option 1: Using Chocolatey (Windows Package Manager)

If you have Chocolatey installed:
```bash
# Install XAMPP
choco install xampp

# Install Node.js
choco install nodejs

# Install Git
choco install git
```

### Option 2: Using Docker

If you prefer Docker:
```bash
# Run MySQL container
docker run --name mysql-adoption -e MYSQL_ROOT_PASSWORD=your_password -e MYSQL_DATABASE=adoption_and_childcare_tracking_system_db -p 3306:3306 -d mysql:8.0

# Import schema
docker exec -i mysql-adoption mysql -uroot -pyour_password adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

## Troubleshooting

### Common Issues

#### 1. XAMPP Won't Start
- Check if other services are using ports 80/3306
- Run XAMPP as administrator
- Check Windows Firewall settings

#### 2. Node.js Not Found
- Restart terminal after installation
- Check PATH environment variable
- Reinstall Node.js if needed

#### 3. Database Connection Failed
- Ensure MySQL is running in XAMPP
- Check database name spelling
- Verify credentials in `.env` file

#### 4. Port Already in Use
- Change backend port in `.env`
- Kill conflicting processes
- Use different port for services

### Error Messages and Solutions

#### "npm is not recognized"
**Solution**: Install Node.js and restart terminal

#### "MySQL service not found"
**Solution**: Install XAMPP and start MySQL service

#### "Connection refused"
**Solution**: Ensure MySQL is running and port 3306 is available

#### "Access denied"
**Solution**: Check MySQL credentials in `.env` file

## Development Workflow

### Starting Development
1. Start XAMPP (Apache + MySQL)
2. Start backend server: `cd backend && npm start`
3. Open web application in browser
4. Make changes and test

### Stopping Development
1. Stop backend server (Ctrl+C)
2. Stop XAMPP services
3. Close terminal

## File Structure

```
adoption-and-child-care-main/
├── backend/                 # Node.js backend
│   ├── server.js           # Main server file
│   ├── package.json        # Dependencies
│   └── .env               # Environment variables
├── database/               # Database files
│   └── adoption_and_childcare_tracking_system_db.sql
├── src/                   # Frontend files
├── android/               # Android app
└── README files
```

## API Endpoints

Once running, these endpoints will be available:

### Authentication
- `POST /register` - User registration
- `POST /login` - User login
- `POST /user-exists` - Check username

### Data Management
- `GET /users` - List users
- `GET /children` - List children
- `GET /family_profile` - List families
- `POST /upload-photo` - Upload photos

## Security Notes

### Development
- Use default settings for easy setup
- Empty MySQL password is acceptable
- Simple JWT secret is fine

### Production
- Set strong passwords
- Use environment variables
- Enable SSL
- Restrict database access

## Quick Commands Reference

```bash
# Start XAMPP services
# (Use XAMPP Control Panel)

# Install backend dependencies
cd backend && npm install

# Start backend server
cd backend && npm start

# Test database
mysql -u root -h localhost -e "SELECT 1;"

# Test API
curl http://localhost:50000/users

# Create database
mysql -u root -h localhost -e "CREATE DATABASE adoption_and_childcare_tracking_system_db;"

# Import schema
mysql -u root -h localhost adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

## Support

If you encounter issues:
1. Check all services are running
2. Verify installations
3. Review error logs
4. Test connections manually
5. Check file permissions

## Next Steps

After successful setup:
1. Explore the application features
2. Add test data
3. Customize the system
4. Deploy to production (if needed)

Happy coding! 🚀
