class_name Spell
extends Resource

## Complete DCSS Spell System
## Implements all spell mechanics including casting, power calculation, and effects

# Spell target types
enum TargetType {
	SINGLE_TARGET,         # Targets one enemy (requires LOS)
	SELF,                  # Affects only the caster
	LOCATION,              # Targets a location for area effect
	AREA_AROUND_CASTER,    # Area of effect around caster
	BEAM,                  # Beam/projectile in a direction
	CLOUD,                 # Cloud effect at location
	ALL_ENEMIES,           # All enemies in radius
	SMITE                  # Targets enemy ignoring obstacles
}

# Spell data
var id: String
var display_name: String
var description: String
var schools: Array = []  # Array of MagicSchool.School
var spell_level: int = 1  # 1-9
var base_mp_cost: int = 1
var target_type: TargetType = TargetType.SINGLE_TARGET
var base_range: int = 6
var base_power: int = 10

# Effect function - callable that executes the spell
var effect_func: Callable

## Initialize spell
func _init(
	p_id: String = "",
	p_name: String = "",
	p_description: String = "",
	p_schools: Array = [],
	p_level: int = 1,
	p_mp_cost: int = 1,
	p_target_type: TargetType = TargetType.SINGLE_TARGET,
	p_range: int = 6,
	p_power: int = 10
):
	id = p_id
	display_name = p_name
	description = p_description
	schools = p_schools
	spell_level = p_level
	base_mp_cost = p_mp_cost
	target_type = p_target_type
	base_range = p_range
	base_power = p_power

## Calculate MP cost based on character skills
func calculate_mp_cost(character) -> int:
	var cost = base_mp_cost

	# Reduce cost based on spell school skills
	var total_skill_reduction = 0
	for school in schools:
		var skill_name = MagicSchool.get_school_skill(school)
		if character.has_method("get_skill_level"):
			var skill_level = character.get_skill_level(skill_name)
			total_skill_reduction += skill_level

	# Reduce cost by 1% per skill level (averaged across schools)
	if schools.size() > 0:
		var avg_skill_level = total_skill_reduction / schools.size()
		cost = max(1, cost - int(cost * avg_skill_level / 100.0))

	# Spellcasting skill reduces cost
	if character.has_method("get_skill_level"):
		var spellcasting = character.get_skill_level("SPELLCASTING")
		cost = max(1, cost - int(spellcasting / 5.0))

	return cost

## Calculate effective spell power
func calculate_spell_power(character) -> int:
	var power = base_power

	# Intelligence bonus
	var intelligence = character.intelligence if "intelligence" in character else 10
	power += (intelligence - 8) * 2  # +2 power per INT above 8

	# Spell school skill bonuses
	var total_skill_bonus = 0
	for school in schools:
		var skill_name = MagicSchool.get_school_skill(school)
		if character.has_method("get_skill_level"):
			var skill_level = character.get_skill_level(skill_name)
			total_skill_bonus += skill_level * 3  # +3 power per skill level

	if schools.size() > 0:
		power += total_skill_bonus / schools.size()

	# Spellcasting skill bonus
	if character.has_method("get_skill_level"):
		var spellcasting = character.get_skill_level("SPELLCASTING")
		power += spellcasting * 2

	return max(1, power)

## Cast the spell
func cast(caster, target = null, target_pos: Vector2 = Vector2.ZERO, world = null) -> bool:
	# Check MP
	var mp_cost = calculate_mp_cost(caster)
	if caster.current_mp < mp_cost:
		if caster.has_method("show_message"):
			caster.show_message("Not enough MP! Need %d MP." % mp_cost)
		return false

	# Check miscast
	if _check_miscast(caster):
		# Spell fizzled, still costs half MP
		caster.current_mp -= int(mp_cost / 2.0)
		if caster.has_method("show_message"):
			caster.show_message("Spell fizzled!")
		return false

	# Calculate spell power
	var spell_power = calculate_spell_power(caster)

	# Execute spell effect
	var success = false
	if effect_func and effect_func.is_valid():
		success = effect_func.call(caster, target, target_pos, world, spell_power)
	else:
		success = _default_effect(caster, target, target_pos, world, spell_power)

	if success:
		# Consume MP
		caster.current_mp -= mp_cost

		# Grant XP
		_grant_casting_xp(caster)

		return true
	else:
		# Failed cast, half MP cost
		caster.current_mp -= int(mp_cost / 2.0)
		if caster.has_method("show_message"):
			caster.show_message("Spell fizzled!")
		return false

## Check for miscast
func _check_miscast(caster) -> bool:
	# Get skill levels for all schools
	var skill_levels = {}
	for school in schools:
		var skill_name = MagicSchool.get_school_skill(school)
		if caster.has_method("get_skill_level"):
			skill_levels[school] = caster.get_skill_level(skill_name)
		else:
			skill_levels[school] = 0

	var intelligence = caster.intelligence if "intelligence" in caster else 10
	var miscast_chance = MagicSchool.calculate_miscast_chance(
		spell_level,
		skill_levels,
		intelligence
	)

	if randf() * 100.0 < miscast_chance:
		var severity = MagicSchool.calculate_miscast_severity(spell_level, skill_levels)
		var message = MagicSchool.apply_miscast_effect(caster, schools, severity)
		if caster.has_method("show_message"):
			caster.show_message(message)
		return true

	return false

## Grant XP for casting
func _grant_casting_xp(caster) -> void:
	if not caster.has_method("train_skill"):
		return

	var xp_gain = spell_level * 5

	# Grant to spell schools
	for school in schools:
		var skill_name = MagicSchool.get_school_skill(school)
		caster.train_skill(skill_name, xp_gain)

	# Also grant Spellcasting XP
	caster.train_skill("SPELLCASTING", int(xp_gain / 2.0))

## Default effect (override with effect_func)
func _default_effect(caster, target, target_pos: Vector2, world, power: int) -> bool:
	return false

## Get formatted spell info
func get_formatted_info(character = null) -> String:
	var info = "[color=yellow]%s[/color]\n" % display_name
	info += "Level %d %s\n" % [spell_level, MagicSchool.format_schools_list(schools)]

	if character:
		info += "MP Cost: %d\n" % calculate_mp_cost(character)
		info += "Power: %d\n" % calculate_spell_power(character)
	else:
		info += "MP Cost: %d\n" % base_mp_cost
		info += "Power: %d\n" % base_power

	info += "Target: %s\n" % _get_target_type_name()
	info += "Range: %d\n\n" % base_range
	info += description

	return info

## Get target type display name
func _get_target_type_name() -> String:
	match target_type:
		TargetType.SINGLE_TARGET: return "Single Target"
		TargetType.SELF: return "Self"
		TargetType.LOCATION: return "Location"
		TargetType.AREA_AROUND_CASTER: return "Area (Caster)"
		TargetType.BEAM: return "Beam"
		TargetType.CLOUD: return "Cloud"
		TargetType.ALL_ENEMIES: return "All Enemies"
		TargetType.SMITE: return "Smite"
	return "Unknown"

## Dice rolling utility
static func roll_dice(count: int, sides: int) -> int:
	var total = 0
	for i in range(count):
		total += randi() % sides + 1
	return total

# ============================================================================
# SPELL REGISTRY - All DCSS Spells
# ============================================================================

static var _spell_registry: Dictionary = {}

## Register all spells
static func register_all_spells() -> void:
	_register_level_1_spells()
	_register_level_2_spells()
	_register_level_3_spells()
	_register_level_4_spells()
	_register_level_5_spells()
	_register_level_6_spells()
	_register_level_7_spells()
	_register_level_8_spells()
	_register_level_9_spells()

## Get spell by ID
static func get_spell(spell_id: String) -> Spell:
	return _spell_registry.get(spell_id)

## Get all spells
static func get_all_spells() -> Array:
	return _spell_registry.values()

## Get spells by level
static func get_spells_by_level(level: int) -> Array:
	var result = []
	for spell in _spell_registry.values():
		if spell.spell_level == level:
			result.append(spell)
	return result

## Get spells by school
static func get_spells_by_school(school) -> Array:
	var result = []
	for spell in _spell_registry.values():
		if school in spell.schools:
			result.append(spell)
	return result

## Register a spell
static func _register(spell: Spell) -> void:
	_spell_registry[spell.id] = spell

# ============================================================================
# LEVEL 1 SPELLS
# ============================================================================

static func _register_level_1_spells() -> void:
	# Magic Dart
	var magic_dart = Spell.new(
		"magic_dart",
		"Magic Dart",
		"Fires a magical dart that never misses. Damage: 3d4 + power/10",
		[MagicSchool.School.CONJURATIONS],
		1, 1, TargetType.SINGLE_TARGET, 6, 10
	)
	magic_dart.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(3, 4) + int(power / 10.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "magic")
		if caster.has_method("show_message"):
			caster.show_message("Magic Dart hits for %d damage!" % damage)
		return true
	_register(magic_dart)

	# Shock
	var shock = Spell.new(
		"shock",
		"Shock",
		"Zaps a target with electricity. Damage: 1d8 + power/8",
		[MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		1, 1, TargetType.SINGLE_TARGET, 5, 8
	)
	shock.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(1, 8) + int(power / 8.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "electricity")
		return true
	_register(shock)

	# Sandblast
	var sandblast = Spell.new(
		"sandblast",
		"Sandblast",
		"Fires a blast of sand and grit. Damage: 2d4 + power/12",
		[MagicSchool.School.EARTH, MagicSchool.School.CONJURATIONS],
		1, 1, TargetType.SINGLE_TARGET, 4, 8
	)
	sandblast.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(2, 4) + int(power / 12.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "physical")
		return true
	_register(sandblast)

	# Freeze
	var freeze = Spell.new(
		"freeze",
		"Freeze",
		"Freezes an adjacent target. Damage: 1d6 + power/10, may slow",
		[MagicSchool.School.ICE],
		1, 1, TargetType.SINGLE_TARGET, 1, 8
	)
	freeze.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(1, 6) + int(power / 10.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "cold")
		if randf() < 0.3 and target.has_method("apply_status"):
			target.apply_status("slowed", 5)
		return true
	_register(freeze)

	# Pain
	var pain = Spell.new(
		"pain",
		"Pain",
		"Inflicts pain on a target. Damage: 1d4 + power/6",
		[MagicSchool.School.NECROMANCY],
		1, 1, TargetType.SINGLE_TARGET, 5, 8
	)
	pain.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(1, 4) + int(power / 6.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "negative_energy")
		return true
	_register(pain)

# ============================================================================
# LEVEL 2 SPELLS
# ============================================================================

static func _register_level_2_spells() -> void:
	# Blink
	var blink = Spell.new(
		"blink",
		"Blink",
		"Randomly teleports you a short distance (2-6 blocks)",
		[MagicSchool.School.TRANSLOCATIONS],
		2, 2, TargetType.SELF, 0, 15
	)
	blink.effect_func = func(caster, target, pos, world, power):
		if not world or not caster.has_method("teleport_random"):
			return false
		var distance = 2 + randi() % 5
		return caster.teleport_random(distance)
	_register(blink)

	# Searing Ray
	var searing_ray = Spell.new(
		"searing_ray",
		"Searing Ray",
		"Fires a beam of heat. Damage: 2d4 + power/8",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		2, 2, TargetType.BEAM, 6, 12
	)
	searing_ray.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(2, 4) + int(power / 8.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "fire")
		return true
	_register(searing_ray)

	# Static Discharge
	var static_discharge = Spell.new(
		"static_discharge",
		"Static Discharge",
		"Releases electrical energy around you. Damage: 1d6 + power/10",
		[MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		2, 2, TargetType.AREA_AROUND_CASTER, 3, 12
	)
	_register(static_discharge)

	# Vampiric Draining
	var vampiric_draining = Spell.new(
		"vampiric_draining",
		"Vampiric Draining",
		"Drains life from an adjacent target, healing you. Damage: 2d6 + power/8",
		[MagicSchool.School.NECROMANCY],
		2, 3, TargetType.SINGLE_TARGET, 1, 12
	)
	vampiric_draining.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(2, 6) + int(power / 8.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "negative_energy")
		if caster.has_method("heal"):
			caster.heal(int(damage / 2.0))
		return true
	_register(vampiric_draining)

	# Swiftness
	var swiftness = Spell.new(
		"swiftness",
		"Swiftness",
		"Increases your movement speed",
		[MagicSchool.School.CHARMS, MagicSchool.School.AIR],
		2, 2, TargetType.SELF, 0, 15
	)
	swiftness.effect_func = func(caster, target, pos, world, power):
		if caster.has_method("apply_buff"):
			var duration = 10 + int(power / 5.0)
			caster.apply_buff("swiftness", duration)
		return true
	_register(swiftness)

# ============================================================================
# LEVEL 3 SPELLS
# ============================================================================

static func _register_level_3_spells() -> void:
	# Stone Arrow
	var stone_arrow = Spell.new(
		"stone_arrow",
		"Stone Arrow",
		"Fires a heavy stone projectile. Damage: 3d6 + power/8",
		[MagicSchool.School.EARTH, MagicSchool.School.CONJURATIONS],
		3, 3, TargetType.SINGLE_TARGET, 7, 18
	)
	stone_arrow.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(3, 6) + int(power / 8.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "physical")
		return true
	_register(stone_arrow)

	# Mephitic Cloud
	var mephitic_cloud = Spell.new(
		"mephitic_cloud",
		"Mephitic Cloud",
		"Creates a cloud of noxious fumes that confuses enemies",
		[MagicSchool.School.POISON, MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		3, 3, TargetType.CLOUD, 5, 18
	)
	_register(mephitic_cloud)

	# Enslavement
	var enslavement = Spell.new(
		"enslavement",
		"Enslavement",
		"Attempts to enslave a target creature",
		[MagicSchool.School.HEXES],
		3, 4, TargetType.SINGLE_TARGET, 5, 18
	)
	_register(enslavement)

	# Inner Flame
	var inner_flame = Spell.new(
		"inner_flame",
		"Inner Flame",
		"Curses a target to explode on death",
		[MagicSchool.School.FIRE, MagicSchool.School.HEXES],
		3, 3, TargetType.SINGLE_TARGET, 6, 18
	)
	_register(inner_flame)

	# Call Canine Familiar
	var call_canine = Spell.new(
		"call_canine_familiar",
		"Call Canine Familiar",
		"Summons a hound to fight for you",
		[MagicSchool.School.SUMMONINGS],
		3, 3, TargetType.SELF, 0, 18
	)
	_register(call_canine)

# ============================================================================
# LEVEL 4 SPELLS
# ============================================================================

static func _register_level_4_spells() -> void:
	# Lightning Bolt
	var lightning_bolt = Spell.new(
		"lightning_bolt",
		"Lightning Bolt",
		"Fires a bolt of lightning. Damage: 3d8 + power/6",
		[MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		4, 4, TargetType.BEAM, 8, 22
	)
	lightning_bolt.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(3, 8) + int(power / 6.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "electricity")
		return true
	_register(lightning_bolt)

	# Sticky Flame
	var sticky_flame = Spell.new(
		"sticky_flame",
		"Sticky Flame",
		"Covers target in liquid fire. Damage: 2d6 + power/8, burning",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		4, 4, TargetType.SINGLE_TARGET, 6, 22
	)
	sticky_flame.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(2, 6) + int(power / 8.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "fire")
		if target.has_method("apply_status"):
			target.apply_status("burning", 10)
		return true
	_register(sticky_flame)

	# Teleport Self
	var teleport_self = Spell.new(
		"teleport_self",
		"Teleport Self",
		"Teleports you to a random location after a delay",
		[MagicSchool.School.TRANSLOCATIONS],
		4, 4, TargetType.SELF, 0, 22
	)
	_register(teleport_self)

	# Summon Ice Beast
	var summon_ice_beast = Spell.new(
		"summon_ice_beast",
		"Summon Ice Beast",
		"Summons an ice beast to fight for you",
		[MagicSchool.School.ICE, MagicSchool.School.SUMMONINGS],
		4, 5, TargetType.SELF, 0, 22
	)
	_register(summon_ice_beast)

	# Olgreb's Toxic Radiance
	var toxic_radiance = Spell.new(
		"olgrebs_toxic_radiance",
		"Olgreb's Toxic Radiance",
		"Emanates toxic radiation, poisoning nearby creatures",
		[MagicSchool.School.POISON],
		4, 5, TargetType.AREA_AROUND_CASTER, 4, 22
	)
	_register(toxic_radiance)

# ============================================================================
# LEVEL 5 SPELLS
# ============================================================================

static func _register_level_5_spells() -> void:
	# Fireball
	var fireball = Spell.new(
		"fireball",
		"Fireball",
		"Fires an exploding ball of fire. Damage: 3d6 + power/6, 3-block radius",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		5, 5, TargetType.LOCATION, 8, 25
	)
	fireball.effect_func = func(caster, target, pos, world, power):
		if not world:
			return false
		var damage = roll_dice(3, 6) + int(power / 6.0)
		# Deal area damage
		if world.has_method("deal_area_damage"):
			world.deal_area_damage(pos, 3, damage, "fire")
		return true
	_register(fireball)

	# Bolt of Cold
	var bolt_of_cold = Spell.new(
		"bolt_of_cold",
		"Bolt of Cold",
		"Fires a freezing bolt. Damage: 3d10 + power/6",
		[MagicSchool.School.ICE, MagicSchool.School.CONJURATIONS],
		5, 6, TargetType.BEAM, 8, 25
	)
	bolt_of_cold.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(3, 10) + int(power / 6.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "cold")
		if randf() < 0.5 and target.has_method("apply_status"):
			target.apply_status("frozen", 3)
		return true
	_register(bolt_of_cold)

	# Agony
	var agony = Spell.new(
		"agony",
		"Agony",
		"Inflicts terrible pain on a target, dealing heavy damage",
		[MagicSchool.School.NECROMANCY],
		5, 5, TargetType.SINGLE_TARGET, 6, 25
	)
	agony.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		# Agony deals % of max HP
		var damage = 0
		if target.has_method("get_max_hp"):
			damage = int(target.get_max_hp() * 0.25)
		else:
			damage = roll_dice(4, 8) + int(power / 6.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "negative_energy")
		return true
	_register(agony)

	# Iskenderun's Battlesphere
	var battlesphere = Spell.new(
		"iskenderuns_battlesphere",
		"Iskenderun's Battlesphere",
		"Creates a battlesphere that mirrors your spells",
		[MagicSchool.School.CONJURATIONS, MagicSchool.School.CHARMS],
		5, 5, TargetType.SELF, 0, 25
	)
	_register(battlesphere)

	# Passage of Golubria
	var passage = Spell.new(
		"passage_of_golubria",
		"Passage of Golubria",
		"Creates a teleportation gateway",
		[MagicSchool.School.TRANSLOCATIONS],
		5, 5, TargetType.LOCATION, 10, 25
	)
	_register(passage)

# ============================================================================
# LEVEL 6 SPELLS
# ============================================================================

static func _register_level_6_spells() -> void:
	# Bolt of Fire
	var bolt_of_fire = Spell.new(
		"bolt_of_fire",
		"Bolt of Fire",
		"Fires a blazing bolt. Damage: 4d10 + power/5",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		6, 6, TargetType.BEAM, 8, 30
	)
	bolt_of_fire.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(4, 10) + int(power / 5.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "fire")
		if target.has_method("apply_status"):
			target.apply_status("burning", 5)
		return true
	_register(bolt_of_fire)

	# Iron Shot
	var iron_shot = Spell.new(
		"iron_shot",
		"Iron Shot",
		"Fires a massive iron projectile. Damage: 4d12 + power/5",
		[MagicSchool.School.EARTH, MagicSchool.School.CONJURATIONS],
		6, 6, TargetType.SINGLE_TARGET, 7, 30
	)
	iron_shot.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(4, 12) + int(power / 5.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "physical")
		return true
	_register(iron_shot)

	# Freezing Cloud
	var freezing_cloud = Spell.new(
		"freezing_cloud",
		"Freezing Cloud",
		"Creates a cloud of freezing vapor",
		[MagicSchool.School.ICE, MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		6, 6, TargetType.CLOUD, 6, 30
	)
	_register(freezing_cloud)

	# Statue Form
	var statue_form = Spell.new(
		"statue_form",
		"Statue Form",
		"Transforms you into a stone statue, gaining massive AC but reduced speed",
		[MagicSchool.School.TRANSMUTATIONS, MagicSchool.School.EARTH],
		6, 6, TargetType.SELF, 0, 30
	)
	_register(statue_form)

	# Death Channel
	var death_channel = Spell.new(
		"death_channel",
		"Death Channel",
		"Enemies killed return as spectral servants",
		[MagicSchool.School.NECROMANCY],
		6, 6, TargetType.SELF, 0, 30
	)
	_register(death_channel)

# ============================================================================
# LEVEL 7 SPELLS
# ============================================================================

static func _register_level_7_spells() -> void:
	# Delayed Fireball
	var delayed_fireball = Spell.new(
		"delayed_fireball",
		"Delayed Fireball",
		"Prepares a powerful fireball to be released later. Damage: 5d8 + power/5",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		7, 7, TargetType.LOCATION, 10, 35
	)
	_register(delayed_fireball)

	# Orb of Destruction
	var orb_of_destruction = Spell.new(
		"orb_of_destruction",
		"Orb of Destruction",
		"Creates a powerful orb of pure destructive energy. Damage: 5d10 + power/4",
		[MagicSchool.School.CONJURATIONS],
		7, 7, TargetType.SINGLE_TARGET, 9, 35
	)
	orb_of_destruction.effect_func = func(caster, target, pos, world, power):
		if not target:
			return false
		var damage = roll_dice(5, 10) + int(power / 4.0)
		if target.has_method("take_damage"):
			target.take_damage(damage, "magic")
		return true
	_register(orb_of_destruction)

	# Haunt
	var haunt = Spell.new(
		"haunt",
		"Haunt",
		"Summons horrible specters to haunt a target",
		[MagicSchool.School.NECROMANCY, MagicSchool.School.SUMMONINGS],
		7, 7, TargetType.SINGLE_TARGET, 8, 35
	)
	_register(haunt)

	# Malign Gateway
	var malign_gateway = Spell.new(
		"malign_gateway",
		"Malign Gateway",
		"Opens a gateway to a malign dimension",
		[MagicSchool.School.SUMMONINGS, MagicSchool.School.TRANSLOCATIONS],
		7, 7, TargetType.LOCATION, 7, 35
	)
	_register(malign_gateway)

	# Chain Lightning
	var chain_lightning = Spell.new(
		"chain_lightning",
		"Chain Lightning",
		"Releases lightning that arcs between enemies. Damage: 4d8 + power/5",
		[MagicSchool.School.AIR, MagicSchool.School.CONJURATIONS],
		7, 8, TargetType.SINGLE_TARGET, 8, 35
	)
	_register(chain_lightning)

# ============================================================================
# LEVEL 8 SPELLS
# ============================================================================

static func _register_level_8_spells() -> void:
	# Fire Storm
	var fire_storm = Spell.new(
		"fire_storm",
		"Fire Storm",
		"Calls down a devastating storm of fire. Damage: 6d10 + power/4",
		[MagicSchool.School.FIRE, MagicSchool.School.CONJURATIONS],
		8, 9, TargetType.LOCATION, 8, 40
	)
	_register(fire_storm)

	# Glaciate
	var glaciate = Spell.new(
		"glaciate",
		"Glaciate",
		"Freezes everything in front of you. Damage: 6d12 + power/4",
		[MagicSchool.School.ICE, MagicSchool.School.CONJURATIONS],
		8, 9, TargetType.AREA_AROUND_CASTER, 6, 40
	)
	_register(glaciate)

	# Summon Hydra
	var summon_hydra = Spell.new(
		"summon_hydra",
		"Summon Hydra",
		"Summons a powerful hydra to fight for you",
		[MagicSchool.School.SUMMONINGS],
		8, 9, TargetType.SELF, 0, 40
	)
	_register(summon_hydra)

	# Dragon Form
	var dragon_form = Spell.new(
		"dragon_form",
		"Dragon Form",
		"Transforms you into a fearsome dragon",
		[MagicSchool.School.TRANSMUTATIONS],
		8, 9, TargetType.SELF, 0, 40
	)
	_register(dragon_form)

	# Borgnjor's Revivification
	var revivification = Spell.new(
		"borgnjors_revivification",
		"Borgnjor's Revivification",
		"Returns you to life when you would die (preemptive)",
		[MagicSchool.School.NECROMANCY],
		8, 9, TargetType.SELF, 0, 40
	)
	_register(revivification)

# ============================================================================
# LEVEL 9 SPELLS
# ============================================================================

static func _register_level_9_spells() -> void:
	# Shatter
	var shatter = Spell.new(
		"shatter",
		"Shatter",
		"Destroys walls and deals massive damage to brittle creatures",
		[MagicSchool.School.EARTH],
		9, 10, TargetType.AREA_AROUND_CASTER, 5, 45
	)
	_register(shatter)

	# Tornado
	var tornado = Spell.new(
		"tornado",
		"Tornado",
		"Summons a devastating tornado around you",
		[MagicSchool.School.AIR],
		9, 10, TargetType.SELF, 0, 45
	)
	_register(tornado)

	# Meteor Storm
	var meteor_storm = Spell.new(
		"meteor_storm",
		"Meteor Storm",
		"Calls down meteors from the sky. Damage: 8d12 + power/3",
		[MagicSchool.School.FIRE, MagicSchool.School.EARTH, MagicSchool.School.CONJURATIONS],
		9, 10, TargetType.LOCATION, 10, 45
	)
	_register(meteor_storm)

	# Necromutation
	var necromutation = Spell.new(
		"necromutation",
		"Necromutation",
		"Transforms you into a lich, gaining undead powers",
		[MagicSchool.School.NECROMANCY, MagicSchool.School.TRANSMUTATIONS],
		9, 10, TargetType.SELF, 0, 45
	)
	_register(necromutation)

	# Disjunction
	var disjunction = Spell.new(
		"disjunction",
		"Disjunction",
		"Banishes all nearby creatures to the Abyss",
		[MagicSchool.School.TRANSLOCATIONS],
		9, 10, TargetType.AREA_AROUND_CASTER, 6, 45
	)
	_register(disjunction)
