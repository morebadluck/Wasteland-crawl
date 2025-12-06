# Wasteland Crawl - Godot Edition

DCSS-inspired roguelike implemented in Godot Engine 4.5.1

## Project Structure

```
wasteland-godot/
├── assets/
│   └── fonts/           # IBM Plex Mono font
├── data/                # Game data (races, classes, spells - to be added)
├── scenes/
│   └── main.tscn        # Main game scene
├── scripts/
│   ├── game.gd          # Main game controller
│   ├── grid.gd          # Grid-based map system
│   ├── player.gd        # Player entity
│   └── dungeon_generator.gd  # BSP dungeon generation
├── project.godot        # Godot project configuration
└── icon.svg             # Project icon
```

## Features Implemented

### Core Systems
- ✅ Grid-based map system (80x24 tiles)
- ✅ Turn-based movement (arrow keys, numpad, diagonals)
- ✅ Simple dungeon generation (BSP algorithm)
- ✅ Player character with stats (HP, MP, level)
- ✅ Equipment slots (10 DCSS-style slots)
- ✅ Camera following player
- ✅ HUD with player stats

### Controls
- **Arrow Keys / Numpad 8,4,6,2**: Move (cardinal directions)
- **Numpad 7,9,1,3**: Move diagonally
- **I**: Equipment screen (placeholder)
- **M**: Skills screen (placeholder)
- **Z**: Cast spell (placeholder)
- **ESC**: Quit game

## How to Run

### Option 1: Godot Editor
1. Open Godot 4.5.1
2. Click "Import"
3. Navigate to `/Users/mojo/git/crawl/wasteland-godot`
4. Select `project.godot`
5. Click "Import & Edit"
6. Press F5 to run

### Option 2: Command Line
```bash
cd /Users/mojo/git/crawl/wasteland-godot
godot --path . --verbose
```

Or to run directly:
```bash
godot --path /Users/mojo/git/crawl/wasteland-godot
```

## Next Steps

### Phase 1: Core Gameplay (Current)
- [x] Basic grid system
- [x] Turn-based movement
- [x] Dungeon generation
- [ ] Monster AI
- [ ] Basic combat system
- [ ] Item system
- [ ] Inventory management

### Phase 2: DCSS Features
- [ ] Equipment screen UI
- [ ] Skills system
- [ ] Magic system (spells, memorization)
- [ ] Religion system (gods, altars)
- [ ] Character creation (races, backgrounds)
- [ ] Experience and leveling

### Phase 3: Content
- [ ] Multiple dungeon branches
- [ ] Monster variety (ported from Minecraft version)
- [ ] Item types (weapons, armor, jewelry)
- [ ] Vaults and special rooms
- [ ] Unique monsters

### Phase 4: Polish
- [ ] Save/load system
- [ ] Message log
- [ ] Death screen
- [ ] Victory conditions
- [ ] Sound effects and music

## Differences from Minecraft Version

### Advantages
✅ **Native turn-based support** - No world freezing hacks needed
✅ **Full UI control** - Can implement exact DCSS interface
✅ **Cleaner codebase** - GDScript is simpler than Java/Forge
✅ **Better performance** - Lighter weight engine
✅ **Roguelike-friendly** - Built for 2D grid-based games

### Trade-offs
⚠️ **No 3D visuals** - Classic 2D/ASCII instead of first-person 3D
⚠️ **Smaller player base** - Standalone game vs Minecraft mod
⚠️ **Need to rebuild content** - Porting systems from Minecraft

## Technical Details

### Tile System
- 32x32 pixel tiles
- 80 columns x 24 rows (classic roguelike dimensions)
- ASCII characters rendered with IBM Plex Mono font
- Color-coded tile types

### Turn System
- Pure turn-based (no real-time element)
- Player input triggers turn processing
- Camera follows player automatically
- No world freezing needed

### Map Generation
- Binary Space Partitioning (BSP) algorithm
- 8-12 rooms per dungeon level
- L-shaped corridors connecting rooms
- Easily extensible for features like doors, vaults, etc.

## License

Same as original Wasteland Crawl Minecraft mod.
