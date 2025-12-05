extends Node2D

## Main game controller
## Manages turn-based gameplay, rendering, and input

@onready var grid: Grid = $Grid
@onready var player: Player = $Player
@onready var equipment_screen: EquipmentScreen = $EquipmentScreen

var font: Font

func _ready():
	# Load IBM Plex Mono font
	font = load("res://assets/fonts/IBMPlexMono-Regular.otf")

	# Initialize game
	grid.initialize_map()

	# Generate dungeon
	var spawn_pos = DungeonGenerator.generate(grid, 10)

	# Spawn player
	player.set_grid(grid)
	player.spawn_at(spawn_pos)

	# Give player starter equipment
	_give_starter_equipment()

	# Center camera on player
	$Camera2D.position = grid.grid_to_world(spawn_pos) + Vector2(Grid.TILE_SIZE / 2, Grid.TILE_SIZE / 2)

	# Setup equipment screen
	equipment_screen.hide()

	print("Wasteland Crawl - Godot Edition")
	print("Use arrow keys or numpad to move")
	print("Press 'I' for equipment screen")

func _give_starter_equipment():
	"""Give player starting equipment"""
	# Starter weapon
	var shiv = WastelandWeapon.new(WastelandWeapon.WeaponType.SHIV, WastelandItem.ItemRarity.COMMON, 0)
	player.add_to_inventory(shiv)

	# Starter armor
	var robe = WastelandArmor.new(WastelandArmor.ArmorType.ROBE, WastelandItem.ItemRarity.COMMON, 0)
	player.add_to_inventory(robe)

	# Some extra items to test with
	var knife = WastelandWeapon.new(WastelandWeapon.WeaponType.COMBAT_KNIFE, WastelandItem.ItemRarity.UNCOMMON, 1)
	player.add_to_inventory(knife)

	var leather = WastelandArmor.new(WastelandArmor.ArmorType.LEATHER_ARMOR, WastelandItem.ItemRarity.COMMON, 0)
	player.add_to_inventory(leather)

	var helmet = WastelandArmor.new(WastelandArmor.ArmorType.HELMET, WastelandItem.ItemRarity.COMMON, 0)
	player.add_to_inventory(helmet)

	print("Starter equipment added to inventory")

func _draw():
	"""Render the dungeon and entities"""
	# Draw tiles
	for y in range(grid.MAP_HEIGHT):
		for x in range(grid.MAP_WIDTH):
			var pos = Vector2i(x, y)
			var world_pos = grid.grid_to_world(pos)
			var tile_type = grid.get_tile(pos)

			# Choose color and character based on tile type
			var color = Color.WHITE
			var character = " "

			match tile_type:
				Grid.TileType.FLOOR:
					color = Color.DIM_GRAY
					character = "."
				Grid.TileType.WALL:
					color = Color.GRAY
					character = "#"
				Grid.TileType.WATER:
					color = Color.BLUE
					character = "~"
				Grid.TileType.DOOR_CLOSED:
					color = Color.SADDLE_BROWN
					character = "+"
				Grid.TileType.DOOR_OPEN:
					color = Color.SADDLE_BROWN
					character = "-"
				Grid.TileType.STAIRS_DOWN:
					color = Color.YELLOW
					character = ">"
				Grid.TileType.STAIRS_UP:
					color = Color.YELLOW
					character = "<"

			# Draw tile background
			if tile_type == Grid.TileType.FLOOR:
				draw_rect(Rect2(world_pos, Vector2(Grid.TILE_SIZE, Grid.TILE_SIZE)), Color(0.1, 0.1, 0.1))

			# Draw tile character
			if font:
				draw_string(font, world_pos + Vector2(8, 24), character, HORIZONTAL_ALIGNMENT_LEFT, -1, 20, color)

	# Draw player
	var player_world_pos = grid.grid_to_world(player.grid_position)
	if font:
		draw_string(font, player_world_pos + Vector2(8, 24), "@", HORIZONTAL_ALIGNMENT_LEFT, -1, 20, Color.WHITE)

	# Draw HUD
	_draw_hud()

func _draw_hud():
	"""Draw UI overlay with player stats"""
	var hud_x = 10
	var hud_y = grid.MAP_HEIGHT * Grid.TILE_SIZE + 20

	if font:
		# HP bar
		var hp_text = "HP: %d / %d" % [player.current_hp, player.max_hp]
		draw_string(font, Vector2(hud_x, hud_y), hp_text, HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.RED)

		# MP bar
		var mp_text = "MP: %d / %d" % [player.current_mp, player.max_mp]
		draw_string(font, Vector2(hud_x + 150, hud_y), mp_text, HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.CYAN)

		# Level
		var level_text = "Level: %d" % player.level
		draw_string(font, Vector2(hud_x + 300, hud_y), level_text, HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.YELLOW)

		# Race
		var race_text = "Race: %s" % player.race
		draw_string(font, Vector2(hud_x + 450, hud_y), race_text, HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.WHITE)

func _process(_delta):
	queue_redraw()  # Redraw every frame

func _input(event):
	"""Handle turn-based input"""
	if event is InputEventKey and event.pressed:
		var direction = Vector2i.ZERO

		# Arrow keys and numpad movement
		match event.keycode:
			KEY_UP, KEY_KP_8:
				direction = Vector2i(0, -1)
			KEY_DOWN, KEY_KP_2:
				direction = Vector2i(0, 1)
			KEY_LEFT, KEY_KP_4:
				direction = Vector2i(-1, 0)
			KEY_RIGHT, KEY_KP_6:
				direction = Vector2i(1, 0)
			KEY_KP_7:  # Diagonal up-left
				direction = Vector2i(-1, -1)
			KEY_KP_9:  # Diagonal up-right
				direction = Vector2i(1, -1)
			KEY_KP_1:  # Diagonal down-left
				direction = Vector2i(-1, 1)
			KEY_KP_3:  # Diagonal down-right
				direction = Vector2i(1, 1)
			KEY_I:
				equipment_screen.open(player)
			KEY_M:
				print("Skills screen - not yet implemented")
			KEY_Z:
				print("Cast spell - not yet implemented")
			KEY_ESCAPE:
				get_tree().quit()

		if direction != Vector2i.ZERO:
			if player.try_move(direction):
				# Center camera on player
				$Camera2D.position = grid.grid_to_world(player.grid_position) + Vector2(Grid.TILE_SIZE / 2, Grid.TILE_SIZE / 2)
