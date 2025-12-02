#!/bin/bash
# Wasteland Crawl - Quick Start (for local builds)

set -e

echo "========================================"
echo "  Wasteland Crawl: Ruins of America"
echo "========================================"
echo ""

# Check if game is built
if [ ! -f "crawl-ref/source/crawl" ]; then
    echo "Error: Game not built yet!"
    echo ""
    echo "Build the game first:"
    echo "  ./build-local.sh"
    echo ""
    echo "Or use Docker:"
    echo "  ./run-wasteland.sh"
    exit 1
fi

cd crawl-ref/source

echo "Controls:"
echo "  - Arrow keys or hjkl: Move"
echo "  - ?: Help menu"
echo "  - o: Auto-explore"
echo "  - S: Save and quit"
echo ""
echo "Survive another day, wanderer."
echo ""
sleep 2

# Launch the game
./crawl
