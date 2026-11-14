$ErrorActionPreference = 'Stop'

Set-Location $PSScriptRoot

if (-not (Test-Path '.env')) {
    Copy-Item '.env.example' '.env'
    Write-Host 'Created .env from .env.example.'
}

Get-Content '.env' | Where-Object {
    $_ -match '^\s*[A-Za-z_][A-Za-z0-9_]*=' -and $_ -notmatch '^\s*#'
} | ForEach-Object {
    $name, $value = $_ -split '=', 2
    [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), 'Process')
}

$docker = Get-Command docker -ErrorAction SilentlyContinue
$dockerReady = $false
if ($docker) {
    try {
        & docker info *> $null
        $dockerReady = $LASTEXITCODE -eq 0
    } catch {
        $dockerReady = $false
    }
}

if ($dockerReady) {
    Write-Host 'Starting SoloTrail with Docker...'
    & docker compose up -d --build
    if ($LASTEXITCODE -ne 0) {
        throw 'SoloTrail could not be started. Run "docker compose logs" to inspect the service logs.'
    }

    $appPort = '80'
    $portSetting = Select-String -Path '.env' -Pattern '^APP_PORT=(\d+)$' | Select-Object -First 1
    if ($portSetting) {
        $appPort = $portSetting.Matches[0].Groups[1].Value
    }

    Write-Host "SoloTrail is running at http://localhost:$appPort"
    exit 0
}

$mysqlService = Get-Service -Name 'MySQL80' -ErrorAction SilentlyContinue
if (-not $mysqlService) {
    throw 'Docker Desktop is unavailable and the MySQL80 Windows service was not found.'
}
if ($mysqlService.Status -ne 'Running') {
    Start-Service -Name 'MySQL80'
}

if (-not $env:DB_USERNAME -or -not $env:DB_PASSWORD -or -not $env:DB_URL) {
    throw 'Local MySQL settings are incomplete. Set DB_URL, DB_USERNAME, and DB_PASSWORD in .env.'
}

Write-Host 'Docker is unavailable. Starting SoloTrail with local MySQL...'
$backendPath = Join-Path $PSScriptRoot 'backend'
$frontendPath = Join-Path $PSScriptRoot 'frontend'
$backendCommand = "Set-Location '$backendPath'; .\mvnw.cmd spring-boot:run"
$frontendCommand = "Set-Location '$frontendPath'; npm start"
Start-Process 'powershell.exe' -ArgumentList @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command', $backendCommand) -WorkingDirectory $backendPath
Start-Process 'powershell.exe' -ArgumentList @('-NoExit', '-ExecutionPolicy', 'Bypass', '-Command', $frontendCommand) -WorkingDirectory $frontendPath
Write-Host 'SoloTrail is starting at http://localhost:4200'
exit 0
