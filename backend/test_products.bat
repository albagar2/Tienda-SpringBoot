@echo off
echo Testing /api/productos endpoint...
curl -v http://localhost:8080/api/productos > products_response.txt
echo.
echo Check products_response.txt for output.
pause
