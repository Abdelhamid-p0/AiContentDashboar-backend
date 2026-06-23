param(
    [string]$SqlFilePath = (Join-Path $PSScriptRoot 'backup.sql')
)

$envFile = Join-Path $PSScriptRoot '.env'

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if (-not $line -or $line.StartsWith('#')) { return }

        $parts = $line.Split('=', 2)
        if ($parts.Count -eq 2) {
            [System.Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), 'Process')
        }
    }
}

if (-not (Test-Path $SqlFilePath)) {
    throw "SQL file not found: $SqlFilePath"
}

if (-not (Test-Path (Join-Path $PSScriptRoot 'mvnw.cmd'))) {
    throw 'mvnw.cmd is not available in the backend folder.'
}

$resolvedSqlFile = (Resolve-Path $SqlFilePath).Path

$env:BACKUP_SQL_FILE = $resolvedSqlFile
$mavenArgs = @(
    '-q'
    '-DskipTests'
    'compile'
    'exec:java'
    '-Dexec.mainClass=com.quiz.ai.tools.BackupImporter'
)

& "$PSScriptRoot\mvnw.cmd" @mavenArgs
Remove-Item Env:BACKUP_SQL_FILE -ErrorAction SilentlyContinue
exit $LASTEXITCODE