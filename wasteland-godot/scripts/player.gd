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

# XP progression (DCSS-style exponential)
const XP_PER_LEVEL = [
	0, 10, 30, 60, 100, 150,  # Levels 1-6
	210, 280, 360, 450, 550,  # Levels 7-11
	660, 780, 910, 1050, 1200  # Levels 12-16
]

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

var equipment: Dictionary = {}  # EquipSlot -> WastelandItem
var inventory: Array[WastelandItem] = []  # Player inventory (52 slots like DCSS)
const MAX_INVENTORY = 52

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
	print(">>> Player try_move: current=%s, new=%s, direction=%s" % [grid_position, new_pos, direction])

	# Check for entity at target position FIRST (before walkability)
	var entity = grid.get_entity(new_pos)
	print("  Entity at target: ", entity)
	print("  Entity valid: ", is_instance_valid(entity) if entity else false)

	# Only attack valid entities (ignore freed objects)
	if entity != null and is_instance_valid(entity) and entity != self:
		# Attack the entity instead of moving
		print("  >>> ATTACKING ENTITY!")
		attack_entity(entity)
		turn_ended.emit()
		return false
	elif entity != null and not is_instance_valid(entity):
		# Clean up freed entity from grid
		print("  Cleaning up freed object at ", new_pos)
		grid.remove_entity(new_pos)

	# Check if position is walkable
	if not grid.is_walkable(new_pos):
		print("  Position not walkable")
		var tile_type = grid.get_tile(new_pos)
		var entity_there = grid.get_entity(new_pos)
		print("  Tile type: ", tile_type)
		print("  Entity blocking: ", entity_there)
		if entity_there:
			print("  Entity class: ", entity_there.get_class())
			print("  Is valid: ", is_instance_valid(entity_there))
		return false

	# Move to new position
	print("  Moving to new position")
	var old_pos = grid_position
	grid.move_entity(grid_position, new_pos)
	grid_position = new_pos
	position = grid.grid_to_world(new_pos)

	# Auto-pickup loot (DCSS-style)
	var loot = grid.get_loot(new_pos)
	if loot:
		loot.try_pickup(self)

	player_moved.emit(old_pos, new_pos)
	turn_ended.emit()
	return true

func attack_entity(entity):
	"""Attack an entity with DCSS-style combat"""
	# Monsters always have monster_name property
	var target_name = entity.monster_name if entity.get("monster_name") else "Unknown"

	print("Player attacking %s..." % target_name)

	# Get equipped weapon
	var weapon = get_equipped(EquipSlot.WEAPON)
	var base_damage = 3  # Unarmed damage

	if weapon and weapon is WastelandWeapon:
		base_damage = weapon.calculate_damage(self)
		print("  Using weapon, base_damage=", base_damage)
	else:
		print("  Unarmed, base_damage=", base_damage)

	# Add randomness (80-120%)
	var variance = 0.8 + (randf() * 0.4)
	var final_damage = max(1, base_damage * variance)
	print("  Final damage=%.1f (variance=%.2f)" % [final_damage, variance])

	# Apply damage to target
	if entity.has_method("take_damage"):
		print("  Calling entity.take_damage(%.1f, 'physical')" % final_damage)
		var damage_dealt = entity.take_damage(final_damage, "physical")
		print("  Damage dealt: %.1f" % damage_dealt)
		print("You hit %s for %.1f damage!" % [target_name, damage_dealt])
	else:
		print("  ERROR: Entity has no take_damage method!")
		print("You attack %s!" % target_name)

func take_damage(amount: float, damage_type: String = "physical") -> float:
	"""Take damage with optional damage type"""
	print("Player taking %.1f %s damage! HP: %d -> %d" % [amount, damage_type, current_hp, current_hp - int(amount)])
	current_hp -= int(amount)
	if current_hp <= 0:
		current_hp = 0
		die()
	return amount

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

func add_to_inventory(item: WastelandItem) -> bool:
	"""Add item to inventory. Returns true if successful"""
	if inventory.size() >= MAX_INVENTORY:
		return false
	inventory.append(item)
	return true

func remove_from_inventory(item: WastelandItem) -> bool:
	"""Remove item from inventory. Returns true if found and removed"""
	var index = inventory.find(item)
	if index >= 0:
		inventory.remove_at(index)
		return true
	return false

func get_inventory_item(index: int):
	"""Get item at inventory index"""
	if index >= 0 and index < inventory.size():
		return inventory[index]
	return null

func can_equip_to_slot(item: WastelandItem, slot: EquipSlot) -> bool:
	"""Check if item can be equipped to a specific slot"""
	if item is WastelandWeapon:
		# Weapons can go in weapon or offhand
		return slot == EquipSlot.WEAPON or slot == EquipSlot.OFFHAND
	elif item is WastelandArmor:
		var armor = item as WastelandArmor
		# Armor must match the slot
		return WastelandArmor.get_slot(armor.armor_type) == slot
	return false

func gain_experience(amount: int):
	"""Gain XP and check for level up"""
	experience += amount
	print("Gained %d XP! Total: %d" % [amount, experience])

	# Check for level up
	while level < XP_PER_LEVEL.size() and experience >= XP_PER_LEVEL[level]:
		level_up()

func level_up():
	"""Level up and increase stats"""
	level += 1
	print("*** LEVEL UP! Now level %d ***" % level)

	# DCSS-style stat increases
	max_hp += 5  # +5 HP per level
	current_hp = max_hp  # Full heal on level up

	if level % 2 == 0:  # Every 2 levels
		max_mp += 2
		current_mp = max_mp

	print("  HP: %d, MP: %d" % [max_hp, max_mp])

func get_xp_needed() -> int:
	"""Get XP needed for next level"""
	if level >= XP_PER_LEVEL.size():
		return 9999  # Max level
	return XP_PER_LEVEL[level] - experience

func get_xp_progress() -> float:
	"""Get progress to next level (0.0 - 1.0)"""
	if level >= XP_PER_LEVEL.size():
		return 1.0  # Max level

	var current_level_xp = XP_PER_LEVEL[level - 1] if level > 0 else 0
	var next_level_xp = XP_PER_LEVEL[level]
	var xp_in_level = experience - current_level_xp
	var xp_for_level = next_level_xp - current_level_xp

	return float(xp_in_level) / float(xp_for_level)
