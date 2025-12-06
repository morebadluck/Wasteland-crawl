extends WastelandItem
class_name WastelandWeapon

## Weapon item with DCSS-style damage and speed stats
## Ported from Minecraft WastelandWeapon.java

enum WeaponType {
	# SHORT BLADES
	SHIV,
	COMBAT_KNIFE,
	MACHETE,

	# LONG BLADES
	RUSTY_SWORD,
	SCRAP_BLADE,
	KATANA,
	RIPPER,

	# AXES
	HATCHET,
	FIRE_AXE,
	SUPER_SLEDGE,

	# MACES & FLAILS
	PIPE,
	CROWBAR,
	BASEBALL_BAT,
	SLEDGEHAMMER,
	PNEUMATIC_HAMMER,

	# POLEARMS
	SPEAR,
	PITCHFORK,
	HALBERD,

	# STAVES
	WOODEN_STAFF,
	REBAR_STAFF,

	# BOWS
	MAKESHIFT_BOW,
	COMPOUND_BOW,

	# CROSSBOWS
	HAND_CROSSBOW,
	CROSSBOW,

	# GUNS - Pistols
	PIPE_PISTOL,
	PISTOL_22,
	PISTOL_9MM,
	PISTOL_45,
	MAGNUM_44,
	DESERT_EAGLE,

	# GUNS - Rifles
	HUNTING_RIFLE,
	VARMINT_RIFLE,
	ASSAULT_RIFLE,
	BATTLE_RIFLE,
	SNIPER_RIFLE,

	# GUNS - Shotguns
	SAWED_OFF_SHOTGUN,
	COMBAT_SHOTGUN,

	# GUNS - Energy
	LASER_PISTOL,
	LASER_RIFLE,
	PLASMA_RIFLE,
	GAUSS_RIFLE,

	# THROWING
	THROWING_KNIFE,
	GRENADE,
	MOLOTOV
}

# Weapon properties
var weapon_type: WeaponType
var bonus_accuracy: int = 0
var bonus_slaying: int = 0

func _init(type: WeaponType = WeaponType.SHIV, rar: ItemRarity = ItemRarity.COMMON, ench: int = 0):
	weapon_type = type
	rarity = rar
	enchantment_level = ench
	item_name = get_weapon_name(type)
	identified = true  # Weapons are identified by default

static func get_weapon_name(type: WeaponType) -> String:
	"""Get display name for weapon type"""
	match type:
		WeaponType.SHIV: return "Shiv"
		WeaponType.COMBAT_KNIFE: return "Combat Knife"
		WeaponType.MACHETE: return "Machete"
		WeaponType.RUSTY_SWORD: return "Rusty Sword"
		WeaponType.SCRAP_BLADE: return "Scrap Blade"
		WeaponType.KATANA: return "Katana"
		WeaponType.RIPPER: return "Ripper"
		WeaponType.HATCHET: return "Hatchet"
		WeaponType.FIRE_AXE: return "Fire Axe"
		WeaponType.SUPER_SLEDGE: return "Super Sledge"
		WeaponType.PIPE: return "Lead Pipe"
		WeaponType.CROWBAR: return "Crowbar"
		WeaponType.BASEBALL_BAT: return "Baseball Bat"
		WeaponType.SLEDGEHAMMER: return "Sledgehammer"
		WeaponType.PNEUMATIC_HAMMER: return "Pneumatic Hammer"
		WeaponType.SPEAR: return "Spear"
		WeaponType.PITCHFORK: return "Pitchfork"
		WeaponType.HALBERD: return "Halberd"
		WeaponType.WOODEN_STAFF: return "Wooden Staff"
		WeaponType.REBAR_STAFF: return "Rebar Staff"
		WeaponType.MAKESHIFT_BOW: return "Makeshift Bow"
		WeaponType.COMPOUND_BOW: return "Compound Bow"
		WeaponType.HAND_CROSSBOW: return "Hand Crossbow"
		WeaponType.CROSSBOW: return "Crossbow"
		WeaponType.PIPE_PISTOL: return "Pipe Pistol"
		WeaponType.PISTOL_22: return "22 Pistol"
		WeaponType.PISTOL_9MM: return "9mm Pistol"
		WeaponType.PISTOL_45: return "45 Pistol"
		WeaponType.MAGNUM_44: return "44 Magnum"
		WeaponType.DESERT_EAGLE: return "Desert Eagle"
		WeaponType.HUNTING_RIFLE: return "Hunting Rifle"
		WeaponType.VARMINT_RIFLE: return "Varmint Rifle"
		WeaponType.ASSAULT_RIFLE: return "Assault Rifle"
		WeaponType.BATTLE_RIFLE: return "Battle Rifle"
		WeaponType.SNIPER_RIFLE: return "Sniper Rifle"
		WeaponType.SAWED_OFF_SHOTGUN: return "Sawed-Off Shotgun"
		WeaponType.COMBAT_SHOTGUN: return "Combat Shotgun"
		WeaponType.LASER_PISTOL: return "Laser Pistol"
		WeaponType.LASER_RIFLE: return "Laser Rifle"
		WeaponType.PLASMA_RIFLE: return "Plasma Rifle"
		WeaponType.GAUSS_RIFLE: return "Gauss Rifle"
		WeaponType.THROWING_KNIFE: return "Throwing Knife"
		WeaponType.GRENADE: return "Grenade"
		WeaponType.MOLOTOV: return "Molotov Cocktail"
		_: return "Unknown Weapon"

static func get_base_damage(type: WeaponType) -> int:
	"""Get base damage for weapon type"""
	match type:
		WeaponType.SHIV: return 3
		WeaponType.COMBAT_KNIFE: return 5
		WeaponType.MACHETE: return 7
		WeaponType.RUSTY_SWORD: return 7
		WeaponType.SCRAP_BLADE: return 8
		WeaponType.KATANA: return 10
		WeaponType.RIPPER: return 13
		WeaponType.HATCHET: return 7
		WeaponType.FIRE_AXE: return 11
		WeaponType.SUPER_SLEDGE: return 18
		WeaponType.PIPE: return 5
		WeaponType.CROWBAR: return 6
		WeaponType.BASEBALL_BAT: return 7
		WeaponType.SLEDGEHAMMER: return 15
		WeaponType.PNEUMATIC_HAMMER: return 17
		WeaponType.SPEAR: return 6
		WeaponType.PITCHFORK: return 8
		WeaponType.HALBERD: return 13
		WeaponType.WOODEN_STAFF: return 5
		WeaponType.REBAR_STAFF: return 8
		WeaponType.MAKESHIFT_BOW: return 9
		WeaponType.COMPOUND_BOW: return 15
		WeaponType.HAND_CROSSBOW: return 12
		WeaponType.CROSSBOW: return 18
		WeaponType.PIPE_PISTOL: return 8
		WeaponType.PISTOL_22: return 10
		WeaponType.PISTOL_9MM: return 12
		WeaponType.PISTOL_45: return 15
		WeaponType.MAGNUM_44: return 18
		WeaponType.DESERT_EAGLE: return 20
		WeaponType.HUNTING_RIFLE: return 16
		WeaponType.VARMINT_RIFLE: return 14
		WeaponType.ASSAULT_RIFLE: return 20
		WeaponType.BATTLE_RIFLE: return 24
		WeaponType.SNIPER_RIFLE: return 28
		WeaponType.SAWED_OFF_SHOTGUN: return 22
		WeaponType.COMBAT_SHOTGUN: return 26
		WeaponType.LASER_PISTOL: return 18
		WeaponType.LASER_RIFLE: return 26
		WeaponType.PLASMA_RIFLE: return 32
		WeaponType.GAUSS_RIFLE: return 35
		WeaponType.THROWING_KNIFE: return 3
		WeaponType.GRENADE: return 25
		WeaponType.MOLOTOV: return 15
		_: return 1

static func get_attack_delay(type: WeaponType) -> int:
	"""Get attack delay for weapon type (lower = faster)"""
	match type:
		WeaponType.SHIV, WeaponType.COMBAT_KNIFE: return 10
		WeaponType.MACHETE: return 12
		WeaponType.RUSTY_SWORD, WeaponType.SCRAP_BLADE: return 14
		WeaponType.KATANA: return 12
		WeaponType.RIPPER: return 16
		WeaponType.HATCHET: return 13
		WeaponType.FIRE_AXE: return 15
		WeaponType.SUPER_SLEDGE: return 18
		WeaponType.PIPE, WeaponType.CROWBAR, WeaponType.BASEBALL_BAT: return 13
		WeaponType.SLEDGEHAMMER: return 17
		WeaponType.PNEUMATIC_HAMMER: return 18
		WeaponType.SPEAR, WeaponType.PITCHFORK: return 13
		WeaponType.HALBERD: return 15
		WeaponType.WOODEN_STAFF: return 13
		WeaponType.REBAR_STAFF: return 14
		WeaponType.MAKESHIFT_BOW: return 13
		WeaponType.COMPOUND_BOW: return 15
		WeaponType.HAND_CROSSBOW: return 15
		WeaponType.CROSSBOW: return 19
		WeaponType.PIPE_PISTOL, WeaponType.PISTOL_22: return 12
		WeaponType.PISTOL_9MM, WeaponType.PISTOL_45: return 12
		WeaponType.MAGNUM_44, WeaponType.DESERT_EAGLE: return 14
		WeaponType.HUNTING_RIFLE, WeaponType.VARMINT_RIFLE: return 15
		WeaponType.ASSAULT_RIFLE, WeaponType.BATTLE_RIFLE: return 15
		WeaponType.SNIPER_RIFLE: return 18
		WeaponType.SAWED_OFF_SHOTGUN: return 14
		WeaponType.COMBAT_SHOTGUN: return 15
		WeaponType.LASER_PISTOL: return 12
		WeaponType.LASER_RIFLE: return 14
		WeaponType.PLASMA_RIFLE: return 16
		WeaponType.GAUSS_RIFLE: return 18
		WeaponType.THROWING_KNIFE: return 10
		WeaponType.GRENADE, WeaponType.MOLOTOV: return 15
		_: return 10

static func is_two_handed(type: WeaponType) -> bool:
	"""Check if weapon is two-handed"""
	match type:
		WeaponType.RIPPER, WeaponType.FIRE_AXE, WeaponType.SUPER_SLEDGE, \
		WeaponType.SLEDGEHAMMER, WeaponType.PNEUMATIC_HAMMER, \
		WeaponType.SPEAR, WeaponType.PITCHFORK, WeaponType.HALBERD, \
		WeaponType.WOODEN_STAFF, WeaponType.REBAR_STAFF, \
		WeaponType.MAKESHIFT_BOW, WeaponType.COMPOUND_BOW, WeaponType.CROSSBOW, \
		WeaponType.HUNTING_RIFLE, WeaponType.VARMINT_RIFLE, WeaponType.ASSAULT_RIFLE, \
		WeaponType.BATTLE_RIFLE, WeaponType.SNIPER_RIFLE, WeaponType.COMBAT_SHOTGUN, \
		WeaponType.LASER_RIFLE, WeaponType.PLASMA_RIFLE, WeaponType.GAUSS_RIFLE:
			return true
		_:
			return false

func calculate_damage(character) -> int:
	"""Calculate total damage with character stats"""
	var damage = get_base_damage(weapon_type)

	# Enchantment bonus
	damage += enchantment_level

	# Slaying bonuses
	damage += bonus_slaying
	if has_artifact_property(ArtifactProperty.SLAYING):
		damage += get_artifact_property(ArtifactProperty.SLAYING)

	# TODO: Add character stat/skill bonuses when character system is ported

	return max(1, damage)

func calculate_accuracy(character) -> int:
	"""Calculate to-hit accuracy with character stats"""
	var accuracy = enchantment_level
	accuracy += bonus_accuracy

	if has_artifact_property(ArtifactProperty.ACCURACY):
		accuracy += get_artifact_property(ArtifactProperty.ACCURACY)
	if has_artifact_property(ArtifactProperty.SLAYING):
		accuracy += get_artifact_property(ArtifactProperty.SLAYING)

	# TODO: Add character stat/skill bonuses

	return accuracy

func get_tooltip() -> Array[String]:
	var tooltip = super.get_tooltip()

	# Weapon stats
	var damage = get_base_damage(weapon_type)
	if enchantment_level != 0:
		tooltip.append("Damage: %d (+%d)" % [damage, enchantment_level])
	else:
		tooltip.append("Damage: %d" % damage)

	tooltip.append("Attack Delay: %d" % get_attack_delay(weapon_type))

	if bonus_accuracy != 0:
		tooltip.append("Accuracy: %+d" % bonus_accuracy)
	if bonus_slaying != 0:
		tooltip.append("Slaying: %+d" % bonus_slaying)

	if is_two_handed(weapon_type):
		tooltip.append("(Two-handed)")
	else:
		tooltip.append("(One-handed)")

	return tooltip
