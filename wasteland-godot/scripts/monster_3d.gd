extends Node3D
class_name Monster3D

## 3D monster with grid-based positioning
## Wraps the 2D Monster class with 3D visuals

var monster_data: Monster  # Reference to 2D monster data (stats, AI, etc.)
var grid_position: Vector2i
var grid: Grid

const TILE_SIZE = 2.0  # Must match DungeonManager3D.TILE_SIZE

# Smooth movement
var target_position: Vector3
var is_moving: bool = false
const MOVE_SPEED: float = 8.0  # Units per second

# Model mapping
const MODELS = {
	"giant spider": "res://models/monsters/spider.glb",
	"skeleton": "res://models/monsters/skeleton.glb",
	"bat": "res://models/monsters/bat.glb"
}

func initialize_with_monster(monster_ref: Monster, spawn_pos: Vector2i, grid_ref: Grid):
	"""Initialize with existing monster instance"""
	monster_data = monster_ref
	grid_position = spawn_pos
	grid = grid_ref

	# Set 3D position
	position = Vector3(
		spawn_pos.x * TILE_SIZE,
		0.5,  # Monster height above floor
		spawn_pos.y * TILE_SIZE
	)
	target_position = position  # Start at spawn position

	# Load and display 3D model
	load_model()

	# Connect to monster signals
	if monster_data.has_signal("died"):
		monster_data.died.connect(_on_monster_died)
	if monster_data.has_signal("monster_moved"):
		monster_data.monster_moved.connect(_on_monster_moved)

func load_model():
	"""Load the appropriate 3D model for this monster"""
	var monster_name = monster_data.monster_name.to_lower()

	# Get model path
	var model_path = MODELS.get(monster_name, "")

	if model_path != "" and ResourceLoader.exists(model_path):
		# Load GLTF model
		var scene = load(model_path)
		if scene:
			var model_instance = scene.instantiate()
			add_child(model_instance)

			# Scale down models (they're often too big)
			model_instance.scale = Vector3(0.5, 0.5, 0.5)

			# Bats need special handling
			if monster_name == "bat":
				model_instance.scale = Vector3(0.3, 0.3, 0.3)  # Scale bats smaller
				model_instance.position.y = 1.0  # Raise bats above ground

			print("Loaded 3D model for %s: %s" % [monster_name, model_path])
	else:
		# Fallback: Simple capsule mesh
		create_placeholder_mesh(monster_name)

func create_placeholder_mesh(monster_name: String):
	"""Create a simple colored capsule as fallback"""
	var mesh_instance = MeshInstance3D.new()
	var capsule = CapsuleMesh.new()
	capsule.radius = 0.8  # MUCH LARGER - was 0.3
	capsule.height = 2.5  # MUCH TALLER - was 1.2

	# Different colors for different monsters - BRIGHT colors
	var material = StandardMaterial3D.new()
	material.emission_enabled = true  # Make them glow!

	match monster_name:
		"giant spider":
			material.albedo_color = Color(0.6, 0.3, 0.1)  # Brown
			material.emission = Color(0.4, 0.2, 0.0)  # Brown glow
		"skeleton":
			material.albedo_color = Color(1.0, 1.0, 1.0)  # Bright white
			material.emission = Color(0.8, 0.8, 0.8)  # White glow
		"bat":
			material.albedo_color = Color(0.3, 0.3, 0.3)  # Gray
			material.emission = Color(0.2, 0.2, 0.2)  # Gray glow
		_:
			material.albedo_color = Color(1.0, 0.0, 0.0)  # Bright red
			material.emission = Color(0.8, 0.0, 0.0)  # Red glow

	capsule.material = material
	mesh_instance.mesh = capsule
	add_child(mesh_instance)

	# Add a point light so they're REALLY visible
	var light = OmniLight3D.new()
	light.light_energy = 2.0
	light.omni_range = 5.0
	light.light_color = material.albedo_color
	light.position = Vector3(0, 1, 0)
	add_child(light)

	print("Created LARGE glowing placeholder mesh for %s" % monster_name)

func _process(delta: float):
	"""Smooth movement interpolation"""
	if is_moving:
		# Smoothly interpolate to target position
		position = position.move_toward(target_position, MOVE_SPEED * delta)

		# Stop moving when close enough
		if position.distance_to(target_position) < 0.01:
			position = target_position
			is_moving = false

func _on_monster_moved(from: Vector2i, to: Vector2i):
	"""Called when monster moves - update 3D position"""
	grid_position = to

	# Set target position for smooth movement
	target_position = Vector3(
		to.x * TILE_SIZE,
		0.5,  # Monster height above floor
		to.y * TILE_SIZE
	)

	is_moving = true
	print("%s moved from %s to %s" % [monster_data.monster_name, from, to])

func _on_monster_died(_corpse_position: Vector2i):
	"""Called when monster dies"""
	print("%s died, removing 3D model" % monster_data.monster_name)
	# Play death animation/effect here later
	queue_free()

func get_monster_data() -> Monster:
	"""Get the underlying monster data"""
	return monster_data
