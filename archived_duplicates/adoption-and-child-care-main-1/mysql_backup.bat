@echo off
REM === MySQL Backup Script ===
set db_user=root
set db_pass=
set db_name=adoption_and_childcare_tracking_system_db
set backup_dir=C:\Users\brian\Desktop\project\adoption-and-child-care-main

REM Create backup directory if it doesn't exist
if not exist "%backup_dir%" mkdir "%backup_dir%"

REM Format date as YYYY-MM-DD
for /f "tokens=2 delims==." %%I in ('"wmic os get localdatetime /value"') do set datetime=%%I
set datestamp=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%

REM Run mysqldump
mysqldump -u %db_user% %db_name% > "%backup_dir%\backup_%datestamp%.sql"

REM Optional: echo completion
echo Backup completed: %backup_dir%\backup_%datestamp%.sql
pause 