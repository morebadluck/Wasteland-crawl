extends Node
class_name TurnManager

## Turn-based combat manager for Wasteland Crawl
## Processes player and monster turns in sequence
## DCSS-style turn management

signal all_turns_complete

var grid: Grid
var player: Player
var dungeon_manager  # DungeonManager3D reference

# Turn state
var is_processing_turns: bool = false
var monsters_in_dungeon: Array = []

func _ready():
	add_to_group("turn_managers")

func initialize(player_ref: Player, grid_ref: Grid, dungeon_ref):
	"""Initialize the turn manager with references"""
	player = player_ref
	grid = grid_ref
	dungeon_manager = dungeon_ref

	# Connect to player's turn_ended signal
	if player and player.has_signal("turn_ended"):
		player.turn_ended.connect(_on_player_turn_ended)
		print("TurnManager: Connected to player turn_ended signal")

func _on_player_turn_ended():
	"""Called when player ends their turn - process all monster turns"""
	if is_processing_turns:
		return  # Already processing

	print("\n=== PLAYER TURN ENDED - PROCESSING MONSTER TURNS ===")
	is_processing_turns = true

	# Get all active monsters from the grid
	update_monster_list()

	# Process each monster's turn
	for monster in monsters_in_dungeon:
		if not is_instance_valid(monster) or monster.current_hp <= 0:
			continue  # Skip dead/invalid monsters

		# Execute monster turn (AI decides action)
		var all_combatants = [player] + monsters_in_dungeon
		monster.take_turn(player, all_combatants)

	print("=== MONSTER TURNS COMPLETE ===\n")
	is_processing_turns = false
	all_turns_complete.emit()

func update_monster_list():
	"""Scan the grid for all active monsters"""
	monsters_in_dungeon.clear()

	if not grid:
		return

	# Iterate through all entities in the grid
	for pos in grid.entities.keys():
		var entity = grid.get_entity(pos)
		if entity and is_instance_valid(entity) and entity is Monster:
			if entity.current_hp > 0:  # Only include living monsters
				monsters_in_dungeon.append(entity)

	print("TurnManager: Found %d active monsters" % monsters_in_dungeon.size())

func get_monsters() -> Array:
	"""Get list of active monsters"""
	return monsters_in_dungeon
