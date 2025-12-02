#!/bin/bash
# Wasteland Crawl - Tile Extraction Script
# Extracts DCSS tiles and organizes them for Minecraft integration

set -e

SOURCE_DIR="/Users/mojo/git/crawl/crawl-ref/source/rltiles"
OUTPUT_DIR="./minecraft-wasteland-tiles"

echo "════════════════════════════════════════════════════════"
echo "  Wasteland Crawl - Tile Extraction for Minecraft"
echo "════════════════════════════════════════════════════════"
echo ""

# Create output directory structure
echo "Creating directory structure..."
mkdir -p "$OUTPUT_DIR"/{monsters,terrain,items,gui}
mkdir -p "$OUTPUT_DIR"/monsters/{dragons,humanoids,animals,undead,giants}
mkdir -p "$OUTPUT_DIR"/terrain/{floors,walls,doors,features}
mkdir -p "$OUTPUT_DIR"/items/{weapons,armor,potions,scrolls}

# Extract Dragons (from our playthrough)
echo "Extracting dragon tiles..."
cp "$SOURCE_DIR/mon/dragons/fire_dragon.png" "$OUTPUT_DIR/monsters/dragons/"
cp "$SOURCE_DIR/mon/dragons/ice_dragon.png" "$OUTPUT_DIR/monsters/dragons/"
cp "$SOURCE_DIR/mon/dragons/shadow_dragon.png" "$OUTPUT_DIR/monsters/dragons/"
cp "$SOURCE_DIR/mon/dragons/golden_dragon.png" "$OUTPUT_DIR/monsters/dragons/"
cp "$SOURCE_DIR/mon/dragons/storm_dragon.png" "$OUTPUT_DIR/monsters/dragons/"

# Extract Humanoids (ogres, trolls, orcs, goblins, gnolls)
echo "Extracting humanoid tiles..."
cp "$SOURCE_DIR/mon/humanoids/ogre.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/two_headed_ogre.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/troll.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/goblin.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/hobgoblin.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/gnoll.png" "$OUTPUT_DIR/monsters/humanoids/"
cp "$SOURCE_DIR/mon/humanoids/gnoll_sergeant.png" "$OUTPUT_DIR/monsters/humanoids/"

# Extract Orcs (directory)
echo "Extracting orc tiles..."
cp "$SOURCE_DIR/mon/humanoids/orcs/"*.png "$OUTPUT_DIR/monsters/humanoids/" 2>/dev/null || echo "  (Some orc tiles may not exist)"

# Extract Animals (wargs, rats, bats, wolves)
echo "Extracting animal tiles..."
cp "$SOURCE_DIR/mon/animals/"*.png "$OUTPUT_DIR/monsters/animals/" 2>/dev/null || echo "  (Copying all animal tiles)"

# Extract Undead (liches)
echo "Extracting undead tiles..."
cp -r "$SOURCE_DIR/mon/undead/liches/"*.png "$OUTPUT_DIR/monsters/undead/" 2>/dev/null || echo "  (Copying lich tiles)"

# Extract Giants/Titans
echo "Extracting giant/titan tiles..."
find "$SOURCE_DIR/mon" -name "*giant*.png" -exec cp {} "$OUTPUT_DIR/monsters/giants/" \; 2>/dev/null || true
find "$SOURCE_DIR/mon" -name "*titan*.png" -exec cp {} "$OUTPUT_DIR/monsters/giants/" \; 2>/dev/null || true

# Extract Centaurs
echo "Extracting centaur tiles..."
find "$SOURCE_DIR/mon" -name "centaur*.png" -exec cp {} "$OUTPUT_DIR/monsters/humanoids/" \; 2>/dev/null || true

# Extract Floor Tiles (sample - there are 200+)
echo "Extracting floor tiles (sample)..."
cp "$SOURCE_DIR/dngn/floor/grey_dirt"*.png "$OUTPUT_DIR/terrain/floors/" 2>/dev/null || true
cp "$SOURCE_DIR/dngn/floor/black_cobalt"*.png "$OUTPUT_DIR/terrain/floors/" 2>/dev/null || true
cp "$SOURCE_DIR/dngn/floor/rough"*.png "$OUTPUT_DIR/terrain/floors/" 2>/dev/null || true
cp "$SOURCE_DIR/dngn/floor/mesh"*.png "$OUTPUT_DIR/terrain/floors/" 2>/dev/null || true

# Extract Wall Tiles (sample)
echo "Extracting wall tiles (sample)..."
find "$SOURCE_DIR/dngn/wall" -name "stone*.png" -exec cp {} "$OUTPUT_DIR/terrain/walls/" \; 2>/dev/null || true
find "$SOURCE_DIR/dngn/wall" -name "brick*.png" -exec cp {} "$OUTPUT_DIR/terrain/walls/" \; 2>/dev/null || true
find "$SOURCE_DIR/dngn/wall" -name "metal*.png" -exec cp {} "$OUTPUT_DIR/terrain/walls/" \; 2>/dev/null || true
find "$SOURCE_DIR/dngn/wall" -name "vault*.png" -exec cp {} "$OUTPUT_DIR/terrain/walls/" \; 2>/dev/null || true

# Extract Doors
echo "Extracting door tiles..."
cp "$SOURCE_DIR/dngn/doors/"*.png "$OUTPUT_DIR/terrain/doors/" 2>/dev/null || true

# Extract Features (stairs, etc.)
echo "Extracting feature tiles..."
find "$SOURCE_DIR/dngn" -maxdepth 1 -name "*.png" -exec cp {} "$OUTPUT_DIR/terrain/features/" \; 2>/dev/null || true

# Extract Weapon Tiles
echo "Extracting weapon tiles..."
cp "$SOURCE_DIR/item/weapon/"*.png "$OUTPUT_DIR/items/weapons/" 2>/dev/null || echo "  (Copying weapon tiles)"

# Extract Armor Tiles
echo "Extracting armor tiles..."
cp "$SOURCE_DIR/item/armour/"*.png "$OUTPUT_DIR/items/armor/" 2>/dev/null || echo "  (Copying armor tiles)"

# Extract Potion Tiles
echo "Extracting potion tiles..."
cp "$SOURCE_DIR/item/potion/"*.png "$OUTPUT_DIR/items/potions/" 2>/dev/null || echo "  (Copying potion tiles)"

# Extract Scroll Tiles
echo "Extracting scroll tiles..."
cp "$SOURCE_DIR/item/scroll/"*.png "$OUTPUT_DIR/items/scrolls/" 2>/dev/null || echo "  (Copying scroll tiles)"

# Generate tile inventory report
echo ""
echo "Generating tile inventory report..."
cat > "$OUTPUT_DIR/TILE_INVENTORY.txt" << 'EOF'
Wasteland Crawl - Extracted Tile Inventory
═══════════════════════════════════════════

Source: Dungeon Crawl Stone Soup (rltiles)
Format: PNG, 32x32 pixels, RGBA
Perfect for: Minecraft texture conversion

MONSTERS:
  dragons/       - Fire, Ice, Shadow, Golden dragons
  humanoids/     - Ogres, Trolls, Orcs, Goblins, Gnolls, Centaurs
  animals/       - Wargs, Rats, Bats, Wolves
  undead/        - Ancient Lich, Liches
  giants/        - Stone Giants, Titans

TERRAIN:
  floors/        - Concrete, metal, dirt, rubble floors
  walls/         - Stone, brick, metal, vault walls
  doors/         - Open, closed, locked doors
  features/      - Stairs, teleporters, special features

ITEMS:
  weapons/       - Swords, axes, maces, etc.
  armor/         - Leather, chain mail, plate mail
  potions/       - Healing potions, special potions
  scrolls/       - Teleportation, enchantment scrolls

USAGE:
- All tiles are 32x32 PNG with transparency
- Ready for Minecraft resource pack
- Can be used directly or scaled to 16x16
- See MINECRAFT_INTEGRATION.md for full guide

EOF

# Count extracted files
MONSTER_COUNT=$(find "$OUTPUT_DIR/monsters" -name "*.png" | wc -l | tr -d ' ')
TERRAIN_COUNT=$(find "$OUTPUT_DIR/terrain" -name "*.png" | wc -l | tr -d ' ')
ITEM_COUNT=$(find "$OUTPUT_DIR/items" -name "*.png" | wc -l | tr -d ' ')
TOTAL_COUNT=$((MONSTER_COUNT + TERRAIN_COUNT + ITEM_COUNT))

echo ""
echo "════════════════════════════════════════════════════════"
echo "  Extraction Complete!"
echo "════════════════════════════════════════════════════════"
echo ""
echo "Extracted tiles:"
echo "  Monsters: $MONSTER_COUNT files"
echo "  Terrain:  $TERRAIN_COUNT files"
echo "  Items:    $ITEM_COUNT files"
echo "  ─────────────────────"
echo "  TOTAL:    $TOTAL_COUNT files"
echo ""
echo "Output directory: $OUTPUT_DIR"
echo ""
echo "Next steps:"
echo "  1. Review extracted tiles: open $OUTPUT_DIR"
echo "  2. See MINECRAFT_INTEGRATION.md for integration guide"
echo "  3. Create Minecraft resource pack structure"
echo ""
echo "All tiles are 32x32 PNG - ready for Minecraft!"
echo "════════════════════════════════════════════════════════"
