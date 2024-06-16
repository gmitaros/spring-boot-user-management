# Define the list of directories
$directories = @("authentication-core", "auth-server", "discovery-service", "email-service", "frontend", "gateway-service")

# Function to log messages
function Log-Message {
    param (
        [string]$message
    )
    Write-Host "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss') - $message"
}

# Loop through each directory and run 'mvn clean package'
foreach ($dir in $directories) {
    Log-Message "Entering directory: $dir"
    Set-Location $dir

    Log-Message "Running 'mvn clean package' in $dir"
    mvn clean package

    if ($LASTEXITCODE -eq 0) {
        Log-Message "'mvn clean package' completed successfully in $dir"
    } else {
        Log-Message "'mvn clean package' failed in $dir"
        Exit 1
    }

    Log-Message "Returning to parent directory"
    Set-Location ..
}

# Run 'docker-compose build'
Log-Message "Running 'docker-compose build'"
docker-compose build

if ($LASTEXITCODE -eq 0) {
    Log-Message "'docker-compose build' completed successfully"
} else {
    Log-Message "'docker-compose build' failed"
    Exit 1
}

# Run 'docker-compose up'
Log-Message "Running 'docker-compose up'"
docker-compose up

if ($LASTEXITCODE -eq 0) {
    Log-Message "'docker-compose up' completed successfully"
} else {
    Log-Message "'docker-compose up' failed"
    Exit 1
}
