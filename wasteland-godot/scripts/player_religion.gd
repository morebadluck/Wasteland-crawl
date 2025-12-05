extends Resource
class_name PlayerReligion

# Tracks a player's religious affiliation and piety
# Based on DCSS religion system

# Current god being worshipped
@export var current_god: God.Type = God.Type.NONE

# Piety with current god (0-200)
@export var piety: int = 0

# Has the player ever abandoned a god?
@export var has_abandoned: bool = false

# Piety gain/loss history (for debugging)
var piety_log: Array = []

# Signals
signal god_joined(god_type: God.Type)
signal god_abandoned(god_type: God.Type)
signal piety_gained(amount: int, new_piety: int)
signal piety_lost(amount: int, new_piety: int)
signal piety_rank_up(old_rank: int, new_rank: int)
signal piety_rank_down(old_rank: int, new_rank: int)
signal excommunicated(god_type: God.Type)

func _init():
	current_god = God.Type.NONE
	piety = 0
	has_abandoned = false

# Join a god's religion
func join_god(god_type: God.Type) -> bool:
	if god_type == current_god:
		print("Already worshipping %s" % God.get_god(god_type).display_name)
		return false

	if current_god != God.Type.NONE:
		print("Cannot join %s while worshipping %s" % [
			God.get_god(god_type).display_name,
			God.get_god(current_god).display_name
		])
		return false

	var god_data = God.get_god(god_type)
	current_god = god_type

	# Start with some piety (except for gods that don't use piety)
	if god_data.uses_piety:
		piety = God.PIETY_RANK_1
	else:
		piety = 0

	has_abandoned = false

	print("Joined %s" % god_data.display_name)
	god_joined.emit(god_type)

	return true

# Abandon current god
func abandon_god():
	if current_god == God.Type.NONE:
		return

	var old_god = current_god
	var god_data = God.get_god(old_god)

	print("Abandoned %s" % god_data.display_name)

	current_god = God.Type.NONE
	piety = 0
	has_abandoned = true

	god_abandoned.emit(old_god)

# Gain piety
func gain_piety(amount: int, reason: String = ""):
	var god_data = God.get_god(current_god)

	if not god_data.uses_piety:
		return

	var old_rank = get_piety_rank()
	piety = clampi(piety + amount, God.MIN_PIETY, God.MAX_PIETY)
	var new_rank = get_piety_rank()

	# Log the change
	piety_log.append({
		"type": "gain",
		"amount": amount,
		"reason": reason,
		"piety": piety,
		"time": Time.get_ticks_msec()
	})

	print("Gained %d piety with %s (now %d/%d)%s" % [
		amount,
		god_data.display_name,
		piety,
		God.MAX_PIETY,
		(" - " + reason) if reason else ""
	])

	piety_gained.emit(amount, piety)

	# Check for rank up
	if new_rank > old_rank:
		print("Reached piety rank %d with %s!" % [new_rank, god_data.display_name])
		piety_rank_up.emit(old_rank, new_rank)

# Lose piety
func lose_piety(amount: int, reason: String = ""):
	var god_data = God.get_god(current_god)

	if not god_data.uses_piety:
		return

	var old_rank = get_piety_rank()
	piety = clampi(piety - amount, God.MIN_PIETY, God.MAX_PIETY)
	var new_rank = get_piety_rank()

	# Log the change
	piety_log.append({
		"type": "loss",
		"amount": amount,
		"reason": reason,
		"piety": piety,
		"time": Time.get_ticks_msec()
	})

	print("Lost %d piety with %s (now %d/%d)%s" % [
		amount,
		god_data.display_name,
		piety,
		God.MAX_PIETY,
		(" - " + reason) if reason else ""
	])

	piety_lost.emit(amount, piety)

	# Check for rank down
	if new_rank < old_rank:
		print("WARNING: Dropped to piety rank %d with %s" % [new_rank, god_data.display_name])
		piety_rank_down.emit(old_rank, new_rank)

	# Auto-abandon if piety drops to 0
	if piety <= 0 and current_god != God.Type.NONE:
		print("EXCOMMUNICATED by %s!" % god_data.display_name)
		excommunicated.emit(current_god)
		abandon_god()

# Get current piety rank (0-6)
func get_piety_rank() -> int:
	var god_data = God.get_god(current_god)

	if not god_data.uses_piety or current_god == God.Type.NONE:
		return 0

	return God.get_piety_rank(piety)

# Get piety as stars
func get_piety_stars() -> String:
	return God.get_piety_stars(piety)

# Get piety percentage (for UI bars)
func get_piety_percentage() -> float:
	return float(piety) / float(God.MAX_PIETY)

# Check if has a god
func has_god() -> bool:
	return current_god != God.Type.NONE

# Get current god data
func get_god_data() -> God.GodData:
	return God.get_god(current_god)

# Handle kill event - award piety based on god preferences
func on_kill(victim_type: String, was_melee: bool = false, was_spell: bool = false):
	if current_god == God.Type.NONE:
		return

	var god_data = get_god_data()
	var piety_gain = 0
	var message = ""

	match current_god:
		God.Type.TROG:
			# Trog likes killing, especially in melee
			if was_melee:
				piety_gain = 3
				message = "Trog accepts your kill."
			else:
				piety_gain = 2

		God.Type.THE_SHINING_ONE:
			# TSO likes killing evil/undead/demons
			if victim_type in ["undead", "demon", "evil"]:
				piety_gain = 3
				message = "The Shining One approves of your destruction."
			else:
				piety_gain = 1

		God.Type.ZIN:
			# Zin likes killing chaotic/unclean creatures
			if victim_type in ["chaotic", "unclean", "mutant"]:
				piety_gain = 2
				message = "Zin appreciates your cleansing of chaos."

		God.Type.ELYVILON:
			# Elyvilon DISLIKES killing
			lose_piety(3, "unnecessary killing")
			print("Elyvilon is displeased by this killing!")
			return

		God.Type.OKAWARU:
			# Okawaru likes all combat kills
			piety_gain = 2
			if victim_type in ["strong", "boss"]:
				piety_gain = 4
				message = "Okawaru is pleased by your victory!"

		God.Type.VEHUMET:
			# Vehumet likes kills with spells
			if was_spell:
				piety_gain = 3
				message = "Vehumet is pleased by your destructive magic!"
			else:
				piety_gain = 1

		God.Type.KIKUBAAQUDGHA:
			# Kikubaaqudgha likes killing living creatures
			if victim_type != "undead":
				piety_gain = 2
				message = "Kikubaaqudgha appreciates the death."

		God.Type.MAKHLEB:
			# Makhleb likes killing anything
			piety_gain = 2
			message = "Makhleb revels in destruction!"

		God.Type.YREDELEMNUL:
			# Yredelemnul likes killing living creatures
			if victim_type != "undead":
				piety_gain = 2
				message = "Yredelemnul feeds on death."

		God.Type.SIF_MUNA:
			# Sif Muna doesn't care much about kills
			piety_gain = 1

		God.Type.XOM, God.Type.GOZAG:
			# These gods don't use piety
			return

	if piety_gain > 0:
		gain_piety(piety_gain, "kill: " + victim_type)
		if message:
			print(message)

# Handle spell cast - award/penalize piety
func on_spell_cast(is_destructive: bool = false):
	if current_god == God.Type.NONE:
		return

	match current_god:
		God.Type.TROG:
			# Trog HATES magic
			lose_piety(5, "casting spell")
			print("TROG IS FURIOUS AT YOUR USE OF MAGIC!")

		God.Type.SIF_MUNA:
			# Sif Muna loves magic
			gain_piety(1, "casting spell")

		God.Type.VEHUMET:
			# Vehumet loves destructive magic
			if is_destructive:
				gain_piety(2, "destructive spell")
				print("Vehumet is pleased by your destructive magic!")

# Handle exploration - award piety
func on_exploration():
	if current_god == God.Type.NONE:
		return

	match current_god:
		God.Type.SIF_MUNA:
			gain_piety(1, "exploration")
			print("Sif Muna appreciates your pursuit of knowledge.")

		God.Type.ELYVILON:
			gain_piety(1, "peaceful exploration")
			print("Elyvilon smiles upon your peaceful exploration.")

# Handle ally attack - penalize piety
func on_ally_attack():
	if current_god == God.Type.NONE:
		return

	var god_data = get_god_data()

	# Most good gods penalize attacking allies
	match current_god:
		God.Type.THE_SHINING_ONE, God.Type.ZIN, God.Type.ELYVILON:
			lose_piety(10, "attacking ally")
			print("%s IS FURIOUS AT YOUR BETRAYAL!" % god_data.display_name.to_upper())

# Handle gold donation (for Zin)
func on_gold_donated(amount: int):
	if current_god == God.Type.ZIN:
		var piety_gain = amount / 10  # 1 piety per 10 gold
		gain_piety(piety_gain, "gold donation")
		print("Zin accepts your offering.")

# Serialize to dictionary for saving
func to_dict() -> Dictionary:
	return {
		"current_god": current_god,
		"piety": piety,
		"has_abandoned": has_abandoned
	}

# Load from dictionary
func from_dict(data: Dictionary):
	current_god = data.get("current_god", God.Type.NONE)
	piety = data.get("piety", 0)
	has_abandoned = data.get("has_abandoned", false)
