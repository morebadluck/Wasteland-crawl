extends Control
class_name MiniMap

## DCSS-style minimap renderer
## Shows top-down view of the dungeon with player position

var grid = null  # Grid instance
var player_pos: Vector2i = Vector2i.ZERO

# Map display settings
const TILE_SIZE = 3  # Pixels per tile on minimap
const MAP_WIDTH = 80  # Tiles to show
const MAP_HEIGHT = 24  # Tiles to show

# Colors
const COLOR_FLOOR = Color(0.2, 0.2, 0.2, 1.0)
const COLOR_WALL = Color(0.6, 0.6, 0.6, 1.0)
const COLOR_PLAYER = Color(1.0, 1.0, 0.0, 1.0)  # Yellow
const COLOR_MONSTER = Color(1.0, 0.0, 0.0, 1.0)  # Red
const COLOR_STAIRS_DOWN = Color(1.0, 0.5, 0.0, 1.0)  # Orange
const COLOR_STAIRS_UP = Color(0.5, 1.0, 1.0, 1.0)  # Cyan
const COLOR_UNEXPLORED = Color(0.0, 0.0, 0.0, 1.0)  # Black

# Fog of war (explored tiles)
var explored_tiles: Dictionary = {}

func _ready():
	custom_minimum_size = Vector2(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE)

	# Set up background
	var panel = StyleBoxFlat.new()
	panel.bg_color = Color(0.1, 0.1, 0.1, 0.8)
	panel.border_color = Color(0.5, 0.5, 0.5, 1.0)
	panel.set_border_width_all(2)
	add_theme_stylebox_override("panel", panel)

func initialize(grid_ref, player_ref):
	"""Initialize minimap with grid and player"""
	grid = grid_ref
	if player_ref:
		player_pos = player_ref.grid_position
		# Mark starting area as explored
		_explore_around(player_pos, 5)

func update_player_position(pos: Vector2i):
	"""Update player position and mark tiles as explored"""
	player_pos = pos
	_explore_around(pos, 3)  # Explore 3 tiles around player
	queue_redraw()

func _explore_around(center: Vector2i, radius: int):
	"""Mark tiles around center as explored"""
	for y in range(center.y - radius, center.y + radius + 1):
		for x in range(center.x - radius, center.x + radius + 1):
			var key = Vector2i(x, y)
			explored_tiles[key] = true

func _draw():
	if not grid:
		return

	# Calculate camera center around player
	var camera_x = player_pos.x - MAP_WIDTH / 2
	var camera_y = player_pos.y - MAP_HEIGHT / 2

	# Draw tiles
	for y in range(MAP_HEIGHT):
		for x in range(MAP_WIDTH):
			var grid_x = camera_x + x
			var grid_y = camera_y + y
			var grid_pos = Vector2i(grid_x, grid_y)

			# Check if tile is explored
			if not explored_tiles.has(grid_pos):
				draw_rect(Rect2(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE), COLOR_UNEXPLORED, true)
				continue

			# Check bounds
			if grid_x < 0 or grid_x >= grid.MAP_WIDTH or grid_y < 0 or grid_y >= grid.MAP_HEIGHT:
				continue

			var tile = grid.get_tile(grid_pos)
			var color = _get_tile_color(tile)

			draw_rect(Rect2(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE), color, true)

	# Draw player
	var player_screen_x = (player_pos.x - camera_x) * TILE_SIZE
	var player_screen_y = (player_pos.y - camera_y) * TILE_SIZE
	draw_rect(Rect2(player_screen_x, player_screen_y, TILE_SIZE, TILE_SIZE), COLOR_PLAYER, true)

func _get_tile_color(tile: Grid.TileType) -> Color:
	"""Get color for tile type"""
	match tile:
		Grid.TileType.FLOOR, Grid.TileType.DOOR_OPEN:
			return COLOR_FLOOR
		Grid.TileType.WALL:
			return COLOR_WALL
		Grid.TileType.STAIRS_DOWN:
			return COLOR_STAIRS_DOWN
		Grid.TileType.STAIRS_UP:
			return COLOR_STAIRS_UP
		_:
			return COLOR_FLOOR
