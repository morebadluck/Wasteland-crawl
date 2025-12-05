extends Node
class_name MonsterTypes

## Monster type database for Wasteland Crawl
## Includes all DCSS monster types and Fallout-style robots
## Ported from WEAPONS_AND_MONSTERS.md and MonsterSpawner.java

# All monster definitions
static var MONSTERS = {}

## Initialize monster database
static func init_monsters():
	if not MONSTERS.is_empty():
		return  # Already initialized

	# ========================================
	# EARLY GAME MONSTERS (Tier 1-3, Depth 1-5)
	# ========================================

	# Animals - Tier 1
	MONSTERS["rat"] = {
		"name": "Rat",
		"tier": 1,
		"hp": 3.0,
		"damage": 1.0,
		"ac": 0,
		"ev": 10,
		"speed": 1.2,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"flee_hp_percent": 0.3
	}

	MONSTERS["giant_rat"] = {
		"name": "Giant Rat",
		"tier": 1,
		"hp": 6.0,
		"damage": 2.0,
		"ac": 1,
		"ev": 9,
		"speed": 1.1,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["bat"] = {
		"name": "Bat",
		"tier": 1,
		"hp": 4.0,
		"damage": 1.0,
		"ac": 0,
		"ev": 14,
		"speed": 1.5,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"flee_hp_percent": 0.5
	}

	MONSTERS["jackal"] = {
		"name": "Jackal",
		"tier": 1,
		"hp": 8.0,
		"damage": 3.0,
		"ac": 1,
		"ev": 11,
		"speed": 1.3,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["hound"] = {
		"name": "Hound",
		"tier": 2,
		"hp": 12.0,
		"damage": 5.0,
		"ac": 2,
		"ev": 10,
		"speed": 1.2,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["adder"] = {
		"name": "Adder",
		"tier": 2,
		"hp": 7.0,
		"damage": 3.0,
		"ac": 1,
		"ev": 12,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0  # Immune to poison
	}

	# Undead - Early
	MONSTERS["zombie"] = {
		"name": "Zombie",
		"tier": 1,
		"hp": 15.0,
		"damage": 4.0,
		"ac": 2,
		"ev": 5,
		"speed": 0.8,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_cold": 0.5
	}

	MONSTERS["skeleton"] = {
		"name": "Skeleton",
		"tier": 1,
		"hp": 10.0,
		"damage": 5.0,
		"ac": 3,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_cold": 0.5
	}

	MONSTERS["wight"] = {
		"name": "Wight",
		"tier": 2,
		"hp": 18.0,
		"damage": 7.0,
		"ac": 4,
		"ev": 9,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_cold": 0.5
	}

	# Humanoids - Early
	MONSTERS["goblin"] = {
		"name": "Goblin",
		"tier": 1,
		"hp": 8.0,
		"damage": 3.0,
		"ac": 1,
		"ev": 10,
		"speed": 1.1,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["hobgoblin"] = {
		"name": "Hobgoblin",
		"tier": 2,
		"hp": 12.0,
		"damage": 5.0,
		"ac": 2,
		"ev": 9,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["gnoll"] = {
		"name": "Gnoll",
		"tier": 2,
		"hp": 15.0,
		"damage": 6.0,
		"ac": 3,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["orc"] = {
		"name": "Orc",
		"tier": 2,
		"hp": 18.0,
		"damage": 7.0,
		"ac": 3,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["orc_warrior"] = {
		"name": "Orc Warrior",
		"tier": 3,
		"hp": 25.0,
		"damage": 10.0,
		"ac": 6,
		"ev": 7,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["orc_priest"] = {
		"name": "Orc Priest",
		"tier": 3,
		"hp": 22.0,
		"damage": 6.0,
		"ac": 4,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.SUPPORT,
		"attack_range": 3,
		"resist_magic": 0.3
	}

	MONSTERS["orc_wizard"] = {
		"name": "Orc Wizard",
		"tier": 3,
		"hp": 18.0,
		"damage": 8.0,
		"ac": 2,
		"ev": 10,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 5,
		"resist_magic": 0.5
	}

	MONSTERS["kobold"] = {
		"name": "Kobold",
		"tier": 1,
		"hp": 7.0,
		"damage": 2.0,
		"ac": 1,
		"ev": 12,
		"speed": 1.2,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	# ========================================
	# FALLOUT ROBOTS - EARLY GAME (Tier 1-3)
	# ========================================

	MONSTERS["eyebot"] = {
		"name": "Eyebot",
		"tier": 1,
		"hp": 15.0,
		"damage": 2.0,
		"ac": 0,
		"ev": 12,
		"speed": 1.4,
		"ai_type": Monster.AIType.FLEEING,
		"attack_range": 1,
		"flee_hp_percent": 0.5,
		"is_robot": true,
		"robot_armor": "LIGHT",
		"resist_poison": 1.0,
		"resist_electric": -0.5  # Vulnerable to electric
	}

	MONSTERS["protectron"] = {
		"name": "Protectron",
		"tier": 2,
		"hp": 30.0,
		"damage": 5.0,
		"ac": 6,
		"ev": 5,
		"speed": 0.7,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_fire": 0.25,
		"resist_electric": -0.5
	}

	MONSTERS["mr_handy"] = {
		"name": "Mr. Handy",
		"tier": 2,
		"hp": 25.0,
		"damage": 6.0,
		"ac": 3,
		"ev": 8,
		"speed": 1.1,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"is_robot": true,
		"robot_armor": "LIGHT",
		"resist_poison": 1.0,
		"resist_fire": 0.5,  # Has flamethrower
		"resist_electric": -0.5
	}

	MONSTERS["security_bot"] = {
		"name": "Security Bot",
		"tier": 3,
		"hp": 35.0,
		"damage": 8.0,
		"ac": 7,
		"ev": 6,
		"speed": 0.8,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_fire": 0.25,
		"resist_electric": -0.5
	}

	# ========================================
	# MID GAME MONSTERS (Tier 4-6, Depth 6-10)
	# ========================================

	# Lesser Demons
	MONSTERS["crimson_imp"] = {
		"name": "Crimson Imp",
		"tier": 4,
		"hp": 20.0,
		"damage": 8.0,
		"ac": 2,
		"ev": 12,
		"speed": 1.3,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 4,
		"resist_fire": 0.5,
		"resist_cold": -0.5
	}

	MONSTERS["white_imp"] = {
		"name": "White Imp",
		"tier": 4,
		"hp": 20.0,
		"damage": 8.0,
		"ac": 2,
		"ev": 12,
		"speed": 1.3,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 4,
		"resist_cold": 0.5,
		"resist_fire": -0.5
	}

	MONSTERS["quasit"] = {
		"name": "Quasit",
		"tier": 4,
		"hp": 18.0,
		"damage": 6.0,
		"ac": 1,
		"ev": 15,
		"speed": 1.5,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	# Advanced Humanoids
	MONSTERS["centaur"] = {
		"name": "Centaur",
		"tier": 4,
		"hp": 28.0,
		"damage": 10.0,
		"ac": 3,
		"ev": 7,
		"speed": 1.3,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 5
	}

	MONSTERS["minotaur"] = {
		"name": "Minotaur",
		"tier": 5,
		"hp": 40.0,
		"damage": 15.0,
		"ac": 5,
		"ev": 5,
		"speed": 1.0,
		"ai_type": Monster.AIType.BERSERKER,
		"attack_range": 1
	}

	MONSTERS["naga"] = {
		"name": "Naga",
		"tier": 5,
		"hp": 35.0,
		"damage": 12.0,
		"ac": 6,
		"ev": 8,
		"speed": 0.9,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 0.5
	}

	# Young Dragons
	MONSTERS["fire_drake"] = {
		"name": "Fire Drake",
		"tier": 5,
		"hp": 45.0,
		"damage": 14.0,
		"ac": 7,
		"ev": 6,
		"speed": 1.1,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 3,
		"resist_fire": 0.8
	}

	MONSTERS["swamp_dragon"] = {
		"name": "Swamp Dragon",
		"tier": 5,
		"hp": 42.0,
		"damage": 13.0,
		"ac": 8,
		"ev": 5,
		"speed": 1.0,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 0.8
	}

	# Constructs
	MONSTERS["golem"] = {
		"name": "Golem",
		"tier": 5,
		"hp": 50.0,
		"damage": 12.0,
		"ac": 10,
		"ev": 3,
		"speed": 0.7,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_magic": 0.5
	}

	MONSTERS["war_gargoyle"] = {
		"name": "War Gargoyle",
		"tier": 6,
		"hp": 55.0,
		"damage": 16.0,
		"ac": 12,
		"ev": 4,
		"speed": 0.8,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_electric": 0.5
	}

	# ========================================
	# FALLOUT ROBOTS - MID GAME (Tier 4-6)
	# ========================================

	MONSTERS["assaultron"] = {
		"name": "Assaultron",
		"tier": 4,
		"hp": 50.0,
		"damage": 10.0,
		"ac": 6,
		"ev": 8,
		"speed": 1.4,
		"ai_type": Monster.AIType.BERSERKER,
		"attack_range": 1,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_fire": 0.25,
		"resist_electric": -0.5
	}

	MONSTERS["mr_gusty"] = {
		"name": "Mr. Gusty",
		"tier": 4,
		"hp": 40.0,
		"damage": 12.0,
		"ac": 5,
		"ev": 7,
		"speed": 1.0,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 4,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_fire": 0.8,  # Military flamer
		"resist_electric": -0.5
	}

	MONSTERS["robobrain"] = {
		"name": "Robobrain",
		"tier": 5,
		"hp": 45.0,
		"damage": 14.0,
		"ac": 8,
		"ev": 6,
		"speed": 0.9,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 5,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_magic": 0.4,
		"resist_electric": -0.5
	}

	MONSTERS["sentry_bot"] = {
		"name": "Sentry Bot",
		"tier": 5,
		"hp": 80.0,
		"damage": 18.0,
		"ac": 12,
		"ev": 3,
		"speed": 0.6,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 6,
		"is_robot": true,
		"robot_armor": "HEAVY",
		"self_destruct": true,
		"resist_poison": 1.0,
		"resist_fire": 0.8,
		"resist_electric": -0.5
	}

	MONSTERS["experimental_bot"] = {
		"name": "Experimental Bot",
		"tier": 6,
		"hp": 60.0,
		"damage": 16.0,
		"ac": 10,
		"ev": 5,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 5,
		"is_robot": true,
		"robot_armor": "HEAVY",
		"self_destruct": true,
		"resist_poison": 1.0,
		"resist_fire": 0.5,
		"resist_electric": -0.3
	}

	# ========================================
	# LATE GAME MONSTERS (Tier 7-9, Depth 11+)
	# ========================================

	# Adult Dragons
	MONSTERS["fire_dragon"] = {
		"name": "Fire Dragon",
		"tier": 7,
		"hp": 100.0,
		"damage": 25.0,
		"ac": 10,
		"ev": 5,
		"speed": 1.0,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 5,
		"resist_fire": 1.0,
		"resist_cold": -0.5
	}

	MONSTERS["ice_dragon"] = {
		"name": "Ice Dragon",
		"tier": 7,
		"hp": 95.0,
		"damage": 24.0,
		"ac": 10,
		"ev": 5,
		"speed": 1.0,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 5,
		"resist_cold": 1.0,
		"resist_fire": -0.5
	}

	MONSTERS["storm_dragon"] = {
		"name": "Storm Dragon",
		"tier": 8,
		"hp": 110.0,
		"damage": 28.0,
		"ac": 12,
		"ev": 6,
		"speed": 1.1,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 6,
		"resist_electric": 1.0
	}

	MONSTERS["shadow_dragon"] = {
		"name": "Shadow Dragon",
		"tier": 8,
		"hp": 105.0,
		"damage": 26.0,
		"ac": 11,
		"ev": 7,
		"speed": 1.2,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 0.8,
		"resist_magic": 0.6
	}

	MONSTERS["golden_dragon"] = {
		"name": "Golden Dragon",
		"tier": 9,
		"hp": 150.0,
		"damage": 35.0,
		"ac": 15,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 6,
		"resist_fire": 0.8,
		"resist_cold": 0.8,
		"resist_poison": 0.8
	}

	# Greater Demons
	MONSTERS["hellion"] = {
		"name": "Hellion",
		"tier": 7,
		"hp": 85.0,
		"damage": 22.0,
		"ac": 8,
		"ev": 9,
		"speed": 1.2,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 5,
		"resist_fire": 1.0
	}

	MONSTERS["ice_fiend"] = {
		"name": "Ice Fiend",
		"tier": 8,
		"hp": 100.0,
		"damage": 30.0,
		"ac": 12,
		"ev": 7,
		"speed": 1.1,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 5,
		"resist_cold": 1.0
	}

	MONSTERS["brimstone_fiend"] = {
		"name": "Brimstone Fiend",
		"tier": 9,
		"hp": 120.0,
		"damage": 35.0,
		"ac": 15,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 6,
		"resist_fire": 1.0,
		"resist_magic": 0.7
	}

	# Giants
	MONSTERS["ettin"] = {
		"name": "Ettin",
		"tier": 7,
		"hp": 90.0,
		"damage": 24.0,
		"ac": 7,
		"ev": 4,
		"speed": 0.9,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1
	}

	MONSTERS["cyclops"] = {
		"name": "Cyclops",
		"tier": 7,
		"hp": 85.0,
		"damage": 20.0,
		"ac": 8,
		"ev": 3,
		"speed": 0.8,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 6
	}

	MONSTERS["titan"] = {
		"name": "Titan",
		"tier": 9,
		"hp": 130.0,
		"damage": 40.0,
		"ac": 14,
		"ev": 6,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 8,
		"resist_electric": 1.0
	}

	# Powerful Undead
	MONSTERS["vampire"] = {
		"name": "Vampire",
		"tier": 7,
		"hp": 75.0,
		"damage": 20.0,
		"ac": 9,
		"ev": 10,
		"speed": 1.3,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_cold": 0.5
	}

	MONSTERS["lich"] = {
		"name": "Lich",
		"tier": 8,
		"hp": 80.0,
		"damage": 25.0,
		"ac": 12,
		"ev": 10,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 7,
		"resist_poison": 1.0,
		"resist_cold": 0.8,
		"resist_magic": 0.8
	}

	MONSTERS["ancient_lich"] = {
		"name": "Ancient Lich",
		"tier": 9,
		"hp": 120.0,
		"damage": 35.0,
		"ac": 15,
		"ev": 12,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 8,
		"resist_poison": 1.0,
		"resist_cold": 1.0,
		"resist_magic": 0.9
	}

	# ========================================
	# FALLOUT ROBOTS - LATE GAME (Tier 7-9)
	# ========================================

	MONSTERS["annihilator_sentry_bot"] = {
		"name": "Annihilator Sentry Bot",
		"tier": 7,
		"hp": 120.0,
		"damage": 25.0,
		"ac": 15,
		"ev": 3,
		"speed": 0.6,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 7,
		"is_robot": true,
		"robot_armor": "HEAVY",
		"self_destruct": true,
		"resist_poison": 1.0,
		"resist_fire": 0.9,
		"resist_electric": -0.5
	}

	MONSTERS["quantum_assaultron"] = {
		"name": "Quantum Assaultron",
		"tier": 8,
		"hp": 90.0,
		"damage": 30.0,
		"ac": 10,
		"ev": 10,
		"speed": 1.6,
		"ai_type": Monster.AIType.BERSERKER,
		"attack_range": 1,
		"is_robot": true,
		"robot_armor": "MEDIUM",
		"resist_poison": 1.0,
		"resist_fire": 0.3,
		"resist_electric": -0.5
	}

	MONSTERS["overlord_bot"] = {
		"name": "Overlord Bot",
		"tier": 9,
		"hp": 200.0,
		"damage": 45.0,
		"ac": 20,
		"ev": 5,
		"speed": 0.7,
		"ai_type": Monster.AIType.RANGED,
		"attack_range": 8,
		"is_robot": true,
		"robot_armor": "HEAVY",
		"self_destruct": true,
		"resist_poison": 1.0,
		"resist_fire": 0.9,
		"resist_electric": -0.3,
		"resist_magic": 0.5
	}

	# ========================================
	# SPECIAL ENEMIES
	# ========================================

	# Slimes & Jellies
	MONSTERS["jelly"] = {
		"name": "Jelly",
		"tier": 3,
		"hp": 25.0,
		"damage": 8.0,
		"ac": 0,
		"ev": 2,
		"speed": 0.6,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0
	}

	MONSTERS["acid_blob"] = {
		"name": "Acid Blob",
		"tier": 6,
		"hp": 60.0,
		"damage": 18.0,
		"ac": 2,
		"ev": 1,
		"speed": 0.5,
		"ai_type": Monster.AIType.MELEE,
		"attack_range": 1,
		"resist_poison": 1.0,
		"resist_fire": 0.5
	}

	# Eyes & Floating
	MONSTERS["giant_eyeball"] = {
		"name": "Giant Eyeball",
		"tier": 5,
		"hp": 35.0,
		"damage": 10.0,
		"ac": 3,
		"ev": 8,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 6
	}

	MONSTERS["great_orb_of_eyes"] = {
		"name": "Great Orb of Eyes",
		"tier": 8,
		"hp": 80.0,
		"damage": 20.0,
		"ac": 10,
		"ev": 10,
		"speed": 1.0,
		"ai_type": Monster.AIType.SPELLCASTER,
		"attack_range": 8,
		"resist_magic": 0.6
	}

## Get monster stats by ID
static func get_monster_stats(monster_id: String) -> Dictionary:
	if MONSTERS.is_empty():
		init_monsters()

	return MONSTERS.get(monster_id, {})

## Get random monster for tier
static func get_random_monster_for_tier(tier: int) -> String:
	if MONSTERS.is_empty():
		init_monsters()

	# Collect all monsters matching tier range
	var candidates = []

	# Tier ranges: 1-3 (early), 4-6 (mid), 7-9 (late)
	var min_tier = 1
	var max_tier = 3
	if tier >= 4 and tier <= 6:
		min_tier = 4
		max_tier = 6
	elif tier >= 7:
		min_tier = 7
		max_tier = 9

	for monster_id in MONSTERS.keys():
		var monster_tier = MONSTERS[monster_id].get("tier", 1)
		if monster_tier >= min_tier and monster_tier <= max_tier:
			candidates.append(monster_id)

	if candidates.is_empty():
		return "goblin"  # Fallback

	return candidates[randi() % candidates.size()]

## Get all monster IDs for tier
static func get_monsters_for_tier(tier: int) -> Array:
	if MONSTERS.is_empty():
		init_monsters()

	var result = []
	for monster_id in MONSTERS.keys():
		var monster_tier = MONSTERS[monster_id].get("tier", 1)
		if monster_tier == tier:
			result.append(monster_id)

	return result

## Get monster count
static func get_monster_count() -> int:
	if MONSTERS.is_empty():
		init_monsters()

	return MONSTERS.size()

## Get robot count
static func get_robot_count() -> int:
	if MONSTERS.is_empty():
		init_monsters()

	var count = 0
	for monster_id in MONSTERS.keys():
		if MONSTERS[monster_id].get("is_robot", false):
			count += 1

	return count
