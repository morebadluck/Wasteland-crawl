extends Node
class_name DungeonGenerator

## Simple dungeon generator using BSP (Binary Space Partitioning)
## Creates rooms and corridors similar to classic roguelikes

const MIN_ROOM_SIZE = 5
const MAX_ROOM_SIZE = 12

class Room:
	var x: int
	var y: int
	var width: int
	var height: int

	func _init(rx: int, ry: int, rw: int, rh: int):
		x = rx
		y = ry
		width = rw
		height = rh

	func center() -> Vector2i:
		return Vector2i(x + width / 2, y + height / 2)

	func intersects(other: Room) -> bool:
		return (x < other.x + other.width and x + width > other.x and
				y < other.y + other.height and y + height > other.y)

static func generate(grid: Grid, num_rooms: int = 8, game_node: Node = null) -> Dictionary:
	"""Generate a simple dungeon and return player spawn position and monsters"""
	var rooms: Array[Room] = []
	var attempts = 0
	var max_attempts = 100

	# Generate rooms
	while rooms.size() < num_rooms and attempts < max_attempts:
		attempts += 1

		var w = randi_range(MIN_ROOM_SIZE, MAX_ROOM_SIZE)
		var h = randi_range(MIN_ROOM_SIZE, MAX_ROOM_SIZE)
		var x = randi_range(1, grid.MAP_WIDTH - w - 1)
		var y = randi_range(1, grid.MAP_HEIGHT - h - 1)

		var new_room = Room.new(x, y, w, h)

		# Check if room overlaps with existing rooms
		var overlaps = false
		for room in rooms:
			if new_room.intersects(room):
				overlaps = true
				break

		if not overlaps:
			_carve_room(grid, new_room)

			# Connect to previous room with corridor
			if rooms.size() > 0:
				_create_corridor(grid, rooms[-1].center(), new_room.center())

			rooms.append(new_room)

	# Spawn monsters in rooms (skip first room - player spawns there)
	var monsters = []
	for i in range(1, rooms.size()):
		var room = rooms[i]
		var num_monsters = randi_range(1, 3)  # 1-3 monsters per room

		for j in range(num_monsters):
			var spawn_pos = _get_random_floor_in_room(grid, room)
			if spawn_pos != Vector2i(-1, -1) and game_node:
				var monster = _spawn_monster(grid, spawn_pos, 1, game_node)  # Level 1 dungeon for now
				if monster:
					monsters.append(monster)

	# Return spawn data
	var result = {
		"player_pos": rooms[0].center() if rooms.size() > 0 else Vector2i(grid.MAP_WIDTH / 2, grid.MAP_HEIGHT / 2),
		"monsters": monsters
	}
	return result

static func _carve_room(grid: Grid, room: Room):
	"""Carve out a room in the grid"""
	for y in range(room.y, room.y + room.height):
		for x in range(room.x, room.x + room.width):
			grid.set_tile(Vector2i(x, y), Grid.TileType.FLOOR)

static func _create_corridor(grid: Grid, from: Vector2i, to: Vector2i):
	"""Create an L-shaped corridor between two points"""
	var x = from.x
	var y = from.y

	# Horizontal then vertical
	while x != to.x:
		grid.set_tile(Vector2i(x, y), Grid.TileType.FLOOR)
		x += 1 if to.x > x else -1

	while y != to.y:
		grid.set_tile(Vector2i(x, y), Grid.TileType.FLOOR)
		y += 1 if to.y > y else -1

static func _get_random_floor_in_room(grid: Grid, room: Room) -> Vector2i:
	"""Get a random walkable floor position in a room"""
	var attempts = 0
	while attempts < 20:
		var rx = randi_range(room.x + 1, room.x + room.width - 2)
		var ry = randi_range(room.y + 1, room.y + room.height - 2)
		var pos = Vector2i(rx, ry)

		# Check if position is floor and not occupied
		if grid.get_tile(pos) == Grid.TileType.FLOOR and not grid.get_entity(pos):
			return pos

		attempts += 1

	return Vector2i(-1, -1)  # Failed to find spot

static func _spawn_monster(grid: Grid, pos: Vector2i, dungeon_level: int, game_node: Node) -> Monster:
	"""Spawn a monster at the given position"""
	# Initialize monster database
	MonsterTypes.init_monsters()

	# Pick a random early-game monster
	var early_monsters = ["rat", "giant_rat", "bat", "jackal", "zombie", "kobold"]
	var monster_id = early_monsters[randi() % early_monsters.size()]

	# Create monster instance
	var monster = Monster.new()
	monster.initialize(monster_id, dungeon_level)
	monster.spawn_at(pos, grid)

	# Add to scene tree
	game_node.add_child(monster)

	return monster
