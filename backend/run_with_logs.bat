@echo off
echo Running backend with logs...
echo. > backend.log
echo Starting... >> backend.log
call mvnw.cmd clean install >> backend.log 2>&1
if errorlevel 1 (
    echo BUILD FAILED >> backend.log
    echo Check backend.log for details.
    exit /b
)
echo Build success. Starting app... >> backend.log
call mvnw.cmd spring-boot:run >> backend.log 2>&1
