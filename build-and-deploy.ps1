# Run Maven clean install
Write-Host "Running 'mvn clean install'"
$mavenResult = Start-Process -FilePath "mvn" -ArgumentList "clean install" -NoNewWindow -Wait -PassThru

# Check if Maven build was successful
if ($mavenResult.ExitCode -eq 0) {
    Write-Host "Maven build successful"

    # Run Docker Compose build
    Write-Host "Running 'docker-compose build'"
    $dockerBuildResult = Start-Process -FilePath "docker-compose" -ArgumentList "build" -NoNewWindow -Wait -PassThru

    # Check if Docker Compose build was successful
    if ($dockerBuildResult.ExitCode -eq 0) {
        Write-Host "Docker Compose build successful"

        # Run Docker Compose up
        Write-Host "Running 'docker-compose up'"
        Start-Process -FilePath "docker-compose" -ArgumentList "up" -NoNewWindow -Wait
    } else {
        Write-Host "Docker Compose build failed with exit code $($dockerBuildResult.ExitCode)"
        Exit $dockerBuildResult.ExitCode
    }
} else {
    Write-Host "Maven build failed with exit code $($mavenResult.ExitCode)"
    Exit $mavenResult.ExitCode
}
