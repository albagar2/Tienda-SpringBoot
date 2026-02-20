@echo off
echo Testing API... > api_test_results.txt
echo. >> api_test_results.txt
echo 1. Testing Root URL... >> api_test_results.txt
curl -v http://localhost:8080/ >> api_test_results.txt 2>&1
echo. >> api_test_results.txt

echo 2. Testing Login Endpoint (Expect 404 or 401 or 200)... >> api_test_results.txt
curl -v -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\", \"password\":\"admin\"}" >> api_test_results.txt 2>&1
echo. >> api_test_results.txt

echo 3. Testing Register Endpoint... >> api_test_results.txt
curl -v -X POST http://localhost:8080/api/auth/register -H "Content-Type: application/json" -d "{\"nombre\":\"TestUser\", \"username\":\"testuser999\", \"password\":\"123456\", \"email\":\"test999@example.com\", \"direccion\":\"Test St\"}" >> api_test_results.txt 2>&1
echo. >> api_test_results.txt

echo Done. Check api_test_results.txt
type api_test_results.txt
pause
