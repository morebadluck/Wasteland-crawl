extends Node
class_name God

# Gods from Dungeon Crawl Stone Soup (DCSS)
# Complete religion system with piety, abilities, and conduct

# God alignments
enum Alignment {
	GOOD,      # Righteous gods (The Shining One, Zin, Elyvilon)
	NEUTRAL,   # Balanced gods (Trog, Okawaru, Sif Muna, Vehumet, Gozag)
	EVIL,      # Dark gods (Kikubaaqudgha, Makhleb, Yredelemnul)
	CHAOTIC    # Unpredictable gods (Xom)
}

# God types
enum Type {
	NONE,
	THE_SHINING_ONE,
	ZIN,
	ELYVILON,
	TROG,
	OKAWARU,
	SIF_MUNA,
	VEHUMET,
	KIKUBAAQUDGHA,
	MAKHLEB,
	YREDELEMNUL,
	XOM,
	GOZAG
}

# Piety constants (0-200 scale from DCSS)
const MIN_PIETY = 0
const MAX_PIETY = 200
const PIETY_RANK_1 = 30   # "*"     - First power
const PIETY_RANK_2 = 50   # "**"    - Second power
const PIETY_RANK_3 = 75   # "***"   - Third power
const PIETY_RANK_4 = 100  # "****"  - Fourth power
const PIETY_RANK_5 = 120  # "*****" - Fifth power
const PIETY_RANK_6 = 160  # "******" - Sixth power (max)

# God data structure
class GodData:
	var type: Type
	var display_name: String
	var description: String
	var alignment: Alignment
	var likes: Array[String]
	var dislikes: Array[String]
	var uses_piety: bool
	var altar_color: Color

	func _init(t: Type, name: String, desc: String, align: Alignment,
			   like_list: Array[String], dislike_list: Array[String],
			   piety: bool = true, color: Color = Color.GRAY):
		type = t
		display_name = name
		description = desc
		alignment = align
		likes = like_list
		dislikes = dislike_list
		uses_piety = piety
		altar_color = color

# Registry of all gods
static var gods: Dictionary = {}

# Initialize god data
static func _static_init():
	# NONE - No god
	gods[Type.NONE] = GodData.new(
		Type.NONE,
		"None",
		"You worship no god.",
		Alignment.NEUTRAL,
		[],
		[],
		false,
		Color.GRAY
	)

	# === GOOD GODS ===

	# The Shining One - God of holy light and righteousness
	gods[Type.THE_SHINING_ONE] = GodData.new(
		Type.THE_SHINING_ONE,
		"The Shining One",
		"The Shining One is a god of holy light and righteousness. Followers must fight evil and never use poison or unholy magic.",
		Alignment.GOOD,
		[
			"Killing evil beings",
			"Killing undead",
			"Killing demons"
		],
		[
			"Using poison",
			"Using necromancy",
			"Attacking allies",
			"Desecrating holy corpses"
		],
		true,
		Color.GOLD
	)

	# Zin - God of law and order
	gods[Type.ZIN] = GodData.new(
		Type.ZIN,
		"Zin",
		"Zin is the god of law and order. Followers must maintain purity and avoid chaos.",
		Alignment.GOOD,
		[
			"Donating gold",
			"Killing chaotic creatures",
			"Killing unclean creatures"
		],
		[
			"Using mutations",
			"Cannibalism",
			"Using unclean weapons",
			"Attacking allies"
		],
		true,
		Color.WHITE
	)

	# Elyvilon - God of healing and pacifism
	gods[Type.ELYVILON] = GodData.new(
		Type.ELYVILON,
		"Elyvilon",
		"Elyvilon the Healer is a god of healing and pacifism. Followers should avoid killing when possible.",
		Alignment.GOOD,
		[
			"Healing yourself or allies",
			"Exploring peacefully"
		],
		[
			"Unnecessary killing",
			"Attacking allies"
		],
		true,
		Color.PINK
	)

	# === NEUTRAL GODS ===

	# Trog - God of anger and violence
	gods[Type.TROG] = GodData.new(
		Type.TROG,
		"Trog",
		"Trog is the god of anger and violence. Trog hates all magic and grants great power to berserkers.",
		Alignment.NEUTRAL,
		[
			"Killing in melee combat",
			"Berserk killing"
		],
		[
			"Casting spells",
			"Using magic items"
		],
		true,
		Color.RED
	)

	# Okawaru - God of battle
	gods[Type.OKAWARU] = GodData.new(
		Type.OKAWARU,
		"Okawaru",
		"Okawaru is a straightforward god of battle. Followers gain rewards for heroic kills.",
		Alignment.NEUTRAL,
		[
			"Killing strong enemies",
			"Winning battles"
		],
		[
			"Inactivity",
			"Fleeing from combat"
		],
		true,
		Color.STEEL_BLUE
	)

	# Sif Muna - God of magic and knowledge
	gods[Type.SIF_MUNA] = GodData.new(
		Type.SIF_MUNA,
		"Sif Muna",
		"Sif Muna is the god of magic and knowledge. Followers gain increased spell power and easier memorization.",
		Alignment.NEUTRAL,
		[
			"Casting spells",
			"Learning new spells",
			"Exploring"
		],
		[
			"Forgetting spells voluntarily"
		],
		true,
		Color.CYAN
	)

	# Vehumet - God of destructive magic
	gods[Type.VEHUMET] = GodData.new(
		Type.VEHUMET,
		"Vehumet",
		"Vehumet is the god of destructive magic. Followers gain powerful offensive spells.",
		Alignment.NEUTRAL,
		[
			"Killing with spells",
			"Using destructive magic"
		],
		[
			"Inactivity"
		],
		true,
		Color.ORANGE_RED
	)

	# Gozag - God of gold
	gods[Type.GOZAG] = GodData.new(
		Type.GOZAG,
		"Gozag",
		"Gozag Ym Sagoz is the god of gold. Followers trade gold for divine powers.",
		Alignment.NEUTRAL,
		[
			"Collecting gold"
		],
		[
			"Nothing - gold is all that matters"
		],
		false,  # Gozag uses gold, not piety
		Color.GOLD
	)

	# === EVIL GODS ===

	# Kikubaaqudgha - God of necromancy
	gods[Type.KIKUBAAQUDGHA] = GodData.new(
		Type.KIKUBAAQUDGHA,
		"Kikubaaqudgha",
		"Kikubaaqudgha is the god of necromancy. Followers gain undead servants and necromantic power.",
		Alignment.EVIL,
		[
			"Killing living creatures",
			"Using necromancy",
			"Raising undead"
		],
		[
			"Using holy magic",
			"Destroying undead"
		],
		true,
		Color.DARK_GREEN
	)

	# Makhleb - God of chaos and bloodshed
	gods[Type.MAKHLEB] = GodData.new(
		Type.MAKHLEB,
		"Makhleb",
		"Makhleb the Destroyer is the god of chaos and bloodshed. Followers gain demonic servants.",
		Alignment.EVIL,
		[
			"Killing anything",
			"Causing destruction"
		],
		[
			"Inactivity"
		],
		true,
		Color.DARK_RED
	)

	# Yredelemnul - God of death
	gods[Type.YREDELEMNUL] = GodData.new(
		Type.YREDELEMNUL,
		"Yredelemnul",
		"Yredelemnul is the god of death. Followers can enslave souls and raise powerful undead.",
		Alignment.EVIL,
		[
			"Killing living creatures",
			"Raising undead",
			"Enslaving souls"
		],
		[
			"Using holy magic",
			"Destroying undead"
		],
		true,
		Color.PURPLE
	)

	# === CHAOTIC GODS ===

	# Xom - God of chaos
	gods[Type.XOM] = GodData.new(
		Type.XOM,
		"Xom",
		"Xom is the god of chaos. Xom acts randomly and doesn't use piety. Worship at your own risk!",
		Alignment.CHAOTIC,
		[
			"Doing interesting things",
			"Taking risks",
			"Causing chaos"
		],
		[
			"Being boring"
		],
		false,  # Xom doesn't use piety
		Color.MAGENTA
	)

# Get god data by type
static func get_god(god_type: Type) -> GodData:
	return gods.get(god_type, gods[Type.NONE])

# Get all worshipable gods (excludes NONE)
static func get_worshipable_gods() -> Array[GodData]:
	var result: Array[GodData] = []
	for god_type in gods:
		if god_type != Type.NONE:
			result.append(gods[god_type])
	return result

# Get gods by alignment
static func get_gods_by_alignment(alignment: Alignment) -> Array[GodData]:
	var result: Array[GodData] = []
	for god_type in gods:
		var god_data = gods[god_type]
		if god_data.alignment == alignment and god_type != Type.NONE:
			result.append(god_data)
	return result

# Get piety rank (0-6)
static func get_piety_rank(piety: int) -> int:
	if piety >= PIETY_RANK_6:
		return 6
	elif piety >= PIETY_RANK_5:
		return 5
	elif piety >= PIETY_RANK_4:
		return 4
	elif piety >= PIETY_RANK_3:
		return 3
	elif piety >= PIETY_RANK_2:
		return 2
	elif piety >= PIETY_RANK_1:
		return 1
	else:
		return 0

# Get piety as star string
static func get_piety_stars(piety: int) -> String:
	var rank = get_piety_rank(piety)
	if rank == 0:
		return ""
	return "*".repeat(rank)

# Get alignment color
static func get_alignment_color(alignment: Alignment) -> Color:
	match alignment:
		Alignment.GOOD:
			return Color.GREEN
		Alignment.NEUTRAL:
			return Color.GRAY
		Alignment.EVIL:
			return Color.RED
		Alignment.CHAOTIC:
			return Color.MAGENTA
		_:
			return Color.WHITE

# Get alignment name
static func get_alignment_name(alignment: Alignment) -> String:
	match alignment:
		Alignment.GOOD:
			return "Good"
		Alignment.NEUTRAL:
			return "Neutral"
		Alignment.EVIL:
			return "Evil"
		Alignment.CHAOTIC:
			return "Chaotic"
		_:
			return "Unknown"
