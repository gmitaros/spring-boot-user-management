#!/bin/bash

# Run Maven clean install
echo "Running 'mvn clean install'"
mvn clean install
mavenResult=$?

# Check if Maven build was successful
if [ $mavenResult -eq 0 ]; then
    echo "Maven build successful"

    # Assuming Docker Compose file is in the current directory
    echo "Running 'docker-compose build'"
    docker-compose build
    dockerBuildResult=$?

    # Check if Docker Compose build was successful
    if [ $dockerBuildResult -eq 0 ]; then
        echo "Docker Compose build successful"

        # Run Docker Compose up
        echo "Running 'docker-compose up'"
        docker-compose up
    else
        echo "Docker Compose build failed with exit code $dockerBuildResult"
        exit $dockerBuildResult
    fi
else
    echo "Maven build failed with exit code $mavenResult"
    exit $mavenResult
fi
