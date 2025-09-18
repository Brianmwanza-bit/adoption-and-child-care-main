Param(
    [Parameter(Mandatory=$true)][string]$RepoUrl,
    [Parameter(Mandatory=$true)][string]$UserName,
    [Parameter(Mandatory=$true)][string]$UserEmail,
    [string]$CommitMessage = "feat: auto-push scheduled commit"
)

$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$autoPush = Join-Path $PSScriptRoot 'auto-push.ps1'
$taskName = 'AdoptionApp-AutoPush-30min'

if (-not (Test-Path $autoPush)) { throw "auto-push.ps1 not found at $autoPush" }

$action = New-ScheduledTaskAction -Execute 'powershell.exe' -Argument "-NoProfile -ExecutionPolicy Bypass -File `"$autoPush`" -RepoUrl `"$RepoUrl`" -CommitMessage `"$CommitMessage`" -UserName `"$UserName`" -UserEmail `"$UserEmail`"" -WorkingDirectory $projectRoot
$trigger = New-ScheduledTaskTrigger -Once -At (Get-Date).AddMinutes(1) -RepetitionInterval (New-TimeSpan -Minutes 30) -RepetitionDuration ([TimeSpan]::MaxValue)
$settings = New-ScheduledTaskSettingsSet -AllowStartIfOnBatteries -DontStopIfGoingOnBatteries -MultipleInstances IgnoreNew -StartWhenAvailable

try {
    Unregister-ScheduledTask -TaskName $taskName -Confirm:$false -ErrorAction SilentlyContinue | Out-Null
} catch {}

Register-ScheduledTask -TaskName $taskName -Action $action -Trigger $trigger -Settings $settings -Description 'Auto-push adoption app changes every 30 minutes' | Out-Null

Write-Host "[register-auto-push] Task '$taskName' registered."
Write-Host "Next run: " (Get-ScheduledTask -TaskName $taskName | Get-ScheduledTaskInfo).NextRunTime


