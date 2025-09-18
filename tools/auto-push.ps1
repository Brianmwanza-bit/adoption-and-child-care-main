Param(
    [Parameter(Mandatory=$true)][string]$RepoUrl,
    [Parameter(Mandatory=$true)][string]$CommitMessage,
    [string]$UserName,
    [string]$UserEmail
)

$ErrorActionPreference = 'Stop'

function Invoke-Git {
    param([Parameter(Mandatory=$true)][string]$ArgsLine)
    & git.exe @($ArgsLine.Split(' ')) 2>&1 | Out-String | Write-Output
}

Write-Host "[auto-push] Starting in $(Get-Location)"

# Ensure git exists
try { git --version | Out-Null } catch { throw "git is not installed or not on PATH." }

# Initialize repo if needed
if (-not (Test-Path -LiteralPath .git)) {
    Write-Host "[auto-push] Initializing new git repository"
    Invoke-Git "init"
}

# Optional user config
if ($UserName) { Invoke-Git "config user.name `"$UserName`"" }
if ($UserEmail) { Invoke-Git "config user.email `"$UserEmail`"" }

# Stage changes
Invoke-Git "add ."

# Commit if there are staged changes
$status = & git status --porcelain
if (-not [string]::IsNullOrWhiteSpace($status)) {
    Write-Host "[auto-push] Committing changes"
    Invoke-Git "commit -m `"$CommitMessage`""
} else {
    Write-Host "[auto-push] No changes to commit"
}

# Set remote to provided URL
$remotes = & git remote
if ($remotes -match "origin") {
    Write-Host "[auto-push] Updating existing remote 'origin'"
    try { Invoke-Git "remote remove origin" } catch { }
}
Invoke-Git "remote add origin $RepoUrl"

# Ensure branch is main
Invoke-Git "branch -M main"

# Push
Write-Host "[auto-push] Pushing to $RepoUrl (branch: main)"
Invoke-Git "push -u origin main"

Write-Host "[auto-push] Done"


