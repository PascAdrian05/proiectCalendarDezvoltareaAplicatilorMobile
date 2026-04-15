# Script pentru pornirea sigură a backend-ului cu MySQL conectat

$ErrorActionPreference = "Stop"

Write-Host "╔════════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║          Backend Startup Script - Calendar App UMFST          ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════════╝" -ForegroundColor Green

# Set JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "`n[1/5] Verificare Java..." -ForegroundColor Yellow
java -version 2>&1 | Select-Object -First 1
Write-Host "✓ Java este disponibil" -ForegroundColor Green

Write-Host "`n[2/5] Verificare MySQL connection..." -ForegroundColor Yellow
try {
    # Testează conexiune MySQL
    $mysqlTest = "cmd /c `"mysql -u root -h localhost -e 'SELECT 1' 2>&1`""
    $result = Invoke-Expression $mysqlTest
    if ($result -contains "1") {
        Write-Host "✓ MySQL este conectat" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ MySQL nu răspunde. Pornește MySQL service:" -ForegroundColor Yellow
    Write-Host "  net start MySQL80  (sau MySQL57, depinde de versiune)" -ForegroundColor Cyan
}

Write-Host "`n[3/5] Configurare variabile de mediu..." -ForegroundColor Yellow
# Citim credențiale din input sigur
$dbUser = Read-Host "Username database (default: root)"
if ([string]::IsNullOrEmpty($dbUser)) { $dbUser = "root" }

$dbPass = Read-Host -AsSecureString "Password database"
$dbPassPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [System.Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($dbPass)
)

$jwtSecret = Read-Host -AsSecureString "JWT Secret (min 32 caractere random)"
$jwtSecretPlain = [System.Runtime.InteropServices.Marshal]::PtrToStringAuto(
    [System.Runtime.InteropServices.Marshal]::SecureStringToCoTaskMemUnicode($jwtSecret)
)

# Validări
if ($jwtSecretPlain.Length -lt 32) {
    Write-Host "✗ JWT Secret trebuie să aibă minimum 32 caractere!" -ForegroundColor Red
    Write-Host "  Sugestie: Generează cu: `[System.Convert]::ToBase64String([System.Text.Encoding]::UTF8.GetBytes((1..50|ForEach-Object {[char]::FromCharCode((Random).Next(33,126))})-join''))" -ForegroundColor Cyan
    exit 1
}

$env:SPRING_DATASOURCE_URL = "jdbc:mysql://localhost:3306/bazaDeDateCalendar?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = $dbUser
$env:SPRING_DATASOURCE_PASSWORD = $dbPassPlain
$env:JWT_SECRET_KEY = $jwtSecretPlain
$env:SERVER_PORT = "8080"

Write-Host "✓ Variabile configurate:" -ForegroundColor Green
Write-Host "  - Database: bazaDeDateCalendar @ localhost:3306" -ForegroundColor Gray
Write-Host "  - User: $dbUser" -ForegroundColor Gray
Write-Host "  - Server port: 8080" -ForegroundColor Gray
Write-Host "  - JWT Secret: $(if($jwtSecretPlain.Length -gt 0) { '***' + $jwtSecretPlain.Substring($jwtSecretPlain.Length-4) } else { 'MISSING' })" -ForegroundColor Gray

Write-Host "`n[4/5] Verificare că portul 8080 este liber..." -ForegroundColor Yellow
$portCheck = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($portCheck) {
    Write-Host "✗ Portul 8080 este deja în folosire!" -ForegroundColor Red
    Write-Host "  Proces: $(Get-Process -Id $portCheck.OwningProcess | Select-Object -ExpandProperty Name)" -ForegroundColor Cyan
    $kill = Read-Host "Vrei să termini procesul? (y/n)"
    if ($kill -eq "y") {
        Stop-Process -Id $portCheck.OwningProcess -Force
        Write-Host "✓ Proces terminat" -ForegroundColor Green
    } else {
        exit 1
    }
} else {
    Write-Host "✓ Portul 8080 este liber" -ForegroundColor Green
}

Write-Host "`n[5/5] Pornire Spring Boot backend..." -ForegroundColor Yellow
Write-Host "Backend va rula pe: http://localhost:8080" -ForegroundColor Cyan
Write-Host "`nPress Ctrl+C pentru a opri serverul`n" -ForegroundColor Yellow

Set-Location "C:\Users\PASC\AndroidStudioProjects\proiectCalendarUMFST\backend"

# Pornește backend
try {
    & .\mvnw.cmd spring-boot:run
} catch {
    Write-Host "✗ Eroare la pornire:" -ForegroundColor Red
    Write-Host $_ -ForegroundColor Red
    exit 1
}

