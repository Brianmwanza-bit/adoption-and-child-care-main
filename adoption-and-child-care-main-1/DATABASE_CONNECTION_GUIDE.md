# Database Connection Guide

This guide will help you connect the Adoption & Child Care application to your local MySQL database at `http://localhost/phpmyadmin/index.php?route=/database/structure&db=adoption_and_childcare_tracking_system_db`.

## Quick Setup

### 1. Run Database Setup Script
```bash
# PowerShell (Recommended)
.\database-setup.ps1

# Or manually create the .env file
```

### 2. Database Configuration

The application is configured to connect to:
- **Host**: `localhost`
- **Database**: `adoption_and_childcare_tracking_system_db`
- **User**: `root` (default phpMyAdmin user)
- **Password**: `` (empty by default)
- **Port**: `3306`

## Step-by-Step Setup

### Step 1: Verify MySQL Installation
1. Ensure MySQL/MariaDB is installed and running
2. Verify phpMyAdmin is accessible at `http://localhost/phpmyadmin`
3. Check that the MySQL service is running

### Step 2: Create Database
1. Open phpMyAdmin: `http://localhost/phpmyadmin`
2. Click "New" to create a new database
3. Enter database name: `adoption_and_childcare_tracking_system_db`
4. Select collation: `utf8mb4_general_ci`
5. Click "Create"

### Step 3: Import Database Schema
1. In phpMyAdmin, select the `adoption_and_childcare_tracking_system_db` database
2. Click "Import" tab
3. Choose file: `database/adoption_and_childcare_tracking_system_db.sql`
4. Click "Go" to import the schema

### Step 4: Configure Environment Variables
The setup script creates `backend/.env` with:
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

### Step 5: Install Backend Dependencies
```bash
cd backend
npm install
```

### Step 6: Start the Backend Server
```bash
cd backend
npm start
```

## Database Schema Overview

The application uses the following main tables:

### Core Tables
- **users** - System users (admin, case_worker, etc.)
- **children** - Child information and records
- **guardians** - Legal guardians and caregivers
- **family_profile** - Foster family profiles
- **foster_tasks** - Tasks assigned to case workers
- **foster_matches** - Matching families with case workers

### Supporting Tables
- **court_cases** - Legal proceedings
- **placements** - Child placement history
- **medical_records** - Healthcare information
- **education_records** - School and education data
- **documents** - File uploads and documents
- **audit_logs** - System activity tracking
- **background_checks** - Background verification
- **notifications** - System notifications

## Testing the Connection

### 1. Test Database Connection
```bash
# Test MySQL connection
mysql -u root -e "USE adoption_and_childcare_tracking_system_db; SELECT COUNT(*) FROM users;"
```

### 2. Test Backend API
```bash
# Start the server
cd backend
npm start

# Test API endpoint
curl http://localhost:50000/users
```

### 3. Test Frontend Connection
1. Open the web application
2. Try to log in with default credentials:
   - Username: `admin`
   - Password: `admin` (or check the database for actual password)

## Troubleshooting

### Common Issues

#### 1. MySQL Connection Failed
**Error**: `ER_ACCESS_DENIED_ERROR`
**Solution**: 
- Check if MySQL root password is set
- Update `backend/.env` with correct password
- Or reset MySQL root password

#### 2. Database Not Found
**Error**: `ER_BAD_DB_ERROR`
**Solution**:
- Create the database in phpMyAdmin
- Import the SQL schema file
- Verify database name in `.env` file

#### 3. Port Already in Use
**Error**: `EADDRINUSE`
**Solution**:
- Change PORT in `backend/.env`
- Or kill the process using port 50000

#### 4. Permission Denied
**Error**: `EACCES`
**Solution**:
- Run as administrator
- Check file permissions
- Ensure MySQL user has proper privileges

### MySQL User Setup (if needed)

If you need to create a dedicated MySQL user:

```sql
-- Create user
CREATE USER 'adoption_user'@'localhost' IDENTIFIED BY 'your_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON adoption_and_childcare_tracking_system_db.* TO 'adoption_user'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

Then update `backend/.env`:
```env
DB_USER=adoption_user
DB_PASSWORD=your_password
```

## Security Considerations

### Production Setup
1. **Change JWT Secret**: Update `JWT_SECRET` in `.env`
2. **Use Strong Passwords**: Set MySQL user passwords
3. **Enable SSL**: Configure MySQL SSL connections
4. **Restrict Access**: Limit database user privileges
5. **Environment Variables**: Use proper environment management

### Development vs Production
- **Development**: Use default settings for easy setup
- **Production**: Implement proper security measures
- **Environment**: Use different `.env` files for different environments

## API Endpoints

Once connected, the following API endpoints will be available:

### Authentication
- `POST /register` - User registration
- `POST /login` - User login
- `POST /user-exists` - Check username availability

### Core Data
- `GET /users` - List all users
- `GET /children` - List all children
- `GET /family_profile` - List family profiles
- `GET /foster_tasks` - List foster tasks

### File Management
- `POST /upload-photo` - Upload user photos
- `GET /uploads/:filename` - Access uploaded files

## Monitoring and Logs

### Database Logs
- Check MySQL error logs for connection issues
- Monitor query performance
- Review audit logs for data changes

### Application Logs
- Backend logs are in the console
- Check for API errors
- Monitor file uploads

## Support

If you encounter issues:
1. Check the troubleshooting section above
2. Verify MySQL service is running
3. Test database connection manually
4. Review application logs
5. Ensure all dependencies are installed

## Quick Commands

```bash
# Setup database
.\database-setup.ps1

# Start backend
cd backend && npm start

# Test connection
curl http://localhost:50000/users

# Check MySQL status
Get-Service -Name "MySQL*"

# Backup database
mysqldump -u root adoption_and_childcare_tracking_system_db > backup.sql
```

Happy coding! 🚀
