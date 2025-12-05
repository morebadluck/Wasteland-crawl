extends Control
class_name EquipmentScreen

## Equipment screen with proper slot validation
## THIS ONE ACTUALLY WORKS! (Unlike the Minecraft version)

var player: Player
var font: Font

# UI Layout constants
const SLOT_SIZE = 32
const SLOT_SPACING = 40
const EQUIPMENT_COL1_X = 20
const EQUIPMENT_COL2_X = 250
const EQUIPMENT_Y = 60
const STATS_PANEL_X = 480
const STATS_PANEL_Y = 60
const INVENTORY_PANEL_X = 20
const INVENTORY_PANEL_Y = 350
const INVENTORY_COLS = 13
const INVENTORY_ROWS = 4

# Slot colors
const SLOT_BG_COLOR = Color(0.3, 0.3, 0.3)
const SLOT_HIGHLIGHT_COLOR = Color(0.5, 0.5, 0.5)
const SLOT_VALID_COLOR = Color(0.2, 0.6, 0.2)
const SLOT_INVALID_COLOR = Color(0.6, 0.2, 0.2)

# State
var held_item: WastelandItem = null
var hovered_equipment_slot: Player.EquipSlot = -1
var hovered_inventory_index: int = -1

func _ready():
	font = load("res://assets/fonts/IBMPlexMono-Regular.otf")
	set_process_input(true)
	mouse_filter = Control.MOUSE_FILTER_STOP  # Capture mouse events
	focus_mode = Control.FOCUS_ALL  # Allow focus

func open(p: Player):
	"""Open equipment screen for player"""
	player = p
	show()
	set_process_input(true)
	# Don't need grab_focus() with proper mouse_filter

func _draw():
	if not player:
		return

	# Dark background
	draw_rect(Rect2(0, 0, get_viewport_rect().size.x, get_viewport_rect().size.y), Color(0.1, 0.1, 0.1))

	# Title
	draw_string(font, Vector2(300, 30), "EQUIPMENT (Press I to close)", HORIZONTAL_ALIGNMENT_LEFT, -1, 24, Color.YELLOW)

	# Equipment slots - Two columns
	_draw_equipment_slots()

	# Stats panel
	_draw_stats_panel()

	# Inventory
	_draw_inventory()

	# Held item (follows cursor)
	if held_item:
		var mouse_pos = get_local_mouse_position()
		_draw_item(held_item, mouse_pos - Vector2(SLOT_SIZE/2, SLOT_SIZE/2), Color.WHITE, true)

func _draw_equipment_slots():
	"""Draw all 10 equipment slots in two columns"""
	# Column 1: Weapon, Body, Helmet, Cloak, Boots
	var column1 = [
		Player.EquipSlot.WEAPON,
		Player.EquipSlot.BODY,
		Player.EquipSlot.HELMET,
		Player.EquipSlot.CLOAK,
		Player.EquipSlot.BOOTS
	]

	# Column 2: Offhand, Gloves, Amulet, Left Ring, Right Ring
	var column2 = [
		Player.EquipSlot.OFFHAND,
		Player.EquipSlot.GLOVES,
		Player.EquipSlot.AMULET,
		Player.EquipSlot.LEFT_RING,
		Player.EquipSlot.RIGHT_RING
	]

	# Draw column 1
	var y = EQUIPMENT_Y
	for slot in column1:
		_draw_equipment_slot(slot, EQUIPMENT_COL1_X, y)
		y += SLOT_SPACING

	# Draw column 2
	y = EQUIPMENT_Y
	for slot in column2:
		_draw_equipment_slot(slot, EQUIPMENT_COL2_X, y)
		y += SLOT_SPACING

func _draw_equipment_slot(slot: Player.EquipSlot, x: int, y: int):
	"""Draw a single equipment slot"""
	var slot_rect = Rect2(x, y, SLOT_SIZE, SLOT_SIZE)

	# Determine slot color
	var color = SLOT_BG_COLOR
	if hovered_equipment_slot == slot:
		if held_item:
			# Show valid/invalid color when holding an item
			if player.can_equip_to_slot(held_item, slot):
				color = SLOT_VALID_COLOR
			else:
				color = SLOT_INVALID_COLOR
		else:
			color = SLOT_HIGHLIGHT_COLOR

	# Draw slot background
	draw_rect(slot_rect, color)
	draw_rect(slot_rect, Color.WHITE, false, 1)  # Border

	# Draw slot label
	var slot_names = ["Weapon (a)", "Offhand (b)", "Body (c)", "Helmet (d)",
					  "Cloak (e)", "Gloves (f)", "Boots (g)", "Amulet (h)",
					  "L.Ring (i)", "R.Ring (j)"]
	draw_string(font, Vector2(x + SLOT_SIZE + 5, y + 20), slot_names[slot],
				HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)

	# Draw equipped item
	var item = player.get_equipped(slot)
	if item:
		_draw_item(item, Vector2(x, y), Color.WHITE, false)

func _draw_item(item: WastelandItem, pos: Vector2, tint: Color, show_tooltip: bool):
	"""Draw an item icon and optionally tooltip"""
	# Draw item representation (for now, just text)
	var item_char = "?"
	if item is WastelandWeapon:
		item_char = ")"  # DCSS weapon character
	elif item is WastelandArmor:
		item_char = "["  # DCSS armor character

	var item_color = item.get_rarity_color() * tint
	draw_string(font, pos + Vector2(8, 24), item_char, HORIZONTAL_ALIGNMENT_LEFT, -1, 24, item_color)

	# Tooltip on hover
	if show_tooltip:
		_draw_tooltip(item, pos + Vector2(SLOT_SIZE + 5, 0))

func _draw_tooltip(item: WastelandItem, pos: Vector2):
	"""Draw item tooltip"""
	var lines = item.get_tooltip()
	var line_height = 16
	var padding = 5

	# Calculate tooltip size
	var max_width = 200
	var height = lines.size() * line_height + padding * 2

	# Draw tooltip background
	draw_rect(Rect2(pos.x, pos.y, max_width, height), Color(0.1, 0.1, 0.1, 0.9))
	draw_rect(Rect2(pos.x, pos.y, max_width, height), Color.WHITE, false, 1)

	# Draw tooltip text
	var y = pos.y + padding + line_height
	for line in lines:
		draw_string(font, Vector2(pos.x + padding, y), line, HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)
		y += line_height

func _draw_stats_panel():
	"""Draw character stats"""
	var x = STATS_PANEL_X
	var y = STATS_PANEL_Y

	draw_string(font, Vector2(x, y), "CHARACTER STATS", HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.YELLOW)
	y += 25

	# Calculate total stats from equipment
	var total_ac = 0
	var total_ev = 10  # Base EV
	var total_damage = 0

	for slot in Player.EquipSlot.values():
		var item = player.get_equipped(slot)
		if not item:
			continue

		if item is WastelandArmor:
			var armor = item as WastelandArmor
			total_ac += armor.calculate_ac(player)
			total_ev += armor.calculate_ev_modifier(player)
		elif item is WastelandWeapon:
			var weapon = item as WastelandWeapon
			total_damage = weapon.calculate_damage(player)

	# Display stats
	draw_string(font, Vector2(x, y), "HP: %d / %d" % [player.current_hp, player.max_hp],
				HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.RED)
	y += 18

	draw_string(font, Vector2(x, y), "MP: %d / %d" % [player.current_mp, player.max_mp],
				HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.CYAN)
	y += 18

	draw_string(font, Vector2(x, y), "AC: %d" % total_ac,
				HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)
	y += 18

	draw_string(font, Vector2(x, y), "EV: %d" % total_ev,
				HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)
	y += 18

	if total_damage > 0:
		draw_string(font, Vector2(x, y), "Damage: %d" % total_damage,
					HORIZONTAL_ALIGNMENT_LEFT, -1, 14, Color.WHITE)
		y += 18

func _draw_inventory():
	"""Draw inventory grid"""
	draw_string(font, Vector2(INVENTORY_PANEL_X, INVENTORY_PANEL_Y - 20),
				"INVENTORY (%d/%d)" % [player.inventory.size(), player.MAX_INVENTORY],
				HORIZONTAL_ALIGNMENT_LEFT, -1, 16, Color.YELLOW)

	for row in range(INVENTORY_ROWS):
		for col in range(INVENTORY_COLS):
			var slot_x = INVENTORY_PANEL_X + col * SLOT_SPACING
			var slot_y = INVENTORY_PANEL_Y + row * SLOT_SPACING
			var slot_rect = Rect2(slot_x, slot_y, SLOT_SIZE, SLOT_SIZE)

			var inv_index = row * INVENTORY_COLS + col

			# Slot color
			var color = SLOT_BG_COLOR
			if hovered_inventory_index == inv_index:
				color = SLOT_HIGHLIGHT_COLOR

			# Draw slot
			draw_rect(slot_rect, color)
			draw_rect(slot_rect, Color.WHITE, false, 1)

			# Draw item if present
			if inv_index < player.inventory.size():
				var item = player.inventory[inv_index]
				_draw_item(item, Vector2(slot_x, slot_y), Color.WHITE, false)

func _input(event):
	if not is_visible_in_tree():
		return

	# Close on I or Escape
	if event is InputEventKey and event.pressed:
		if event.keycode == KEY_I or event.keycode == KEY_ESCAPE:
			close()
			accept_event()

	# Mouse hover and click
	if event is InputEventMouse:
		var mouse_pos = get_local_mouse_position()
		_update_hover(mouse_pos)

		if event is InputEventMouseButton and event.pressed and event.button_index == MOUSE_BUTTON_LEFT:
			_handle_click(mouse_pos)
			queue_redraw()

func _update_hover(mouse_pos: Vector2):
	"""Update hovered slot based on mouse position"""
	hovered_equipment_slot = -1
	hovered_inventory_index = -1

	# Check equipment slots - Column 1
	var y = EQUIPMENT_Y
	var column1 = [Player.EquipSlot.WEAPON, Player.EquipSlot.BODY, Player.EquipSlot.HELMET,
				   Player.EquipSlot.CLOAK, Player.EquipSlot.BOOTS]
	for slot in column1:
		if Rect2(EQUIPMENT_COL1_X, y, SLOT_SIZE, SLOT_SIZE).has_point(mouse_pos):
			hovered_equipment_slot = slot
			queue_redraw()
			return
		y += SLOT_SPACING

	# Check equipment slots - Column 2
	y = EQUIPMENT_Y
	var column2 = [Player.EquipSlot.OFFHAND, Player.EquipSlot.GLOVES, Player.EquipSlot.AMULET,
				   Player.EquipSlot.LEFT_RING, Player.EquipSlot.RIGHT_RING]
	for slot in column2:
		if Rect2(EQUIPMENT_COL2_X, y, SLOT_SIZE, SLOT_SIZE).has_point(mouse_pos):
			hovered_equipment_slot = slot
			queue_redraw()
			return
		y += SLOT_SPACING

	# Check inventory slots
	for row in range(INVENTORY_ROWS):
		for col in range(INVENTORY_COLS):
			var slot_x = INVENTORY_PANEL_X + col * SLOT_SPACING
			var slot_y = INVENTORY_PANEL_Y + row * SLOT_SPACING
			if Rect2(slot_x, slot_y, SLOT_SIZE, SLOT_SIZE).has_point(mouse_pos):
				hovered_inventory_index = row * INVENTORY_COLS + col
				queue_redraw()
				return

func _handle_click(mouse_pos: Vector2):
	"""Handle mouse click"""
	# Click on equipment slot
	if hovered_equipment_slot >= 0:
		_click_equipment_slot(hovered_equipment_slot)
		return

	# Click on inventory
	if hovered_inventory_index >= 0:
		_click_inventory_slot(hovered_inventory_index)
		return

func _click_equipment_slot(slot: Player.EquipSlot):
	"""Click on an equipment slot"""
	if held_item:
		# Try to equip held item
		if player.can_equip_to_slot(held_item, slot):
			var old_item = player.equip_item(slot, held_item)
			held_item = old_item  # Swap
			print("Equipped to slot: ", slot)
		else:
			print("Cannot equip to that slot!")
	else:
		# Pick up equipped item
		held_item = player.unequip_item(slot)
		if held_item:
			print("Picked up from slot: ", slot)

func _click_inventory_slot(index: int):
	"""Click on an inventory slot"""
	if held_item:
		# Put held item in inventory
		if index < player.inventory.size():
			# Swap with existing item
			var temp = player.inventory[index]
			player.inventory[index] = held_item
			held_item = temp
		else:
			# Place in empty slot
			player.inventory.append(held_item)
			held_item = null
	else:
		# Pick up item from inventory
		if index < player.inventory.size():
			held_item = player.inventory[index]
			player.inventory.remove_at(index)
			print("Picked up from inventory")

func close():
	"""Close equipment screen"""
	# Return held item to inventory
	if held_item:
		player.add_to_inventory(held_item)
		held_item = null
	hide()

func _process(_delta):
	if is_visible_in_tree():
		queue_redraw()
