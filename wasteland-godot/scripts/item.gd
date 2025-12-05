extends Resource
class_name WastelandItem

## Base item class with rarity, enchantment, and artifact properties
## Ported from Minecraft WastelandItem.java

enum ItemRarity {
	COMMON,      # White - Standard items
	UNCOMMON,    # Green - Better than average
	RARE,        # Blue - Significant bonuses
	EPIC,        # Purple - Powerful effects
	LEGENDARY,   # Orange - Unique artifacts
	ARTIFACT     # Gold - Named unique items with special powers
}

enum ArtifactProperty {
	# Combat
	SLAYING,         # Bonus to damage and accuracy
	ACCURACY,        # Bonus to hit chance
	ATTACK_SPEED,    # Reduces attack delay

	# Defense
	ARMOR_CLASS,     # Bonus AC
	EVASION,         # Bonus EV

	# Stats
	STRENGTH,        # Bonus STR
	DEXTERITY,       # Bonus DEX
	INTELLIGENCE,    # Bonus INT

	# Resistances
	RESIST_FIRE,     # Fire resistance (0-3)
	RESIST_COLD,     # Cold resistance (0-3)
	RESIST_POISON,   # Poison resistance (0-3)
	RESIST_ELEC,     # Electricity resistance (0-3)

	# Special
	SEE_INVISIBLE,   # Can see invisible creatures
	REGENERATION,    # HP regeneration
	MAGIC_RESISTANCE # Resistance to magic (0-3)
}

# Base properties
var item_name: String = ""
var rarity: ItemRarity = ItemRarity.COMMON
var enchantment_level: int = 0
var identified: bool = false
var is_artifact: bool = false
var artifact_name: String = ""
var artifact_properties: Dictionary = {}  # ArtifactProperty -> int

func _init():
	pass

func get_display_name() -> String:
	"""Get the full display name with enchantment"""
	var name = item_name

	# Artifact name overrides base name
	if is_artifact and artifact_name != "":
		name = artifact_name

	# Show enchantment if identified
	if identified and enchantment_level != 0:
		if enchantment_level > 0:
			name = "%s +%d" % [name, enchantment_level]
		else:
			name = "%s %d" % [name, enchantment_level]
	elif not identified:
		name = "unidentified " + name.to_lower()

	return name

func get_rarity_color() -> Color:
	"""Get color for this item's rarity"""
	match rarity:
		ItemRarity.COMMON:
			return Color.WHITE
		ItemRarity.UNCOMMON:
			return Color(0.0, 1.0, 0.0)  # Green
		ItemRarity.RARE:
			return Color(0.0, 0.5, 1.0)  # Blue
		ItemRarity.EPIC:
			return Color(0.7, 0.0, 1.0)  # Purple
		ItemRarity.LEGENDARY:
			return Color(1.0, 0.5, 0.0)  # Orange
		ItemRarity.ARTIFACT:
			return Color(1.0, 0.8, 0.0)  # Gold
		_:
			return Color.WHITE

func identify():
	"""Identify this item"""
	identified = true

func is_identified() -> bool:
	return identified

func has_artifact_property(property: ArtifactProperty) -> bool:
	"""Check if this item has an artifact property"""
	return artifact_properties.has(property)

func get_artifact_property(property: ArtifactProperty) -> int:
	"""Get the value of an artifact property"""
	return artifact_properties.get(property, 0)

func set_artifact_property(property: ArtifactProperty, value: int):
	"""Set an artifact property"""
	artifact_properties[property] = value

func get_tooltip() -> Array[String]:
	"""Get tooltip lines for this item"""
	var tooltip: Array[String] = []

	# Name with enchantment
	tooltip.append(get_display_name())

	# Rarity
	if identified:
		var rarity_names = ["Common", "Uncommon", "Rare", "Epic", "Legendary", "Artifact"]
		tooltip.append("Rarity: " + rarity_names[rarity])

	# Artifact properties
	if identified and is_artifact:
		for prop in artifact_properties:
			var value = artifact_properties[prop]
			var prop_name = ArtifactProperty.keys()[prop]
			tooltip.append("%s: %+d" % [prop_name, value])

	return tooltip
