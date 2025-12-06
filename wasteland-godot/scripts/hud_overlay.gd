extends CanvasLayer
class_name HUDOverlay

## DCSS-style HUD overlay for 3D mode
## Displays minimap, HP/MP, messages, inventory, and monster list

# References
@onready var minimap = $Minimap
@onready var hp_bar: ProgressBar = $StatsPanel/HPBar
@onready var hp_label: Label = $StatsPanel/HPLabel
@onready var mp_bar: ProgressBar = $StatsPanel/MPBar
@onready var mp_label: Label = $StatsPanel/MPLabel
@onready var message_log = $MessageLog
@onready var inventory_panel = $InventoryPanel
@onready var monster_list = $MonsterList
@onready var status_container: HBoxContainer = $StatusEffects

var player = null  # Player instance
var dungeon_manager = null  # DungeonManager3D instance

func _ready():
	# Hide inventory by default
	if inventory_panel:
		inventory_panel.visible = false

	print("HUD Overlay initialized")

func initialize(player_ref, dungeon_ref):
	"""Initialize HUD with player and dungeon references"""
	player = player_ref
	dungeon_manager = dungeon_ref

	# Connect player signals
	if player:
		if player.has_signal("hp_changed"):
			player.hp_changed.connect(_on_player_hp_changed)
		if player.has_signal("mp_changed"):
			player.mp_changed.connect(_on_player_mp_changed)

	# Initialize sub-components
	if minimap and dungeon_manager:
		minimap.initialize(dungeon_manager.get_grid(), player)

	if message_log:
		message_log.add_message("Welcome to the Wasteland!")

	# Update initial display
	_update_hp_mp()

	print("HUD initialized with player and dungeon")

func _process(_delta):
	# Update minimap every frame
	if minimap and player:
		minimap.update_player_position(player.grid_position)

func _input(event):
	# Toggle inventory with 'i' key
	if event.is_action_pressed("ui_inventory"):
		toggle_inventory()

func _update_hp_mp():
	"""Update HP/MP bars and labels"""
	if not player:
		return

	# Update HP
	if hp_bar:
		hp_bar.max_value = player.max_hp
		hp_bar.value = player.current_hp

	if hp_label:
		hp_label.text = "HP: %d/%d" % [player.current_hp, player.max_hp]

	# Update MP
	if mp_bar:
		mp_bar.max_value = player.max_mp
		mp_bar.value = player.current_mp

	if mp_label:
		mp_label.text = "MP: %d/%d" % [player.current_mp, player.max_mp]

func _on_player_hp_changed(new_hp: float, max_hp: float):
	"""Called when player HP changes"""
	if hp_bar:
		hp_bar.max_value = max_hp
		hp_bar.value = new_hp

	if hp_label:
		hp_label.text = "HP: %d/%d" % [new_hp, max_hp]

func _on_player_mp_changed(new_mp: float, max_mp: float):
	"""Called when player MP changes"""
	if mp_bar:
		mp_bar.max_value = max_mp
		mp_bar.value = new_mp

	if mp_label:
		mp_label.text = "MP: %d/%d" % [new_mp, max_mp]

func add_message(text: String):
	"""Add message to the message log"""
	if message_log:
		message_log.add_message(text)

func toggle_inventory():
	"""Toggle inventory panel visibility"""
	if inventory_panel:
		inventory_panel.visible = not inventory_panel.visible
		if inventory_panel.visible and player:
			inventory_panel.update_inventory(player.inventory)

func update_minimap(position: Vector2i):
	"""Update minimap with player position"""
	if minimap:
		minimap.update_player_position(position)

func update_monster_list(monsters: Array):
	"""Update the monster detection list"""
	if monster_list:
		monster_list.update_monsters(monsters)

func add_status_effect(effect_name: String, icon_texture: Texture2D):
	"""Add a status effect icon"""
	if status_container:
		var icon = TextureRect.new()
		icon.texture = icon_texture
		icon.custom_minimum_size = Vector2(32, 32)
		icon.name = effect_name
		status_container.add_child(icon)

func remove_status_effect(effect_name: String):
	"""Remove a status effect icon"""
	if status_container:
		var icon = status_container.get_node_or_null(effect_name)
		if icon:
			icon.queue_free()
