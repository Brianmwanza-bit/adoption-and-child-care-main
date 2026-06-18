@echo off
REM === MySQL Backup Script ===
set db_user=root
set db_pass=
set db_name=adoption_and_childcare_tracking_system_db
set db_port=3306
set backup_dir=%~dp0backups

REM Create backup directory if it doesn't exist
if not exist "%backup_dir%" mkdir "%backup_dir%"

REM Format date as YYYY-MM-DD
for /f "tokens=2 delims==." %%I in ('"wmic os get localdatetime /value"') do set datetime=%%I
set datestamp=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%

REM Run mysqldump from XAMPP
echo Backing up database %db_name% on port %db_port%...
"C:\xampp\mysql\bin\mysqldump.exe" -u %db_user% -P %db_port% %db_name% > "%backup_dir%\backup_%datestamp%.sql"

if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Backup completed: %backup_dir%\backup_%datestamp%.sql
) else (
    echo [ERROR] Backup failed. Please check if MySQL is running on port %db_port%.
)
pause
