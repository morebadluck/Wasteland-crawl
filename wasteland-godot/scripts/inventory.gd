extends RefCounted
class_name Inventory

## Simple inventory system for managing items

signal item_added(item)
signal item_removed(item)

var items: Array = []
const MAX_ITEMS = 52  # DCSS-style

func add_item(item: Dictionary) -> bool:
	"""Add item to inventory"""
	if items.size() >= MAX_ITEMS:
		return false

	items.append(item)
	item_added.emit(item)
	return true

func remove_item(index: int) -> bool:
	"""Remove item at index"""
	if index < 0 or index >= items.size():
		return false

	var item = items[index]
	items.remove_at(index)
	item_removed.emit(item)
	return true

func get_items() -> Array:
	"""Get all items"""
	return items

func get_item(index: int):
	"""Get item at index"""
	if index < 0 or index >= items.size():
		return null
	return items[index]

func has_space() -> bool:
	"""Check if inventory has space"""
	return items.size() < MAX_ITEMS

func is_empty() -> bool:
	"""Check if inventory is empty"""
	return items.is_empty()

func size() -> int:
	"""Get number of items"""
	return items.size()
