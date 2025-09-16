# Manual Database Setup Guide

Since MySQL is not installed as a Windows service, this guide will help you set up the database connection manually.

## Option 1: Install XAMPP (Recommended)

### Step 1: Download and Install XAMPP
1. Download XAMPP from: https://www.apachefriends.org/download.html
2. Install XAMPP with default settings
3. Start XAMPP Control Panel
4. Start Apache and MySQL services

### Step 2: Access phpMyAdmin
1. Open browser and go to: `http://localhost/phpmyadmin`
2. Default credentials:
   - Username: `root`
   - Password: `` (empty)

### Step 3: Create Database
1. Click "New" in phpMyAdmin
2. Enter database name: `adoption_and_childcare_tracking_system_db`
3. Select collation: `utf8mb4_general_ci`
4. Click "Create"

### Step 4: Import Schema
1. Select the database you just created
2. Click "Import" tab
3. Choose file: `database/adoption_and_childcare_tracking_system_db.sql`
4. Click "Go"

## Option 2: Install MySQL Standalone

### Step 1: Download MySQL
1. Download MySQL Community Server from: https://dev.mysql.com/downloads/mysql/
2. Install with default settings
3. Set root password when prompted

### Step 2: Install phpMyAdmin
1. Download phpMyAdmin from: https://www.phpmyadmin.net/downloads/
2. Extract to your web server directory
3. Configure phpMyAdmin for your MySQL installation

## Option 3: Use Docker (Advanced)

### Step 1: Install Docker
1. Download Docker Desktop from: https://www.docker.com/products/docker-desktop
2. Install and start Docker

### Step 2: Run MySQL Container
```bash
docker run --name mysql-adoption -e MYSQL_ROOT_PASSWORD=your_password -e MYSQL_DATABASE=adoption_and_childcare_tracking_system_db -p 3306:3306 -d mysql:8.0
```

### Step 3: Import Schema
```bash
docker exec -i mysql-adoption mysql -uroot -pyour_password adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

## Configuration Files

### 1. Backend Environment File
Create `backend/.env`:
```env
# Database Configuration
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_mysql_password
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

### 2. Update Server Configuration
The server is already configured to use environment variables. Make sure `backend/server.js` has:
```javascript
const db = mysql.createConnection({
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'root',
  password: process.env.DB_PASSWORD || '',
  database: process.env.DB_NAME || 'adoption_and_childcare_tracking_system_db',
  port: process.env.DB_PORT || 3306,
  multipleStatements: true,
  charset: 'utf8mb4',
  timezone: '+00:00'
});
```

## Testing the Connection

### 1. Test Database Connection
```bash
# If using XAMPP
mysql -u root -h localhost -e "USE adoption_and_childcare_tracking_system_db; SELECT COUNT(*) FROM users;"

# If using standalone MySQL
mysql -u root -p -h localhost -e "USE adoption_and_childcare_tracking_system_db; SELECT COUNT(*) FROM users;"
```

### 2. Test Backend API
```bash
cd backend
npm install
npm start
```

### 3. Test API Endpoint
```bash
curl http://localhost:50000/users
```

## Troubleshooting

### Common Issues

#### 1. Connection Refused
**Error**: `ECONNREFUSED`
**Solution**:
- Ensure MySQL service is running
- Check if port 3306 is available
- Verify firewall settings

#### 2. Access Denied
**Error**: `ER_ACCESS_DENIED_ERROR`
**Solution**:
- Check MySQL root password
- Update `.env` file with correct password
- Reset MySQL root password if needed

#### 3. Database Not Found
**Error**: `ER_BAD_DB_ERROR`
**Solution**:
- Create the database manually
- Import the SQL schema
- Check database name spelling

#### 4. Port Already in Use
**Error**: `EADDRINUSE`
**Solution**:
- Change backend port in `.env`
- Kill conflicting processes
- Use different port for MySQL

### XAMPP Specific Issues

#### 1. Apache/MySQL Won't Start
- Check if other services are using ports 80/3306
- Run XAMPP as administrator
- Check XAMPP error logs

#### 2. phpMyAdmin Access Issues
- Verify Apache is running
- Check phpMyAdmin configuration
- Clear browser cache

## Quick Setup Commands

### For XAMPP Users
```bash
# Start XAMPP services
# (Use XAMPP Control Panel)

# Test connection
mysql -u root -h localhost -e "SELECT 1;"

# Create database
mysql -u root -h localhost -e "CREATE DATABASE IF NOT EXISTS adoption_and_childcare_tracking_system_db;"

# Import schema
mysql -u root -h localhost adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

### For Standalone MySQL Users
```bash
# Test connection
mysql -u root -p -h localhost -e "SELECT 1;"

# Create database
mysql -u root -p -h localhost -e "CREATE DATABASE IF NOT EXISTS adoption_and_childcare_tracking_system_db;"

# Import schema
mysql -u root -p -h localhost adoption_and_childcare_tracking_system_db < database/adoption_and_childcare_tracking_system_db.sql
```

## Security Notes

### Development Environment
- Use default settings for easy setup
- Empty root password is acceptable for development
- JWT secret can be simple for development

### Production Environment
- Set strong MySQL root password
- Create dedicated database user
- Use strong JWT secret
- Enable SSL connections
- Restrict database privileges

## Next Steps

After setting up the database:

1. **Start the Backend**:
   ```bash
   cd backend
   npm install
   npm start
   ```

2. **Test the API**:
   ```bash
   curl http://localhost:50000/users
   ```

3. **Access the Web Application**:
   - Open browser to the frontend URL
   - Try logging in with default credentials

4. **Monitor Logs**:
   - Check backend console for errors
   - Monitor database connection status

## Support

If you need help:
1. Check XAMPP/MySQL error logs
2. Verify all services are running
3. Test database connection manually
4. Review the troubleshooting section
5. Check file permissions and paths

Happy coding! 🚀
