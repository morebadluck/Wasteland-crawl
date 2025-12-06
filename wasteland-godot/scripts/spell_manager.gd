class_name SpellManager
extends Node

## Manages known and memorized spells for a player
## Based on DCSS spell memorization system

# Maximum spell slots
const BASE_SPELL_SLOTS = 5
const SLOTS_PER_XL = 1  # +1 slot every 2 levels

# Player reference
var player_character = null

# Known spells (player's spell library)
var known_spells: Array = []  # Array of spell IDs

# Memorized spells (quick-cast slots)
var spell_slots: Dictionary = {}  # {slot_number: spell_id}

## Initialize with player character
func _init(p_character = null):
	player_character = p_character

## Learn a spell (add to library)
func learn_spell(spell_id: String) -> bool:
	if spell_id in known_spells:
		print("Already know spell: %s" % spell_id)
		return false

	known_spells.append(spell_id)
	print("Learned spell: %s" % spell_id)
	return true

## Forget a spell (remove from library)
func forget_spell(spell_id: String) -> bool:
	if not spell_id in known_spells:
		return false

	known_spells.erase(spell_id)

	# Also remove from memorized slots
	var slots_to_remove = []
	for slot in spell_slots:
		if spell_slots[slot] == spell_id:
			slots_to_remove.append(slot)

	for slot in slots_to_remove:
		spell_slots.erase(slot)

	print("Forgot spell: %s" % spell_id)
	return true

## Memorize a spell in a slot
func memorize_spell(spell_id: String, slot_number: int) -> bool:
	# Check if spell is known
	if not spell_id in known_spells:
		print("Cannot memorize unknown spell: %s" % spell_id)
		return false

	# Check slot validity
	if slot_number < 0 or slot_number >= get_max_spell_slots():
		print("Invalid spell slot: %d" % slot_number)
		return false

	# Check if spell is already memorized
	if is_memorized(spell_id):
		print("Spell already memorized: %s" % spell_id)
		return false

	spell_slots[slot_number] = spell_id
	print("Memorized %s in slot %d" % [spell_id, slot_number])
	return true

## Unmemorize a spell from a slot
func unmemorize_spell(slot_number: int) -> bool:
	if not slot_number in spell_slots:
		return false

	var spell_id = spell_slots[slot_number]
	spell_slots.erase(slot_number)
	print("Unmemorized %s from slot %d" % [spell_id, slot_number])
	return true

## Get spell in a specific slot
func get_memorized_spell(slot_number: int) -> Spell:
	if not slot_number in spell_slots:
		return null
	return Spell.get_spell(spell_slots[slot_number])

## Get all memorized spells
func get_memorized_spells() -> Array:
	var spells = []
	for i in range(get_max_spell_slots()):
		var spell = get_memorized_spell(i)
		if spell:
			spells.append(spell)
	return spells

## Get all known spells
func get_known_spells() -> Array:
	var spells = []
	for spell_id in known_spells:
		var spell = Spell.get_spell(spell_id)
		if spell:
			spells.append(spell)
	return spells

## Check if player knows a spell
func knows_spell(spell_id: String) -> bool:
	return spell_id in known_spells

## Check if spell is memorized
func is_memorized(spell_id: String) -> bool:
	return spell_id in spell_slots.values()

## Get slot number for a spell (-1 if not memorized)
func get_slot_number(spell_id: String) -> int:
	for slot in spell_slots:
		if spell_slots[slot] == spell_id:
			return slot
	return -1

## Get maximum spell slots based on character level
func get_max_spell_slots() -> int:
	if not player_character:
		return BASE_SPELL_SLOTS

	var xl = 1
	if "experience_level" in player_character:
		xl = player_character.experience_level
	elif player_character.has_method("get_experience_level"):
		xl = player_character.get_experience_level()

	return BASE_SPELL_SLOTS + int(xl / 2) * SLOTS_PER_XL

## Get number of used spell slots
func get_used_slots() -> int:
	return spell_slots.size()

## Get number of free spell slots
func get_free_slots() -> int:
	return get_max_spell_slots() - get_used_slots()

## Cast a spell from a slot
func cast_spell_from_slot(slot_number: int, target = null, target_pos: Vector2 = Vector2.ZERO, world = null) -> bool:
	var spell = get_memorized_spell(slot_number)
	if not spell:
		print("No spell in slot %d" % slot_number)
		return false

	return spell.cast(player_character, target, target_pos, world)

## Cast a spell by ID (must be memorized)
func cast_spell_by_id(spell_id: String, target = null, target_pos: Vector2 = Vector2.ZERO, world = null) -> bool:
	var slot = get_slot_number(spell_id)
	if slot == -1:
		print("Spell not memorized: %s" % spell_id)
		return false

	return cast_spell_from_slot(slot, target, target_pos, world)

## Auto-fill empty slots with known spells
func auto_memorize_spells() -> void:
	var available_spells = get_known_spells()
	if available_spells.size() == 0:
		return

	# Sort by level (easier spells first)
	available_spells.sort_custom(func(a, b): return a.spell_level < b.spell_level)

	var current_slot = 0
	for spell in available_spells:
		if current_slot >= get_max_spell_slots():
			break

		# Skip if already memorized
		if is_memorized(spell.id):
			continue

		# Find next empty slot
		while current_slot in spell_slots and current_slot < get_max_spell_slots():
			current_slot += 1

		if current_slot < get_max_spell_slots():
			memorize_spell(spell.id, current_slot)
			current_slot += 1

## Get spell slot layout for UI
func get_slot_layout() -> Array:
	var layout = []
	for i in range(get_max_spell_slots()):
		var spell = get_memorized_spell(i)
		layout.append({
			"slot": i,
			"spell": spell,
			"key": _get_slot_hotkey(i)
		})
	return layout

## Get hotkey for slot
func _get_slot_hotkey(slot: int) -> String:
	match slot:
		0: return "Q"
		1: return "W"
		2: return "E"
		3: return "R"
		4: return "T"
		5: return "A"
		6: return "S"
		7: return "D"
		8: return "F"
		9: return "G"
		_: return str(slot)

## Save spell data to dictionary
func save_to_dict() -> Dictionary:
	return {
		"known_spells": known_spells.duplicate(),
		"spell_slots": spell_slots.duplicate()
	}

## Load spell data from dictionary
func load_from_dict(data: Dictionary) -> void:
	known_spells = data.get("known_spells", []).duplicate()
	spell_slots = data.get("spell_slots", {}).duplicate()

	print("Loaded spell data: %d known, %d memorized" % [known_spells.size(), spell_slots.size()])

## Get formatted spell book display
func get_spell_book_text() -> String:
	var text = "[b]Spell Library[/b]\n"
	text += "Spell Slots: %d/%d\n\n" % [get_used_slots(), get_max_spell_slots()]

	# Memorized spells
	text += "[color=yellow]Memorized Spells:[/color]\n"
	var has_memorized = false
	for i in range(get_max_spell_slots()):
		var spell = get_memorized_spell(i)
		if spell:
			has_memorized = true
			text += "  %s) %s (Lv%d, %s)\n" % [
				_get_slot_hotkey(i),
				spell.display_name,
				spell.spell_level,
				MagicSchool.format_schools_list(spell.schools)
			]

	if not has_memorized:
		text += "  (none)\n"

	# Known but not memorized
	text += "\n[color=cyan]Known Spells:[/color]\n"
	var unmemorized = []
	for spell_id in known_spells:
		if not is_memorized(spell_id):
			var spell = Spell.get_spell(spell_id)
			if spell:
				unmemorized.append(spell)

	if unmemorized.size() > 0:
		unmemorized.sort_custom(func(a, b): return a.spell_level < b.spell_level)
		for spell in unmemorized:
			text += "  %s (Lv%d, %s)\n" % [
				spell.display_name,
				spell.spell_level,
				MagicSchool.format_schools_list(spell.schools)
			]
	else:
		text += "  (all spells memorized)\n"

	return text

## Get MP cost for spell in slot
func get_spell_mp_cost(slot_number: int) -> int:
	var spell = get_memorized_spell(slot_number)
	if not spell or not player_character:
		return 0
	return spell.calculate_mp_cost(player_character)

## Get spell power for spell in slot
func get_spell_power(slot_number: int) -> int:
	var spell = get_memorized_spell(slot_number)
	if not spell or not player_character:
		return 0
	return spell.calculate_spell_power(player_character)

## Check if player can cast spell in slot
func can_cast_spell(slot_number: int) -> bool:
	var spell = get_memorized_spell(slot_number)
	if not spell or not player_character:
		return false

	var mp_cost = spell.calculate_mp_cost(player_character)
	var current_mp = 0
	if "current_mp" in player_character:
		current_mp = player_character.current_mp
	elif player_character.has_method("get_current_mp"):
		current_mp = player_character.get_current_mp()

	return current_mp >= mp_cost

## Clear all spells (for testing)
func clear_all() -> void:
	known_spells.clear()
	spell_slots.clear()
	print("Cleared all spell data")
