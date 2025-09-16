@echo off
setlocal
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d C:\Users\brian\Desktop\adoption-and-child-care-main\android
call gradlew.bat installDebug --no-daemon --stacktrace
if errorlevel 1 exit /b %errorlevel%
"C:\Users\brian\AppData\Local\Android\Sdk\platform-tools\adb.exe" shell am start -n com.yourdomain.adoptionchildcare/.MainActivity
endlocal
@echo off
set \" JAVA_HOME=C:\Program Files\Android\Android "Studio\jbr\
