extends Node2D
class_name Monster

## Base monster class for Wasteland Crawl
## Implements DCSS-style monster mechanics with turn-based combat
## Ported from Minecraft Wasteland mod's MonsterScalingSystem and EnemyAI

signal died(monster: Monster)
signal took_damage(amount: float)

# Grid positioning
var grid_position: Vector2i = Vector2i(0, 0)
var grid: Grid = null

# Monster identity
var monster_id: String = ""
var monster_name: String = ""
var monster_tier: int = 1  # 1-9 DCSS difficulty tier
var display_char: String = "M"  # Character to display in ASCII mode
var display_color: Color = Color.RED

# Core stats (DCSS-style)
var max_hp: float = 10.0
var current_hp: float = 10.0
var attack_damage: float = 3.0
var armor_class: int = 0  # AC - reduces physical damage
var evasion: int = 5  # EV - chance to dodge attacks
var movement_speed: float = 1.0  # Movement delay multiplier

# Resistances (DCSS-style)
var resist_fire: float = 0.0  # -1.0 to 1.0 (negative = vulnerable, positive = resistant)
var resist_cold: float = 0.0
var resist_electric: float = 0.0
var resist_poison: float = 0.0
var resist_magic: float = 0.0

# Robot-specific (from RobotEntity.java)
var is_robot: bool = false
var robot_armor_class: String = "NONE"  # LIGHT, MEDIUM, HEAVY
var bullet_deflection_chance: float = 0.0
var physical_resistance: float = 0.0
var can_self_destruct: bool = false

# AI behavior
enum AIType {
	MELEE,      # Simple melee attacker
	RANGED,     # Stays at distance, shoots
	SPELLCASTER, # Casts spells
	FLEEING,    # Runs away when damaged
	SUPPORT,    # Heals/buffs allies
	BERSERKER   # Aggressive melee, no retreat
}
var ai_type: AIType = AIType.MELEE
var ai_target: Node2D = null  # Current target (usually player)
var ai_attack_range: int = 1  # Attack range in tiles (1 = melee)
var ai_flee_hp_percent: float = 0.0  # Flee when HP drops below this (0 = never)

# Status effects
var is_poisoned: bool = false
var is_burning: bool = false
var is_frozen: bool = false
var is_stunned: bool = false
var poison_damage: float = 0.0
var burn_duration: int = 0
var stun_duration: int = 0

# Area scaling (from MonsterScalingSystem.java)
var area_level: int = 1  # Difficulty level of spawn area
var visual_variant: int = 0  # 0-5 visual tier

# References
var game_manager = null

# Constants for scaling (from MonsterScalingSystem.java)
const HP_PER_LEVEL = 2.0
const DAMAGE_PER_LEVEL = 0.5
const ARMOR_PER_LEVEL = 0.3
const SPEED_PER_LEVEL = 0.002

## Initialize monster from type ID
func initialize(type_id: String, level: int = 1):
	monster_id = type_id
	area_level = level

	# Load base stats from database
	var base_stats = MonsterTypes.get_monster_stats(type_id)
	if base_stats:
		apply_base_stats(base_stats)

	# Apply area scaling
	apply_area_scaling(area_level)

	# Full HP on spawn
	current_hp = max_hp

# Robot armor constants (from RobotEntity.java)
const ROBOT_ARMOR = {
	"LIGHT": {"deflection": 0.0, "damage_reduction": 0.0, "physical_resistance": 0.25},
	"MEDIUM": {"deflection": 0.30, "damage_reduction": 0.50, "physical_resistance": 0.35},
	"HEAVY": {"deflection": 0.60, "damage_reduction": 0.75, "physical_resistance": 0.50}
}

func _ready():
	add_to_group("monsters")

## Initialize monster with type and area level
func setup(type_id: String, spawn_area_level: int = 1):
	monster_id = type_id
	area_level = spawn_area_level

	# Load base stats from MonsterTypes
	var base_stats = MonsterTypes.get_monster_stats(type_id)
	if base_stats:
		apply_base_stats(base_stats)

	# Apply area scaling
	apply_area_scaling(area_level)

	# Set visual variant
	visual_variant = get_visual_variant(area_level)

	# Full HP on spawn
	current_hp = max_hp

## Apply base stats from monster definition
func apply_base_stats(stats: Dictionary):
	monster_name = stats.get("name", "Unknown")
	monster_tier = stats.get("tier", 1)
	max_hp = stats.get("hp", 10.0)
	attack_damage = stats.get("damage", 3.0)
	armor_class = stats.get("ac", 0)
	evasion = stats.get("ev", 5)
	movement_speed = stats.get("speed", 1.0)

	# Display settings
	display_char = stats.get("char", "M")
	display_color = stats.get("color", Color.RED)

	# AI settings
	ai_type = stats.get("ai_type", AIType.MELEE)
	ai_attack_range = stats.get("attack_range", 1)
	ai_flee_hp_percent = stats.get("flee_hp_percent", 0.0)

	# Resistances
	resist_fire = stats.get("resist_fire", 0.0)
	resist_cold = stats.get("resist_cold", 0.0)
	resist_electric = stats.get("resist_electric", 0.0)
	resist_poison = stats.get("resist_poison", 0.0)
	resist_magic = stats.get("resist_magic", 0.0)

	# Robot properties
	is_robot = stats.get("is_robot", false)
	robot_armor_class = stats.get("robot_armor", "NONE")
	can_self_destruct = stats.get("self_destruct", false)

	# Apply robot armor bonuses
	if is_robot and robot_armor_class != "NONE":
		var armor_data = ROBOT_ARMOR.get(robot_armor_class, {})
		bullet_deflection_chance = armor_data.get("deflection", 0.0)
		physical_resistance = armor_data.get("physical_resistance", 0.0)

## Apply area scaling (from MonsterScalingSystem.java)
func apply_area_scaling(level: int):
	if level <= 1:
		return

	var level_bonus = level - 1

	# Scale stats
	max_hp += HP_PER_LEVEL * level_bonus
	attack_damage += DAMAGE_PER_LEVEL * level_bonus
	armor_class += int(ARMOR_PER_LEVEL * level_bonus)
	movement_speed += SPEED_PER_LEVEL * level_bonus

## Get visual variant tier (from MonsterScalingSystem.java)
func get_visual_variant(level: int) -> int:
	if level <= 5: return 0
	if level <= 10: return 1
	if level <= 15: return 2
	if level <= 20: return 3
	if level <= 25: return 4
	return 5  # Nightmare tier

## Execute monster turn (called by turn manager)
func take_turn(player: Node2D, all_combatants: Array) -> float:
	# Status effect ticks
	process_status_effects()

	# Check if stunned
	if is_stunned:
		return 1.0  # Skip turn

	# Use AI to decide action
	ai_target = player
	return MonsterAI.execute_turn(self, player, all_combatants, game_manager)

## Process ongoing status effects
func process_status_effects():
	# Poison damage
	if is_poisoned and poison_damage > 0:
		take_damage(poison_damage, "poison")

	# Burning
	if is_burning and burn_duration > 0:
		take_damage(2.0, "fire")
		burn_duration -= 1
		if burn_duration <= 0:
			is_burning = false

	# Stun countdown
	if is_stunned and stun_duration > 0:
		stun_duration -= 1
		if stun_duration <= 0:
			is_stunned = false

## Take damage with resistances and armor (from RobotEntity.java hurt())
func take_damage(amount: float, damage_type: String = "physical") -> float:
	print(">>> Monster %s taking %.1f %s damage (HP: %.1f/%.1f)" % [monster_name, amount, damage_type, current_hp, max_hp])
	var modified_damage = amount

	# Apply resistance modifiers
	if damage_type == "fire":
		modified_damage *= (1.0 - resist_fire)
	elif damage_type == "cold":
		modified_damage *= (1.0 - resist_cold)
	elif damage_type == "electric":
		modified_damage *= (1.0 - resist_electric)
	elif damage_type == "poison":
		modified_damage *= (1.0 - resist_poison)

	# Robot-specific resistances
	if is_robot:
		# VULNERABLE to electrical (150-200%)
		if damage_type == "electric":
			modified_damage *= 1.5 + (randf() * 0.5)
		# RESISTANT to fire (25%)
		elif damage_type == "fire":
			modified_damage *= 0.75
		# Physical damage handling
		elif damage_type == "physical" or damage_type == "bullet":
			# Bullet deflection
			if damage_type == "bullet" and randf() < bullet_deflection_chance:
				var armor_data = ROBOT_ARMOR.get(robot_armor_class, {})
				var reduction = armor_data.get("damage_reduction", 0.0)
				modified_damage *= (1.0 - reduction)

			# Physical resistance
			modified_damage *= (1.0 - physical_resistance)

	# Apply armor class (reduces physical damage)
	if damage_type == "physical" or damage_type == "bullet":
		var armor_reduction = armor_class * 0.5  # Each AC point reduces 0.5 damage
		modified_damage = max(1.0, modified_damage - armor_reduction)

	# Apply damage
	current_hp -= modified_damage

	# Check death
	if current_hp <= 0:
		die()

	return modified_damage

## Monster death
func die():
	print("Monster %s died at position %s" % [monster_name, grid_position])

	# Self-destruct explosion for certain robots
	if is_robot and can_self_destruct:
		explode()

	# Drop loot
	drop_loot()

	# Grant experience to player
	if game_manager and game_manager.has_method("grant_experience"):
		var xp = get_experience_value()
		game_manager.grant_experience(xp)

	# Remove from grid BEFORE removing from scene
	if grid:
		grid.remove_entity(grid_position)
		print("  Removed from grid at %s" % grid_position)

	# Emit died signal
	died.emit(self)

	# Remove from game
	queue_free()

## Calculate experience value (from MonsterScalingSystem.java)
func get_experience_value() -> int:
	var base_xp = 5
	return base_xp + (area_level * 2)

## Self-destruct explosion (from RobotEntity.java)
func explode():
	# Create explosion effect at monster position
	var explosion_radius = 3
	var explosion_damage = 15.0

	if game_manager and game_manager.has_method("create_explosion"):
		game_manager.create_explosion(grid_position.x, grid_position.y, explosion_radius, explosion_damage)

## Drop loot based on monster tier
func drop_loot():
	"""Drop loot at monster position"""

	# 50% chance to drop loot (higher tier monsters drop more often)
	var drop_chance = 0.5 + (monster_tier * 0.05)  # +5% per tier

	if randf() > drop_chance:
		print("  No loot dropped")
		return

	# Generate loot based on monster tier and area level
	var item = LootGenerator.generate_monster_loot(monster_tier, area_level, is_robot)

	if item and game_manager and game_manager.has_method("spawn_loot"):
		game_manager.spawn_loot(grid_position, item)
		print("  Dropped: %s" % item.item_name)

## Attack a target (melee)
func attack_target(target: Node2D) -> bool:
	if not target or not target.has_method("take_damage"):
		return false

	# Calculate damage with variance (80-120%)
	var variance = 0.8 + (randf() * 0.4)
	var final_damage = max(1.0, attack_damage * variance)

	# Deal damage
	var damage_dealt = target.take_damage(final_damage, "physical")

	# Log to combat system
	if game_manager and game_manager.has_method("log_combat"):
		game_manager.log_combat("%s attacks for %.1f damage!" % [monster_name, damage_dealt])

	return true

## Check if monster should flee
func should_flee() -> bool:
	if ai_flee_hp_percent <= 0:
		return false

	var hp_percent = current_hp / max_hp
	return hp_percent < ai_flee_hp_percent

## Get display name with level suffix (from MonsterScalingSystem.java)
func get_display_name() -> String:
	if area_level == 0:
		return monster_name

	var color = get_level_color(area_level)
	return "%s [Lv%d]" % [monster_name, area_level]

## Get level color code
func get_level_color(level: int) -> String:
	if level <= 5: return "gray"
	if level <= 10: return "yellow"
	if level <= 15: return "orange"
	if level <= 20: return "red"
	if level <= 25: return "darkred"
	return "purple"

## Check if this is a high threat to player
func is_high_threat(player_level: int) -> bool:
	return area_level >= (player_level + 5)

## Get stat breakdown for debugging (from MonsterScalingSystem.java)
func get_stat_breakdown() -> String:
	return "%s [Lv%d]: HP=%.1f/%.1f, DMG=%.1f, AC=%d, EV=%d, Variant=%d" % [
		monster_name, area_level, current_hp, max_hp,
		attack_damage, armor_class, evasion, visual_variant
	]

## Spawn monster at grid position
func spawn_at(pos: Vector2i, new_grid: Grid):
	grid_position = pos
	grid = new_grid
	position = grid.grid_to_world(pos)
	grid.add_entity(pos, self)

## Move to grid position
func move_to(new_pos: Vector2i) -> bool:
	if not grid or not grid.is_walkable(new_pos):
		return false

	var old_pos = grid_position
	grid.move_entity(grid_position, new_pos)
	grid_position = new_pos
	position = grid.grid_to_world(new_pos)
	return true

## Try to move in direction
func try_move(direction: Vector2i) -> bool:
	return move_to(grid_position + direction)
