extends Resource
class_name Race

## DCSS player races (species)
## Each race has different stat modifiers and aptitudes
## This is a port from the Wasteland Crawl Minecraft mod

# All playable races
enum Type {
	# Simple races (good for beginners)
	HUMAN,
	HILL_ORC,
	MINOTAUR,
	MERFOLK,
	GARGOYLE,
	DRACONIAN,

	# Advanced races
	DEEP_ELF,
	DEEP_DWARF,
	TROLL,
	OGRE,
	SPRIGGAN,
	KOBOLD,
	CENTAUR,

	# Monster races
	NAGA,
	VAMPIRE,
	GHOUL,
	MUMMY,
	FELID,
	OCTOPODE
}

# Race data structure
class RaceData:
	var display_name: String
	var description: String
	var hp_modifier: int           # Base HP bonus/penalty
	var mp_modifier: int           # Base MP bonus/penalty
	var speed_modifier: int        # Movement speed modifier
	var strength_modifier: int     # STR bonus/penalty
	var dexterity_modifier: int    # DEX bonus/penalty
	var intelligence_modifier: int # INT bonus/penalty

	func _init(
		p_display_name: String,
		p_description: String,
		p_hp_modifier: int,
		p_mp_modifier: int,
		p_speed_modifier: int,
		p_strength_modifier: int,
		p_dexterity_modifier: int,
		p_intelligence_modifier: int
	):
		display_name = p_display_name
		description = p_description
		hp_modifier = p_hp_modifier
		mp_modifier = p_mp_modifier
		speed_modifier = p_speed_modifier
		strength_modifier = p_strength_modifier
		dexterity_modifier = p_dexterity_modifier
		intelligence_modifier = p_intelligence_modifier

# Static race definitions
# Format: name, description, HP, MP, Speed, STR, DEX, INT
static var race_data = {
	# Simple races (good for beginners)
	Type.HUMAN: RaceData.new(
		"Human",
		"Versatile and balanced",
		0, 0, 0, 0, 0, 0
	),
	Type.HILL_ORC: RaceData.new(
		"Hill Orc",
		"Strong warriors with fire resistance",
		1, -1, 0, 2, -1, -1
	),
	Type.MINOTAUR: RaceData.new(
		"Minotaur",
		"Powerful melee fighters",
		3, -2, 0, 4, -2, -2
	),
	Type.MERFOLK: RaceData.new(
		"Merfolk",
		"Agile fighters, bonus in water",
		0, 0, 1, 0, 2, 1
	),
	Type.GARGOYLE: RaceData.new(
		"Gargoyle",
		"Stone-skinned, resistant to damage",
		2, -2, 0, 2, -2, 0
	),
	Type.DRACONIAN: RaceData.new(
		"Draconian",
		"Dragon-like humanoids with breath weapons",
		0, 0, 1, 2, 0, 0
	),

	# Advanced races
	Type.DEEP_ELF: RaceData.new(
		"Deep Elf",
		"Magical specialists, fragile",
		-2, 3, 2, -3, 2, 4
	),
	Type.DEEP_DWARF: RaceData.new(
		"Deep Dwarf",
		"Hardy but cannot regenerate naturally",
		2, -1, -1, 3, -2, 0
	),
	Type.TROLL: RaceData.new(
		"Troll",
		"Regenerating hulks",
		4, -3, -2, 5, -3, -4
	),
	Type.OGRE: RaceData.new(
		"Ogre",
		"Large and strong",
		3, -3, -1, 4, -3, -3
	),
	Type.SPRIGGAN: RaceData.new(
		"Spriggan",
		"Tiny and fast",
		-3, 0, 3, -4, 4, 2
	),
	Type.KOBOLD: RaceData.new(
		"Kobold",
		"Small and sneaky",
		-2, 0, 2, -2, 3, 0
	),
	Type.CENTAUR: RaceData.new(
		"Centaur",
		"Fast with natural aptitude for ranged combat",
		1, -1, 1, 2, 2, -2
	),

	# Monster races
	Type.NAGA: RaceData.new(
		"Naga",
		"Serpentine, slow but poison resistant",
		1, 0, -2, 2, 0, 0
	),
	Type.VAMPIRE: RaceData.new(
		"Vampire",
		"Undead bloodsucker",
		0, 1, 1, 0, 2, 2
	),
	Type.GHOUL: RaceData.new(
		"Ghoul",
		"Undead flesh-eater",
		2, -2, -1, 3, -1, -2
	),
	Type.MUMMY: RaceData.new(
		"Mummy",
		"Ancient undead, no regeneration",
		0, 0, -2, 0, 0, -2
	),
	Type.FELID: RaceData.new(
		"Felid",
		"Cat with nine lives",
		-4, 1, 3, -5, 4, 2
	),
	Type.OCTOPODE: RaceData.new(
		"Octopode",
		"Eight-tentacled aquatic creature",
		-2, 1, 2, -2, 2, 3
	)
}

# Beginner-friendly races
static var beginner_races = [
	Type.HUMAN,
	Type.HILL_ORC,
	Type.MINOTAUR,
	Type.MERFOLK,
	Type.GARGOYLE,
	Type.DRACONIAN
]

## Get display name for a race
static func get_display_name(race: Type) -> String:
	if race_data.has(race):
		return race_data[race].display_name
	return "Unknown"

## Get description for a race
static func get_description(race: Type) -> String:
	if race_data.has(race):
		return race_data[race].description
	return ""

## Get HP modifier for a race
static func get_hp_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].hp_modifier
	return 0

## Get MP modifier for a race
static func get_mp_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].mp_modifier
	return 0

## Get speed modifier for a race
static func get_speed_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].speed_modifier
	return 0

## Get strength modifier for a race
static func get_strength_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].strength_modifier
	return 0

## Get dexterity modifier for a race
static func get_dexterity_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].dexterity_modifier
	return 0

## Get intelligence modifier for a race
static func get_intelligence_modifier(race: Type) -> int:
	if race_data.has(race):
		return race_data[race].intelligence_modifier
	return 0

## Get all beginner-friendly races
static func get_beginner_races() -> Array:
	return beginner_races.duplicate()

## Get all playable races
static func get_all_races() -> Array:
	return race_data.keys()

## Check if a race is beginner-friendly
static func is_beginner_race(race: Type) -> bool:
	return race in beginner_races

## Get skill aptitude for a race and skill
## Returns aptitude modifier (-5 to +5, with 0 being average)
## Positive values = faster learning, negative = slower learning
static func get_aptitude(race: Type, skill: Skill.Type) -> int:
	# This is a simplified aptitude system
	# In full DCSS, each race has unique aptitudes for every skill
	# For now, we'll return race-based modifiers based on stats

	var data = race_data.get(race)
	if not data:
		return 0

	# Combat skills favor STR
	if skill in [
		Skill.Type.FIGHTING,
		Skill.Type.AXES,
		Skill.Type.MACES_FLAILS,
		Skill.Type.POLEARMS,
		Skill.Type.UNARMED_COMBAT
	]:
		return data.strength_modifier / 2

	# Finesse combat skills favor DEX
	if skill in [
		Skill.Type.SHORT_BLADES,
		Skill.Type.LONG_BLADES,
		Skill.Type.STAVES,
		Skill.Type.DODGING,
		Skill.Type.STEALTH
	]:
		return data.dexterity_modifier / 2

	# Ranged skills favor DEX
	if skill in [
		Skill.Type.THROWING,
		Skill.Type.BOWS,
		Skill.Type.CROSSBOWS,
		Skill.Type.GUNS
	]:
		return data.dexterity_modifier / 2

	# Defense skills
	if skill == Skill.Type.ARMOUR:
		return data.strength_modifier / 3  # STR helps with heavy armor
	if skill == Skill.Type.SHIELDS:
		return (data.strength_modifier + data.dexterity_modifier) / 4

	# Magic skills favor INT
	if skill in [
		Skill.Type.SPELLCASTING,
		Skill.Type.CONJURATIONS,
		Skill.Type.HEXES,
		Skill.Type.SUMMONINGS,
		Skill.Type.NECROMANCY,
		Skill.Type.TRANSLOCATIONS,
		Skill.Type.TRANSMUTATIONS,
		Skill.Type.FIRE_MAGIC,
		Skill.Type.ICE_MAGIC,
		Skill.Type.AIR_MAGIC,
		Skill.Type.EARTH_MAGIC,
		Skill.Type.POISON_MAGIC
	]:
		return data.intelligence_modifier / 2

	# Utility skills
	if skill in [Skill.Type.INVOCATIONS, Skill.Type.EVOCATIONS]:
		return data.intelligence_modifier / 3

	# Default
	return 0

## Get a formatted stat summary for a race
static func get_stat_summary(race: Type) -> String:
	var data = race_data.get(race)
	if not data:
		return ""

	var stats = []
	if data.hp_modifier != 0:
		stats.append("HP %+d" % data.hp_modifier)
	if data.mp_modifier != 0:
		stats.append("MP %+d" % data.mp_modifier)
	if data.speed_modifier != 0:
		stats.append("Speed %+d" % data.speed_modifier)
	if data.strength_modifier != 0:
		stats.append("STR %+d" % data.strength_modifier)
	if data.dexterity_modifier != 0:
		stats.append("DEX %+d" % data.dexterity_modifier)
	if data.intelligence_modifier != 0:
		stats.append("INT %+d" % data.intelligence_modifier)

	if stats.is_empty():
		return "No modifiers"
	return ", ".join(stats)
