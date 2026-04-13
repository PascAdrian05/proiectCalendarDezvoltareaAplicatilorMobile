param(
    [string]$JavaHome,
    [string]$DbHost = "localhost",
    [int]$DbPort = 3306,
    [string]$DbName = "bazaDeDateCalendar",
    [string]$DbUser = "root",
    [string]$DbPass = "",
    [int]$ServerPort = 8080,
    [string]$JwtSecret = "",
    [switch]$DryRun,
    [switch]$PersistJavaHome
)

$ErrorActionPreference = "Stop"

function New-RandomSecret {
    param([int]$ByteLength = 48)

    $bytes = New-Object byte[] $ByteLength
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try {
        $rng.GetBytes($bytes)
    }
    finally {
        $rng.Dispose()
    }

    return [Convert]::ToBase64String($bytes).TrimEnd('=') -replace '\+', '-' -replace '/', '_'
}

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

$resolvedDbPass = if ($PSBoundParameters.ContainsKey('DbPass')) {
    $DbPass
} elseif ($env:SPRING_DATASOURCE_PASSWORD) {
    $env:SPRING_DATASOURCE_PASSWORD
} else {
    ""
}

$jwtWasProvided = ($PSBoundParameters.ContainsKey('JwtSecret') -and $JwtSecret) -or [bool]$env:JWT_SECRET_KEY
$resolvedJwtSecret = if ($PSBoundParameters.ContainsKey('JwtSecret') -and $JwtSecret) {
    $JwtSecret
} elseif ($env:JWT_SECRET_KEY) {
    $env:JWT_SECRET_KEY
} else {
    New-RandomSecret
}

$env:SPRING_DATASOURCE_URL = "jdbc:mysql://${DbHost}:$DbPort/${DbName}?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:SPRING_DATASOURCE_USERNAME = $DbUser
$env:SPRING_DATASOURCE_PASSWORD = $resolvedDbPass
$env:JWT_SECRET_KEY = $resolvedJwtSecret
$env:SERVER_PORT = "$ServerPort"

Write-Host "JAVA_HOME = $env:JAVA_HOME"
Write-Host "SPRING_DATASOURCE_URL = $env:SPRING_DATASOURCE_URL"
Write-Host "SPRING_DATASOURCE_USERNAME = $env:SPRING_DATASOURCE_USERNAME"
if ([string]::IsNullOrWhiteSpace($resolvedDbPass)) {
    Write-Host "SPRING_DATASOURCE_PASSWORD = <gol - foloseste parola goala sau seteaza -DbPass>"
} else {
    Write-Host "SPRING_DATASOURCE_PASSWORD = <setat din argument sau mediu>"
}
if ($jwtWasProvided) {
    Write-Host "JWT_SECRET_KEY = <setat din argument sau mediu>"
} else {
    Write-Host "JWT_SECRET_KEY = <generat temporar pentru sesiunea curenta>"
}
Write-Host "SERVER_PORT = $env:SERVER_PORT"

& java -version

if ($DryRun) {
    Write-Host "DryRun activ. Comanda de start ar fi: .\\mvnw.cmd spring-boot:run"
    exit 0
}

& .\mvnw.cmd spring-boot:run

