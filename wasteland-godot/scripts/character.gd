extends Resource
class_name PlayerCharacter

## DCSS-style player character system
## Tracks race, skills, spells, stats, and progression
## This is a port from the Wasteland Crawl Minecraft mod

# Signals for character events
signal level_up(new_level: int)
signal skill_improved(skill: Skill.Type, new_level: int)
signal hp_changed(current: int, max: int)
signal mp_changed(current: int, max: int)
signal stat_changed(stat_name: String, new_value: int)
signal character_died()

# Core attributes
var player_id: String = ""
var race: Race.Type = Race.Type.HUMAN
var character_name: String = "Unknown"

# Experience
var experience_level: int = 1  # Character level (1-27 in DCSS)
var total_xp: int = 0

# Primary stats (DCSS-style)
var base_strength: int = 8       # Base STR (affected by level-ups)
var base_dexterity: int = 8      # Base DEX
var base_intelligence: int = 8   # Base INT

# Equipment bonuses (populated by equipment system)
var equipment_strength: int = 0
var equipment_dexterity: int = 0
var equipment_intelligence: int = 0

# Mutation/status effect bonuses (can be added later)
var mutation_strength: int = 0
var mutation_dexterity: int = 0
var mutation_intelligence: int = 0
var mutation_hp_multiplier: float = 1.0
var mutation_mp_multiplier: float = 1.0

# Status effect bonuses (temporary)
var status_strength: int = 0
var status_dexterity: int = 0
var status_intelligence: int = 0

# Secondary stats (derived from race and primary stats)
var max_hp: int = 15
var current_hp: int = 15
var max_mp: int = 5
var current_mp: int = 5

# Skills: Dictionary mapping Skill.Type to level (0-27 in DCSS)
var skill_levels: Dictionary = {}
# Skill progress toward next level (0.0-1.0)
var skill_progress: Dictionary = {}

# Spells (for future implementation)
# var known_spells: Array = []      # All spells learned
# var memorized_spells: Array = []  # Active spell slots

## Initialize a new character
func _init(p_player_id: String = "", p_race: Race.Type = Race.Type.HUMAN, p_character_name: String = "Adventurer"):
	player_id = p_player_id
	race = p_race
	character_name = p_character_name

	# Initialize all skills to 0
	for skill in Skill.Type.values():
		skill_levels[skill] = 0
		skill_progress[skill] = 0.0

	# Starting stats
	experience_level = 1
	total_xp = 0

	# Base primary stats (all races start at 8)
	base_strength = 8
	base_dexterity = 8
	base_intelligence = 8

	# Calculate derived stats
	_update_derived_stats()

	print("Created new character: %s (%s) | STR: %d, DEX: %d, INT: %d" % [
		character_name,
		Race.get_display_name(race),
		get_strength(),
		get_dexterity(),
		get_intelligence()
	])

# ===== Primary Stat Getters (with all modifiers) =====

## Get total strength (base + race + equipment + mutations + status effects)
func get_strength() -> int:
	return base_strength + \
		Race.get_strength_modifier(race) + \
		equipment_strength + \
		mutation_strength + \
		status_strength

## Get total dexterity (base + race + equipment + mutations + status effects)
func get_dexterity() -> int:
	return base_dexterity + \
		Race.get_dexterity_modifier(race) + \
		equipment_dexterity + \
		mutation_dexterity + \
		status_dexterity

## Get total intelligence (base + race + equipment + mutations + status effects)
func get_intelligence() -> int:
	return base_intelligence + \
		Race.get_intelligence_modifier(race) + \
		equipment_intelligence + \
		mutation_intelligence + \
		status_intelligence

## Get base strength (before modifiers)
func get_base_strength() -> int:
	return base_strength

## Get base dexterity (before modifiers)
func get_base_dexterity() -> int:
	return base_dexterity

## Get base intelligence (before modifiers)
func get_base_intelligence() -> int:
	return base_intelligence

# ===== Stat Modification Methods =====

## Increase base strength (from level-ups or potions)
func increase_strength(amount: int) -> void:
	base_strength += amount
	_update_derived_stats()
	print("Strength increased by %d! Now: %d" % [amount, get_strength()])
	stat_changed.emit("STR", get_strength())

## Increase base dexterity (from level-ups or potions)
func increase_dexterity(amount: int) -> void:
	base_dexterity += amount
	_update_derived_stats()
	print("Dexterity increased by %d! Now: %d" % [amount, get_dexterity()])
	stat_changed.emit("DEX", get_dexterity())

## Increase base intelligence (from level-ups or potions)
func increase_intelligence(amount: int) -> void:
	base_intelligence += amount
	_update_derived_stats()
	print("Intelligence increased by %d! Now: %d" % [amount, get_intelligence()])
	stat_changed.emit("INT", get_intelligence())

## Update equipment stat bonuses (called by equipment system)
func update_equipment_bonuses(str_bonus: int, dex_bonus: int, int_bonus: int) -> void:
	equipment_strength = str_bonus
	equipment_dexterity = dex_bonus
	equipment_intelligence = int_bonus
	_update_derived_stats()

# ===== HP/MP Getters =====

## Get maximum HP (with mutation modifiers)
func get_max_hp() -> int:
	return int(max_hp * mutation_hp_multiplier)

## Get current HP
func get_current_hp() -> int:
	return current_hp

## Get maximum MP (with mutation modifiers)
func get_max_mp() -> int:
	return int(max_mp * mutation_mp_multiplier)

## Get current MP
func get_current_mp() -> int:
	return current_mp

# ===== HP/MP Modification Methods =====

## Heal HP
func heal(amount: int) -> void:
	current_hp = mini(current_hp + amount, get_max_hp())
	hp_changed.emit(current_hp, get_max_hp())

## Restore MP
func restore_mp(amount: int) -> void:
	current_mp = mini(current_mp + amount, get_max_mp())
	mp_changed.emit(current_mp, get_max_mp())

## Consume MP (for spellcasting)
func consume_mp(amount: int) -> bool:
	if current_mp < amount:
		return false
	current_mp = maxi(0, current_mp - amount)
	mp_changed.emit(current_mp, get_max_mp())
	return true

## Take damage
func take_damage(damage: int) -> void:
	current_hp = maxi(0, current_hp - damage)
	hp_changed.emit(current_hp, get_max_hp())
	if current_hp == 0:
		print("Character %s has died!" % character_name)
		character_died.emit()

## Check if character is alive
func is_alive() -> bool:
	return current_hp > 0

# ===== Skill System =====

## Get skill level (0-27)
func get_skill_level(skill: Skill.Type) -> int:
	return skill_levels.get(skill, 0)

## Get skill progress toward next level (0.0-1.0)
func get_skill_progress(skill: Skill.Type) -> float:
	return skill_progress.get(skill, 0.0)

## Train a skill by adding XP
## Takes aptitude into account
func train_skill(skill: Skill.Type, xp: int) -> void:
	var current_level = skill_levels.get(skill, 0)
	if current_level >= 27:
		print("Skill %s already at max level (27)" % Skill.get_display_name(skill))
		return

	# Apply aptitude modifier
	var aptitude = Race.get_aptitude(race, skill)
	var aptitude_multiplier = 1.0 + (aptitude * 0.1)  # Each aptitude point = 10% faster/slower
	var effective_xp = xp * aptitude_multiplier

	# Add to progress
	var progress = skill_progress.get(skill, 0.0)
	progress += effective_xp / 100.0  # Simplified: 100 XP per level

	# Level up if necessary
	while progress >= 1.0 and current_level < 27:
		progress -= 1.0
		current_level += 1
		skill_levels[skill] = current_level
		print("Skill improved: %s is now level %d!" % [Skill.get_display_name(skill), current_level])
		skill_improved.emit(skill, current_level)

		# Update derived stats (HP/MP may have changed)
		_update_derived_stats()

	skill_progress[skill] = progress

## Set skill level directly (for testing/debugging)
func set_skill_level(skill: Skill.Type, level: int) -> void:
	level = clampi(level, 0, 27)
	skill_levels[skill] = level
	skill_progress[skill] = 0.0
	_update_derived_stats()
	skill_improved.emit(skill, level)

# ===== Experience and Leveling =====

## Gain experience and possibly level up
func gain_xp(xp: int) -> void:
	total_xp += xp

	# Check for level up (simplified: 100 XP per level)
	var xp_for_next_level = experience_level * 100
	if total_xp >= xp_for_next_level and experience_level < 27:
		experience_level += 1
		print("═══════════════════════════════════════════════════════")
		print("  LEVEL UP! You are now level %d!" % experience_level)
		print("═══════════════════════════════════════════════════════")
		_update_derived_stats()
		level_up.emit(experience_level)

		# Fully heal on level up
		current_hp = get_max_hp()
		current_mp = get_max_mp()
		hp_changed.emit(current_hp, get_max_hp())
		mp_changed.emit(current_mp, get_max_mp())

## Get XP required for next level
func get_xp_for_next_level() -> int:
	if experience_level >= 27:
		return 0
	return experience_level * 100

## Get progress to next level (0.0-1.0)
func get_level_progress() -> float:
	var xp_needed = get_xp_for_next_level()
	if xp_needed == 0:
		return 1.0
	var xp_previous_level = (experience_level - 1) * 100
	var xp_into_level = total_xp - xp_previous_level
	return clampf(float(xp_into_level) / float(xp_needed), 0.0, 1.0)

# ===== Derived Stats Calculation =====

## Update HP/MP when skills or XL change
func _update_derived_stats() -> void:
	# HP increases with Fighting skill and experience level
	var fighting_level = get_skill_level(Skill.Type.FIGHTING)
	max_hp = 15 + Race.get_hp_modifier(race) * 2 + \
			 experience_level * 2 + \
			 fighting_level

	# MP increases with Spellcasting skill and experience level
	var spellcasting_level = get_skill_level(Skill.Type.SPELLCASTING)
	max_mp = 5 + Race.get_mp_modifier(race) * 2 + \
			 experience_level + \
			 spellcasting_level * 2

	# Don't let current values exceed max
	current_hp = mini(current_hp, get_max_hp())
	current_mp = mini(current_mp, get_max_mp())

	hp_changed.emit(current_hp, get_max_hp())
	mp_changed.emit(current_mp, get_max_mp())

# ===== Spell System (future implementation) =====

## Calculate max spell slots based on XL and spellcasting skill
func get_max_spell_slots() -> int:
	var spellcasting_level = get_skill_level(Skill.Type.SPELLCASTING)
	return 5 + experience_level + (spellcasting_level / 2)

# ===== Display and Utility Methods =====

## Get character summary for display
func get_summary() -> String:
	return "%s the %s - Level %d | HP: %d/%d | MP: %d/%d | XP: %d" % [
		character_name,
		Race.get_display_name(race),
		experience_level,
		current_hp, get_max_hp(),
		current_mp, get_max_mp(),
		total_xp
	]

## Get detailed stat block
func get_stat_block() -> String:
	var lines = []
	lines.append("=== %s ===" % character_name)
	lines.append("Race: %s" % Race.get_display_name(race))
	lines.append("Level: %d (XP: %d/%d)" % [experience_level, total_xp, get_xp_for_next_level()])
	lines.append("")
	lines.append("STR: %d (%d base)" % [get_strength(), base_strength])
	lines.append("DEX: %d (%d base)" % [get_dexterity(), base_dexterity])
	lines.append("INT: %d (%d base)" % [get_intelligence(), base_intelligence])
	lines.append("")
	lines.append("HP: %d/%d" % [current_hp, get_max_hp()])
	lines.append("MP: %d/%d" % [current_mp, get_max_mp()])
	return "\n".join(lines)

## Get list of trained skills (level > 0)
func get_trained_skills() -> Array:
	var trained = []
	for skill in skill_levels.keys():
		if skill_levels[skill] > 0:
			trained.append({
				"skill": skill,
				"level": skill_levels[skill],
				"progress": skill_progress[skill],
				"name": Skill.get_display_name(skill)
			})
	# Sort by level (highest first)
	trained.sort_custom(func(a, b): return a.level > b.level)
	return trained

## Get speed modifier (based on race)
func get_speed_modifier() -> int:
	return Race.get_speed_modifier(race)

## Serialize character to dictionary (for saving)
func to_dict() -> Dictionary:
	return {
		"player_id": player_id,
		"race": race,
		"character_name": character_name,
		"experience_level": experience_level,
		"total_xp": total_xp,
		"base_strength": base_strength,
		"base_dexterity": base_dexterity,
		"base_intelligence": base_intelligence,
		"equipment_strength": equipment_strength,
		"equipment_dexterity": equipment_dexterity,
		"equipment_intelligence": equipment_intelligence,
		"current_hp": current_hp,
		"current_mp": current_mp,
		"skill_levels": skill_levels,
		"skill_progress": skill_progress
	}

## Load character from dictionary (for loading saves)
func from_dict(data: Dictionary) -> void:
	player_id = data.get("player_id", "")
	race = data.get("race", Race.Type.HUMAN)
	character_name = data.get("character_name", "Unknown")
	experience_level = data.get("experience_level", 1)
	total_xp = data.get("total_xp", 0)
	base_strength = data.get("base_strength", 8)
	base_dexterity = data.get("base_dexterity", 8)
	base_intelligence = data.get("base_intelligence", 8)
	equipment_strength = data.get("equipment_strength", 0)
	equipment_dexterity = data.get("equipment_dexterity", 0)
	equipment_intelligence = data.get("equipment_intelligence", 0)
	current_hp = data.get("current_hp", 15)
	current_mp = data.get("current_mp", 5)
	skill_levels = data.get("skill_levels", {})
	skill_progress = data.get("skill_progress", {})

	# Ensure all skills are initialized
	for skill in Skill.Type.values():
		if not skill_levels.has(skill):
			skill_levels[skill] = 0
		if not skill_progress.has(skill):
			skill_progress[skill] = 0.0

	_update_derived_stats()
