extends Resource
class_name DivineAbility

# Divine abilities granted by gods at various piety levels
# Based on DCSS god powers

# Ability types
enum AbilityType {
	PASSIVE,   # Always active when requirements met
	ACTIVE,    # Must be invoked
	BERSERK,   # Berserk rage
	HEAL,      # Healing
	SUMMON,    # Summon allies
	ATTACK,    # Direct attack
	BUFF,      # Buff player
	UTILITY    # Misc utility
}

# Ability properties
@export var id: String = ""
@export var name: String = ""
@export var description: String = ""
@export var min_piety: int = 0
@export var max_piety: int = -1  # -1 means no max
@export var invocation_cost: int = 0  # 0 means passive ability
@export var type: AbilityType = AbilityType.ACTIVE
@export var cooldown: float = 0.0  # Cooldown in seconds

# Runtime state
var last_used_time: float = 0.0

func _init(p_id: String = "", p_name: String = "", p_description: String = "",
		   p_min_piety: int = 0, p_max_piety: int = -1, p_invocation_cost: int = 0,
		   p_type: AbilityType = AbilityType.ACTIVE, p_cooldown: float = 0.0):
	id = p_id
	name = p_name
	description = p_description
	min_piety = p_min_piety
	max_piety = p_max_piety
	invocation_cost = p_invocation_cost
	type = p_type
	cooldown = p_cooldown

# Check if this is a passive ability
func is_passive() -> bool:
	return type == AbilityType.PASSIVE or invocation_cost == 0

# Check if available at given piety level
func is_available_at_piety(piety: int) -> bool:
	if piety < min_piety:
		return false
	if max_piety == -1:
		return true
	return piety <= max_piety

# Check if ability is ready (not on cooldown)
func is_ready() -> bool:
	if cooldown == 0.0:
		return true
	var current_time = Time.get_ticks_msec() / 1000.0
	return (current_time - last_used_time) >= cooldown

# Get remaining cooldown
func get_cooldown_remaining() -> float:
	if cooldown == 0.0:
		return 0.0
	var current_time = Time.get_ticks_msec() / 1000.0
	var remaining = cooldown - (current_time - last_used_time)
	return max(0.0, remaining)

# Use the ability
func use():
	last_used_time = Time.get_ticks_msec() / 1000.0

# Registry of all god abilities
class AbilityRegistry:
	static var god_abilities: Dictionary = {}  # God.Type -> Array[DivineAbility]

	# Initialize all god abilities
	static func initialize():
		print("Registering divine abilities...")

		register_trog_abilities()
		register_tso_abilities()
		register_zin_abilities()
		register_elyvilon_abilities()
		register_okawaru_abilities()
		register_sif_muna_abilities()
		register_vehumet_abilities()
		register_kikubaaqudgha_abilities()
		register_makhleb_abilities()
		register_yredelemnul_abilities()
		register_gozag_abilities()
		register_xom_abilities()

		var total = 0
		for abilities in god_abilities.values():
			total += abilities.size()
		print("Registered %d divine abilities for %d gods" % [total, god_abilities.size()])

	# Get abilities for a god
	static func get_abilities(god_type: God.Type) -> Array[DivineAbility]:
		if god_type in god_abilities:
			return god_abilities[god_type]
		return []

	# Get available abilities at current piety
	static func get_available_abilities(god_type: God.Type, piety: int) -> Array[DivineAbility]:
		var all_abilities = get_abilities(god_type)
		var available: Array[DivineAbility] = []

		for ability in all_abilities:
			if ability.is_available_at_piety(piety):
				available.append(ability)

		return available

	# ==== TROG - God of Anger ====
	static func register_trog_abilities():
		var abilities: Array[DivineAbility] = []

		# Passive: Magic resistance
		abilities.append(DivineAbility.new(
			"trog_magic_resist",
			"Magic Resistance",
			"Passive magic resistance",
			God.PIETY_RANK_1,
			-1,
			0,
			AbilityType.PASSIVE
		))

		# * Berserk
		abilities.append(DivineAbility.new(
			"trog_berserk",
			"Berserk",
			"Enter a berserker rage (+50% damage, +50% speed, -50% defense)",
			God.PIETY_RANK_1,
			-1,
			0,  # No piety cost, but has cooldown
			AbilityType.BERSERK,
			30.0  # 30 second cooldown
		))

		# ** Trog's Hand
		abilities.append(DivineAbility.new(
			"trog_hand",
			"Trog's Hand",
			"Grants AC, SH, and magic resistance for a duration",
			God.PIETY_RANK_2,
			-1,
			2,
			AbilityType.BUFF,
			60.0
		))

		# **** Brothers in Arms
		abilities.append(DivineAbility.new(
			"trog_brothers",
			"Brothers in Arms",
			"Summons powerful berserker allies",
			God.PIETY_RANK_4,
			-1,
			5,
			AbilityType.SUMMON,
			120.0
		))

		god_abilities[God.Type.TROG] = abilities

	# ==== THE SHINING ONE - God of Holy Light ====
	static func register_tso_abilities():
		var abilities: Array[DivineAbility] = []

		# Passive: Halo
		abilities.append(DivineAbility.new(
			"tso_halo",
			"Halo",
			"Emit light, preventing enemy invisibility",
			God.PIETY_RANK_1,
			-1,
			0,
			AbilityType.PASSIVE
		))

		# * Divine Shield
		abilities.append(DivineAbility.new(
			"tso_shield",
			"Divine Shield",
			"Grants temporary divine protection",
			God.PIETY_RANK_1,
			-1,
			2,
			AbilityType.BUFF,
			45.0
		))

		# *** Cleansing Flame
		abilities.append(DivineAbility.new(
			"tso_flame",
			"Cleansing Flame",
			"Blast nearby enemies with holy fire",
			God.PIETY_RANK_3,
			-1,
			3,
			AbilityType.ATTACK,
			60.0
		))

		# ***** Summon Divine Warrior
		abilities.append(DivineAbility.new(
			"tso_angel",
			"Summon Divine Warrior",
			"Summons a powerful angel to fight for you",
			God.PIETY_RANK_5,
			-1,
			6,
			AbilityType.SUMMON,
			120.0
		))

		god_abilities[God.Type.THE_SHINING_ONE] = abilities

	# ==== ZIN - God of Law ====
	static func register_zin_abilities():
		var abilities: Array[DivineAbility] = []

		# * Recite
		abilities.append(DivineAbility.new(
			"zin_recite",
			"Recite",
			"Recite Zin's laws to weaken nearby enemies",
			God.PIETY_RANK_1,
			-1,
			1,
			AbilityType.ATTACK,
			30.0
		))

		# ** Vitalisation
		abilities.append(DivineAbility.new(
			"zin_vital",
			"Vitalisation",
			"Restore health and clear status effects",
			God.PIETY_RANK_2,
			-1,
			2,
			AbilityType.HEAL,
			60.0
		))

		# **** Imprison
		abilities.append(DivineAbility.new(
			"zin_imprison",
			"Imprison",
			"Trap a monster in a cage of silver",
			God.PIETY_RANK_4,
			-1,
			4,
			AbilityType.UTILITY,
			90.0
		))

		# ****** Sanctuary
		abilities.append(DivineAbility.new(
			"zin_sanctuary",
			"Sanctuary",
			"Create a holy sanctuary where no violence is possible",
			God.PIETY_RANK_6,
			-1,
			7,
			AbilityType.UTILITY,
			180.0
		))

		god_abilities[God.Type.ZIN] = abilities

	# ==== ELYVILON - God of Healing ====
	static func register_elyvilon_abilities():
		var abilities: Array[DivineAbility] = []

		# * Lesser Healing
		abilities.append(DivineAbility.new(
			"ely_lesser_heal",
			"Lesser Healing",
			"Heal yourself for a small amount",
			God.PIETY_RANK_1,
			-1,
			1,
			AbilityType.HEAL,
			10.0
		))

		# ** Purification
		abilities.append(DivineAbility.new(
			"ely_purify",
			"Purification",
			"Remove negative status effects",
			God.PIETY_RANK_2,
			-1,
			2,
			AbilityType.HEAL,
			30.0
		))

		# *** Greater Healing
		abilities.append(DivineAbility.new(
			"ely_greater_heal",
			"Greater Healing",
			"Heal yourself for a large amount",
			God.PIETY_RANK_3,
			-1,
			3,
			AbilityType.HEAL,
			20.0
		))

		# ***** Divine Vigour
		abilities.append(DivineAbility.new(
			"ely_vigour",
			"Divine Vigour",
			"Increase max HP and MP temporarily",
			God.PIETY_RANK_5,
			-1,
			5,
			AbilityType.BUFF,
			120.0
		))

		god_abilities[God.Type.ELYVILON] = abilities

	# ==== OKAWARU - God of War ====
	static func register_okawaru_abilities():
		var abilities: Array[DivineAbility] = []

		# * Heroism
		abilities.append(DivineAbility.new(
			"oka_heroism",
			"Heroism",
			"Temporarily increase combat skills",
			God.PIETY_RANK_1,
			-1,
			2,
			AbilityType.BUFF,
			60.0
		))

		# *** Finesse
		abilities.append(DivineAbility.new(
			"oka_finesse",
			"Finesse",
			"Move and attack at double speed",
			God.PIETY_RANK_3,
			-1,
			3,
			AbilityType.BUFF,
			90.0
		))

		# ***** Duel
		abilities.append(DivineAbility.new(
			"oka_duel",
			"Duel",
			"Challenge an enemy to single combat",
			God.PIETY_RANK_5,
			-1,
			5,
			AbilityType.UTILITY,
			120.0
		))

		god_abilities[God.Type.OKAWARU] = abilities

	# ==== SIF MUNA - God of Magic ====
	static func register_sif_muna_abilities():
		var abilities: Array[DivineAbility] = []

		# Passive: Channel Magic
		abilities.append(DivineAbility.new(
			"sif_channel",
			"Channel Magic",
			"Passively regenerate MP faster",
			God.PIETY_RANK_1,
			-1,
			0,
			AbilityType.PASSIVE
		))

		# ** Forget Spell
		abilities.append(DivineAbility.new(
			"sif_forget",
			"Forget Spell",
			"Forget a memorized spell to make room",
			God.PIETY_RANK_2,
			-1,
			0,
			AbilityType.UTILITY
		))

		# **** Divine Energy
		abilities.append(DivineAbility.new(
			"sif_energy",
			"Divine Energy",
			"Restore a large amount of MP",
			God.PIETY_RANK_4,
			-1,
			3,
			AbilityType.UTILITY,
			90.0
		))

		god_abilities[God.Type.SIF_MUNA] = abilities

	# ==== VEHUMET - God of Destructive Magic ====
	static func register_vehumet_abilities():
		var abilities: Array[DivineAbility] = []

		# Passive: Spell Power
		abilities.append(DivineAbility.new(
			"veh_power",
			"Destructive Power",
			"Increases damage of offensive spells",
			God.PIETY_RANK_1,
			-1,
			0,
			AbilityType.PASSIVE
		))

		# Passive: Spell Library (gift spells)
		abilities.append(DivineAbility.new(
			"veh_library",
			"Vehumet's Library",
			"Vehumet gifts you destructive spells",
			God.PIETY_RANK_2,
			-1,
			0,
			AbilityType.PASSIVE
		))

		god_abilities[God.Type.VEHUMET] = abilities

	# ==== KIKUBAAQUDGHA - God of Necromancy ====
	static func register_kikubaaqudgha_abilities():
		var abilities: Array[DivineAbility] = []

		# * Receive Corpses
		abilities.append(DivineAbility.new(
			"kiku_corpses",
			"Receive Corpses",
			"Kikubaaqudgha sends you corpses",
			God.PIETY_RANK_1,
			-1,
			2,
			AbilityType.UTILITY,
			60.0
		))

		# *** Torment
		abilities.append(DivineAbility.new(
			"kiku_torment",
			"Torment",
			"Inflict torment on all nearby living creatures",
			God.PIETY_RANK_3,
			-1,
			4,
			AbilityType.ATTACK,
			90.0
		))

		god_abilities[God.Type.KIKUBAAQUDGHA] = abilities

	# ==== MAKHLEB - God of Chaos ====
	static func register_makhleb_abilities():
		var abilities: Array[DivineAbility] = []

		# ** Lesser Servant
		abilities.append(DivineAbility.new(
			"mak_lesser",
			"Lesser Servant of Makhleb",
			"Summon a minor demon",
			God.PIETY_RANK_2,
			-1,
			2,
			AbilityType.SUMMON,
			45.0
		))

		# **** Greater Servant
		abilities.append(DivineAbility.new(
			"mak_greater",
			"Greater Servant of Makhleb",
			"Summon a powerful demon",
			God.PIETY_RANK_4,
			-1,
			4,
			AbilityType.SUMMON,
			90.0
		))

		god_abilities[God.Type.MAKHLEB] = abilities

	# ==== YREDELEMNUL - God of Death ====
	static func register_yredelemnul_abilities():
		var abilities: Array[DivineAbility] = []

		# * Animate Remains
		abilities.append(DivineAbility.new(
			"yred_animate",
			"Animate Remains",
			"Raise a corpse as an undead servant",
			God.PIETY_RANK_1,
			-1,
			2,
			AbilityType.SUMMON,
			30.0
		))

		# *** Drain Life
		abilities.append(DivineAbility.new(
			"yred_drain",
			"Drain Life",
			"Drain life from nearby living creatures",
			God.PIETY_RANK_3,
			-1,
			3,
			AbilityType.ATTACK,
			60.0
		))

		# ***** Enslave Soul
		abilities.append(DivineAbility.new(
			"yred_enslave",
			"Enslave Soul",
			"Permanently enslave the soul of a fallen enemy",
			God.PIETY_RANK_5,
			-1,
			5,
			AbilityType.SUMMON,
			120.0
		))

		god_abilities[God.Type.YREDELEMNUL] = abilities

	# ==== GOZAG - God of Gold ====
	static func register_gozag_abilities():
		var abilities: Array[DivineAbility] = []

		# Passive: Gold distraction
		abilities.append(DivineAbility.new(
			"gozag_distract",
			"Gold Distraction",
			"Gold distracts your enemies",
			God.PIETY_RANK_1,
			-1,
			0,
			AbilityType.PASSIVE
		))

		# * Potion Petition (uses gold not piety)
		abilities.append(DivineAbility.new(
			"gozag_potion",
			"Potion Petition",
			"Buy a set of potions",
			God.PIETY_RANK_1,
			-1,
			0,  # Uses gold instead
			AbilityType.UTILITY,
			30.0
		))

		# * Call Merchant (uses gold not piety)
		abilities.append(DivineAbility.new(
			"gozag_merchant",
			"Call Merchant",
			"Summon a shop",
			God.PIETY_RANK_1,
			-1,
			0,  # Uses gold instead
			AbilityType.UTILITY,
			60.0
		))

		god_abilities[God.Type.GOZAG] = abilities

	# ==== XOM - God of Chaos ====
	static func register_xom_abilities():
		var abilities: Array[DivineAbility] = []

		# Xom has no active abilities - acts randomly!
		abilities.append(DivineAbility.new(
			"xom_chaos",
			"Xom's Whim",
			"Xom will act randomly - good or bad!",
			0,
			-1,
			0,
			AbilityType.PASSIVE
		))

		god_abilities[God.Type.XOM] = abilities
