extends Node2D
class_name OverworldGrid

## Overworld tile-based grid for the wasteland world map
## Similar to Grid but for overworld exploration

signal tile_changed(position: Vector2i, new_type: TileType)

# Overworld tile types
enum TileType {
	WASTELAND,        # Empty desert/wasteland
	ROAD,             # Road for faster travel
	RUINS,            # Ruined buildings (can be explored)
	DUNGEON_ENTRANCE, # Entrance to underground dungeon
	TOWN,             # Settlement/trading post
	FOREST,           # Forest area
	MOUNTAIN,         # Impassable mountains
	WATER,            # Lakes/rivers
	RADIOACTIVE,      # Dangerous radioactive zone
	BUNKER,           # Military bunker entrance
	MALL,             # Shopping mall (from your Minecraft mod!)
	CHURCH,           # Church location
	UNKNOWN           # Unexplored/fog of war
}

# World size
const WORLD_WIDTH = 100
const WORLD_HEIGHT = 100

# Tile data
var tiles: Array = []  # 2D array of TileType
var entities: Dictionary = {}  # Maps Vector2i position -> entity reference
var locations: Dictionary = {}  # Maps Vector2i -> location data (name, level, etc.)

func _ready():
	# Initialize tile grid
	tiles.resize(WORLD_HEIGHT)
	for y in range(WORLD_HEIGHT):
		tiles[y] = []
		tiles[y].resize(WORLD_WIDTH)
		for x in range(WORLD_WIDTH):
			tiles[y][x] = TileType.UNKNOWN

func set_tile(pos: Vector2i, type: TileType):
	"""Set a tile type at position"""
	if not is_valid_position(pos):
		return

	tiles[pos.y][pos.x] = type
	tile_changed.emit(pos, type)

func get_tile(pos: Vector2i) -> TileType:
	"""Get tile type at position"""
	if not is_valid_position(pos):
		return TileType.MOUNTAIN  # Out of bounds = impassable

	return tiles[pos.y][pos.x]

func is_valid_position(pos: Vector2i) -> bool:
	"""Check if position is within world bounds"""
	return pos.x >= 0 and pos.x < WORLD_WIDTH and pos.y >= 0 and pos.y < WORLD_HEIGHT

func is_walkable(pos: Vector2i) -> bool:
	"""Check if player can walk on this tile"""
	if not is_valid_position(pos):
		return false

	var tile = get_tile(pos)

	# Mountains and water are impassable
	if tile == TileType.MOUNTAIN or tile == TileType.WATER:
		return false

	return true

func add_entity(pos: Vector2i, entity):
	"""Add an entity at position"""
	entities[pos] = entity

func remove_entity(pos: Vector2i):
	"""Remove entity at position"""
	entities.erase(pos)

func get_entity(pos: Vector2i):
	"""Get entity at position"""
	return entities.get(pos, null)

func move_entity(from: Vector2i, to: Vector2i):
	"""Move entity from one position to another"""
	var entity = get_entity(from)
	if entity:
		remove_entity(from)
		add_entity(to, entity)

func set_location(pos: Vector2i, name: String, level: int = 1, data: Dictionary = {}):
	"""Set location data for a tile (dungeon name, town name, etc.)"""
	locations[pos] = {
		"name": name,
		"level": level,
		"data": data
	}

func get_location(pos: Vector2i) -> Dictionary:
	"""Get location data at position"""
	return locations.get(pos, {})

func clear_all():
	"""Clear all tiles and entities"""
	entities.clear()
	locations.clear()

	for y in range(WORLD_HEIGHT):
		for x in range(WORLD_WIDTH):
			tiles[y][x] = TileType.UNKNOWN

func get_tile_name(type: TileType) -> String:
	"""Get human-readable name for tile type"""
	match type:
		TileType.WASTELAND:
			return "Wasteland"
		TileType.ROAD:
			return "Road"
		TileType.RUINS:
			return "Ruins"
		TileType.DUNGEON_ENTRANCE:
			return "Dungeon Entrance"
		TileType.TOWN:
			return "Town"
		TileType.FOREST:
			return "Forest"
		TileType.MOUNTAIN:
			return "Mountain"
		TileType.WATER:
			return "Water"
		TileType.RADIOACTIVE:
			return "Radioactive Zone"
		TileType.BUNKER:
			return "Military Bunker"
		TileType.MALL:
			return "Shopping Mall"
		TileType.CHURCH:
			return "Church"
		TileType.UNKNOWN:
			return "Unknown"

	return "Unknown"

func get_tile_color(type: TileType) -> Color:
	"""Get color for tile type (for rendering)"""
	match type:
		TileType.WASTELAND:
			return Color(0.6, 0.5, 0.3)  # Tan/brown
		TileType.ROAD:
			return Color(0.3, 0.3, 0.3)  # Dark gray
		TileType.RUINS:
			return Color(0.4, 0.4, 0.4)  # Gray
		TileType.DUNGEON_ENTRANCE:
			return Color(1.0, 0.5, 0.0)  # Orange (like stairs)
		TileType.TOWN:
			return Color(0.8, 0.8, 0.2)  # Yellow
		TileType.FOREST:
			return Color(0.2, 0.6, 0.2)  # Green
		TileType.MOUNTAIN:
			return Color(0.5, 0.5, 0.5)  # Gray
		TileType.WATER:
			return Color(0.2, 0.4, 0.8)  # Blue
		TileType.RADIOACTIVE:
			return Color(0.5, 1.0, 0.0)  # Toxic green
		TileType.BUNKER:
			return Color(0.3, 0.5, 0.3)  # Dark green
		TileType.MALL:
			return Color(0.8, 0.6, 0.8)  # Pink/purple
		TileType.CHURCH:
			return Color(0.9, 0.9, 0.9)  # White
		TileType.UNKNOWN:
			return Color(0.1, 0.1, 0.1)  # Near black

	return Color.WHITE
