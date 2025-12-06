
extends Node2D
class_name Altar

# Altar system for god worship
# Based on DCSS altars - players can worship by interacting with them

# The god this altar belongs to
@export var god_type: God.Type = God.Type.NONE

# Visual properties
var altar_color: Color = Color.GRAY
var glow_intensity: float = 0.0
var glow_direction: float = 1.0

# Altar state
var discovered: bool = false
var player_nearby: bool = false

# Signals
signal altar_activated(god_type: God.Type)
signal player_entered_altar(god_type: God.Type)
signal player_left_altar(god_type: God.Type)

func _init():
	pass

func _ready():
	# Set up altar visuals based on god
	if god_type != God.Type.NONE:
		var god_data = God.get_god(god_type)
		altar_color = god_data.altar_color
		update_visuals()

func _process(delta: float):
	# Animate glow effect
	glow_intensity += glow_direction * delta * 0.5
	if glow_intensity >= 1.0:
		glow_direction = -1.0
		glow_intensity = 1.0
	elif glow_intensity <= 0.0:
		glow_direction = 1.0
		glow_intensity = 0.0

	if discovered:
		update_glow()

# Set the god for this altar
func set_god(new_god_type: God.Type):
	god_type = new_god_type
	if god_type != God.Type.NONE:
		var god_data = God.get_god(god_type)
		altar_color = god_data.altar_color
		update_visuals()

# Get the god data
func get_god_data() -> God.GodData:
	return God.get_god(god_type)

# Update altar visuals
func update_visuals():
	# This will be implemented with actual sprites/models
	# For now, just set modulate color
	modulate = altar_color

# Update glow animation
func update_glow():
	# Pulse the altar with god's color
	var glow_color = altar_color.lerp(Color.WHITE, glow_intensity * 0.3)
	modulate = glow_color

# Player interacts with altar
func interact(player_religion) -> bool:
	if god_type == God.Type.NONE:
		return false

	discovered = true
	var god_data = get_god_data()

	# Already worshipping this god
	if player_religion.current_god == god_type:
		show_god_info(player_religion)
		return true

	# Already worshipping another god
	if player_religion.current_god != God.Type.NONE:
		print("You already worship %s! You must abandon them first." % God.get_god(player_religion.current_god).display_name)
		return false

	# Has abandoned a god before
	if player_religion.has_abandoned:
		print("The gods remember your betrayal. None will accept you now.")
		return false

	# Join the god
	var success = player_religion.join_god(god_type)
	if success:
		altar_activated.emit(god_type)
		show_worship_message(god_data, player_religion)

	return success

# Show god information
func show_god_info(player_religion):
	var god_data = get_god_data()

	print("\n" + "=".repeat(50))
	print(god_data.display_name)
	print("=".repeat(50))
	print(god_data.description)
	print("")

	if god_data.uses_piety:
		var stars = God.get_piety_stars(player_religion.piety)
		print("Piety: %s %d/%d (Rank %d)" % [stars, player_religion.piety, God.MAX_PIETY, player_religion.get_piety_rank()])
		print("")

	# Show likes
	print("Likes:")
	for like in god_data.likes:
		print("  + %s" % like)

	# Show dislikes
	print("\nDislikes:")
	for dislike in god_data.dislikes:
		print("  - %s" % dislike)

	print("=".repeat(50) + "\n")

# Show worship message when joining
func show_worship_message(god_data: God.GodData, player_religion):
	print("\n" + "=".repeat(50))
	print("YOU NOW WORSHIP %s!" % god_data.display_name.to_upper())
	print("=".repeat(50))
	print(god_data.description)
	print("")

	# Show likes
	print("Likes:")
	for like in god_data.likes:
		print("  + %s" % like)

	# Show dislikes
	print("\nDislikes:")
	for dislike in god_data.dislikes:
		print("  - %s" % dislike)

	print("")
	if god_data.uses_piety:
		var stars = God.get_piety_stars(player_religion.piety)
		print("Piety: %s %d/%d" % [stars, player_religion.piety, God.MAX_PIETY])

	print("=".repeat(50) + "\n")

# Player entered altar area
func _on_area_entered(area):
	if area.is_in_group("player"):
		player_nearby = true
		player_entered_altar.emit(god_type)

		if not discovered:
			discovered = true
			var god_data = get_god_data()
			print("You discovered an altar to %s" % god_data.display_name)

# Player left altar area
func _on_area_left(area):
	if area.is_in_group("player"):
		player_nearby = false
		player_left_altar.emit(god_type)

# Static altar manager functions
class AltarManager:
	# Registry of all altars in the world
	static var altars: Dictionary = {}  # Vector2i -> Altar

	# Register an altar
	static func register_altar(position: Vector2i, altar: Altar):
		altars[position] = altar
		print("Registered altar to %s at %s" % [God.get_god(altar.god_type).display_name, position])

	# Get altar at position
	static func get_altar(position: Vector2i) -> Altar:
		return altars.get(position, null)

	# Check if position has altar
	static func has_altar(position: Vector2i) -> bool:
		return position in altars

	# Remove altar
	static func remove_altar(position: Vector2i):
		if position in altars:
			var altar = altars[position]
			print("Removed altar to %s at %s" % [God.get_god(altar.god_type).display_name, position])
			altars.erase(position)

	# Get all altars
	static func get_all_altars() -> Array:
		return altars.values()

	# Clear all altars
	static func clear_all_altars():
		altars.clear()
		print("Cleared all altars")

	# Place random altars in temple pattern
	static func create_temple(center_pos: Vector2i, count: int, parent_node: Node2D) -> Array[Altar]:
		var worshipable = God.get_worshipable_gods()
		worshipable.shuffle()

		var temple_altars: Array[Altar] = []
		var radius = 4

		for i in range(min(count, worshipable.size())):
			var angle = (2.0 * PI * i) / count
			var x = int(cos(angle) * radius)
			var z = int(sin(angle) * radius)

			var altar_pos = center_pos + Vector2i(x, z)
			var altar = create_altar(altar_pos, worshipable[i].type, parent_node)
			temple_altars.append(altar)

		print("Created temple with %d altars at %s" % [count, center_pos])
		return temple_altars

	# Create a single altar
	static func create_altar(position: Vector2i, god_type: God.Type, parent_node: Node2D) -> Altar:
		var altar = Altar.new()
		altar.set_god(god_type)
		altar.position = Vector2(position.x * 32, position.y * 32)  # Assuming 32x32 tiles

		# Add to scene
		if parent_node:
			parent_node.add_child(altar)

		# Register
		register_altar(position, altar)

		return altar
