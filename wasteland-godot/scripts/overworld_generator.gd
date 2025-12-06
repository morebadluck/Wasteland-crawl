extends Node
class_name OverworldGenerator

## Procedural generation for the wasteland overworld
## Creates a persistent world with dungeons, towns, roads, etc.

static func generate(grid: OverworldGrid) -> Dictionary:
	"""Generate the overworld map"""
	print("=== GENERATING WASTELAND OVERWORLD ===")

	# Fill with wasteland base
	_fill_wasteland(grid)

	# Add terrain features
	_add_mountains(grid)
	_add_water(grid)
	_add_forests(grid)
	_add_radioactive_zones(grid)

	# Add locations
	var town = _add_town(grid)
	var dungeons = _add_dungeons(grid, 8)  # 8 dungeons scattered around
	var malls = _add_malls(grid, 3)
	var churches = _add_churches(grid, 2)
	var bunkers = _add_bunkers(grid, 4)
	var ruins = _add_ruins(grid, 15)

	# Add roads connecting locations
	_add_roads(grid, town, dungeons + malls + churches + bunkers)

	print("Overworld generated: %d dungeons, %d ruins, %d malls, %d churches, %d bunkers" % [
		dungeons.size(), ruins.size(), malls.size(), churches.size(), bunkers.size()
	])

	return {
		"player_spawn": town,  # Player starts in town
		"town": town,
		"dungeons": dungeons,
		"malls": malls,
		"churches": churches,
		"bunkers": bunkers
	}

static func _fill_wasteland(grid: OverworldGrid):
	"""Fill the entire map with wasteland"""
	for y in range(grid.WORLD_HEIGHT):
		for x in range(grid.WORLD_WIDTH):
			grid.set_tile(Vector2i(x, y), OverworldGrid.TileType.WASTELAND)

static func _add_mountains(grid: OverworldGrid):
	"""Add mountain ranges"""
	var num_ranges = randi_range(3, 5)

	for i in range(num_ranges):
		var start_x = randi_range(0, grid.WORLD_WIDTH - 1)
		var start_y = randi_range(0, grid.WORLD_HEIGHT - 1)
		var length = randi_range(10, 25)

		var dir = Vector2i([-1, 0, 1][randi() % 3], [-1, 0, 1][randi() % 3])
		if dir == Vector2i.ZERO:
			dir = Vector2i(1, 0)

		for j in range(length):
			var pos = Vector2i(start_x, start_y) + dir * j
			if grid.is_valid_position(pos):
				grid.set_tile(pos, OverworldGrid.TileType.MOUNTAIN)

				# Add width to mountain range
				for offset in [Vector2i(-1, 0), Vector2i(1, 0), Vector2i(0, -1), Vector2i(0, 1)]:
					var side_pos = pos + offset
					if grid.is_valid_position(side_pos) and randf() < 0.6:
						grid.set_tile(side_pos, OverworldGrid.TileType.MOUNTAIN)

static func _add_water(grid: OverworldGrid):
	"""Add lakes and rivers"""
	var num_lakes = randi_range(2, 4)

	for i in range(num_lakes):
		var center_x = randi_range(10, grid.WORLD_WIDTH - 10)
		var center_y = randi_range(10, grid.WORLD_HEIGHT - 10)
		var radius = randi_range(3, 7)

		# Create circular lake
		for y in range(center_y - radius, center_y + radius):
			for x in range(center_x - radius, center_x + radius):
				var pos = Vector2i(x, y)
				if grid.is_valid_position(pos):
					var dist = pos.distance_to(Vector2i(center_x, center_y))
					if dist < radius:
						grid.set_tile(pos, OverworldGrid.TileType.WATER)

static func _add_forests(grid: OverworldGrid):
	"""Add forest patches"""
	var num_forests = randi_range(5, 10)

	for i in range(num_forests):
		var center_x = randi_range(5, grid.WORLD_WIDTH - 5)
		var center_y = randi_range(5, grid.WORLD_HEIGHT - 5)
		var size = randi_range(3, 8)

		for y in range(center_y - size, center_y + size):
			for x in range(center_x - size, center_x + size):
				var pos = Vector2i(x, y)
				if grid.is_valid_position(pos) and grid.get_tile(pos) == OverworldGrid.TileType.WASTELAND:
					if randf() < 0.7:
						grid.set_tile(pos, OverworldGrid.TileType.FOREST)

static func _add_radioactive_zones(grid: OverworldGrid):
	"""Add dangerous radioactive zones"""
	var num_zones = randi_range(3, 6)

	for i in range(num_zones):
		var center_x = randi_range(10, grid.WORLD_WIDTH - 10)
		var center_y = randi_range(10, grid.WORLD_HEIGHT - 10)
		var radius = randi_range(4, 8)

		for y in range(center_y - radius, center_y + radius):
			for x in range(center_x - radius, center_x + radius):
				var pos = Vector2i(x, y)
				if grid.is_valid_position(pos):
					var dist = pos.distance_to(Vector2i(center_x, center_y))
					if dist < radius and grid.get_tile(pos) == OverworldGrid.TileType.WASTELAND:
						if randf() < 0.8:
							grid.set_tile(pos, OverworldGrid.TileType.RADIOACTIVE)

static func _add_town(grid: OverworldGrid) -> Vector2i:
	"""Add the main starting town"""
	# Place near center of map
	var town_pos = Vector2i(grid.WORLD_WIDTH / 2, grid.WORLD_HEIGHT / 2)

	# Find clear spot near center
	for attempt in range(100):
		var test_pos = town_pos + Vector2i(randi_range(-5, 5), randi_range(-5, 5))
		if grid.is_valid_position(test_pos) and grid.get_tile(test_pos) == OverworldGrid.TileType.WASTELAND:
			town_pos = test_pos
			break

	grid.set_tile(town_pos, OverworldGrid.TileType.TOWN)
	grid.set_location(town_pos, "Haven", 0, {"type": "town", "safe": true})

	print("Town 'Haven' placed at %s" % town_pos)
	return town_pos

static func _add_dungeons(grid: OverworldGrid, count: int) -> Array[Vector2i]:
	"""Add dungeon entrances scattered around the world"""
	var dungeons: Array[Vector2i] = []
	var attempts = 0
	var max_attempts = count * 20

	while dungeons.size() < count and attempts < max_attempts:
		attempts += 1

		var x = randi_range(10, grid.WORLD_WIDTH - 10)
		var y = randi_range(10, grid.WORLD_HEIGHT - 10)
		var pos = Vector2i(x, y)

		# Must be on walkable terrain and not too close to other dungeons
		if grid.get_tile(pos) != OverworldGrid.TileType.WASTELAND:
			continue

		var too_close = false
		for existing in dungeons:
			if pos.distance_to(existing) < 15:  # Minimum distance between dungeons
				too_close = true
				break

		if too_close:
			continue

		# Calculate dungeon level based on distance from center (town)
		var center = Vector2i(grid.WORLD_WIDTH / 2, grid.WORLD_HEIGHT / 2)
		var distance = pos.distance_to(center)
		var dungeon_level = int(distance / 10) + 1  # Further = harder

		grid.set_tile(pos, OverworldGrid.TileType.DUNGEON_ENTRANCE)
		grid.set_location(pos, "Dungeon %d" % (dungeons.size() + 1), dungeon_level, {"type": "dungeon"})

		dungeons.append(pos)
		print("Dungeon %d (Level %d) placed at %s" % [dungeons.size(), dungeon_level, pos])

	return dungeons

static func _add_malls(grid: OverworldGrid, count: int) -> Array[Vector2i]:
	"""Add shopping malls (from Minecraft mod)"""
	var malls: Array[Vector2i] = []

	for i in range(count):
		var pos = _find_clear_spot(grid, malls, 20)
		if pos != Vector2i(-1, -1):
			grid.set_tile(pos, OverworldGrid.TileType.MALL)
			grid.set_location(pos, "Abandoned Mall", 0, {"type": "mall"})
			malls.append(pos)

	return malls

static func _add_churches(grid: OverworldGrid, count: int) -> Array[Vector2i]:
	"""Add churches"""
	var churches: Array[Vector2i] = []

	for i in range(count):
		var pos = _find_clear_spot(grid, churches, 25)
		if pos != Vector2i(-1, -1):
			grid.set_tile(pos, OverworldGrid.TileType.CHURCH)
			grid.set_location(pos, "Old Church", 0, {"type": "church", "safe": true})
			churches.append(pos)

	return churches

static func _add_bunkers(grid: OverworldGrid, count: int) -> Array[Vector2i]:
	"""Add military bunkers"""
	var bunkers: Array[Vector2i] = []

	for i in range(count):
		var pos = _find_clear_spot(grid, bunkers, 20)
		if pos != Vector2i(-1, -1):
			grid.set_tile(pos, OverworldGrid.TileType.BUNKER)
			var level = randi_range(1, 3)
			grid.set_location(pos, "Military Bunker", level, {"type": "bunker"})
			bunkers.append(pos)

	return bunkers

static func _add_ruins(grid: OverworldGrid, count: int) -> Array[Vector2i]:
	"""Add ruined buildings"""
	var ruins: Array[Vector2i] = []

	for i in range(count):
		var pos = _find_clear_spot(grid, ruins, 8)
		if pos != Vector2i(-1, -1):
			grid.set_tile(pos, OverworldGrid.TileType.RUINS)
			grid.set_location(pos, "Ruins", 0, {"type": "ruins"})
			ruins.append(pos)

	return ruins

static func _add_roads(grid: OverworldGrid, start: Vector2i, destinations: Array):
	"""Add roads connecting locations"""
	for dest in destinations:
		if dest is Vector2i:
			_create_road(grid, start, dest)

static func _create_road(grid: OverworldGrid, from: Vector2i, to: Vector2i):
	"""Create a winding road between two points"""
	var current = from
	var max_steps = 500
	var steps = 0

	while current.distance_to(to) > 1 and steps < max_steps:
		steps += 1

		# Move toward destination
		var dx = sign(to.x - current.x)
		var dy = sign(to.y - current.y)

		# Random walk with bias toward destination
		if randf() < 0.8:
			# Move toward destination
			if abs(to.x - current.x) > abs(to.y - current.y):
				current.x += dx
			else:
				current.y += dy
		else:
			# Random perpendicular movement for winding roads
			if randi() % 2 == 0:
				current.x += [-1, 1][randi() % 2]
			else:
				current.y += [-1, 1][randi() % 2]

		# Place road if on wasteland or forest
		if grid.is_valid_position(current):
			var tile = grid.get_tile(current)
			if tile == OverworldGrid.TileType.WASTELAND or tile == OverworldGrid.TileType.FOREST:
				grid.set_tile(current, OverworldGrid.TileType.ROAD)

static func _find_clear_spot(grid: OverworldGrid, existing: Array, min_distance: float) -> Vector2i:
	"""Find a clear spot on the map away from existing locations"""
	for attempt in range(100):
		var x = randi_range(10, grid.WORLD_WIDTH - 10)
		var y = randi_range(10, grid.WORLD_HEIGHT - 10)
		var pos = Vector2i(x, y)

		if grid.get_tile(pos) != OverworldGrid.TileType.WASTELAND:
			continue

		var too_close = false
		for existing_pos in existing:
			if existing_pos is Vector2i and pos.distance_to(existing_pos) < min_distance:
				too_close = true
				break

		if not too_close:
			return pos

	return Vector2i(-1, -1)
