# Wasteland Crawl - 3D World Design (Godot)

**Based on**: Minecraft Organic World System + DCSS Dungeon Structure
**Engine**: Godot 4.5.1
**Date**: December 2025

---

## Architecture Overview

### Two-Tier World System

**Tier 1: Overworld (3D Wasteland)**
- Open world with noise-based terrain
- Chunk-based loading/unloading (16x16 or 32x32 chunks)
- Structures: Houses, Malls, Churches, Military Bases, Portal Entrances
- Dynamic difficulty zones (noise-based clustering)

**Tier 2: Dungeons (Instanced Levels)**
- Separate scenes per dungeon level (like DCSS)
- Procedurally generated floors
- Turn-based grid combat (hybrid 3D visuals + grid mechanics)
- Stairs connect levels

---

## Chunk System Design

### Chunk Specifications

```gdscript
const CHUNK_SIZE = 32  # 32x32 tiles per chunk
const TILE_SIZE = 2.0  # 2 meters per tile (64m chunk)
const RENDER_DISTANCE = 3  # Load 3 chunks in each direction
const UNLOAD_DISTANCE = 5  # Unload chunks 5+ away
```

### Chunk Structure

```
ChunkData:
  - position: Vector2i (chunk coords, not world coords)
  - tiles: Dictionary[Vector2i, TileType]  # Local tile positions
  - entities: Array[EntitySpawn]  # Monsters, NPCs
  - structures: Array[StructureData]  # Buildings, ruins
  - difficulty_level: int (1-25)
  - corruption_level: float (0.0-1.0)
```

### Chunk Loading Strategy

**Player Movement Triggers**:
1. Player crosses chunk boundary
2. Check which chunks should be loaded (within RENDER_DISTANCE)
3. Load new chunks from disk or generate fresh
4. Unload chunks beyond UNLOAD_DISTANCE
5. Save unloaded chunks if modified

**Godot Implementation**:
```gdscript
class_name ChunkManager
extends Node3D

var loaded_chunks: Dictionary = {}  # Vector2i -> Chunk
var chunk_save_path = "user://chunks/"

func update_chunks(player_pos: Vector3):
    var player_chunk = world_to_chunk(player_pos)

    # Load nearby chunks
    for x in range(-RENDER_DISTANCE, RENDER_DISTANCE + 1):
        for z in range(-RENDER_DISTANCE, RENDER_DISTANCE + 1):
            var chunk_pos = player_chunk + Vector2i(x, z)
            if not loaded_chunks.has(chunk_pos):
                load_or_generate_chunk(chunk_pos)

    # Unload distant chunks
    for chunk_pos in loaded_chunks.keys():
        var distance = player_chunk.distance_to(chunk_pos)
        if distance > UNLOAD_DISTANCE:
            unload_chunk(chunk_pos)
```

---

## Terrain Generation (Overworld)

### Noise-Based System

**Godot FastNoiseLite** (built-in):
```gdscript
var terrain_noise = FastNoiseLite.new()
terrain_noise.noise_type = FastNoiseLite.TYPE_SIMPLEX
terrain_noise.frequency = 0.01
terrain_noise.fractal_octaves = 4

var difficulty_noise = FastNoiseLite.new()
difficulty_noise.noise_type = FastNoiseLite.TYPE_PERLIN
difficulty_noise.frequency = 0.005
difficulty_noise.seed = terrain_noise.seed + 1000
```

### Difficulty Calculation

```gdscript
func calculate_difficulty(world_pos: Vector2i) -> int:
    # Base biome level (1-25)
    var biome = get_biome(world_pos)
    var base_level = BIOME_LEVELS[biome]  # Wasteland:3, Ruins:8, Dark Zone:18

    # Noise factor (-5 to +10)
    var noise_val = difficulty_noise.get_noise_2dv(world_pos)
    var noise_bonus = int(noise_val * 15.0) - 5

    # Distance from spawn (gentle curve)
    var distance = world_pos.distance_to(Vector2i.ZERO)
    var dist_bonus = min(10, int(distance / 500.0))  # +1 per 500 meters

    # Structure bonus (added later)
    var structure_bonus = 0

    return clamp(base_level + noise_bonus + dist_bonus + structure_bonus, 1, 25)
```

### Biome Types

```gdscript
enum BiomeType {
    WASTELAND,      # Cracked earth, sparse vegetation (Level 1-5)
    RUINS,          # Collapsed buildings, rubble (Level 3-10)
    IRRADIATED,     # Glowing craters, toxic pools (Level 8-15)
    CORRUPTED,      # Dimensional tears, purple fog (Level 12-20)
    HELLSCAPE       # Burning ground, demon portals (Level 18-25)
}
```

---

## Structure Generation

### Structure Types

**From Minecraft Design**:
1. **Housing** - Small ruins, 1-8 level
2. **Grocery Stores** - Strip malls, 3-10 level
3. **Malls** - Large ruins, 8-20 level
4. **Churches** - Cursed buildings, 12-22 level
5. **Military Bases** - Fortified compounds, 8-20 level
6. **Portal Entrances** - Dungeon access points, varies

### Structure Placement Algorithm

```gdscript
func generate_structures_for_chunk(chunk_pos: Vector2i):
    var world_pos = chunk_to_world(chunk_pos)
    var difficulty = calculate_difficulty(world_pos)

    # Roll for structure placement
    var structure_chance = randf()

    if structure_chance < 0.05:  # 5% per chunk
        var structure_type = select_structure_by_difficulty(difficulty)
        place_structure(chunk_pos, structure_type)
```

### Structure Data

```gdscript
class_name StructureData

var type: String  # "housing", "mall", "portal_lair", etc.
var level: int  # Structure difficulty
var center_pos: Vector2i  # World position
var bounds: Rect2i  # Bounding box
var has_portal: bool  # Is this a dungeon entrance?
var portal_type: String  # "lair", "snake_pit", "vaults", etc.
```

---

## GridMap vs MeshInstance3D

### Option 1: GridMap (Recommended for Start)

**Pros**:
- Built-in voxel system (like Minecraft)
- Easy tile placement
- Fast for prototyping
- Good for chunked worlds

**Cons**:
- Limited to cube-based tiles
- Performance limits with large worlds

```gdscript
var grid_map = GridMap.new()
grid_map.mesh_library = preload("res://assets/wasteland_tiles.meshlib")

# Place tiles
grid_map.set_cell_item(Vector3i(x, 0, z), TILE_GROUND)
grid_map.set_cell_item(Vector3i(x, 1, z), TILE_WALL)
```

### Option 2: Custom MeshInstance3D (Later Optimization)

**Pros**:
- Full control over mesh generation
- Can optimize with greedy meshing
- Better performance at scale

**Cons**:
- More complex implementation
- Need custom collision

---

## Dungeon System (Underground)

### Dungeon Instances

**Separate Scenes** (like Minecraft portals):
```
res://scenes/dungeons/
├── lair_01.tscn
├── lair_02.tscn
├── snake_pit_01.tscn
├── vaults_01.tscn
etc.
```

### Dungeon Entry/Exit

```gdscript
class_name PortalEntrance
extends Area3D

var dungeon_type: String = "lair"
var dungeon_level: int = 8
var target_scene: String = "res://scenes/dungeons/lair_procedural.tscn"

func _on_body_entered(body):
    if body is Player:
        # Save overworld state
        get_node("/root/WorldManager").save_overworld_state()

        # Load dungeon scene
        get_tree().change_scene_to_file(target_scene)
```

### Procedural Dungeon Floors

```gdscript
# In dungeon scene
class_name DungeonLevel
extends Node3D

var floor_number: int = 1
var dungeon_type: String = "lair"
var grid: Grid  # Reuse existing Grid system!

func _ready():
    generate_floor()
    spawn_monsters()
    place_stairs()

func generate_floor():
    # Use existing DungeonGenerator.gd
    # But render in 3D instead of 2D
    var dungeon_data = DungeonGenerator.generate(grid, floor_number, self)

    # Convert 2D grid to 3D GridMap
    for y in range(grid.MAP_HEIGHT):
        for x in range(grid.MAP_WIDTH):
            var tile = grid.get_tile(Vector2i(x, y))
            place_3d_tile(Vector3i(x, 0, y), tile)
```

---

## Hybrid System: 3D Visuals + Grid Combat

### Camera System

**Two Camera Modes**:

1. **Exploration Mode** (Free Camera):
   - Third-person or first-person
   - WASD movement
   - Encounters trigger combat

2. **Combat Mode** (Top-Down Grid):
   - Switch to overhead tactical view
   - Click to move on grid
   - Turn-based like current 2D system

```gdscript
class_name CameraController
extends Node3D

enum Mode { EXPLORATION, COMBAT }
var current_mode = Mode.EXPLORATION

@onready var exploration_camera: Camera3D = $ExplorationCamera
@onready var combat_camera: Camera3D = $CombatCamera

func switch_to_combat():
    current_mode = Mode.COMBAT
    exploration_camera.current = false
    combat_camera.current = true
    combat_camera.position = player.position + Vector3(0, 20, 10)
    combat_camera.look_at(player.position)
```

---

## Data Persistence

### Chunk Save Format

**Save to disk** (user://chunks/):
```gdscript
func save_chunk(chunk: ChunkData):
    var save_data = {
        "position": chunk.position,
        "tiles": chunk.tiles,
        "entities": serialize_entities(chunk.entities),
        "structures": chunk.structures,
        "difficulty": chunk.difficulty_level,
        "modified": chunk.is_modified
    }

    var file_path = "user://chunks/chunk_%d_%d.tres" % [chunk.position.x, chunk.position.y]
    ResourceSaver.save(save_data, file_path)
```

### World State

```gdscript
# user://world.save
{
    "player_position": Vector3(x, y, z),
    "current_location": "overworld" or "dungeon_lair_5",
    "explored_chunks": [Vector2i(0,0), Vector2i(1,0), ...],
    "dungeon_states": {
        "lair": {"current_floor": 5, "cleared_floors": [1,2,3,4]},
        "snake_pit": {"discovered": false}
    },
    "difficulty_seed": 12345,
    "spawn_position": Vector3(0, 0, 0)
}
```

---

## Implementation Phases

### Phase 1: Basic 3D Chunk System
- [ ] Create ChunkManager with load/unload
- [ ] Implement FastNoiseLite terrain generation
- [ ] GridMap-based tile rendering
- [ ] Player 3D movement (WASD + mouse look)
- [ ] Chunk save/load to disk

### Phase 2: Overworld Structures
- [ ] Structure placement algorithm
- [ ] Housing/ruins generation
- [ ] Mall/grocery store structures
- [ ] Difficulty-based spawning

### Phase 3: Dungeon System
- [ ] Portal entrance prefab
- [ ] Dungeon scene loading
- [ ] Reuse existing DungeonGenerator for 3D
- [ ] Stairs up/down system
- [ ] Return to overworld

### Phase 4: Combat Integration
- [ ] Camera mode switching
- [ ] Hybrid exploration/combat
- [ ] 3D monster models
- [ ] Turn-based grid overlay in 3D

### Phase 5: Advanced Features
- [ ] Cursed churches with corruption effects
- [ ] Military bases with Orc enemies
- [ ] Portal variety (Lair, Snake, Vaults, etc.)
- [ ] Rune collection system
- [ ] Realm of Zot endgame

---

## Performance Considerations

### Optimization Strategies

1. **Chunk Culling**: Only render visible chunks
2. **LOD (Level of Detail)**: Simpler meshes for distant chunks
3. **Occlusion Culling**: Don't render chunks blocked by others
4. **Entity Pooling**: Reuse monster/NPC instances
5. **Background Loading**: Load chunks in separate thread

```gdscript
func load_or_generate_chunk(chunk_pos: Vector2i):
    # Try to load from disk first
    var file_path = "user://chunks/chunk_%d_%d.tres" % [chunk_pos.x, chunk_pos.y]
    if FileAccess.file_exists(file_path):
        return load_chunk_from_disk(chunk_pos)
    else:
        # Generate fresh chunk
        return generate_new_chunk(chunk_pos)
```

---

## Technical Specifications

**World Size**: ±5000 chunks (~320km x 320km at 64m per chunk)
**Chunk Size**: 32x32 tiles, 64m x 64m
**Render Distance**: 3 chunks (192m radius)
**Max Loaded Chunks**: ~49 chunks (7x7 grid)
**Memory per Chunk**: ~100KB (estimated)
**Total Memory (max load)**: ~5MB for chunks

---

## Next Steps

1. **Start Small**: Single chunk generation with GridMap
2. **Test Noise**: Verify FastNoiseLite difficulty works
3. **Prototype Portal**: Get dungeon scene switching working
4. **Migrate 2D Code**: Adapt existing systems to 3D
5. **Iterate**: Add features incrementally

---

*Document Status*: Design Phase
*Ready for Implementation*: Phase 1 (Basic Chunk System)
