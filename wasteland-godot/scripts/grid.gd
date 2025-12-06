extends Node2D
class_name Grid

## Grid-based roguelike map system
## Manages tiles, entities, and turn-based movement

const TILE_SIZE = 32
const MAP_WIDTH = 80
const MAP_HEIGHT = 24

enum TileType {
	FLOOR,
	WALL,
	WATER,
	DOOR_CLOSED,
	DOOR_OPEN,
	STAIRS_DOWN,
	STAIRS_UP
}

# 2D array of tile types
var tiles: Array[Array] = []

# Entities on the grid (player, monsters)
var entities: Dictionary = {}  # Key: Vector2i position, Value: Entity

# Loot on the ground (doesn't block movement)
var loot: Dictionary = {}  # Key: Vector2i position, Value: LootEntity

func _ready():
	initialize_map()

func initialize_map():
	"""Create empty map filled with walls"""
	tiles.clear()
	for y in range(MAP_HEIGHT):
		var row: Array = []
		for x in range(MAP_WIDTH):
			row.append(TileType.WALL)
		tiles.append(row)

func get_tile(pos: Vector2i) -> TileType:
	"""Get tile type at position"""
	if pos.x < 0 or pos.x >= MAP_WIDTH or pos.y < 0 or pos.y >= MAP_HEIGHT:
		return TileType.WALL
	return tiles[pos.y][pos.x]

func set_tile(pos: Vector2i, tile_type: TileType):
	"""Set tile type at position"""
	if pos.x >= 0 and pos.x < MAP_WIDTH and pos.y >= 0 and pos.y < MAP_HEIGHT:
		tiles[pos.y][pos.x] = tile_type

func is_walkable(pos: Vector2i) -> bool:
	"""Check if position can be walked on"""
	var tile = get_tile(pos)
	match tile:
		TileType.FLOOR, TileType.DOOR_OPEN, TileType.STAIRS_DOWN, TileType.STAIRS_UP:
			# Check for valid entity blocking (ignore freed objects)
			if entities.has(pos):
				var entity = entities[pos]
				if entity and is_instance_valid(entity):
					return false  # Valid entity blocking
				else:
					# Clean up freed object
					entities.erase(pos)
			return true
		_:
			return false

func add_entity(pos: Vector2i, entity):
	"""Add entity to grid"""
	entities[pos] = entity

func remove_entity(pos: Vector2i):
	"""Remove entity from grid"""
	entities.erase(pos)

func move_entity(from: Vector2i, to: Vector2i):
	"""Move entity from one position to another"""
	if entities.has(from):
		var entity = entities[from]
		entities.erase(from)
		entities[to] = entity
		return true
	return false

func get_entity(pos: Vector2i):
	"""Get entity at position"""
	var entity = entities.get(pos, null)
	# Clean up freed objects automatically
	if entity and not is_instance_valid(entity):
		entities.erase(pos)
		return null
	return entity

func world_to_grid(world_pos: Vector2) -> Vector2i:
	"""Convert world coordinates to grid coordinates"""
	return Vector2i(
		int(world_pos.x / TILE_SIZE),
		int(world_pos.y / TILE_SIZE)
	)

func grid_to_world(grid_pos: Vector2i) -> Vector2:
	"""Convert grid coordinates to world coordinates"""
	return Vector2(
		grid_pos.x * TILE_SIZE,
		grid_pos.y * TILE_SIZE
	)

func add_loot(pos: Vector2i, loot_entity):
	"""Add loot to grid (doesn't block movement)"""
	loot[pos] = loot_entity

func remove_loot(pos: Vector2i):
	"""Remove loot from grid"""
	loot.erase(pos)

func get_loot(pos: Vector2i):
	"""Get loot at position"""
	return loot.get(pos, null)
