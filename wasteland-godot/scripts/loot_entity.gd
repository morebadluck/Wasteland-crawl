extends Node2D
class_name LootEntity

## Loot entity that sits on the ground
## Players can walk over it to automatically pick it up (DCSS-style)

var grid_position: Vector2i = Vector2i(0, 0)
var grid: Grid = null
var item: WastelandItem = null
var display_char: String = "?"
var display_color: Color = Color.WHITE

func _ready():
	add_to_group("loot")

## Spawn loot at grid position
func spawn_at(pos: Vector2i, new_grid: Grid, new_item: WastelandItem):
	grid_position = pos
	grid = new_grid
	item = new_item
	position = grid.grid_to_world(pos)

	# Set display based on item type
	if item is WastelandWeapon:
		display_char = ")"  # DCSS weapon
		display_color = item.get_rarity_color()
	elif item is WastelandArmor:
		display_char = "["  # DCSS armor
		display_color = item.get_rarity_color()
	else:
		display_char = "%"  # Generic item
		display_color = item.get_rarity_color()

	# Register with grid (but don't block movement)
	grid.add_loot(pos, self)

	print("Loot spawned: %s at %s" % [item.item_name, pos])

## Try to pick up loot
func try_pickup(player: Player) -> bool:
	if player.add_to_inventory(item):
		print("Picked up: %s" % item.item_name)

		# Remove from grid
		if grid:
			grid.remove_loot(grid_position)

		# Remove from scene
		queue_free()
		return true
	else:
		print("Inventory full!")
		return false
