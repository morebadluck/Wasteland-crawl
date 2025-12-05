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

static func generate(grid: Grid, num_rooms: int = 8) -> Vector2i:
	"""Generate a simple dungeon and return player spawn position"""
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

	# Return center of first room as spawn position
	if rooms.size() > 0:
		return rooms[0].center()
	else:
		# Fallback if no rooms generated
		return Vector2i(grid.MAP_WIDTH / 2, grid.MAP_HEIGHT / 2)

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
