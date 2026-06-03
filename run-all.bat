@echo off
setlocal

:: Ensure PowerShell and common system paths are in the PATH for mvnw to work
set "PATH=%SystemRoot%\System32\WindowsPowerShell\v1.0\;%SystemRoot%\System32\;%SystemRoot%\;%PATH%"

echo ==========================================
echo   QUANTITY MEASUREMENT APP - RUN ALL
echo ==========================================

:: 1. START REGISTRY
echo [1/5] Starting Registry (Port 8761)...
start "QMA-Registry" cmd /k "cd QMA-Registry && mvnw.cmd clean spring-boot:run"

:: Wait for registry to initialize (Eureka takes time)
echo Waiting 15 seconds for Registry to stabilize...
timeout /t 15 /nobreak

:: 2. START AUTH SERVICE
echo [2/5] Starting Auth Service (Port 5000)...
start "QMA-Auth" cmd /k "cd QMA-Auth && mvnw.cmd clean spring-boot:run"

:: 3. START QUANTITY SERVICE
echo [3/5] Starting Quantity Service (Port 6000)...
start "QMA-Service" cmd /k "cd QMA-Service && mvnw.cmd clean spring-boot:run"

:: 4. START API GATEWAY
echo [4/5] Starting API Gateway (Port 4000)...
start "QMA-API" cmd /k "cd QMA-API && mvnw.cmd clean spring-boot:run"

:: 5. START FRONTEND
echo [5/5] Starting React Frontend (Port 5173)...
start "QMA-Client" cmd /k "cd ..\QuantityMeasurementApp-Frontend\QMA-Client && npm run dev"

echo.
echo ==========================================
echo   ALL SERVICES ARE STARTING UP
echo ==========================================
echo 1. Monitor Registry: http://localhost:8761
echo 2. API Documentation: http://localhost:4000/swagger
echo 3. Open Application: https://quantitymeasurementapp-p0gz.onrender.com
echo.
echo Note: It may take up to 60 seconds for all services 
echo to fully register and become reachable via Gateway.
echo ==========================================
pause
