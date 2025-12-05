extends Node2D
class_name Player

## Player character with turn-based movement
## Matches DCSS player mechanics

signal player_moved(from: Vector2i, to: Vector2i)
signal turn_ended

var grid_position: Vector2i = Vector2i(0, 0)
var grid: Grid

# Character stats (ported from Minecraft version)
var character_name: String = "Player"
var race: String = "Human"
var max_hp: int = 20
var current_hp: int = 20
var max_mp: int = 5
var current_mp: int = 5
var experience: int = 0
var level: int = 1

# Equipment slots (DCSS-style)
enum EquipSlot {
	WEAPON,
	OFFHAND,
	BODY,
	HELMET,
	CLOAK,
	GLOVES,
	BOOTS,
	AMULET,
	LEFT_RING,
	RIGHT_RING
}

var equipment: Dictionary = {}  # EquipSlot -> Item

func _ready():
	# Position will be set by dungeon generator
	pass

func set_grid(new_grid: Grid):
	"""Set the grid reference"""
	grid = new_grid

func spawn_at(pos: Vector2i):
	"""Spawn player at grid position"""
	grid_position = pos
	position = grid.grid_to_world(pos)
	grid.add_entity(pos, self)

func try_move(direction: Vector2i) -> bool:
	"""Attempt to move in a direction (turn-based)"""
	var new_pos = grid_position + direction

	# Check if position is walkable
	if not grid.is_walkable(new_pos):
		return false

	# Check for entity at target position
	var entity = grid.get_entity(new_pos)
	if entity != null:
		# Attack the entity instead of moving
		attack_entity(entity)
		turn_ended.emit()
		return false

	# Move to new position
	var old_pos = grid_position
	grid.move_entity(grid_position, new_pos)
	grid_position = new_pos
	position = grid.grid_to_world(new_pos)

	player_moved.emit(old_pos, new_pos)
	turn_ended.emit()
	return true

func attack_entity(entity):
	"""Attack an entity (placeholder for combat system)"""
	print("Player attacks: ", entity.name if entity.has("name") else "Unknown")
	# TODO: Implement combat system

func take_damage(amount: int):
	"""Take damage"""
	current_hp -= amount
	if current_hp <= 0:
		current_hp = 0
		die()

func die():
	"""Player death"""
	print("You have died!")
	# TODO: Show death screen, restart, etc.

func heal(amount: int):
	"""Restore HP"""
	current_hp = min(current_hp + amount, max_hp)

func restore_mp(amount: int):
	"""Restore MP"""
	current_mp = min(current_mp + amount, max_mp)

func equip_item(slot: EquipSlot, item):
	"""Equip an item to a slot"""
	var old_item = equipment.get(slot, null)
	equipment[slot] = item
	return old_item

func unequip_item(slot: EquipSlot):
	"""Remove item from slot"""
	var item = equipment.get(slot, null)
	equipment.erase(slot)
	return item

func get_equipped(slot: EquipSlot):
	"""Get item in equipment slot"""
	return equipment.get(slot, null)
