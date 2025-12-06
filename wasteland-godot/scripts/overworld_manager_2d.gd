extends Node2D
class_name OverworldManager2D

## 2D overworld manager for the wasteland world map
## Top-down tile-based exploration with dungeon entrances

@onready var grid: OverworldGrid = $OverworldGrid
@onready var camera: Camera2D = $Camera2D

# Player state
var player_position: Vector2i = Vector2i(50, 50)
var player_data: Player  # Reference to persistent player data

# Rendering
const TILE_SIZE = 32  # Pixels per tile
var tile_sprites: Dictionary = {}  # Maps Vector2i -> ColorRect for now

# View settings
const VIEWPORT_TILES_X = 25  # How many tiles visible horizontally
const VIEWPORT_TILES_Y = 19  # How many tiles visible vertically

# World data
var world_data: Dictionary = {}

func _ready():
	print("=== OVERWORLD MANAGER STARTING ===")

	# Generate world
	world_data = OverworldGenerator.generate(grid)
	player_position = world_data["player_spawn"]

	# Create or load player data
	if not player_data:
		player_data = Player.new()
		add_child(player_data)

	# Set player on grid
	grid.add_entity(player_position, player_data)

	# Render initial view
	render_view()

	# Position camera on player
	update_camera()

	print("=== OVERWORLD READY ===")
	print("Player at: %s (%s)" % [player_position, grid.get_tile_name(grid.get_tile(player_position))])

func _input(event):
	if not event is InputEventKey or not event.pressed or event.echo:
		return

	# WASD / Arrow keys for movement
	var direction = Vector2i.ZERO

	if event.keycode == KEY_W or event.keycode == KEY_UP:
		direction = Vector2i(0, -1)
	elif event.keycode == KEY_S or event.keycode == KEY_DOWN:
		direction = Vector2i(0, 1)
	elif event.keycode == KEY_A or event.keycode == KEY_LEFT:
		direction = Vector2i(-1, 0)
	elif event.keycode == KEY_D or event.keycode == KEY_RIGHT:
		direction = Vector2i(1, 0)

	# Try to move
	if direction != Vector2i.ZERO:
		try_move(direction)
		get_viewport().set_input_as_handled()

	# Enter location (dungeon, town, etc.) with '>'
	if event.keycode == KEY_PERIOD and event.shift_pressed:
		try_enter_location()
		get_viewport().set_input_as_handled()

func try_move(direction: Vector2i):
	"""Try to move player in direction"""
	var new_pos = player_position + direction

	# Check if walkable
	if not grid.is_walkable(new_pos):
		print("Can't move there - %s" % grid.get_tile_name(grid.get_tile(new_pos)))
		return

	# Move player
	grid.move_entity(player_position, new_pos)
	player_position = new_pos

	# Update view
	update_camera()
	render_view()

	# Show current tile info
	var tile = grid.get_tile(player_position)
	var location = grid.get_location(player_position)
	if location.has("name"):
		print("Arrived at: %s" % location["name"])
	else:
		print("Moved to: %s" % grid.get_tile_name(tile))

func try_enter_location():
	"""Try to enter the current location (dungeon, town, etc.)"""
	var tile = grid.get_tile(player_position)
	var location = grid.get_location(player_position)

	match tile:
		OverworldGrid.TileType.DUNGEON_ENTRANCE:
			enter_dungeon(location)
		OverworldGrid.TileType.TOWN:
			print("Entering town... (not implemented yet)")
		OverworldGrid.TileType.MALL:
			print("Entering shopping mall... (not implemented yet)")
		OverworldGrid.TileType.CHURCH:
			print("Entering church... (not implemented yet)")
		OverworldGrid.TileType.BUNKER:
			print("Entering bunker... (not implemented yet)")
		_:
			print("Nothing to enter here.")

func enter_dungeon(location: Dictionary):
	"""Enter a dungeon from the overworld"""
	var dungeon_name = location.get("name", "Unknown Dungeon")
	var dungeon_level = location.get("level", 1)

	print("\n=== ENTERING %s (Level %d) ===" % [dungeon_name, dungeon_level])

	# Load the dungeon scene
	var dungeon_scene = load("res://scenes/dungeon_3d.tscn")
	if not dungeon_scene:
		print("ERROR: Could not load dungeon scene!")
		return

	# Instance the dungeon
	var dungeon_instance = dungeon_scene.instantiate()

	# TODO: Pass player data and dungeon level to the dungeon
	# For now, this will create a fresh dungeon

	# Switch to dungeon scene
	get_tree().root.add_child(dungeon_instance)
	queue_free()  # Remove overworld

	print("Switched to dungeon view")

func update_camera():
	"""Center camera on player"""
	camera.position = Vector2(player_position.x * TILE_SIZE, player_position.y * TILE_SIZE)

func render_view():
	"""Render the visible portion of the overworld"""
	# Clear existing tiles
	for sprite in tile_sprites.values():
		if is_instance_valid(sprite):
			sprite.queue_free()
	tile_sprites.clear()

	# Calculate visible range
	var start_x = max(0, player_position.x - VIEWPORT_TILES_X / 2)
	var start_y = max(0, player_position.y - VIEWPORT_TILES_Y / 2)
	var end_x = min(grid.WORLD_WIDTH, start_x + VIEWPORT_TILES_X)
	var end_y = min(grid.WORLD_HEIGHT, start_y + VIEWPORT_TILES_Y)

	# Render tiles
	for y in range(start_y, end_y):
		for x in range(start_x, end_x):
			var pos = Vector2i(x, y)
			var tile = grid.get_tile(pos)

			# Create colored rectangle for tile
			var rect = ColorRect.new()
			rect.size = Vector2(TILE_SIZE, TILE_SIZE)
			rect.position = Vector2(x * TILE_SIZE, y * TILE_SIZE)
			rect.color = grid.get_tile_color(tile)

			# Draw border
			var border_thickness = 1
			if tile == OverworldGrid.TileType.DUNGEON_ENTRANCE or tile == OverworldGrid.TileType.TOWN:
				# Make important tiles stand out
				var border = ColorRect.new()
				border.size = rect.size + Vector2(border_thickness * 2, border_thickness * 2)
				border.position = rect.position - Vector2(border_thickness, border_thickness)
				border.color = Color.WHITE
				add_child(border)

			add_child(rect)
			tile_sprites[pos] = rect

	# Draw player
	var player_sprite = ColorRect.new()
	player_sprite.size = Vector2(TILE_SIZE, TILE_SIZE)
	player_sprite.position = Vector2(player_position.x * TILE_SIZE, player_position.y * TILE_SIZE)
	player_sprite.color = Color(1, 1, 0, 0.8)  # Yellow with alpha
	add_child(player_sprite)

	# Draw location labels (for dungeons, towns, etc.)
	for pos in grid.locations.keys():
		# Only show labels for visible tiles
		if pos.x >= start_x and pos.x < end_x and pos.y >= start_y and pos.y < end_y:
			var location = grid.get_location(pos)
			var label = Label.new()
			label.text = location.get("name", "")
			label.position = Vector2(pos.x * TILE_SIZE, pos.y * TILE_SIZE - 20)
			label.add_theme_color_override("font_color", Color.WHITE)
			label.add_theme_color_override("font_shadow_color", Color.BLACK)
			label.add_theme_constant_override("shadow_offset_x", 2)
			label.add_theme_constant_override("shadow_offset_y", 2)
			add_child(label)
