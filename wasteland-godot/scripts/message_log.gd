extends VBoxContainer
class_name MessageLog

## DCSS-style message log
## Displays combat messages and game events

const MAX_MESSAGES = 5
const MESSAGE_FADE_TIME = 10.0  # Seconds before message fades

var messages: Array[Dictionary] = []

func _ready():
	custom_minimum_size = Vector2(600, 120)

	# Set up background
	var panel = StyleBoxFlat.new()
	panel.bg_color = Color(0.0, 0.0, 0.0, 0.7)
	panel.border_color = Color(0.5, 0.5, 0.5, 1.0)
	panel.set_border_width_all(1)
	add_theme_stylebox_override("panel", panel)

func add_message(text: String, color: Color = Color.WHITE):
	"""Add a message to the log"""
	var message = {
		"text": text,
		"color": color,
		"time": Time.get_ticks_msec() / 1000.0
	}

	messages.append(message)

	# Keep only recent messages
	if messages.size() > MAX_MESSAGES:
		messages.pop_front()

	_update_display()

	print("[MESSAGE] %s" % text)

func _update_display():
	"""Update the visual display of messages"""
	# Clear existing labels
	for child in get_children():
		child.queue_free()

	# Add message labels
	for msg in messages:
		var label = Label.new()
		label.text = msg["text"]
		label.add_theme_color_override("font_color", msg["color"])
		label.autowrap_mode = TextServer.AUTOWRAP_WORD_SMART
		add_child(label)

func _process(_delta):
	# Fade old messages
	var current_time = Time.get_ticks_msec() / 1000.0
	var changed = false

	for i in range(messages.size() - 1, -1, -1):
		if current_time - messages[i]["time"] > MESSAGE_FADE_TIME:
			messages.remove_at(i)
			changed = true

	if changed:
		_update_display()
