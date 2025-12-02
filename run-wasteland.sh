#!/bin/bash
# Wasteland Crawl - Quick Launch Script

set -e

echo "========================================"
echo "  Wasteland Crawl: Ruins of America"
echo "========================================"
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed."
    echo "Please install Docker from: https://docs.docker.com/get-docker/"
    exit 1
fi

# Check if docker-compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed."
    echo "Please install Docker Compose from: https://docs.docker.com/compose/install/"
    exit 1
fi

# Create saves directory if it doesn't exist
mkdir -p saves

echo "Building Wasteland Crawl Docker image..."
echo ""

# Build the Docker image
docker-compose build

echo ""
echo "========================================"
echo "  Starting Wasteland Crawl..."
echo "========================================"
echo ""
echo "Controls:"
echo "  - Arrow keys or hjkl: Move"
echo "  - ?: Help menu"
echo "  - o: Auto-explore"
echo "  - S: Save and quit"
echo ""
echo "Your save games will be stored in ./saves/"
echo ""
echo "Press Enter to begin..."
read

# Run the game
docker-compose run --rm wasteland-crawl

echo ""
echo "Thanks for playing Wasteland Crawl!"
echo "Survive another day, wanderer."
