class_name MagicSchool
extends Resource

## Magic School System for DCSS-style spell system
## Represents the various schools of magic and their interactions

enum School {
	# Elemental schools
	FIRE,
	ICE,
	AIR,
	EARTH,
	POISON,

	# Core magic schools
	CONJURATIONS,
	HEXES,
	CHARMS,
	SUMMONINGS,
	NECROMANCY,
	TRANSLOCATIONS,
	TRANSMUTATIONS,

	# Special
	DIVINATIONS
}

# School metadata
const SCHOOL_DATA = {
	School.FIRE: {
		"name": "Fire Magic",
		"description": "Spells of flame and heat",
		"color": Color(1.0, 0.33, 0.0),  # FF5500
		"skill": "FIRE_MAGIC"
	},
	School.ICE: {
		"name": "Ice Magic",
		"description": "Spells of frost and cold",
		"color": Color(0.0, 0.87, 1.0),  # 00DDFF
		"skill": "ICE_MAGIC"
	},
	School.AIR: {
		"name": "Air Magic",
		"description": "Spells of lightning and wind",
		"color": Color(0.8, 0.8, 1.0),  # CCCCFF
		"skill": "AIR_MAGIC"
	},
	School.EARTH: {
		"name": "Earth Magic",
		"description": "Spells of stone and gravity",
		"color": Color(0.53, 0.33, 0.13),  # 885522
		"skill": "EARTH_MAGIC"
	},
	School.POISON: {
		"name": "Poison Magic",
		"description": "Spells of venom and toxins",
		"color": Color(0.0, 0.8, 0.0),  # 00CC00
		"skill": "POISON_MAGIC"
	},
	School.CONJURATIONS: {
		"name": "Conjurations",
		"description": "Direct damage spells",
		"color": Color(1.0, 0.0, 0.0),  # FF0000
		"skill": "CONJURATIONS"
	},
	School.HEXES: {
		"name": "Hexes",
		"description": "Debuffs and curses",
		"color": Color(0.67, 0.0, 0.67),  # AA00AA
		"skill": "HEXES"
	},
	School.CHARMS: {
		"name": "Charms",
		"description": "Buffs and enhancements",
		"color": Color(1.0, 0.67, 0.0),  # FFAA00
		"skill": "CHARMS"
	},
	School.SUMMONINGS: {
		"name": "Summonings",
		"description": "Summoning creatures",
		"color": Color(0.0, 0.67, 1.0),  # 00AAFF
		"skill": "SUMMONINGS"
	},
	School.NECROMANCY: {
		"name": "Necromancy",
		"description": "Death and undead magic",
		"color": Color(0.27, 0.27, 0.27),  # 444444
		"skill": "NECROMANCY"
	},
	School.TRANSLOCATIONS: {
		"name": "Translocations",
		"description": "Teleportation and space",
		"color": Color(1.0, 0.0, 1.0),  # FF00FF
		"skill": "TRANSLOCATIONS"
	},
	School.TRANSMUTATIONS: {
		"name": "Transmutations",
		"description": "Shape-shifting and transformation",
		"color": Color(1.0, 1.0, 0.0),  # FFFF00
		"skill": "TRANSMUTATIONS"
	},
	School.DIVINATIONS: {
		"name": "Divinations",
		"description": "Information and detection",
		"color": Color(0.8, 0.8, 0.8),  # CCCCCC
		"skill": "DIVINATIONS"
	}
}

# School oppositions (incompatible schools that are harder to train together)
const SCHOOL_OPPOSITIONS = {
	School.FIRE: [School.ICE],
	School.ICE: [School.FIRE],
	School.AIR: [School.EARTH],
	School.EARTH: [School.AIR],
	School.NECROMANCY: [School.CHARMS],
	School.CHARMS: [School.NECROMANCY]
}

# Miscast severity levels
enum MiscastSeverity {
	HARMLESS,  # No effect or minor inconvenience
	MINOR,     # Minor damage or status effect
	MAJOR,     # Significant damage or dangerous effect
	SEVERE     # Potentially lethal or permanent effect
}

## Get school display name
static func get_school_name(school: School) -> String:
	return SCHOOL_DATA[school]["name"]

## Get school description
static func get_school_description(school: School) -> String:
	return SCHOOL_DATA[school]["description"]

## Get school color
static func get_school_color(school: School) -> Color:
	return SCHOOL_DATA[school]["color"]

## Get associated skill for a school
static func get_school_skill(school: School) -> String:
	return SCHOOL_DATA[school]["skill"]

## Check if two schools are opposed
static func are_schools_opposed(school1: School, school2: School) -> bool:
	if school1 in SCHOOL_OPPOSITIONS:
		return school2 in SCHOOL_OPPOSITIONS[school1]
	return false

## Calculate miscast chance based on skill level
## Returns chance as percentage (0-100)
static func calculate_miscast_chance(
	spell_level: int,
	skill_levels: Dictionary,  # {School: int}
	intelligence: int
) -> float:
	var base_chance = spell_level * 5.0  # 5% per spell level

	# Reduce by skill levels (average of all schools)
	var total_skill_reduction = 0.0
	for school in skill_levels:
		total_skill_reduction += skill_levels[school]

	if skill_levels.size() > 0:
		var avg_skill = total_skill_reduction / skill_levels.size()
		base_chance -= avg_skill * 2.0  # -2% per skill level

	# Intelligence reduces miscast chance
	var int_modifier = (intelligence - 8) * 0.5
	base_chance -= int_modifier

	return clamp(base_chance, 0.0, 95.0)

## Calculate miscast severity
static func calculate_miscast_severity(
	spell_level: int,
	skill_levels: Dictionary
) -> MiscastSeverity:
	var avg_skill = 0.0
	if skill_levels.size() > 0:
		var total = 0.0
		for school in skill_levels:
			total += skill_levels[school]
		avg_skill = total / skill_levels.size()

	var skill_deficit = spell_level - avg_skill

	if skill_deficit <= 0:
		return MiscastSeverity.HARMLESS
	elif skill_deficit <= 2:
		return MiscastSeverity.MINOR
	elif skill_deficit <= 4:
		return MiscastSeverity.MAJOR
	else:
		return MiscastSeverity.SEVERE

## Apply miscast effect
static func apply_miscast_effect(
	caster: Node,
	spell_schools: Array,
	severity: MiscastSeverity
) -> String:
	match severity:
		MiscastSeverity.HARMLESS:
			return _apply_harmless_miscast(caster, spell_schools)
		MiscastSeverity.MINOR:
			return _apply_minor_miscast(caster, spell_schools)
		MiscastSeverity.MAJOR:
			return _apply_major_miscast(caster, spell_schools)
		MiscastSeverity.SEVERE:
			return _apply_severe_miscast(caster, spell_schools)
	return "Nothing happens."

## Harmless miscast effects
static func _apply_harmless_miscast(caster: Node, schools: Array) -> String:
	var effects = [
		"Sparks fly from your fingertips.",
		"You smell ozone.",
		"Your hair stands on end momentarily.",
		"You feel a tingle of magical energy.",
		"Nothing happens."
	]
	return effects[randi() % effects.size()]

## Minor miscast effects
static func _apply_minor_miscast(caster: Node, schools: Array) -> String:
	var damage = randi() % 6 + 1  # 1d6 damage
	if caster.has_method("take_damage"):
		caster.take_damage(damage, "miscast")

	var effects = [
		"The spell backfires! You take %d damage." % damage,
		"Magical energy burns you for %d damage!" % damage,
		"You are confused by the magical feedback!",
		"You feel drained. (-2 MP)"
	]
	return effects[randi() % effects.size()]

## Major miscast effects
static func _apply_major_miscast(caster: Node, schools: Array) -> String:
	var damage = randi() % 12 + 6  # 1d12+6 damage
	if caster.has_method("take_damage"):
		caster.take_damage(damage, "miscast")

	# Check for school-specific effects
	if School.FIRE in schools:
		return "Flames erupt around you! You take %d fire damage and are set ablaze!" % damage
	elif School.ICE in schools:
		return "You are flash-frozen! You take %d cold damage and are slowed!" % damage
	elif School.TRANSLOCATIONS in schools:
		return "Space warps around you! You are randomly teleported!"
	elif School.NECROMANCY in schools:
		return "Death magic drains your life force! You take %d damage and lose 1 STR!" % damage
	else:
		return "The spell violently backfires! You take %d damage!" % damage

## Severe miscast effects
static func _apply_severe_miscast(caster: Node, schools: Array) -> String:
	var damage = randi() % 20 + 15  # 1d20+15 damage
	if caster.has_method("take_damage"):
		caster.take_damage(damage, "miscast")

	# Severe school-specific catastrophes
	if School.FIRE in schools:
		return "A FIRESTORM erupts! You take %d fire damage and everything around you ignites!" % damage
	elif School.ICE in schools:
		return "Absolute zero engulfs you! You take %d cold damage and are frozen solid!" % damage
	elif School.TRANSLOCATIONS in schools:
		return "You are torn apart by spatial distortion! You take %d damage and are banished!" % damage
	elif School.NECROMANCY in schools:
		return "Your soul is ripped from your body! You take %d damage and gain a permanent debuff!" % damage
	elif School.SUMMONINGS in schools:
		return "A hostile demon is summoned! You take %d damage!" % damage
	elif School.TRANSMUTATIONS in schools:
		return "Your body twists and mutates! You take %d damage and gain a random mutation!" % damage
	else:
		return "CATASTROPHIC MAGICAL FAILURE! You take %d damage!" % damage

## Get training cost modifier for opposing schools
static func get_training_cost_modifier(
	target_school: School,
	existing_skills: Dictionary  # {School: int}
) -> float:
	var modifier = 1.0

	for school in existing_skills:
		if are_schools_opposed(target_school, school):
			var skill_level = existing_skills[school]
			# Each level in opposed school increases cost by 5%
			modifier += skill_level * 0.05

	return modifier

## Get all schools as array
static func get_all_schools() -> Array:
	return [
		School.FIRE,
		School.ICE,
		School.AIR,
		School.EARTH,
		School.POISON,
		School.CONJURATIONS,
		School.HEXES,
		School.CHARMS,
		School.SUMMONINGS,
		School.NECROMANCY,
		School.TRANSLOCATIONS,
		School.TRANSMUTATIONS,
		School.DIVINATIONS
	]

## Format schools list for display
static func format_schools_list(schools: Array) -> String:
	var names = []
	for school in schools:
		names.append(get_school_name(school))
	return "/".join(names)
