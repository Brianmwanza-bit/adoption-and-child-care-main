@echo off
echo ========================================
echo GitHub Repository Update Script
echo ========================================
echo.

echo Step 1: Replacing old README with updated version...
if exist "README.md" (
    move "README.md" "README_OLD.md"
    echo - Old README backed up to README_OLD.md
)
if exist "README_UPDATED.md" (
    move "README_UPDATED.md" "README.md"
    echo - New README_UPDATED.md renamed to README.md
)
echo.

echo Step 2: Updating Windsurf configuration...
if exist ".windsurf\config.json" (
    move ".windsurf\config.json" ".windsurf\config_old.json"
    echo - Old config backed up to config_old.json
)
if exist ".windsurf\config_updated.json" (
    move ".windsurf\config_updated.json" ".windsurf\config.json"
    echo - New config_updated.json renamed to config.json
)
echo.

echo Step 3: Staging all changes...
git add .
if %errorlevel% neq 0 (
    echo Error: git add failed
    pause
    exit /b 1
)
echo - All changes staged successfully
echo.

echo Step 4: Committing changes...
git commit -m "Update GitHub repository configuration and documentation

- Updated remote URL to Brianmwanza-bit repository
- Configured Git Credential Manager for authentication
- Added MIT License file
- Created updated README with accurate project information
- Fixed Windsurf configuration with correct package paths
- Updated technology stack documentation to reflect actual project
- Cleaned up old repository references"
if %errorlevel% neq 0 (
    echo Error: git commit failed
    pause
    exit /b 1
)
echo - Changes committed successfully
echo.

echo Step 5: Pushing to GitHub...
git push origin main
if %errorlevel% neq 0 (
    echo Error: git push failed
    echo Note: You may need to authenticate with GitHub
    pause
    exit /b 1
)
echo - Push completed successfully
echo.

echo ========================================
echo Repository Update Complete!
echo ========================================
echo.
echo Your repository has been successfully updated and pushed to:
echo https://github.com/Brianmwanza-bit/adoption-and-child-care-main.git
echo.
pause