extends PanelContainer
class_name InventoryPanel

## DCSS-style inventory panel
## Shows player's items with hotkeys

@onready var item_list: VBoxContainer = $MarginContainer/VBoxContainer/ItemList
@onready var title_label: Label = $MarginContainer/VBoxContainer/TitleLabel

var inventory = null  # Inventory instance

func _ready():
	custom_minimum_size = Vector2(400, 500)

	# Set up background
	var panel = StyleBoxFlat.new()
	panel.bg_color = Color(0.1, 0.1, 0.1, 0.95)
	panel.border_color = Color(0.6, 0.6, 0.6, 1.0)
	panel.set_border_width_all(2)
	add_theme_stylebox_override("panel", panel)

	# Position in center of screen
	anchor_left = 0.3
	anchor_right = 0.7
	anchor_top = 0.1
	anchor_bottom = 0.9

	if title_label:
		title_label.text = "INVENTORY - Press 'i' to close"
		title_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER

func update_inventory(inv):
	"""Update inventory display"""
	inventory = inv

	# Clear existing items
	if item_list:
		for child in item_list.get_children():
			child.queue_free()

	if not inventory:
		return

	# Add items
	var hotkey_index = 0
	var hotkeys = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
	               "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]

	for item in inventory.get_items():
		var hotkey = hotkeys[hotkey_index] if hotkey_index < hotkeys.size() else "?"
		hotkey_index += 1

		var item_label = Label.new()
		var equipped_str = " (equipped)" if item.get("equipped", false) else ""
		item_label.text = "%s) %s%s" % [hotkey, item.get("name", "Unknown Item"), equipped_str]

		# Color equipped items differently
		if item.get("equipped", false):
			item_label.add_theme_color_override("font_color", Color(0.5, 1.0, 0.5))
		else:
			item_label.add_theme_color_override("font_color", Color(0.9, 0.9, 0.9))

		item_list.add_child(item_label)

	# Add "no items" message if empty
	if inventory.get_items().is_empty():
		var empty_label = Label.new()
		empty_label.text = "(No items)"
		empty_label.add_theme_color_override("font_color", Color(0.5, 0.5, 0.5))
		item_list.add_child(empty_label)
