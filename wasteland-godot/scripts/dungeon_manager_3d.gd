extends Node3D
class_name DungeonManager3D

## 3D dungeon manager that renders the grid-based dungeon in 3D
## Uses GridMap for voxel-style rendering with floors, walls, and ceilings

@onready var grid_map: GridMap = $GridMap
@onready var grid: Grid = $Grid
@onready var player_3d: PlayerController3D = $PlayerController3D
@onready var hud = $HUDOverlay  # HUDOverlay instance

# Tile indices in MeshLibrary
enum TileIndex {
	FLOOR = 0,
	WALL = 1,
	CEILING = 2,
	FLOOR_WITH_CEILING = 3,  # Combo tile
	STAIRS_DOWN = 4
}

const TILE_SIZE = 2.0  # 2 meters per grid tile
const WALL_HEIGHT = 3.0  # 3 meters tall

var dungeon_level: int = 1
var turn_manager: TurnManager = null
var monster_3d_map: Dictionary = {}  # Maps Monster (2D) -> Monster3D
var overworld_entrance_pos: Vector2i = Vector2i(-1, -1)  # Track where player entered from overworld

func _ready():
	print("=== DungeonManager3D _ready() called ===")
	setup_mesh_library()
	generate_dungeon()
	print("=== DungeonManager3D _ready() finished ===")

func setup_mesh_library():
	"""Create a basic MeshLibrary with simple cube tiles"""
	var mesh_lib = MeshLibrary.new()

	# Create materials
	var floor_mat = StandardMaterial3D.new()
	floor_mat.albedo_color = Color(0.3, 0.3, 0.35)  # Dark gray floor

	var wall_mat = StandardMaterial3D.new()
	wall_mat.albedo_color = Color(0.5, 0.4, 0.3)  # Brown walls

	var ceiling_mat = StandardMaterial3D.new()
	ceiling_mat.albedo_color = Color(0.2, 0.2, 0.25)  # Very dark ceiling

	# FLOOR tile (flat)
	var floor_mesh = BoxMesh.new()
	floor_mesh.size = Vector3(TILE_SIZE, 0.2, TILE_SIZE)
	floor_mesh.material = floor_mat
	mesh_lib.create_item(TileIndex.FLOOR)
	mesh_lib.set_item_mesh(TileIndex.FLOOR, floor_mesh)

	# WALL tile (full cube)
	var wall_mesh = BoxMesh.new()
	wall_mesh.size = Vector3(TILE_SIZE, WALL_HEIGHT, TILE_SIZE)
	wall_mesh.material = wall_mat
	mesh_lib.create_item(TileIndex.WALL)
	mesh_lib.set_item_mesh(TileIndex.WALL, wall_mesh)

	# Add collision to wall
	var wall_shape = BoxShape3D.new()
	wall_shape.size = Vector3(TILE_SIZE, WALL_HEIGHT, TILE_SIZE)
	mesh_lib.set_item_shapes(TileIndex.WALL, [wall_shape])

	# CEILING tile (flat, positioned at top)
	var ceiling_mesh = BoxMesh.new()
	ceiling_mesh.size = Vector3(TILE_SIZE, 0.2, TILE_SIZE)
	ceiling_mesh.material = ceiling_mat
	mesh_lib.create_item(TileIndex.CEILING)
	mesh_lib.set_item_mesh(TileIndex.CEILING, ceiling_mesh)

	# STAIRS_DOWN tile (glowing orange indicator)
	var stairs_mat = StandardMaterial3D.new()
	stairs_mat.albedo_color = Color(1.0, 0.5, 0.0)  # Orange
	stairs_mat.emission_enabled = true
	stairs_mat.emission = Color(1.0, 0.5, 0.0)
	stairs_mat.emission_energy = 2.0

	var stairs_mesh = BoxMesh.new()
	stairs_mesh.size = Vector3(TILE_SIZE * 0.8, 0.3, TILE_SIZE * 0.8)  # Slightly smaller, raised
	stairs_mesh.material = stairs_mat
	mesh_lib.create_item(TileIndex.STAIRS_DOWN)
	mesh_lib.set_item_mesh(TileIndex.STAIRS_DOWN, stairs_mesh)

	grid_map.mesh_library = mesh_lib
	grid_map.cell_size = Vector3(TILE_SIZE, WALL_HEIGHT, TILE_SIZE)

func generate_dungeon():
	"""Generate dungeon and render in 3D"""
	print("=== STARTING 3D DUNGEON GENERATION ===")

	# Check if grid exists
	if not grid:
		print("ERROR: Grid is null!")
		return

	print("Grid exists, generating dungeon data...")

	# Generate using existing 2D generator
	var dungeon_data = DungeonGenerator.generate(grid, 8, self)

	print("Dungeon data generated, checking monsters...")
	var monsters = dungeon_data.get("monsters", [])
	print("Found %d monsters in dungeon_data" % monsters.size())

	# Render the grid in 3D
	render_grid_to_3d()

	# Create 3D representations for all monsters
	spawn_monsters_3d(monsters)

	# Initialize player with grid reference and spawn position
	if player_3d:
		var spawn_pos = dungeon_data["player_pos"]
		var world_pos = grid_to_world_3d(spawn_pos)
		player_3d.position = world_pos

		# Set dungeon manager reference for stairs
		player_3d.dungeon_manager = self

		# Initialize player's grid reference (must happen after grid is ready)
		if player_3d.has_method("initialize_with_grid"):
			player_3d.initialize_with_grid(grid, spawn_pos)
		elif player_3d.has_method("set_grid_position"):
			player_3d.set_grid_position(spawn_pos)

		# Initialize HUD after player is ready
		if hud and player_3d.has_method("get_player_data"):
			var player_data = player_3d.get_player_data()
			if player_data:
				hud.initialize(player_data, self)

				# Connect to player signals for updates
				if player_data.has_signal("player_moved"):
					player_data.player_moved.connect(_on_player_moved)

				# Create and initialize turn manager
				turn_manager = TurnManager.new()
				add_child(turn_manager)
				turn_manager.initialize(player_data, grid, self)
				print("TurnManager initialized and connected")

	print("3D dungeon generated! Rooms and monsters spawned.")

func render_grid_to_3d():
	"""Convert the 2D grid to 3D GridMap"""
	grid_map.clear()

	for y in range(grid.MAP_HEIGHT):
		for x in range(grid.MAP_WIDTH):
			var grid_pos = Vector2i(x, y)
			var tile = grid.get_tile(grid_pos)

			match tile:
				Grid.TileType.FLOOR, Grid.TileType.DOOR_OPEN:
					# Place floor at Y=0
					grid_map.set_cell_item(Vector3i(x, 0, y), TileIndex.FLOOR)
					# Place ceiling at Y=1 (creates underground feeling)
					grid_map.set_cell_item(Vector3i(x, 1, y), TileIndex.CEILING)

				Grid.TileType.STAIRS_DOWN:
					# Place floor first
					grid_map.set_cell_item(Vector3i(x, 0, y), TileIndex.FLOOR)
					# Place glowing stairs indicator on top
					grid_map.set_cell_item(Vector3i(x, 0, y), TileIndex.STAIRS_DOWN)
					# Place ceiling above
					grid_map.set_cell_item(Vector3i(x, 1, y), TileIndex.CEILING)

				Grid.TileType.STAIRS_UP:
					# Place floor first
					grid_map.set_cell_item(Vector3i(x, 0, y), TileIndex.FLOOR)
					# TODO: Add stairs up visual when needed
					# Place ceiling above
					grid_map.set_cell_item(Vector3i(x, 1, y), TileIndex.CEILING)

				Grid.TileType.WALL:
					# Place wall (occupies Y=0, full height)
					grid_map.set_cell_item(Vector3i(x, 0, y), TileIndex.WALL)

func grid_to_world_3d(grid_pos: Vector2i) -> Vector3:
	"""Convert 2D grid position to 3D world position"""
	return Vector3(
		grid_pos.x * TILE_SIZE,
		0.5,  # Player height above floor
		grid_pos.y * TILE_SIZE
	)

func world_3d_to_grid(world_pos: Vector3) -> Vector2i:
	"""Convert 3D world position to 2D grid position"""
	return Vector2i(
		int(world_pos.x / TILE_SIZE),
		int(world_pos.z / TILE_SIZE)
	)

func get_grid() -> Grid:
	"""Get the underlying grid"""
	return grid

func spawn_monsters_3d(monsters: Array):
	"""Create 3D visual representations for all spawned monsters"""
	print("=== SPAWN_MONSTERS_3D called ===")
	print("Spawning %d monsters in 3D..." % monsters.size())

	if monsters.size() == 0:
		print("WARNING: No monsters to spawn!")
		return

	var spawned_count = 0
	for monster in monsters:
		print("  Processing monster: %s (type: %s, valid: %s)" % [monster, typeof(monster), is_instance_valid(monster)])

		if monster is Monster and is_instance_valid(monster):
			print("    Monster is valid, creating 3D representation...")

			# Create Monster3D wrapper
			var monster_3d = Monster3D.new()
			print("    Monster3D instance created")

			monster_3d.initialize_with_monster(monster, monster.grid_position, grid)
			print("    Monster3D initialized")

			# Add to scene
			add_child(monster_3d)
			print("    Monster3D added to scene as child")

			# Track Monster3D for position updates
			monster_3d_map[monster] = monster_3d

			spawned_count += 1
			print("  ✓ Created 3D model for %s at %s" % [monster.monster_name, monster.grid_position])
		else:
			print("    WARNING: Monster is not valid or wrong type!")

	print("=== SPAWN_MONSTERS_3D finished: %d/%d monsters spawned ===" % [spawned_count, monsters.size()])

func _on_player_moved(from: Vector2i, to: Vector2i):
	"""Update HUD when player moves"""
	if hud:
		# Update minimap
		if hud.has_method("update_minimap"):
			hud.update_minimap(to)

		# Update nearby monsters
		update_monster_list()

func update_monster_list():
	"""Update the HUD's monster list with nearby monsters"""
	if not hud or not grid:
		return

	# Get player position
	var player_pos = player_3d.get_player_data().grid_position if player_3d else Vector2i.ZERO

	# Find all monsters within detection range (e.g., 10 tiles)
	var nearby_monsters = []
	var detection_range = 10

	for entity_pos in grid.entities.keys():
		var entity = grid.get_entity(entity_pos)
		if entity and entity.get("monster_name") != null:  # It's a monster
			var distance = player_pos.distance_to(entity_pos)
			if distance <= detection_range:
				nearby_monsters.append(entity)

	# Update HUD monster list
	if hud.has_method("update_monster_list"):
		hud.update_monster_list(nearby_monsters)

func descend_stairs():
	"""Descend to the next dungeon level"""
	print("\n=== DESCENDING TO LEVEL %d ===" % (dungeon_level + 1))

	# Increment level
	dungeon_level += 1

	# Clear all existing Monster3D instances
	for monster_3d in monster_3d_map.values():
		if is_instance_valid(monster_3d):
			monster_3d.queue_free()
	monster_3d_map.clear()

	# Clear all Monster instances from the scene and grid
	var entities_to_remove = []
	for pos in grid.entities.keys():
		var entity = grid.get_entity(pos)
		if entity and is_instance_valid(entity) and entity is Monster:
			entities_to_remove.append(entity)

	for entity in entities_to_remove:
		entity.queue_free()

	# Clear the grid
	grid.clear_all()

	# Regenerate dungeon
	print("Generating new dungeon for level %d..." % dungeon_level)
	var dungeon_data = DungeonGenerator.generate(grid, 8, self)
	var monsters = dungeon_data.get("monsters", [])

	# Re-render the grid in 3D
	render_grid_to_3d()

	# Spawn new monsters
	spawn_monsters_3d(monsters)

	# Move player to new starting position
	if player_3d:
		var player_data = player_3d.get_player_data()
		var spawn_pos = dungeon_data["player_pos"]
		var old_pos = player_data.grid_position

		# Remove player from old position
		grid.remove_entity(old_pos)

		# Update player position
		player_data.grid_position = spawn_pos
		player_3d.grid_position = spawn_pos
		grid.add_entity(spawn_pos, player_data)

		# Set 3D position
		var world_pos = grid_to_world_3d(spawn_pos)
		player_3d.position = world_pos
		player_3d.target_position = world_pos

		# Update turn manager
		if turn_manager:
			turn_manager.update_monster_list()

	print("=== ARRIVED AT DUNGEON LEVEL %d ===" % dungeon_level)
	print("You descend deeper into the dungeon...")

func ascend_stairs():
	"""Ascend stairs - either to previous level or back to overworld"""
	if dungeon_level == 1:
		# On level 1, exit to overworld
		exit_to_overworld()
	else:
		# Go up one level
		print("Ascending to level %d..." % (dungeon_level - 1))
		dungeon_level -= 1
		# TODO: Implement multi-level dungeon traversal
		print("Multi-level traversal not yet implemented - returning to overworld")
		exit_to_overworld()

func exit_to_overworld():
	"""Exit the dungeon and return to the overworld"""
	print("\n=== EXITING DUNGEON ===")
	print("Returning to the wasteland...")

	# Load overworld scene
	var overworld_scene = load("res://scenes/overworld_2d.tscn")
	if not overworld_scene:
		print("ERROR: Could not load overworld scene!")
		return

	# Instance overworld
	var overworld_instance = overworld_scene.instantiate()

	# TODO: Pass player data and restore position
	# For now, this will create a fresh overworld

	# Switch to overworld
	get_tree().root.add_child(overworld_instance)
	queue_free()  # Remove dungeon

	print("Returned to overworld")
