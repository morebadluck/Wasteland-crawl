extends CharacterBody3D
class_name PlayerController3D

## 3D player controller with grid-based movement
## Moves one tile at a time, synchronized with the 2D grid system

@onready var camera: Camera3D = $Camera3D

var grid: Grid
var player_data: Player  # Reference to 2D player data (stats, inventory, etc.)
var grid_position: Vector2i = Vector2i(0, 0)
var dungeon_manager  # Reference to DungeonManager3D for level transitions

# Movement settings
const MOVE_SPEED = 10.0  # Speed of smooth movement interpolation
const GRID_SIZE = 2.0  # Must match DungeonManager3D.TILE_SIZE

# Camera settings
var camera_mode: String = "first_person"  # "first_person" or "tactical"
var yaw: float = 0.0
var pitch: float = 0.0

# Movement state
var is_moving: bool = false
var target_position: Vector3 = Vector3.ZERO

func _ready():
	# Initialize camera
	setup_first_person_camera()

	# Capture mouse for first-person look
	Input.mouse_mode = Input.MOUSE_MODE_CAPTURED

func initialize_with_grid(grid_ref: Grid, spawn_pos: Vector2i):
	"""Called by DungeonManager3D after the grid is ready"""
	grid = grid_ref
	grid_position = spawn_pos

	# Create player data instance
	player_data = Player.new()
	player_data.set_grid(grid)
	player_data.grid_position = spawn_pos
	add_child(player_data)

	# Set 3D position
	position = Vector3(
		spawn_pos.x * GRID_SIZE,
		0.5,  # Player height
		spawn_pos.y * GRID_SIZE
	)
	target_position = position

	# Add to grid
	if grid:
		grid.add_entity(spawn_pos, player_data)

func setup_first_person_camera():
	"""Setup first-person camera"""
	camera.position = Vector3(0, 1.6, 0)  # Eye height
	camera.rotation = Vector3.ZERO

func setup_tactical_camera():
	"""Setup tactical overhead camera for combat"""
	camera.position = Vector3(0, 15, 10)
	camera.look_at(Vector3.ZERO, Vector3.UP)

func _input(event):
	# Camera look (only in first-person)
	if event is InputEventMouseMotion and camera_mode == "first_person":
		yaw -= event.relative.x * 0.002
		pitch -= event.relative.y * 0.002
		pitch = clamp(pitch, -PI/2, PI/2)

		rotation.y = yaw
		camera.rotation.x = pitch

func _process(delta):
	# Smooth movement to target
	if is_moving:
		position = position.lerp(target_position, MOVE_SPEED * delta)

		# Check if arrived
		if position.distance_to(target_position) < 0.1:
			position = target_position
			is_moving = false

func _unhandled_input(event):
	if is_moving:
		return  # Don't process new moves while moving

	# ESC to release mouse
	if event is InputEventKey and event.pressed and event.keycode == KEY_ESCAPE:
		if Input.mouse_mode == Input.MOUSE_MODE_CAPTURED:
			Input.mouse_mode = Input.MOUSE_MODE_VISIBLE
		else:
			Input.mouse_mode = Input.MOUSE_MODE_CAPTURED
		return

	# Grid-based movement - check for key press events
	if not event is InputEventKey or not event.pressed or event.echo:
		return

	# Descend stairs - Press '>' (Shift + Period)
	if event.keycode == KEY_PERIOD and event.shift_pressed:
		try_use_stairs()
		get_viewport().set_input_as_handled()
		return

	# Camera-relative movement (forward/back/left/right)
	var input_forward = 0.0
	var input_strafe = 0.0

	# WASD / Arrow keys
	if event.keycode == KEY_W or event.keycode == KEY_UP:
		input_forward = 1.0  # Move forward
	elif event.keycode == KEY_S or event.keycode == KEY_DOWN:
		input_forward = -1.0  # Move backward
	elif event.keycode == KEY_A or event.keycode == KEY_LEFT:
		input_strafe = -1.0  # Strafe left
	elif event.keycode == KEY_D or event.keycode == KEY_RIGHT:
		input_strafe = 1.0  # Strafe right

	if input_forward != 0.0 or input_strafe != 0.0:
		var direction = get_camera_relative_direction(input_forward, input_strafe)
		if direction != Vector2i.ZERO:
			try_move_grid(direction)
			get_viewport().set_input_as_handled()

func get_camera_relative_direction(forward: float, strafe: float) -> Vector2i:
	"""Convert camera-relative input to grid direction based on yaw"""
	# Calculate forward and right vectors based on camera yaw
	var forward_3d = Vector3(sin(yaw), 0, cos(yaw))
	var right_3d = Vector3(cos(yaw), 0, -sin(yaw))

	# Combine forward/back and left/right movement (negate forward for correct direction)
	var move_dir_3d = forward_3d * (-forward) + right_3d * strafe

	# Convert to grid coordinates (round to nearest axis)
	var grid_x = round(move_dir_3d.x)
	var grid_y = round(move_dir_3d.z)

	# Ensure we only move in one direction (prefer the dominant axis)
	if abs(grid_x) > abs(grid_y):
		grid_y = 0
	elif abs(grid_y) > abs(grid_x):
		grid_x = 0

	return Vector2i(int(grid_x), int(grid_y))

func try_move_grid(direction: Vector2i) -> bool:
	"""Attempt to move one grid tile in the given direction"""
	if not grid:
		return false

	var new_grid_pos = grid_position + direction

	# Check for entity (attack instead of move)
	var entity = grid.get_entity(new_grid_pos)
	if entity and entity != player_data and is_instance_valid(entity):
		# Attack the entity
		player_data.attack_entity(entity)
		return false

	# Check if walkable
	if grid.is_walkable(new_grid_pos):
		# Move player
		grid.move_entity(grid_position, new_grid_pos)
		grid_position = new_grid_pos
		player_data.grid_position = new_grid_pos

		# Start smooth movement
		target_position = Vector3(
			new_grid_pos.x * GRID_SIZE,
			0.5,
			new_grid_pos.y * GRID_SIZE
		)
		is_moving = true

		# Emit player_moved signal
		player_data.player_moved.emit(grid_position - direction, new_grid_pos)
		player_data.turn_ended.emit()

		return true

	return false

func get_player_data() -> Player:
	"""Get the underlying player data (for UI, stats, etc.)"""
	return player_data

func try_use_stairs():
	"""Try to descend/ascend stairs at current position"""
	if not grid:
		return

	var current_tile = grid.get_tile(grid_position)

	if current_tile == Grid.TileType.STAIRS_DOWN:
		print("Player using downstairs at %s" % grid_position)
		if dungeon_manager and dungeon_manager.has_method("descend_stairs"):
			dungeon_manager.descend_stairs()
		else:
			print("ERROR: No dungeon manager reference!")
	elif current_tile == Grid.TileType.STAIRS_UP:
		print("Player using upstairs at %s" % grid_position)
		if dungeon_manager and dungeon_manager.has_method("ascend_stairs"):
			dungeon_manager.ascend_stairs()
	else:
		print("You are not standing on any stairs.")
