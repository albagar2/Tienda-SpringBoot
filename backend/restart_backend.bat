@echo off
echo Stopping process on port 8080...
powershell -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }"
timeout /t 2 /nobreak >nul

echo Cleaning and building...
call mvnw.cmd clean install
if errorlevel 1 (
    echo.
    echo BUILD FAILED! Please check the errors above.
    pause
    exit /b
)

echo.
echo Starting backend...
call mvnw.cmd spring-boot:run
pause
