param(
    [string]$MysqlPath = "$env:ProgramFiles\MariaDB 12.1\bin\mysql.exe",
    [string]$SqlFile = "database\adoption_and_childcare_tracking_system_db.sql",
    [string]$DatabaseName = "adoption_and_childcare_tracking_system_db",
    [string]$RootUser = "root"
)

function Find-MySqlExe {
    if (Test-Path $MysqlPath) {
        return (Get-Item $MysqlPath).FullName
    }

    $mysqlCmd = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if ($mysqlCmd) {
        return $mysqlCmd.Source
    }

    throw "mysql.exe not found. Install MySQL/MariaDB client tools or update -MysqlPath with the correct path."
}

$mysqlExe = Find-MySqlExe

if (-not (Test-Path $SqlFile)) {
    throw "SQL file not found: $SqlFile. Make sure you are running this from the repository root."
}

$passwordSecure = Read-Host 'MySQL root password (leave blank for no password)' -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($passwordSecure))

$authArgs = @("--user=$RootUser")
if ($plainPassword) {
    $authArgs += "--password=$plainPassword"
}

Write-Host "Using MySQL executable: $mysqlExe"
Write-Host "Creating database: $DatabaseName"

$createArgs = $authArgs + "--execute=CREATE DATABASE IF NOT EXISTS `$DatabaseName CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"
$createProcess = Start-Process -FilePath $mysqlExe -ArgumentList $createArgs -NoNewWindow -Wait -PassThru -RedirectStandardError ([System.IO.StreamWriter]::Null) -RedirectStandardOutput ([System.IO.StreamWriter]::Null)
if ($createProcess.ExitCode -ne 0) {
    throw "Failed to create database. Exit code: $($createProcess.ExitCode)"
}

Write-Host "Importing SQL from $SqlFile into $DatabaseName..."

$importArgs = $authArgs + $DatabaseName
$processInfo = New-Object System.Diagnostics.ProcessStartInfo
$processInfo.FileName = $mysqlExe
$processInfo.Arguments = ($importArgs -join ' ')
$processInfo.RedirectStandardInput = $true
$processInfo.RedirectStandardOutput = $true
$processInfo.RedirectStandardError = $true
$processInfo.UseShellExecute = $false
$processInfo.CreateNoWindow = $true

$process = New-Object System.Diagnostics.Process
$process.StartInfo = $processInfo
$process.Start() | Out-Null

Get-Content $SqlFile | ForEach-Object { $process.StandardInput.WriteLine($_) }
$process.StandardInput.Close()

$stdout = $process.StandardOutput.ReadToEnd()
$stderr = $process.StandardError.ReadToEnd()
$process.WaitForExit()

if ($process.ExitCode -ne 0) {
    Write-Host "Import failed with exit code $($process.ExitCode)" -ForegroundColor Red
    if ($stdout) { Write-Host "STDOUT:`n$stdout" }
    if ($stderr) { Write-Host "STDERR:`n$stderr" }
    throw "Database import failed."
}

Write-Host "Database imported successfully into $DatabaseName." -ForegroundColor Green
