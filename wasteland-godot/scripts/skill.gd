extends Resource
class_name Skill

## DCSS skills that players can train
## Skills improve through use and XP investment
## This is a port from the Wasteland Crawl Minecraft mod

# Skill categories for organization
enum SkillCategory {
	COMBAT,
	RANGED,
	DEFENSE,
	MAGIC,
	UTILITY
}

# All DCSS skills
enum Type {
	# Combat skills
	FIGHTING,
	SHORT_BLADES,
	LONG_BLADES,
	AXES,
	MACES_FLAILS,
	POLEARMS,
	STAVES,
	UNARMED_COMBAT,

	# Ranged combat
	THROWING,
	BOWS,
	CROSSBOWS,
	GUNS,

	# Defense skills
	ARMOUR,
	DODGING,
	SHIELDS,
	STEALTH,

	# Magic schools
	SPELLCASTING,
	CONJURATIONS,
	HEXES,
	SUMMONINGS,
	NECROMANCY,
	TRANSLOCATIONS,
	TRANSMUTATIONS,
	FIRE_MAGIC,
	ICE_MAGIC,
	AIR_MAGIC,
	EARTH_MAGIC,
	POISON_MAGIC,

	# Utility skills
	INVOCATIONS,
	EVOCATIONS,
	SHAPESHIFTING
}

# Skill data structure
class SkillData:
	var display_name: String
	var category: SkillCategory
	var description: String

	func _init(p_display_name: String, p_category: SkillCategory, p_description: String):
		display_name = p_display_name
		category = p_category
		description = p_description

# Static skill definitions
static var skill_data = {
	# Combat skills
	Type.FIGHTING: SkillData.new("Fighting", SkillCategory.COMBAT, "General combat prowess and HP"),
	Type.SHORT_BLADES: SkillData.new("Short Blades", SkillCategory.COMBAT, "Daggers and short swords"),
	Type.LONG_BLADES: SkillData.new("Long Blades", SkillCategory.COMBAT, "Swords and scimitars"),
	Type.AXES: SkillData.new("Axes", SkillCategory.COMBAT, "Hand axes and battleaxes"),
	Type.MACES_FLAILS: SkillData.new("Maces & Flails", SkillCategory.COMBAT, "Clubs, maces, and flails"),
	Type.POLEARMS: SkillData.new("Polearms", SkillCategory.COMBAT, "Spears, tridents, and halberds"),
	Type.STAVES: SkillData.new("Staves", SkillCategory.COMBAT, "Quarterstaves and magical staves"),
	Type.UNARMED_COMBAT: SkillData.new("Unarmed Combat", SkillCategory.COMBAT, "Fighting with fists and claws"),

	# Ranged combat
	Type.THROWING: SkillData.new("Throwing", SkillCategory.RANGED, "Thrown weapons and explosives"),
	Type.BOWS: SkillData.new("Bows", SkillCategory.RANGED, "Bows and arrows"),
	Type.CROSSBOWS: SkillData.new("Crossbows", SkillCategory.RANGED, "Crossbows and bolts"),
	Type.GUNS: SkillData.new("Guns", SkillCategory.RANGED, "Firearms and energy weapons"),

	# Defense skills
	Type.ARMOUR: SkillData.new("Armour", SkillCategory.DEFENSE, "Wearing heavy armor effectively"),
	Type.DODGING: SkillData.new("Dodging", SkillCategory.DEFENSE, "Evading attacks"),
	Type.SHIELDS: SkillData.new("Shields", SkillCategory.DEFENSE, "Using shields to block"),
	Type.STEALTH: SkillData.new("Stealth", SkillCategory.DEFENSE, "Moving silently and unseen"),

	# Magic schools
	Type.SPELLCASTING: SkillData.new("Spellcasting", SkillCategory.MAGIC, "General magic power and MP"),
	Type.CONJURATIONS: SkillData.new("Conjurations", SkillCategory.MAGIC, "Offensive damage spells"),
	Type.HEXES: SkillData.new("Hexes", SkillCategory.MAGIC, "Debuffs and curses"),
	Type.SUMMONINGS: SkillData.new("Summonings", SkillCategory.MAGIC, "Summoning creatures"),
	Type.NECROMANCY: SkillData.new("Necromancy", SkillCategory.MAGIC, "Death magic and undead"),
	Type.TRANSLOCATIONS: SkillData.new("Translocations", SkillCategory.MAGIC, "Teleportation and spatial magic"),
	Type.TRANSMUTATIONS: SkillData.new("Transmutations", SkillCategory.MAGIC, "Shape-shifting and transformation"),
	Type.FIRE_MAGIC: SkillData.new("Fire Magic", SkillCategory.MAGIC, "Fire spells"),
	Type.ICE_MAGIC: SkillData.new("Ice Magic", SkillCategory.MAGIC, "Ice and cold spells"),
	Type.AIR_MAGIC: SkillData.new("Air Magic", SkillCategory.MAGIC, "Lightning and wind spells"),
	Type.EARTH_MAGIC: SkillData.new("Earth Magic", SkillCategory.MAGIC, "Stone and poison spells"),
	Type.POISON_MAGIC: SkillData.new("Poison Magic", SkillCategory.MAGIC, "Poison and venom spells"),

	# Utility skills
	Type.INVOCATIONS: SkillData.new("Invocations", SkillCategory.UTILITY, "Using god-given abilities"),
	Type.EVOCATIONS: SkillData.new("Evocations", SkillCategory.UTILITY, "Using magical items"),
	Type.SHAPESHIFTING: SkillData.new("Shapeshifting", SkillCategory.UTILITY, "Transforming into beasts")
}

# Category display names
static var category_names = {
	SkillCategory.COMBAT: "Combat",
	SkillCategory.RANGED: "Ranged Combat",
	SkillCategory.DEFENSE: "Defense",
	SkillCategory.MAGIC: "Magic",
	SkillCategory.UTILITY: "Utility"
}

## Get display name for a skill
static func get_display_name(skill: Type) -> String:
	if skill_data.has(skill):
		return skill_data[skill].display_name
	return "Unknown"

## Get category for a skill
static func get_category(skill: Type) -> SkillCategory:
	if skill_data.has(skill):
		return skill_data[skill].category
	return SkillCategory.COMBAT

## Get description for a skill
static func get_description(skill: Type) -> String:
	if skill_data.has(skill):
		return skill_data[skill].description
	return ""

## Get category display name
static func get_category_name(category: SkillCategory) -> String:
	if category_names.has(category):
		return category_names[category]
	return "Unknown"

## Get all skills in a specific category
static func get_skills_by_category(category: SkillCategory) -> Array:
	var skills = []
	for skill_type in skill_data.keys():
		if skill_data[skill_type].category == category:
			skills.append(skill_type)
	return skills

## Get XP required for a skill level (simplified DCSS formula)
## In DCSS, XP requirements increase exponentially
static func get_xp_for_level(level: int) -> float:
	if level <= 0:
		return 0.0
	# Simplified: 100 XP base * level
	# In actual DCSS this is more complex
	return 100.0 * level

## Get total XP required to reach a level from 0
static func get_total_xp_for_level(level: int) -> float:
	var total = 0.0
	for i in range(1, level + 1):
		total += get_xp_for_level(i)
	return total
