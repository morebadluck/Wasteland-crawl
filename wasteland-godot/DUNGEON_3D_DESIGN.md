# 3D Dungeon System - Underground DCSS-Style

**Goal**: Convert existing 2D dungeons to 3D with DCSS minimap
**Focus**: Underground environments with ceilings, fog of war, tactical view
**Keep**: Grid-based movement, turn-based combat, existing systems

---

## Overview

### What We're Building

**3D Underground Dungeons:**
- GridMap rendering (floors, walls, ceilings)
- First-person or third-person camera
- DCSS-style minimap (top-down tactical overlay)
- Fog of war (only show explored areas)
- Line-of-sight (see what player can see)

**Reuse Existing Systems:**
- ✅ Grid.gd (80x24 tile grid)
- ✅ DungeonGenerator.gd (procedural rooms/corridors)
- ✅ Player movement (grid-based, turn-based)
- ✅ Monster AI and combat
- ✅ Loot and XP systems

---

## 3D Rendering System

### GridMap Setup

**Godot GridMap** - Perfect for dungeon tiles:
```gdscript
extends Node3D
class_name Dungeon3D

@onready var grid_map: GridMap = $GridMap
@onready var grid: Grid = $Grid  # Existing logical grid

const TILE_SIZE = 2.0  # 2 meters per tile

# Tile IDs in MeshLibrary
enum TileID {
    FLOOR = 0,
    WALL = 1,
    DOOR_CLOSED = 2,
    DOOR_OPEN = 3,
    STAIRS_DOWN = 4,
    STAIRS_UP = 5,
    CEILING = 6
}
```

### Converting Grid to 3D

```gdscript
func generate_3d_dungeon():
    # Use existing DungeonGenerator
    var dungeon_data = DungeonGenerator.generate(grid, dungeon_level, self)

    # Convert 2D grid to 3D GridMap
    for y in range(grid.MAP_HEIGHT):
        for x in range(grid.MAP_WIDTH):
            var tile_type = grid.get_tile(Vector2i(x, y))
            render_tile_3d(Vector3i(x, 0, y), tile_type)

func render_tile_3d(pos: Vector3i, tile_type: Grid.TileType):
    match tile_type:
        Grid.TileType.FLOOR:
            grid_map.set_cell_item(pos, TileID.FLOOR)
            # Add ceiling above
            grid_map.set_cell_item(pos + Vector3i(0, 2, 0), TileID.CEILING)

        Grid.TileType.WALL:
            # Wall fills vertical space
            grid_map.set_cell_item(pos, TileID.WALL)
            grid_map.set_cell_item(pos + Vector3i(0, 1, 0), TileID.WALL)

        Grid.TileType.DOOR_CLOSED:
            grid_map.set_cell_item(pos, TileID.FLOOR)
            grid_map.set_cell_item(pos + Vector3i(0, 1, 0), TileID.DOOR_CLOSED)
            grid_map.set_cell_item(pos + Vector3i(0, 2, 0), TileID.CEILING)

        Grid.TileType.STAIRS_DOWN:
            grid_map.set_cell_item(pos, TileID.STAIRS_DOWN)
            # No ceiling over stairs (open shaft)
```

### MeshLibrary Creation

**Create in Godot Editor:**
1. Create `res://assets/dungeon_tiles.meshlib`
2. Add meshes for each tile type:
   - Floor (2x2m flat plane)
   - Wall (2x2x2m cube)
   - Ceiling (2x2m flat plane, darker)
   - Door (animated mesh)
   - Stairs (sloped mesh)

**Simple placeholder meshes** (use BoxMesh initially):
```gdscript
# In editor: Create MeshLibrary resource
# Add items:
# 0: Floor - BoxMesh (2, 0.1, 2), material: dark gray
# 1: Wall - BoxMesh (2, 2, 2), material: stone
# 2: Door Closed - BoxMesh (2, 2, 0.2), material: wood
# 3: Door Open - BoxMesh (2, 2, 0.2), rotated 90°
# 4: Stairs Down - BoxMesh (2, 0.2, 2), material: stone with arrow
# 5: Stairs Up - BoxMesh (2, 0.2, 2), material: stone with arrow
# 6: Ceiling - BoxMesh (2, 0.1, 2), material: very dark
```

---

## Camera System

### Two Camera Modes

**Mode 1: First-Person (DCSS-like)**
```gdscript
class_name FirstPersonCamera
extends Camera3D

var current_cell: Vector2i = Vector2i.ZERO

func update_position(grid_pos: Vector2i):
    # Place camera at player grid position
    var world_pos = grid_to_world_3d(grid_pos)
    position = world_pos + Vector3(0, 1.5, 0)  # Eye level (1.5m up)

    # Face direction of last movement
    look_at(position + current_direction, Vector3.UP)
```

**Mode 2: Overhead Tactical (Combat)**
```gdscript
class_name TacticalCamera
extends Camera3D

func update_position(grid_pos: Vector2i):
    # Top-down view centered on player
    var world_pos = grid_to_world_3d(grid_pos)
    position = world_pos + Vector3(0, 15, 5)  # High up, slight angle
    look_at(world_pos, Vector3.UP)
```

### Camera Switching

```gdscript
enum CameraMode { FIRST_PERSON, TACTICAL }
var current_mode = CameraMode.FIRST_PERSON

func _input(event):
    if event.is_action_pressed("toggle_camera"):  # Tab key
        match current_mode:
            CameraMode.FIRST_PERSON:
                switch_to_tactical()
            CameraMode.TACTICAL:
                switch_to_first_person()
```

---

## DCSS-Style Minimap

### Minimap Design

**What DCSS Shows:**
- Explored areas (gray)
- Current visible area (white/colored)
- Player position (@)
- Monsters (letters)
- Items on ground
- Stairs (< >)

### Godot Implementation

```gdscript
class_name DungeonMinimap
extends Control

const TILE_SIZE_PIXELS = 8  # 8x8 pixel tiles on minimap
const MAP_WIDTH = 80
const MAP_HEIGHT = 24

@onready var minimap_texture: Image = Image.create(MAP_WIDTH, MAP_HEIGHT, false, Image.FORMAT_RGBA8)
@onready var texture_rect: TextureRect = $TextureRect

# Track what player has seen
var explored: Array[bool] = []  # 80x24 array
var visible: Array[bool] = []  # Current line of sight

func _ready():
    explored.resize(MAP_WIDTH * MAP_HEIGHT)
    visible.resize(MAP_WIDTH * MAP_HEIGHT)
    explored.fill(false)

func update_minimap(grid: Grid, player_pos: Vector2i, visible_tiles: Array):
    # Clear visible array
    visible.fill(false)

    # Mark currently visible tiles
    for tile_pos in visible_tiles:
        var idx = tile_pos.y * MAP_WIDTH + tile_pos.x
        visible[idx] = true
        explored[idx] = true  # Mark as explored

    # Render minimap
    minimap_texture.fill(Color.BLACK)  # Clear

    for y in range(MAP_HEIGHT):
        for x in range(MAP_WIDTH):
            var idx = y * MAP_WIDTH + x
            var pos = Vector2i(x, y)

            if visible[idx]:
                # Currently visible - bright color
                minimap_texture.set_pixel(x, y, get_tile_color(grid.get_tile(pos), true))
            elif explored[idx]:
                # Explored but not visible - dim color
                minimap_texture.set_pixel(x, y, get_tile_color(grid.get_tile(pos), false) * 0.5)
            # Else: Black (unexplored)

    # Draw player
    minimap_texture.set_pixel(player_pos.x, player_pos.y, Color.YELLOW)

    # Update texture
    texture_rect.texture = ImageTexture.create_from_image(minimap_texture)

func get_tile_color(tile_type: Grid.TileType, visible: bool) -> Color:
    var brightness = 1.0 if visible else 0.5
    match tile_type:
        Grid.TileType.FLOOR:
            return Color.DARK_GRAY * brightness
        Grid.TileType.WALL:
            return Color.GRAY * brightness
        Grid.TileType.DOOR_CLOSED:
            return Color.SADDLE_BROWN * brightness
        Grid.TileType.DOOR_OPEN:
            return Color.SADDLE_BROWN * brightness * 0.7
        Grid.TileType.STAIRS_DOWN:
            return Color.YELLOW * brightness
        Grid.TileType.STAIRS_UP:
            return Color.YELLOW * brightness
        _:
            return Color.BLACK
```

### Minimap UI Layout

```
┌─────────────────────────────────┐
│  Dungeon: Lair Level 5          │  ← Top bar
├─────────────────────────────────┤
│ ######  ########                │
│ #....#  #......#    ####        │  ← Minimap
│ #..@.####......#  ###..###      │  @ = Player
│ #....#  #......+###......#      │  # = Wall
│ ######  #......#  #......#      │  . = Floor
│         ########  #......#      │  + = Door
│                   ########      │  > = Stairs
│                                 │
├─────────────────────────────────┤
│ HP: 45/50   MP: 12/15   XP: 234 │  ← Stats bar
└─────────────────────────────────┘
```

---

## Fog of War System

### Line-of-Sight Calculation

**DCSS uses**: Symmetric shadowcasting or raycasting

```gdscript
class_name LineOfSight

# Calculate visible tiles from player position
static func get_visible_tiles(grid: Grid, origin: Vector2i, radius: int) -> Array[Vector2i]:
    var visible: Array[Vector2i] = []
    visible.append(origin)  # Always see own position

    # Cast rays in all directions
    for angle in range(0, 360, 2):  # Every 2 degrees
        var rad = deg_to_rad(angle)
        var dir = Vector2(cos(rad), sin(rad))

        # Trace ray until hit wall or max distance
        for dist in range(1, radius + 1):
            var check_pos = origin + Vector2i(dir * dist)

            if not grid.is_in_bounds(check_pos):
                break

            visible.append(check_pos)

            # Stop at walls
            if grid.get_tile(check_pos) == Grid.TileType.WALL:
                break

    return visible

# Simpler approach: Bresenham line algorithm
static func get_visible_tiles_bresenham(grid: Grid, origin: Vector2i, radius: int) -> Array[Vector2i]:
    var visible: Array[Vector2i] = []

    # Check all points in radius
    for dy in range(-radius, radius + 1):
        for dx in range(-radius, radius + 1):
            if dx * dx + dy * dy > radius * radius:
                continue  # Outside circle

            var target = origin + Vector2i(dx, dy)

            if has_line_of_sight(grid, origin, target):
                visible.append(target)

    return visible

static func has_line_of_sight(grid: Grid, from: Vector2i, to: Vector2i) -> bool:
    # Bresenham's line algorithm
    var dx = abs(to.x - from.x)
    var dy = abs(to.y - from.y)
    var sx = 1 if to.x > from.x else -1
    var sy = 1 if to.y > from.y else -1
    var err = dx - dy

    var current = from

    while current != to:
        # Check if blocked
        if grid.get_tile(current) == Grid.TileType.WALL:
            return false

        var e2 = 2 * err
        if e2 > -dy:
            err -= dy
            current.x += sx
        if e2 < dx:
            err += dx
            current.y += sy

    return true
```

### Vision Radius

```gdscript
# Player vision range (DCSS-style)
const VISION_RADIUS = 8  # Can see 8 tiles in all directions

# Update each turn
func _on_player_moved():
    var visible_tiles = LineOfSight.get_visible_tiles(grid, player.grid_position, VISION_RADIUS)
    minimap.update_minimap(grid, player.grid_position, visible_tiles)
    update_3d_fog(visible_tiles)
```

---

## 3D Fog Rendering

### Option 1: Shader-Based Fog

```gdscript
# Shader for GridMap tiles
shader_type spatial;

uniform bool is_visible = true;
uniform bool is_explored = false;

void fragment() {
    if (!is_explored) {
        ALBEDO = vec3(0.0, 0.0, 0.0);  // Black (unexplored)
    } else if (!is_visible) {
        ALBEDO = ALBEDO * 0.3;  // Dim (explored but not visible)
    }
    // Else: Normal brightness (currently visible)
}
```

### Option 2: Visibility Culling (Simpler)

```gdscript
func update_3d_fog(visible_tiles: Array[Vector2i]):
    # Hide all non-visible tiles
    for y in range(grid.MAP_HEIGHT):
        for x in range(grid.MAP_WIDTH):
            var pos = Vector2i(x, y)
            var cell_pos = Vector3i(x, 0, y)

            if pos in visible_tiles:
                # Make visible
                show_cell(cell_pos)
            elif is_explored(pos):
                # Dim (show but darkened)
                dim_cell(cell_pos)
            else:
                # Hide completely
                hide_cell(cell_pos)

func hide_cell(pos: Vector3i):
    # Set to invisible or remove
    grid_map.set_cell_item(pos, -1)  # -1 = empty
    grid_map.set_cell_item(pos + Vector3i(0, 1, 0), -1)
    grid_map.set_cell_item(pos + Vector3i(0, 2, 0), -1)
```

---

## Monster Rendering in 3D

### 3D Monster Instances

```gdscript
class_name Monster3D
extends CharacterBody3D

var grid_position: Vector2i
var monster_data: Monster  # Reference to logical monster

@onready var mesh_instance: MeshInstance3D = $Mesh
@onready var label: Label3D = $Label

func _ready():
    # Create mesh (simple box for now)
    var box = BoxMesh.new()
    box.size = Vector3(1.5, 1.8, 1.5)
    mesh_instance.mesh = box

    # Set color based on monster type
    var material = StandardMaterial3D.new()
    material.albedo_color = monster_data.display_color
    mesh_instance.material_override = material

    # Show monster letter above
    label.text = monster_data.display_char
    label.pixel_size = 0.01

func update_position():
    # Sync 3D position with grid position
    position = grid_to_world_3d(grid_position) + Vector3(0, 0.9, 0)  # Center at ~1m height
```

---

## Movement System

### Grid-Based Movement in 3D

```gdscript
class_name Player3D
extends CharacterBody3D

var grid_position: Vector2i
var grid: Grid
var is_moving: bool = false

func _input(event):
    if is_moving:
        return

    var direction = Vector2i.ZERO

    if event.is_action_pressed("move_up"):
        direction = Vector2i(0, -1)
    elif event.is_action_pressed("move_down"):
        direction = Vector2i(0, 1)
    elif event.is_action_pressed("move_left"):
        direction = Vector2i(-1, 0)
    elif event.is_action_pressed("move_right"):
        direction = Vector2i(1, 0)

    if direction != Vector2i.ZERO:
        attempt_move(direction)

func attempt_move(direction: Vector2i):
    var new_pos = grid_position + direction

    if grid.is_walkable(new_pos):
        is_moving = true
        grid_position = new_pos

        # Animate movement
        var tween = create_tween()
        tween.tween_property(self, "position", grid_to_world_3d(grid_position) + Vector3(0, 1, 0), 0.2)
        tween.finished.connect(func(): is_moving = false)

        # Update fog of war
        _on_player_moved()
```

---

## Implementation Steps

### Phase 1: Basic 3D Dungeon
1. Create dungeon_3d.tscn scene
2. Add GridMap node
3. Create simple MeshLibrary (boxes)
4. Convert DungeonGenerator output to GridMap
5. Add player 3D model (simple capsule)
6. Implement grid-based movement

### Phase 2: Minimap
1. Create minimap UI (Control + TextureRect)
2. Implement minimap rendering (Image pixel updates)
3. Add fog of war tracking (explored array)
4. Integrate with player movement

### Phase 3: Line of Sight
1. Implement Bresenham raycasting
2. Calculate visible tiles each turn
3. Update minimap with visibility
4. Add 3D fog (hide non-visible tiles)

### Phase 4: Monsters & Combat
1. Create Monster3D scene
2. Spawn monsters at grid positions
3. Sync with existing Monster.gd logic
4. Keep turn-based combat unchanged

---

## Testing Plan

**Test Scenario 1**: Basic 3D dungeon
- Generate single-room dungeon
- Walk around with WASD
- Verify walls/floors/ceiling render

**Test Scenario 2**: Minimap
- Move player
- Check minimap updates
- Verify unexplored areas are black

**Test Scenario 3**: Fog of War
- Walk through corridor
- Verify tiles become visible
- Check explored areas stay visible (dimmed)

**Test Scenario 4**: Combat
- Encounter monster
- Attack (existing system)
- Verify 3D rendering updates

---

## Performance Considerations

**Optimizations**:
1. **Occlusion Culling** - Don't render tiles behind walls
2. **Static Batching** - GridMap automatically batches
3. **LOD** - Not needed for small dungeons (80x24)
4. **Lazy Fog Updates** - Only update visible tiles, not whole grid

**Expected Performance**:
- 80x24 grid = ~2000 tiles max
- GridMap handles this easily (thousands of instances)
- Should run at 60 FPS on most hardware

---

## Visual Style

**Dungeon Atmosphere**:
- Dark, underground feel
- Torchlight lighting (point lights on player)
- Shadows enabled
- Ambient occlusion for depth

**Color Palette**:
- Walls: Gray stone (#4A4A4A)
- Floor: Dark gray (#2A2A2A)
- Ceiling: Very dark (#1A1A1A)
- Doors: Brown wood (#5C4033)
- Stairs: Lighter gray with glow

---

*Ready to implement Phase 1: Basic 3D Dungeon*
*Let me know when to start coding!*
