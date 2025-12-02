#!/bin/bash
# Wasteland Crawl - Local Build Script (No Docker)

set -e

echo "========================================"
echo "  Wasteland Crawl: Local Build"
echo "========================================"
echo ""

# Check if we're in the right directory
if [ ! -d "crawl-ref/source" ]; then
    echo "Error: Must be run from the crawl root directory"
    exit 1
fi

# Detect OS
OS=$(uname -s)

echo "Detected OS: $OS"
echo ""

# Check dependencies
case "$OS" in
    Linux)
        echo "Checking dependencies..."
        if ! command -v make &> /dev/null; then
            echo "Error: make is not installed"
            echo "Install with: sudo apt install build-essential"
            exit 1
        fi

        if ! dpkg -s libncursesw5-dev &> /dev/null 2>&1; then
            echo "Warning: Some dependencies may be missing"
            echo "Install with:"
            echo "  sudo apt install build-essential libncursesw5-dev bison flex \\"
            echo "    liblua5.1-0-dev libsqlite3-dev libz-dev pkg-config python3-yaml"
            echo ""
            read -p "Continue anyway? (y/n) " -n 1 -r
            echo
            if [[ ! $REPLY =~ ^[Yy]$ ]]; then
                exit 1
            fi
        fi
        ;;
    Darwin)
        echo "macOS detected"
        if ! command -v make &> /dev/null; then
            echo "Error: Xcode Command Line Tools not installed"
            echo "Install with: xcode-select --install"
            exit 1
        fi
        ;;
    *)
        echo "Warning: Unsupported OS - $OS"
        echo "Attempting to build anyway..."
        ;;
esac

# Ask user for build type
echo "Select build type:"
echo "  1) Console (ASCII) - Recommended"
echo "  2) Tiles (Graphical)"
echo ""
read -p "Enter choice (1-2): " choice

cd crawl-ref/source

case $choice in
    1)
        echo ""
        echo "Building console version..."
        make -j$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4) TILES=n
        ;;
    2)
        echo ""
        echo "Building tiles version..."
        make -j$(nproc 2>/dev/null || sysctl -n hw.ncpu 2>/dev/null || echo 4) TILES=y
        ;;
    *)
        echo "Invalid choice"
        exit 1
        ;;
esac

echo ""
echo "========================================"
echo "  Build Complete!"
echo "========================================"
echo ""
echo "To play the game, run:"
echo "  cd crawl-ref/source"
echo "  ./crawl"
echo ""
echo "Or use the quick start script:"
echo "  ./start-wasteland.sh"
