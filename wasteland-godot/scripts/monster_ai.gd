extends Node
class_name MonsterAI

## AI behavior system for Wasteland Crawl monsters
## Implements DCSS-style AI with pathfinding, combat decisions, and behaviors
## Ported from EnemyAI.java

## Execute monster turn and return delay multiplier
## Returns time delay for animation (1.0 = normal speed)
static func execute_turn(monster: Monster, player: Node2D, all_combatants: Array, game_manager) -> float:
	if not monster or not player:
		return 1.0

	# Calculate distance to player
	var distance = calculate_distance(monster.grid_position.x, monster.grid_position.y, player.grid_position.x, player.grid_position.y)

	# Decide action based on AI type and situation
	match monster.ai_type:
		Monster.AIType.MELEE:
			return execute_melee_ai(monster, player, all_combatants, game_manager, distance)
		Monster.AIType.RANGED:
			return execute_ranged_ai(monster, player, all_combatants, game_manager, distance)
		Monster.AIType.SPELLCASTER:
			return execute_spellcaster_ai(monster, player, all_combatants, game_manager, distance)
		Monster.AIType.FLEEING:
			return execute_fleeing_ai(monster, player, all_combatants, game_manager, distance)
		Monster.AIType.SUPPORT:
			return execute_support_ai(monster, player, all_combatants, game_manager, distance)
		Monster.AIType.BERSERKER:
			return execute_berserker_ai(monster, player, all_combatants, game_manager, distance)
		_:
			return execute_melee_ai(monster, player, all_combatants, game_manager, distance)

## Melee AI - Simple approach and attack (from EnemyAI.java)
static func execute_melee_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	# Check if should flee (low HP)
	if monster.should_flee():
		return flee_from_target(monster, player, all_combatants, game_manager)

	# In melee range - attack
	if distance <= 1.5:
		return attack_target(monster, player, game_manager)

	# Move toward player
	return move_toward_target(monster, player, all_combatants, game_manager)

## Ranged AI - Maintain distance and shoot
static func execute_ranged_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	var ideal_range = monster.ai_attack_range

	# Too close - back away
	if distance < ideal_range - 1:
		return flee_from_target(monster, player, all_combatants, game_manager)

	# In range - attack
	if distance <= ideal_range:
		return ranged_attack(monster, player, game_manager)

	# Too far - move closer
	return move_toward_target(monster, player, all_combatants, game_manager)

## Spellcaster AI - Cast spells from distance
static func execute_spellcaster_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	var spell_range = monster.ai_attack_range

	# In spell range - cast
	if distance <= spell_range:
		return cast_spell(monster, player, game_manager)

	# Too far - move closer
	return move_toward_target(monster, player, all_combatants, game_manager)

## Fleeing AI - Run away when damaged (Eyebot behavior)
static func execute_fleeing_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	# Always flee if HP is low
	if monster.current_hp < monster.max_hp * 0.5:
		return flee_from_target(monster, player, all_combatants, game_manager)

	# Otherwise attack if in range
	if distance <= monster.ai_attack_range:
		if monster.ai_attack_range == 1:
			return attack_target(monster, player, game_manager)
		else:
			return ranged_attack(monster, player, game_manager)

	# Move toward
	return move_toward_target(monster, player, all_combatants, game_manager)

## Support AI - Heal/buff allies, avoid combat
static func execute_support_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	# Look for wounded allies
	var wounded_ally = find_wounded_ally(monster, all_combatants)
	if wounded_ally:
		return heal_ally(monster, wounded_ally, game_manager)

	# Stay at safe distance
	var safe_distance = 4

	if distance < safe_distance:
		return flee_from_target(monster, player, all_combatants, game_manager)

	# Cast support spell or attack from range
	if distance <= monster.ai_attack_range:
		return cast_spell(monster, player, game_manager)

	return move_toward_target(monster, player, all_combatants, game_manager)

## Berserker AI - Aggressive melee, never retreat (Assaultron behavior)
static func execute_berserker_ai(monster: Monster, player: Node2D, all_combatants: Array, game_manager, distance: float) -> float:
	# Always charge toward enemy
	if distance <= 1.5:
		return attack_target(monster, player, game_manager)

	# Move toward, extra speed
	return move_toward_target(monster, player, all_combatants, game_manager) * 0.8

## Attack target in melee (from EnemyAI.java attackPlayer)
static func attack_target(monster: Monster, target: Node2D, game_manager) -> float:
	if monster.attack_target(target):
		return 1.0  # Normal attack speed
	return 0.5  # Failed attack

## Ranged attack
static func ranged_attack(monster: Monster, target: Node2D, game_manager) -> float:
	# Calculate damage with variance
	var variance = 0.8 + (randf() * 0.4)
	var damage = max(1.0, monster.attack_damage * variance * 0.8)  # Ranged slightly weaker

	# Deal damage
	if target.has_method("take_damage"):
		target.take_damage(damage, "physical")

		# Log combat
		if game_manager and game_manager.has_method("log_combat"):
			game_manager.log_combat("%s shoots for %.1f damage!" % [monster.monster_name, damage])

	return 1.0

## Cast spell
static func cast_spell(monster: Monster, target: Node2D, game_manager) -> float:
	# Determine spell based on monster type
	var spell_damage = monster.attack_damage * 1.2  # Spells hit harder

	# Apply magic damage
	if target.has_method("take_damage"):
		var damage_dealt = target.take_damage(spell_damage, "magic")

		# Log combat
		if game_manager and game_manager.has_method("log_combat"):
			game_manager.log_combat("%s casts a spell for %.1f damage!" % [monster.monster_name, damage_dealt])

	return 1.2  # Spells take longer

## Heal ally (support behavior)
static func heal_ally(monster: Monster, ally: Monster, game_manager) -> float:
	var heal_amount = monster.attack_damage * 1.5

	ally.current_hp = min(ally.max_hp, ally.current_hp + heal_amount)

	if game_manager and game_manager.has_method("log_combat"):
		game_manager.log_combat("%s heals %s for %.1f HP!" % [monster.monster_name, ally.monster_name, heal_amount])

	return 1.5  # Healing takes time

## Move toward target (from EnemyAI.java moveTowardPlayer)
static func move_toward_target(monster: Monster, target: Node2D, all_combatants: Array, game_manager) -> float:
	var current_pos = Vector2i(monster.grid_position.x, monster.grid_position.y)
	var target_pos = Vector2i(target.grid_position.x, target.grid_position.y)

	# Find best move using pathfinding
	var best_move = find_best_move(monster, current_pos, target_pos, all_combatants, game_manager)

	if best_move and best_move != current_pos:
		# Move to new position
		monster.move_to(best_move)

		if game_manager and game_manager.has_method("log_combat"):
			game_manager.log_combat("%s moves closer" % monster.monster_name)

		return 0.5  # Movement is quick
	else:
		# Can't move
		if game_manager and game_manager.has_method("log_combat"):
			game_manager.log_combat("%s cannot move" % monster.monster_name)

		return 0.5

## Flee from target
static func flee_from_target(monster: Monster, target: Node2D, all_combatants: Array, game_manager) -> float:
	var current_pos = Vector2i(monster.grid_position.x, monster.grid_position.y)
	var target_pos = Vector2i(target.grid_position.x, target.grid_position.y)

	# Find position farthest from target
	var best_move = find_flee_position(monster, current_pos, target_pos, all_combatants, game_manager)

	if best_move and best_move != current_pos:
		monster.move_to(best_move)

		if game_manager and game_manager.has_method("log_combat"):
			game_manager.log_combat("%s flees!" % monster.monster_name)

		return 0.5
	else:
		# Trapped - attack in desperation
		if calculate_distance(monster.grid_position.x, monster.grid_position.y, target.grid_position.x, target.grid_position.y) <= 1.5:
			return attack_target(monster, target, game_manager)

		return 0.5

## Find best move position using greedy algorithm (from EnemyAI.java findBestMovePosition)
static func find_best_move(monster: Monster, current: Vector2i, target: Vector2i, all_combatants: Array, game_manager) -> Vector2i:
	var best_pos = null
	var best_distance = INF

	# Check all 8 adjacent positions
	var directions = [
		Vector2i(-1, -1), Vector2i(0, -1), Vector2i(1, -1),
		Vector2i(-1, 0),                   Vector2i(1, 0),
		Vector2i(-1, 1),  Vector2i(0, 1),  Vector2i(1, 1)
	]

	for dir in directions:
		var candidate = current + dir

		# Check if valid position
		if not is_valid_position(monster, candidate, all_combatants, game_manager):
			continue

		# Calculate distance to target
		var distance = Vector2(candidate).distance_to(Vector2(target))

		# Pick closest to target
		if distance < best_distance:
			best_distance = distance
			best_pos = candidate

	return best_pos if best_pos else current

## Find flee position (opposite of find_best_move)
static func find_flee_position(monster: Monster, current: Vector2i, threat: Vector2i, all_combatants: Array, game_manager) -> Vector2i:
	var best_pos = null
	var best_distance = 0.0

	# Check all 8 adjacent positions
	var directions = [
		Vector2i(-1, -1), Vector2i(0, -1), Vector2i(1, -1),
		Vector2i(-1, 0),                   Vector2i(1, 0),
		Vector2i(-1, 1),  Vector2i(0, 1),  Vector2i(1, 1)
	]

	for dir in directions:
		var candidate = current + dir

		# Check if valid position
		if not is_valid_position(monster, candidate, all_combatants, game_manager):
			continue

		# Calculate distance from threat
		var distance = Vector2(candidate).distance_to(Vector2(threat))

		# Pick farthest from threat
		if distance > best_distance:
			best_distance = distance
			best_pos = candidate

	return best_pos if best_pos else current

## Check if position is valid for movement (from EnemyAI.java isValidPosition)
static func is_valid_position(monster: Monster, pos: Vector2i, all_combatants: Array, game_manager) -> bool:
	# Check if walkable
	if not monster.grid or not monster.grid.is_walkable(pos):
		return false

	# Check if occupied by another combatant
	var entity_at_pos = monster.grid.get_entity(pos)
	if entity_at_pos and entity_at_pos != monster:
		return false

	return true

## Calculate Manhattan distance
static func calculate_distance(x1: int, y1: int, x2: int, y2: int) -> float:
	return sqrt((x2 - x1) ** 2 + (y2 - y1) ** 2)

## Find wounded ally for support AI
static func find_wounded_ally(monster: Monster, all_combatants: Array) -> Monster:
	var most_wounded = null
	var lowest_hp_percent = 1.0

	for combatant in all_combatants:
		# Skip self and non-monsters
		if combatant == monster or not combatant is Monster:
			continue

		# Check HP percentage
		var hp_percent = combatant.current_hp / combatant.max_hp
		if hp_percent < 0.7 and hp_percent < lowest_hp_percent:
			lowest_hp_percent = hp_percent
			most_wounded = combatant

	return most_wounded

## A* pathfinding (for advanced AI - future enhancement)
static func find_path_astar(start: Vector2, goal: Vector2, game_manager) -> Array:
	# TODO: Implement proper A* pathfinding
	# For now, use simple greedy approach
	return []

## Check line of sight (for ranged attacks)
static func has_line_of_sight(monster: Monster, target: Node2D, game_manager) -> bool:
	# TODO: Implement raycasting for line of sight
	# For now, assume always visible
	return true

## Get all enemies in range
static func get_enemies_in_range(monster: Monster, all_combatants: Array, range: int) -> Array:
	var enemies = []

	for combatant in all_combatants:
		if combatant == monster:
			continue

		var distance = calculate_distance(
			monster.grid_position.x, monster.grid_position.y,
			combatant.grid_position.x if combatant.has("grid_position") else 0,
			combatant.grid_position.y if combatant.has("grid_position") else 0
		)

		if distance <= range:
			enemies.append(combatant)

	return enemies

## Evaluate threat level of target
static func evaluate_threat(monster: Monster, target: Node2D) -> float:
	var threat = 0.0

	# Base threat on damage potential
	if target.has("attack_damage"):
		threat += target.attack_damage

	# Higher threat if target has high HP
	if target.has("current_hp"):
		threat += target.current_hp * 0.1

	# Distance factor - closer = more threatening
	var target_pos_x = target.grid_position.x if target.has("grid_position") else 0
	var target_pos_y = target.grid_position.y if target.has("grid_position") else 0
	var distance = calculate_distance(
		monster.grid_position.x, monster.grid_position.y,
		target_pos_x, target_pos_y
	)
	threat += (10.0 - distance) * 2.0

	return threat

## Choose best target from multiple enemies
static func choose_best_target(monster: Monster, enemies: Array) -> Node2D:
	if enemies.is_empty():
		return null

	var best_target = null
	var highest_threat = 0.0

	for enemy in enemies:
		var threat = evaluate_threat(monster, enemy)
		if threat > highest_threat:
			highest_threat = threat
			best_target = enemy

	return best_target
