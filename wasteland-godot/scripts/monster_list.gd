extends VBoxContainer
class_name MonsterList

## DCSS-style monster detection list
## Shows nearby visible monsters

@onready var title_label: Label = $TitleLabel
@onready var monster_container: VBoxContainer = $MonsterContainer

const MAX_DISPLAY = 8  # Max monsters to show

func _ready():
	custom_minimum_size = Vector2(250, 300)

	# Set up background
	var panel = StyleBoxFlat.new()
	panel.bg_color = Color(0.0, 0.0, 0.0, 0.7)
	panel.border_color = Color(0.5, 0.5, 0.5, 1.0)
	panel.set_border_width_all(1)
	add_theme_stylebox_override("panel", panel)

	if title_label:
		title_label.text = "MONSTERS DETECTED"
		title_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER

func update_monsters(monsters: Array):
	"""Update monster list"""
	# Clear existing
	if monster_container:
		for child in monster_container.get_children():
			child.queue_free()

	if monsters.is_empty():
		var label = Label.new()
		label.text = "(No monsters nearby)"
		label.add_theme_color_override("font_color", Color(0.5, 0.5, 0.5))
		monster_container.add_child(label)
		return

	# Sort by distance (if monsters have position)
	var sorted_monsters = monsters.duplicate()
	# TODO: Sort by distance from player

	# Add monster labels
	var count = 0
	for monster in sorted_monsters:
		if count >= MAX_DISPLAY:
			break

		var label = Label.new()
		var monster_name = monster.monster_name if monster.get("monster_name") != null else "Unknown"
		var hp_percent = int((float(monster.current_hp) / float(monster.max_hp)) * 100.0) if monster.get("current_hp") != null else 100

		label.text = "%s (%d%%)" % [monster_name, hp_percent]

		# Color by HP
		if hp_percent > 75:
			label.add_theme_color_override("font_color", Color(0.5, 1.0, 0.5))  # Green
		elif hp_percent > 35:
			label.add_theme_color_override("font_color", Color(1.0, 1.0, 0.5))  # Yellow
		else:
			label.add_theme_color_override("font_color", Color(1.0, 0.5, 0.5))  # Red

		monster_container.add_child(label)
		count += 1

	# Add "and more" if needed
	if sorted_monsters.size() > MAX_DISPLAY:
		var more_label = Label.new()
		more_label.text = "... and %d more" % (sorted_monsters.size() - MAX_DISPLAY)
		more_label.add_theme_color_override("font_color", Color(0.7, 0.7, 0.7))
		monster_container.add_child(more_label)
