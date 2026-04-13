param(
    [string]$JavaHome,
    [string]$DbHost = "localhost",
    [int]$DbPort = 3306,
    [string]$DbName = "bazaDeDateCalendar",
    [string]$DbUser = "root",
    [string]$DbPass = "root",
    [int]$ServerPort = 8080,
    [string]$JwtSecret = "5e0a36c198ea02ad460d3e82faca99e740f87e9c9575442572d6b7cddc5d19ec",
    [switch]$DryRun,
    [switch]$PersistJavaHome
)

$ErrorActionPreference = "Stop"

function Resolve-JavaHome {
    param([string]$UserProvided)

    if ($UserProvided -and (Test-Path (Join-Path $UserProvided "bin\\java.exe"))) {
        return $UserProvided
    }

    if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME "bin\\java.exe"))) {
        return $env:JAVA_HOME
    }

    $candidates = @(
        "C:\\Program Files\\Android\\Android Studio\\jbr",
        "C:\\Program Files\\Java\\jdk-21",
        "C:\\Program Files\\Java\\jdk-17",
        "C:\\Program Files\\Eclipse Adoptium\\jdk-21*",
        "C:\\Program Files\\Eclipse Adoptium\\jdk-17*"
    )

    foreach ($candidate in $candidates) {
        # exact folder (ex: Android Studio jbr)
        if (-not $candidate.Contains("*") -and (Test-Path (Join-Path $candidate "bin\\java.exe"))) {
            return $candidate
        }

        # wildcard candidates (ex: Eclipse Adoptium\jdk-21*)
        $matches = Get-ChildItem -Path $candidate -Directory -ErrorAction SilentlyContinue
        foreach ($folder in $matches) {
            if (Test-Path (Join-Path $folder.FullName "bin\\java.exe")) {
                return $folder.FullName
            }
        }
    }

    throw "Nu am gasit un JDK valid. Instaleaza JDK 17/21 sau paseaza -JavaHome explicit."
}

$resolvedJavaHome = Resolve-JavaHome -UserProvided $JavaHome
$env:JAVA_HOME = $resolvedJavaHome
$env:Path = "$resolvedJavaHome\\bin;$env:Path"

if ($PersistJavaHome) {
    [Environment]::SetEnvironmentVariable("JAVA_HOME", $resolvedJavaHome, "User")
}

$env:SPRING_DATASOURCE_URL = "jdbc:mysql://${DbHost}:$DbPort/${DbName}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = $DbUser
$env:SPRING_DATASOURCE_PASSWORD = $DbPass
$env:JWT_SECRET_KEY = $JwtSecret
$env:SERVER_PORT = "$ServerPort"

Write-Host "JAVA_HOME = $env:JAVA_HOME"
Write-Host "SPRING_DATASOURCE_URL = $env:SPRING_DATASOURCE_URL"
Write-Host "SPRING_DATASOURCE_USERNAME = $env:SPRING_DATASOURCE_USERNAME"
Write-Host "SERVER_PORT = $env:SERVER_PORT"

& java -version

if ($DryRun) {
    Write-Host "DryRun activ. Comanda de start ar fi: .\\mvnw.cmd spring-boot:run"
    exit 0
}

& .\mvnw.cmd spring-boot:run

