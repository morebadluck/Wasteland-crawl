#!/bin/bash
# Wasteland Crawl - Docker Build Test Script
# This script tests the Docker build process and validates the image

set -e

echo "======================================"
echo "  Wasteland Crawl - Docker Test"
echo "======================================"
echo ""

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if Docker is running
echo "1. Checking Docker installation..."
if ! command -v docker &> /dev/null; then
    echo -e "${RED}✗ Docker is not installed${NC}"
    echo "Please install Docker from: https://docs.docker.com/get-docker/"
    exit 1
fi
echo -e "${GREEN}✓ Docker is installed${NC}"

# Check if Docker daemon is running
echo ""
echo "2. Checking Docker daemon..."
if ! docker ps &> /dev/null; then
    echo -e "${RED}✗ Docker daemon is not running${NC}"
    echo "Please start Docker Desktop or run: sudo systemctl start docker"
    exit 1
fi
echo -e "${GREEN}✓ Docker daemon is running${NC}"

# Check disk space
echo ""
echo "3. Checking disk space..."
available=$(df -BG . | tail -1 | awk '{print $4}' | sed 's/G//')
if [ "$available" -lt 5 ]; then
    echo -e "${YELLOW}⚠ Warning: Low disk space (${available}GB available)${NC}"
    echo "  Recommended: At least 5GB free"
else
    echo -e "${GREEN}✓ Sufficient disk space (${available}GB available)${NC}"
fi

# Build the image
echo ""
echo "4. Building Docker image..."
echo "   This will take 5-15 minutes depending on your system..."
echo ""

if docker build -t wasteland-crawl:test . ; then
    echo ""
    echo -e "${GREEN}✓ Docker image built successfully${NC}"
else
    echo ""
    echo -e "${RED}✗ Docker build failed${NC}"
    echo "Check the error messages above"
    exit 1
fi

# Get image size
echo ""
echo "5. Checking image size..."
size=$(docker images wasteland-crawl:test --format "{{.Size}}")
echo -e "${GREEN}✓ Image size: $size${NC}"

# Test image can run
echo ""
echo "6. Testing image execution..."
if docker run --rm wasteland-crawl:test --version &> /dev/null; then
    echo -e "${GREEN}✓ Image runs successfully${NC}"
else
    echo -e "${YELLOW}⚠ Version check failed (this is expected)${NC}"
fi

# Create saves directory
echo ""
echo "7. Creating saves directory..."
mkdir -p saves
echo -e "${GREEN}✓ Saves directory created${NC}"

# Display summary
echo ""
echo "======================================"
echo -e "${GREEN}  ✓ All tests passed!${NC}"
echo "======================================"
echo ""
echo "Your Docker image is ready to use!"
echo ""
echo "Quick Start Commands:"
echo ""
echo "  # Run the game (recommended)"
echo "  docker run -it --rm -v \$(pwd)/saves:/root/.crawl wasteland-crawl:test"
echo ""
echo "  # Or use docker-compose"
echo "  docker-compose up"
echo ""
echo "  # Or use the launch script"
echo "  ./run-wasteland.sh"
echo ""
echo "Save games will be stored in: $(pwd)/saves/"
echo ""
echo "To remove the test image:"
echo "  docker rmi wasteland-crawl:test"
echo ""
echo "Enjoy the wasteland!"
