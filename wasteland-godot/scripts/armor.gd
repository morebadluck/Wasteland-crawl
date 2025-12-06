extends WastelandItem
class_name WastelandArmor

## Armor item with DCSS-style AC and EV stats
## Ported from Minecraft WastelandArmor.java

enum ArmorType {
	# BODY ARMOR
	ROBE,
	LEATHER_ARMOR,
	RING_MAIL,
	SCALE_MAIL,
	CHAIN_MAIL,
	PLATE_ARMOR,
	CRYSTAL_PLATE,

	# HELMETS
	HAT,
	HELMET,

	# GLOVES
	GLOVES,

	# BOOTS
	BOOTS,

	# CLOAKS
	CLOAK,

	# SHIELDS
	BUCKLER,
	KITE_SHIELD,
	TOWER_SHIELD
}

var armor_type: ArmorType

func _init(type: ArmorType = ArmorType.ROBE, rar: ItemRarity = ItemRarity.COMMON, ench: int = 0):
	armor_type = type
	rarity = rar
	enchantment_level = ench
	item_name = get_armor_name(type)
	identified = true

static func get_armor_name(type: ArmorType) -> String:
	"""Get display name for armor type"""
	match type:
		ArmorType.ROBE: return "Robe"
		ArmorType.LEATHER_ARMOR: return "Leather Armor"
		ArmorType.RING_MAIL: return "Ring Mail"
		ArmorType.SCALE_MAIL: return "Scale Mail"
		ArmorType.CHAIN_MAIL: return "Chain Mail"
		ArmorType.PLATE_ARMOR: return "Plate Armor"
		ArmorType.CRYSTAL_PLATE: return "Crystal Plate Armor"
		ArmorType.HAT: return "Hat"
		ArmorType.HELMET: return "Helmet"
		ArmorType.GLOVES: return "Gloves"
		ArmorType.BOOTS: return "Boots"
		ArmorType.CLOAK: return "Cloak"
		ArmorType.BUCKLER: return "Buckler"
		ArmorType.KITE_SHIELD: return "Kite Shield"
		ArmorType.TOWER_SHIELD: return "Tower Shield"
		_: return "Unknown Armor"

static func get_base_ac(type: ArmorType) -> int:
	"""Get base AC for armor type"""
	match type:
		ArmorType.ROBE: return 2
		ArmorType.LEATHER_ARMOR: return 3
		ArmorType.RING_MAIL: return 5
		ArmorType.SCALE_MAIL: return 6
		ArmorType.CHAIN_MAIL: return 8
		ArmorType.PLATE_ARMOR: return 10
		ArmorType.CRYSTAL_PLATE: return 14
		ArmorType.HAT: return 0
		ArmorType.HELMET: return 1
		ArmorType.GLOVES: return 1
		ArmorType.BOOTS: return 1
		ArmorType.CLOAK: return 1
		ArmorType.BUCKLER: return 3
		ArmorType.KITE_SHIELD: return 8
		ArmorType.TOWER_SHIELD: return 13
		_: return 0

static func get_base_ev_penalty(type: ArmorType) -> int:
	"""Get base EV penalty for armor type (negative values)"""
	match type:
		ArmorType.ROBE: return 0
		ArmorType.LEATHER_ARMOR: return -1
		ArmorType.RING_MAIL: return -2
		ArmorType.SCALE_MAIL: return -3
		ArmorType.CHAIN_MAIL: return -4
		ArmorType.PLATE_ARMOR: return -5
		ArmorType.CRYSTAL_PLATE: return -7
		ArmorType.HAT, ArmorType.HELMET, ArmorType.GLOVES, ArmorType.BOOTS, ArmorType.CLOAK: return 0
		ArmorType.BUCKLER: return 0
		ArmorType.KITE_SHIELD: return -2
		ArmorType.TOWER_SHIELD: return -5
		_: return 0

static func get_encumbrance(type: ArmorType) -> int:
	"""Get encumbrance rating (0-5, higher = heavier)"""
	match type:
		ArmorType.ROBE, ArmorType.HAT, ArmorType.CLOAK: return 0
		ArmorType.LEATHER_ARMOR, ArmorType.GLOVES, ArmorType.BOOTS, ArmorType.HELMET, ArmorType.BUCKLER: return 1
		ArmorType.RING_MAIL, ArmorType.SCALE_MAIL: return 2
		ArmorType.CHAIN_MAIL, ArmorType.KITE_SHIELD: return 3
		ArmorType.PLATE_ARMOR: return 4
		ArmorType.CRYSTAL_PLATE, ArmorType.TOWER_SHIELD: return 5
		_: return 0

static func get_slot(type: ArmorType) -> Player.EquipSlot:
	"""Get equipment slot for this armor type"""
	match type:
		ArmorType.ROBE, ArmorType.LEATHER_ARMOR, ArmorType.RING_MAIL, \
		ArmorType.SCALE_MAIL, ArmorType.CHAIN_MAIL, ArmorType.PLATE_ARMOR, \
		ArmorType.CRYSTAL_PLATE:
			return Player.EquipSlot.BODY
		ArmorType.HAT, ArmorType.HELMET:
			return Player.EquipSlot.HELMET
		ArmorType.GLOVES:
			return Player.EquipSlot.GLOVES
		ArmorType.BOOTS:
			return Player.EquipSlot.BOOTS
		ArmorType.CLOAK:
			return Player.EquipSlot.CLOAK
		ArmorType.BUCKLER, ArmorType.KITE_SHIELD, ArmorType.TOWER_SHIELD:
			return Player.EquipSlot.OFFHAND
		_:
			return Player.EquipSlot.BODY

static func is_shield(type: ArmorType) -> bool:
	"""Check if this armor is a shield"""
	return type in [ArmorType.BUCKLER, ArmorType.KITE_SHIELD, ArmorType.TOWER_SHIELD]

static func is_body_armor(type: ArmorType) -> bool:
	"""Check if this is body armor"""
	return type in [ArmorType.ROBE, ArmorType.LEATHER_ARMOR, ArmorType.RING_MAIL,
					ArmorType.SCALE_MAIL, ArmorType.CHAIN_MAIL, ArmorType.PLATE_ARMOR,
					ArmorType.CRYSTAL_PLATE]

func calculate_ac(character) -> int:
	"""Calculate total AC with character stats/skills"""
	var ac = get_base_ac(armor_type)

	# Enchantment bonus
	ac += enchantment_level

	# Artifact bonus
	if has_artifact_property(ArtifactProperty.ARMOR_CLASS):
		ac += get_artifact_property(ArtifactProperty.ARMOR_CLASS)

	# TODO: Add character armor skill bonuses

	return max(1, ac)

func calculate_ev_modifier(character) -> int:
	"""Calculate EV modifier (usually negative for heavy armor)"""
	var ev_mod = get_base_ev_penalty(armor_type)

	# Enchantment reduces EV penalty
	if enchantment_level > 0:
		ev_mod = min(0, ev_mod + (enchantment_level / 2))

	# Artifact bonus
	if has_artifact_property(ArtifactProperty.EVASION):
		ev_mod += get_artifact_property(ArtifactProperty.EVASION)

	# TODO: Add character armor skill bonuses

	return ev_mod

func get_tooltip() -> Array[String]:
	var tooltip = super.get_tooltip()

	# AC
	var display_ac = get_base_ac(armor_type) + enchantment_level
	tooltip.append("AC: +%d" % display_ac)

	# EV penalty
	var ev_penalty = get_base_ev_penalty(armor_type)
	if ev_penalty != 0:
		var display_ev = ev_penalty
		if enchantment_level > 0:
			display_ev = min(0, ev_penalty + (enchantment_level / 2))

		if display_ev < 0:
			tooltip.append("EV: %d" % display_ev)
		elif display_ev > 0:
			tooltip.append("EV: +%d" % display_ev)

	# Encumbrance
	if is_body_armor(armor_type) and get_encumbrance(armor_type) > 0:
		tooltip.append("Encumbrance: %d" % get_encumbrance(armor_type))

	# Slot
	var slot_names = ["Weapon", "Offhand", "Body", "Helmet", "Cloak", "Gloves", "Boots", "Amulet", "Ring", "Ring"]
	tooltip.append("Slot: %s" % slot_names[get_slot(armor_type)])

	return tooltip
