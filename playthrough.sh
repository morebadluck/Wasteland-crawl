#!/bin/bash
# Wasteland Crawl - Automated Playthrough Demo

echo "════════════════════════════════════════════════════════════"
echo "  WASTELAND CRAWL - Live Playthrough"
echo "  Character: TestSurvivor (Minotaur Berserker)"
echo "════════════════════════════════════════════════════════════"
echo ""
echo "Starting game..."
echo ""

# Create input sequence for automated demo
cat > /tmp/crawl_input.txt << 'EOF'
c
a
a




o
o
o
o
o
S
y
EOF

# Run crawl with the input, capture output
./crawl -name TestSurvivor -species Minotaur -background Berserker < /tmp/crawl_input.txt 2>&1 | head -500

echo ""
echo "════════════════════════════════════════════════════════════"
echo "  End of demo"
echo "════════════════════════════════════════════════════════════"
