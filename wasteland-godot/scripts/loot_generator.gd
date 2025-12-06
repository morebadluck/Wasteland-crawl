extends Node
class_name LootGenerator

## Loot generation system
## Generates random items based on monster tier and level

## Generate loot for a monster death
static func generate_monster_loot(monster_tier: int, area_level: int, is_robot: bool) -> WastelandItem:
	"""Generate a random item for monster loot"""

	# Determine rarity based on monster tier and level
	var rarity = determine_rarity(monster_tier, area_level)

	# 50/50 chance of weapon or armor
	if randf() < 0.5:
		return generate_weapon(rarity, area_level)
	else:
		return generate_armor(rarity, area_level)

static func determine_rarity(monster_tier: int, area_level: int) -> WastelandItem.ItemRarity:
	"""Determine item rarity based on monster and area difficulty"""

	# Base rarity chances
	var rare_chance = 0.05  # 5% base
	var epic_chance = 0.01  # 1% base
	var legendary_chance = 0.001  # 0.1% base

	# Increase chances with monster tier
	rare_chance += monster_tier * 0.02  # +2% per tier
	epic_chance += monster_tier * 0.005  # +0.5% per tier
	legendary_chance += monster_tier * 0.001  # +0.1% per tier

	# Increase chances with area level
	rare_chance += area_level * 0.01  # +1% per level
	epic_chance += area_level * 0.002  # +0.2% per level
	legendary_chance += area_level * 0.0005  # +0.05% per level

	# Roll for rarity
	var roll = randf()

	if roll < legendary_chance:
		return WastelandItem.ItemRarity.LEGENDARY
	elif roll < epic_chance:
		return WastelandItem.ItemRarity.EPIC
	elif roll < rare_chance:
		return WastelandItem.ItemRarity.RARE
	elif roll < 0.25:  # 25% uncommon
		return WastelandItem.ItemRarity.UNCOMMON
	else:
		return WastelandItem.ItemRarity.COMMON

static func generate_weapon(rarity: WastelandItem.ItemRarity, area_level: int) -> WastelandWeapon:
	"""Generate a random weapon"""

	# Weapon types by tier
	var tier1_weapons = [
		WastelandWeapon.WeaponType.SHIV,
		WastelandWeapon.WeaponType.COMBAT_KNIFE
	]

	var tier2_weapons = [
		WastelandWeapon.WeaponType.MACHETE,
		WastelandWeapon.WeaponType.PIPE
	]

	var tier3_weapons = [
		WastelandWeapon.WeaponType.BASEBALL_BAT,
		WastelandWeapon.WeaponType.CROWBAR
	]

	var tier4_weapons = [
		WastelandWeapon.WeaponType.SLEDGEHAMMER,
		WastelandWeapon.WeaponType.FIRE_AXE
	]

	# Choose weapon tier based on area level
	var weapon_type: WastelandWeapon.WeaponType

	if area_level <= 3:
		weapon_type = tier1_weapons.pick_random()
	elif area_level <= 6:
		weapon_type = tier2_weapons.pick_random()
	elif area_level <= 9:
		weapon_type = tier3_weapons.pick_random()
	else:
		weapon_type = tier4_weapons.pick_random()

	# Item level varies with area level
	var item_level = max(0, area_level + randi_range(-2, 2))

	return WastelandWeapon.new(weapon_type, rarity, item_level)

static func generate_armor(rarity: WastelandItem.ItemRarity, area_level: int) -> WastelandArmor:
	"""Generate a random armor piece"""

	# Armor types by tier
	var tier1_armor = [
		WastelandArmor.ArmorType.ROBE,
		WastelandArmor.ArmorType.LEATHER_ARMOR
	]

	var tier2_armor = [
		WastelandArmor.ArmorType.HELMET,
		WastelandArmor.ArmorType.GLOVES,
		WastelandArmor.ArmorType.BOOTS
	]

	var tier3_armor = [
		WastelandArmor.ArmorType.CHAIN_MAIL,
		WastelandArmor.ArmorType.SCALE_MAIL
	]

	var tier4_armor = [
		WastelandArmor.ArmorType.PLATE_ARMOR,
		WastelandArmor.ArmorType.CRYSTAL_PLATE
	]

	# Choose armor tier based on area level
	var armor_type: WastelandArmor.ArmorType

	if area_level <= 3:
		armor_type = tier1_armor.pick_random()
	elif area_level <= 6:
		armor_type = tier2_armor.pick_random()
	elif area_level <= 9:
		armor_type = tier3_armor.pick_random()
	else:
		armor_type = tier4_armor.pick_random()

	# Item level varies with area level
	var item_level = max(0, area_level + randi_range(-2, 2))

	return WastelandArmor.new(armor_type, rarity, item_level)
