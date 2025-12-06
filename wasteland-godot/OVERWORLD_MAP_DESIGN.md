# Wasteland Crawl - Overworld Map System (Simpler Approach)

**Alternative to Full 3D World**: Strategic Map View
**Inspired by**: DCSS Overmap, FTL, Slay the Spire worldmap
**Focus**: Dungeon crawling, not open-world exploration

---

## Core Concept

**No 3D Exploration** - Instead:
1. **Strategic Map Screen** - Top-down 2D view of wasteland
2. **Portal Markers** - Icons showing dungeon entrances
3. **Click to Enter** - Select portal, load dungeon scene
4. **Dungeons = Core Gameplay** - Keep existing ASCII grid system

---

## Overworld Map Structure

### Map Screen Layout

```
┌─────────────────────────────────────────────────────────────┐
│                   WASTELAND OVERWORLD                        │
│  ┌─────────────────────────────────────────────────────────┐│
│  │                                                           ││
│  │     🏚️ (Housing)     ⛪ (Cursed Church)                  ││
│  │                                                           ││
│  │            🏛️ (Mall)              🎯 (You)               ││
│  │                                                           ││
│  │  🏭 (Military Base)        🐍 (Snake Pit Portal)         ││
│  │                                                           ││
│  │          ⚔️ (Orc Mines)        🏔️ (Lair Portal)         ││
│  │                                                           ││
│  │                    ☠️ (Vaults)                            ││
│  │                                                           ││
│  └─────────────────────────────────────────────────────────┘│
│                                                               │
│  Selected: Lair of Beasts                                    │
│  Level: 8    Status: Unexplored                              │
│  [Enter Dungeon]   [Return to Town]                          │
└─────────────────────────────────────────────────────────────┘
```

### Map Data Structure

```gdscript
class_name OverworldMap
extends Control

# Map size (logical coordinates, not pixels)
const MAP_WIDTH = 200
const MAP_HEIGHT = 150

# Portal locations
var portals: Array[PortalData] = []
var structures: Array[StructureData] = []

# Player position on map
var player_position: Vector2 = Vector2(100, 75)  # Center start
```

---

## Portal Generation

### Random Placement Algorithm

```gdscript
func generate_overworld():
    var spawn_pos = Vector2(MAP_WIDTH / 2, MAP_HEIGHT / 2)
    player_position = spawn_pos

    # Place spawn town
    place_structure("spawn_town", spawn_pos, 1)

    # Place dungeon portals (from Minecraft design)
    place_portal_cluster("lair", 3, 8, 500, 1500)  # 3 Lairs, level 8, 500-1500m from spawn
    place_portal_cluster("orc_mines", 2, 10, 800, 2000)
    place_portal_cluster("snake_pit", 2, 15, 1500, 3000)
    place_portal_cluster("elven_halls", 1, 18, 2000, 4000)
    place_portal_cluster("vaults", 1, 22, 3000, 5000)

    # Place structures
    place_structures_random("housing", 10, 1, 8, 100, 1000)
    place_structures_random("mall", 5, 8, 15, 500, 2000)
    place_structures_random("church_cursed", 3, 12, 20, 1000, 3000)
    place_structures_random("military_base", 4, 10, 18, 800, 2500)

func place_portal_cluster(type: String, count: int, level: int, min_dist: float, max_dist: float):
    for i in range(count):
        var angle = randf() * TAU
        var distance = randf_range(min_dist, max_dist)
        var pos = player_position + Vector2(cos(angle), sin(angle)) * distance

        var portal = PortalData.new()
        portal.type = type
        portal.level = level + randi_range(-2, 2)  # ±2 level variance
        portal.position = pos
        portal.discovered = false  # Fog of war
        portal.depth = randi_range(3, 15)  # Random dungeon depth

        portals.append(portal)
```

### Portal Data

```gdscript
class_name PortalData

var type: String  # "lair", "snake_pit", "vaults", etc.
var level: int  # Difficulty (1-25)
var position: Vector2  # Map coordinates
var discovered: bool = false  # Fog of war
var cleared: bool = false  # Has player beaten it?
var depth: int = 10  # Number of floors
var has_rune: bool = false  # End reward
var rune_type: String = ""  # "serpentine", "silver", etc.

func get_icon() -> String:
    match type:
        "lair": return "🏔️"
        "orc_mines": return "⚔️"
        "snake_pit": return "🐍"
        "elven_halls": return "🌲"
        "vaults": return "☠️"
        "shoals": return "🌊"
        _: return "❓"
```

---

## Map UI Implementation

### Map Rendering

```gdscript
extends Control

@onready var map_container: Control = $MapContainer
@onready var portal_icons: Node2D = $MapContainer/Portals
@onready var player_icon: Sprite2D = $MapContainer/Player

var zoom_level: float = 1.0
var camera_offset: Vector2 = Vector2.ZERO

func _ready():
    render_map()

func render_map():
    # Clear old icons
    for child in portal_icons.get_children():
        child.queue_free()

    # Render portals
    for portal in overworld.portals:
        if portal.discovered or distance_to_player(portal.position) < 100:
            create_portal_icon(portal)

    # Render structures
    for structure in overworld.structures:
        if structure.discovered:
            create_structure_icon(structure)

    # Update player position
    player_icon.position = world_to_screen(overworld.player_position)

func create_portal_icon(portal: PortalData):
    var icon = Button.new()
    icon.text = portal.get_icon()
    icon.position = world_to_screen(portal.position)
    icon.pressed.connect(func(): select_portal(portal))
    portal_icons.add_child(icon)

func select_portal(portal: PortalData):
    # Show portal info panel
    $InfoPanel.show_portal_info(portal)
```

---

## Travel System

### Movement Options

**Option A: Instant Travel**
- Click portal → immediately enter dungeon
- No travel time/risk
- Simplest implementation

**Option B: Travel Phase**
- Click portal → "traveling" state
- Random encounters on the way
- Can retreat back to town
- More engaging, but complex

### Recommended: Option A (Start Simple)

```gdscript
func enter_dungeon(portal: PortalData):
    # Save overworld state
    save_overworld_state()

    # Load dungeon scene
    var dungeon_scene = load("res://scenes/dungeon.tscn").instantiate()
    dungeon_scene.configure(portal.type, portal.level, portal.depth)

    get_tree().root.add_child(dungeon_scene)
    get_tree().current_scene = dungeon_scene
    queue_free()

func return_to_overworld():
    # Load overworld map scene
    get_tree().change_scene_to_file("res://scenes/overworld_map.tscn")
```

---

## Fog of War System

### Discovery Mechanics

**Portals become discovered when:**
1. **Close to player** - Within 100 meters
2. **Explored structures** - Finding maps/intel in dungeons
3. **NPC hints** - Quest markers

```gdscript
func update_fog_of_war():
    for portal in portals:
        if not portal.discovered:
            var distance = portal.position.distance_to(player_position)
            if distance < 100:  # Discovery radius
                portal.discovered = true
                print("Discovered: %s" % portal.type)
```

---

## Structure System

### Structure Types

**Same as Minecraft design**:
1. **Spawn Town** - Safe zone, shops, NPCs
2. **Housing Ruins** - Low-level scavenging
3. **Grocery Stores** - Basic loot
4. **Malls** - High-level loot, mini-dungeons
5. **Cursed Churches** - Hell portal entrances
6. **Military Bases** - Orc strongholds

### Structure Interaction

```gdscript
class_name StructureData

var type: String
var level: int
var position: Vector2
var discovered: bool = false
var looted: bool = false

func interact():
    match type:
        "spawn_town":
            # Show town menu (shop, rest, quest board)
            get_tree().change_scene_to_file("res://scenes/town.tscn")

        "housing", "grocery", "mall":
            # Enter small explorable area (optional)
            # Or just give loot directly
            give_loot()

        "church_cursed":
            # Gateway to Hell dungeons
            if player.level >= 20:
                show_hell_portals()

        "military_base":
            # Enter Orc dungeon
            enter_orc_stronghold()
```

---

## Map Visuals

### Visual Styles (Pick One)

**Option 1: Tactical Map** (Recommended)
- Clean, minimalist design
- Icons for locations
- Grid overlay
- Like XCOM or Fire Emblem world map

**Option 2: Illustrated Map**
- Hand-drawn wasteland
- Artistic portal markers
- More atmospheric
- Like Slay the Spire

**Option 3: Satellite View**
- Grayscale terrain
- Red markers for danger
- Military aesthetic
- Like Metal Gear Solid map

### Example Theme (Tactical)

```gdscript
# Map theme colors
const COLOR_BACKGROUND = Color(0.1, 0.1, 0.12)  # Dark gray
const COLOR_GRID = Color(0.2, 0.2, 0.22, 0.3)  # Faint gridlines
const COLOR_PORTAL_LOW = Color(0.3, 0.8, 0.3)  # Green (safe)
const COLOR_PORTAL_MED = Color(0.9, 0.7, 0.2)  # Yellow (danger)
const COLOR_PORTAL_HIGH = Color(0.9, 0.2, 0.2)  # Red (extreme)
const COLOR_PLAYER = Color(0.3, 0.6, 1.0)  # Blue
```

---

## Integration with Existing Systems

### Keep Current Dungeon System

**No changes needed** to:
- DungeonGenerator.gd
- Grid-based combat
- Turn-based mechanics
- Monster AI
- Loot/XP systems

**Only add**:
- Overworld map scene
- Portal manager
- Save/load for map state

### Scene Structure

```
res://scenes/
├── overworld_map.tscn        ← NEW: Strategic map
├── town.tscn                  ← NEW: Safe zone (optional)
├── dungeon.tscn               ← EXISTING: Reuse current system
└── ui/
    ├── portal_info_panel.tscn ← NEW: Shows dungeon details
    └── map_hud.tscn           ← NEW: Map controls
```

---

## Implementation Phases

### Phase 1: Basic Map System (Start Here)
- [ ] Create overworld_map.tscn scene
- [ ] Implement PortalData class
- [ ] Random portal generation
- [ ] Simple icon rendering
- [ ] Click to enter dungeon

### Phase 2: Map Features
- [ ] Fog of war system
- [ ] Portal info panel
- [ ] Structure placement
- [ ] Discovery mechanics

### Phase 3: Polish
- [ ] Map zoom/pan controls
- [ ] Visual themes
- [ ] Animations (portal glow, etc.)
- [ ] Sound effects

### Phase 4: Advanced Features (Optional)
- [ ] Travel encounters
- [ ] Map markers/notes
- [ ] Quest tracking
- [ ] Dynamic events (rifts appear/disappear)

---

## Advantages of This Approach

✅ **Much Faster** - No 3D terrain generation
✅ **Keeps Focus** - Dungeons are the core gameplay
✅ **DCSS-Like** - Similar to how DCSS treats overworld
✅ **Easier to Balance** - Control portal placement precisely
✅ **Better Performance** - 2D UI vs 3D rendering
✅ **Clearer Progression** - See all portals, plan route

---

## Example Flow

**Player Experience**:
1. Start game → Overworld map opens
2. See spawn town (safe) + 3 nearby portals
3. Click "Lair of Beasts" (Level 8) → Enter dungeon
4. Play through 10 floors of DCSS-style dungeon
5. Beat final boss → Get Serpentine Rune
6. Return to map → Lair marked as "Cleared"
7. Discover new portal (Snake Pit, Level 15)
8. Repeat until 3+ runes → Access Realm of Zot

---

*This approach is **recommended** for faster implementation and better alignment with DCSS philosophy.*
*Full 3D world (WORLD_3D_DESIGN.md) remains as optional future enhancement.*
