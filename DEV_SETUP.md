# Development Setup Guide

## Quick Start

### Option 1: Using npm run dev (Recommended)
From the project root directory:
```bash
npm run dev
```

This will automatically:
1. Detect available MySQL port (3306 or 3307)
2. Check/import the database
3. Set up environment variables
4. Start the backend server

### Option 2: Specify MySQL Port
If you have multiple XAMPP instances, specify the port:

```bash
# Use port 3306
npm run dev:3306

# Use port 3307
npm run dev:3307
```

### Option 3: Manual Backend Start
Navigate to the backend directory and start directly:
```bash
cd backend
npm run dev
```

## Available npm Scripts

### From Root Directory
- `npm run dev` - Starts the backend with automatic database setup on available port
- `npm run dev:3306` - Explicitly use XAMPP on port 3306
- `npm run dev:3307` - Explicitly use XAMPP on port 3307
- `npm run backend:start` - Just start the backend server (no DB setup)
- `npm run backend:install` - Install backend dependencies

### From Backend Directory
- `npm run start` - Start server (uses .env for configuration)
- `npm run dev` - Start with database setup on port 3307
- `npm run dev:3306` - Start with database setup on port 3306
- `npm run dev:3307` - Start with database setup on port 3307

## Environment Configuration

The `.env` file in the `backend/` directory contains database connection settings:

```env
DB_HOST=localhost
DB_PORT=3307
DB_USER=root
DB_PASSWORD=
DB_NAME=adoption_and_childcare_tracking_system_db
JWT_SECRET=your_jwt_secret_key_here
NODE_ENV=development
```

### Changing Configuration

Edit `backend/.env` to change:
- **DB_PORT**: MySQL port (3306 or 3307)
- **DB_PASSWORD**: MySQL password (if set)
- **JWT_SECRET**: JWT signing secret for authentication

## Database Setup

### Automatic Import
When you run `npm run dev`, the system will:
1. Check if MySQL is running on the specified port
2. Verify if the database exists
3. Automatically import the SQL dump if needed

### Manual Import
If automatic import fails, manually import the database:

```bash
# Using PowerShell
mysql -h localhost -P 3307 -u root < database/adoption_and_childcare_tracking_system_db.sql

# Or use the import helper script
.\database\import-db.ps1
```

## Multiple XAMPP Instances

The system automatically detects available XAMPP instances:

```
Port 3306: MySQL Instance 1 (XAMPP 1)
Port 3307: MySQL Instance 2 (XAMPP 2)
```

The dev startup script will use the available port or the one you specify.

To check which ports are active:
```powershell
netstat -ano | findstr ":330" | findstr "LISTENING"
```

## Backend Server Details

When the server starts, you'll see:
```
========================================
Adoption & Child Care - Dev Environment
========================================

✓ MySQL is running on port 3307
✓ .env file created
✓ Database exists
✓ Dependencies ready

========================================
Starting Backend Server...
========================================

Backend Server:
  URL: http://localhost:50000
  Database: adoption_and_childcare_tracking_system_db
  Status: Starting...

Press Ctrl+C to stop the server
```

## API Access

Once the server is running:
- **API Base URL**: `http://localhost:50000`
- **Swagger Docs**: Available via Express endpoints (if configured)
- **Database**: `adoption_and_childcare_tracking_system_db`

## Troubleshooting

### MySQL Connection Failed
```
✗ MySQL not found on port 3307

Available ports:
  Port: 3306
```

**Solution**: 
- Ensure XAMPP MySQL is running
- Use `npm run dev:3306` if MySQL is on port 3306

### Database Import Fails
- Ensure MySQL root user has no password (or update DB_PASSWORD in .env)
- Check that the SQL file exists at `database/adoption_and_childcare_tracking_system_db.sql`
- Manually import using: `mysql -h localhost -P 3307 -u root < database/adoption_and_childcare_tracking_system_db.sql`

### Dependencies Missing
```
npm install
cd backend
npm install
```

### Port Already in Use
If port 50000 (backend) is already in use:
- Change PORT in `.env` or backend/server.js
- Kill the process using that port:
  ```powershell
  netstat -ano | findstr ":50000"
  # Then use the PID to kill: taskkill /PID <PID> /F
  ```

## Development Workflow

1. **Start development server**:
   ```bash
   npm run dev
   ```

2. **Check server status**:
   - Backend logs should show connection details
   - No errors means server is ready

3. **Access the application**:
   - Frontend: Open `index.html` in browser
   - API: Test at `http://localhost:50000`

4. **Stop the server**:
   - Press `Ctrl+C` in the terminal

## Files Modified for Development

- `backend/.env` - Database connection configuration
- `backend/dev.ps1` - Development startup script
- `backend/package.json` - Added dev scripts
- `package.json` - Added npm run dev from root

## Next Steps

- Review `PLACEMENT_AND_FAMILY_SYSTEM.md` for database schema
- Check API documentation in backend files
- Start building features!
