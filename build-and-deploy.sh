#!/bin/bash

# Define the list of directories
directories=("authentication-core" "auth-server" "discovery-service" "email-service" "frontend" "gateway-service")

# Function to log messages
log_message() {
    local message="$1"
    echo "$(date +'%Y-%m-%d %H:%M:%S') - $message"
}

# Loop through each directory and run 'mvn clean package'
for dir in "${directories[@]}"; do
    log_message "Entering directory: $dir"
    cd "$dir" || { log_message "Failed to enter directory: $dir"; exit 1; }

    log_message "Running 'mvn clean package' in $dir"
    mvn clean package
    if [[ $? -eq 0 ]]; then
        log_message "'mvn clean package' completed successfully in $dir"
    else
        log_message "'mvn clean package' failed in $dir"
        exit 1
    fi

    log_message "Returning to parent directory"
    cd ..
done

# Run 'docker-compose build'
log_message "Running 'docker-compose build'"
docker-compose build
if [[ $? -eq 0 ]]; then
    log_message "'docker-compose build' completed successfully"
else
    log_message "'docker-compose build' failed"
    exit 1
fi

# Run 'docker-compose up'
log_message "Running 'docker-compose up'"
docker-compose up
if [[ $? -eq 0 ]]; then
    log_message "'docker-compose up' completed successfully"
else
    log_message "'docker-compose up' failed"
    exit 1
fi
