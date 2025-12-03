package com.wasteland.character;

import java.util.Arrays;
import java.util.List;

/**
 * DCSS spells organized by magic school
 * Each spell has a level (1-9) and associated schools
 */
public enum Spell {
    // Level 1 spells
    MAGIC_DART("Magic Dart", 1, List.of(Skill.CONJURATIONS), "Simple bolt of magical energy"),
    SUMMON_SMALL_MAMMAL("Summon Small Mammal", 1, List.of(Skill.SUMMONINGS), "Summons a rat or similar creature"),
    APPORTATION("Apportation", 1, List.of(Skill.TRANSLOCATIONS), "Pulls distant items to you"),
    PAIN("Pain", 1, List.of(Skill.NECROMANCY), "Inflicts pain on the target"),
    FREEZE("Freeze", 1, List.of(Skill.ICE_MAGIC), "Freezes an adjacent enemy"),
    FLAME_TONGUE("Flame Tongue", 1, List.of(Skill.FIRE_MAGIC), "Shoots a small flame"),
    SHOCK("Shock", 1, List.of(Skill.AIR_MAGIC), "Electrocutes an enemy"),
    SANDBLAST("Sandblast", 1, List.of(Skill.EARTH_MAGIC), "Blasts enemy with sand and rocks"),
    BEASTLY_APPENDAGE("Beastly Appendage", 1, List.of(Skill.TRANSMUTATIONS), "Mutates your body temporarily"),

    // Level 2 spells
    SEARING_RAY("Searing Ray", 2, List.of(Skill.CONJURATIONS), "Continuous beam of heat"),
    THROW_FLAME("Throw Flame", 2, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Throws a bolt of fire"),
    THROW_FROST("Throw Frost", 2, List.of(Skill.CONJURATIONS, Skill.ICE_MAGIC), "Throws a bolt of frost"),
    STATIC_DISCHARGE("Static Discharge", 2, List.of(Skill.CONJURATIONS, Skill.AIR_MAGIC), "Electrical explosion around you"),
    PASSWALL("Passwall", 2, List.of(Skill.TRANSMUTATIONS, Skill.EARTH_MAGIC), "Walk through walls"),
    SWIFTNESS("Swiftness", 2, List.of(Skill.TRANSLOCATIONS, Skill.AIR_MAGIC), "Move quickly"),
    SONG_OF_SLAYING("Song of Slaying", 2, List.of(Skill.HEXES), "Increases combat power"),

    // Level 3 spells
    MEPHITIC_CLOUD("Mephitic Cloud", 3, List.of(Skill.CONJURATIONS, Skill.POISON_MAGIC, Skill.AIR_MAGIC), "Creates cloud of confusion gas"),
    STONE_ARROW("Stone Arrow", 3, List.of(Skill.CONJURATIONS, Skill.EARTH_MAGIC), "Fires an arrow of stone"),
    INNER_FLAME("Inner Flame", 3, List.of(Skill.HEXES, Skill.FIRE_MAGIC), "Curses enemy to explode on death"),
    PORTAL_PROJECTILE("Portal Projectile", 3, List.of(Skill.TRANSLOCATIONS, Skill.HEXES), "Teleports projectiles past obstacles"),
    BLINK("Blink", 3, List.of(Skill.TRANSLOCATIONS), "Random short-range teleport"),
    CALL_CANINE_FAMILIAR("Call Canine Familiar", 3, List.of(Skill.SUMMONINGS), "Summons a hound or wolf"),
    GELL_S_GRAVITAS("Gell's Gravitas", 3, List.of(Skill.TRANSLOCATIONS), "Pulls enemies toward a point"),

    // Level 4 spells
    FIREBALL("Fireball", 4, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Exploding ball of fire"),
    STICKY_FLAME("Sticky Flame", 4, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Fire that sticks and burns"),
    CONJURE_FLAME("Conjure Flame", 4, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Creates a pillar of fire"),
    OZO_S_ARMOUR("Ozo's Armour", 4, List.of(Skill.TRANSMUTATIONS, Skill.EARTH_MAGIC), "Magically increases AC"),
    PETRIFY("Petrify", 4, List.of(Skill.TRANSMUTATIONS, Skill.EARTH_MAGIC), "Turns enemy to stone"),
    ANIMATE_DEAD("Animate Dead", 4, List.of(Skill.NECROMANCY), "Raises corpses as zombies"),
    AGONY("Agony", 4, List.of(Skill.NECROMANCY), "Causes terrible pain"),
    DIMENSIONAL_BULLSEYE("Dimensional Bullseye", 4, List.of(Skill.TRANSLOCATIONS, Skill.HEXES), "Marks target for teleporting attacks"),

    // Level 5 spells
    BOLT_OF_FIRE("Bolt of Fire", 5, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Piercing bolt of fire"),
    BOLT_OF_COLD("Bolt of Cold", 5, List.of(Skill.CONJURATIONS, Skill.ICE_MAGIC), "Piercing bolt of ice"),
    LIGHTNING_BOLT("Lightning Bolt", 5, List.of(Skill.CONJURATIONS, Skill.AIR_MAGIC), "Powerful lightning bolt"),
    IRON_SHOT("Iron Shot", 5, List.of(Skill.CONJURATIONS, Skill.EARTH_MAGIC), "Heavy metal projectile"),
    POISON_ARROW("Poison Arrow", 5, List.of(Skill.CONJURATIONS, Skill.POISON_MAGIC), "Poisonous arrow"),
    FREEZING_CLOUD("Freezing Cloud", 5, List.of(Skill.CONJURATIONS, Skill.ICE_MAGIC, Skill.AIR_MAGIC), "Cloud of freezing vapor"),
    INVISIBILITY("Invisibility", 5, List.of(Skill.HEXES), "Become invisible"),
    DEATHS_DOOR("Death's Door", 5, List.of(Skill.NECROMANCY, Skill.TRANSMUTATIONS), "Prevents death temporarily"),
    SUMMON_DEMON("Summon Demon", 5, List.of(Skill.SUMMONINGS), "Summons a demon"),

    // Level 6 spells
    IRON_SHOT_GREATER("Iron Shot", 6, List.of(Skill.CONJURATIONS, Skill.EARTH_MAGIC), "Massive metal projectile"),
    STATUE_FORM("Statue Form", 6, List.of(Skill.TRANSMUTATIONS, Skill.EARTH_MAGIC), "Transform into a statue"),
    BLADE_HANDS("Blade Hands", 6, List.of(Skill.TRANSMUTATIONS), "Turn hands into deadly blades"),
    BORGNJOR_S_REVIVIFICATION("Borgnjor's Revivification", 6, List.of(Skill.NECROMANCY), "Instantly heal near death"),
    SILENCE("Silence", 6, List.of(Skill.HEXES, Skill.AIR_MAGIC), "Create zone of magical silence"),

    // Level 7 spells
    FIRE_STORM("Fire Storm", 7, List.of(Skill.CONJURATIONS, Skill.FIRE_MAGIC), "Massive fire explosion"),
    GLACIATE("Glaciate", 7, List.of(Skill.CONJURATIONS, Skill.ICE_MAGIC), "Devastating cone of ice"),
    CHAIN_LIGHTNING("Chain Lightning", 7, List.of(Skill.CONJURATIONS, Skill.AIR_MAGIC), "Lightning that chains between enemies"),
    DRAGON_FORM("Dragon Form", 7, List.of(Skill.TRANSMUTATIONS), "Transform into a dragon"),
    NECROMUTATION("Necromutation", 7, List.of(Skill.NECROMANCY, Skill.TRANSMUTATIONS), "Become a lich"),
    HAUNT("Haunt", 7, List.of(Skill.NECROMANCY, Skill.SUMMONINGS), "Summon vengeful spirits"),

    // Level 8 spells
    LEHUDIB_S_CRYSTAL_SPEAR("Lehudib's Crystal Spear", 8, List.of(Skill.CONJURATIONS, Skill.EARTH_MAGIC), "Massive crystal projectile"),
    DISJUNCTION("Disjunction", 8, List.of(Skill.TRANSLOCATIONS), "Banishes nearby creatures"),
    MALIGN_GATEWAY("Malign Gateway", 8, List.of(Skill.SUMMONINGS, Skill.TRANSLOCATIONS), "Opens portal to eldritch horrors"),

    // Level 9 spells
    SHATTER("Shatter", 9, List.of(Skill.EARTH_MAGIC), "Destroys walls and damages all nearby"),
    TORNADO("Tornado", 9, List.of(Skill.AIR_MAGIC), "Creates a devastating whirlwind"),
    GLACIATE_ULTIMATE("Ultimate Glaciate", 9, List.of(Skill.ICE_MAGIC), "Freeze everything nearby");

    private final String displayName;
    private final int level;
    private final List<Skill> schools;
    private final String description;

    Spell(String displayName, int level, List<Skill> schools, String description) {
        this.displayName = displayName;
        this.level = level;
        this.schools = schools;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getLevel() {
        return level;
    }

    public List<Skill> getSchools() {
        return schools;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get the primary school for this spell (first one listed)
     */
    public Skill getPrimarySchool() {
        return schools.get(0);
    }

    /**
     * Get all spells of a certain level
     */
    public static Spell[] getSpellsByLevel(int level) {
        return Arrays.stream(values())
                .filter(spell -> spell.getLevel() == level)
                .toArray(Spell[]::new);
    }

    /**
     * Get all spells from a specific school
     */
    public static Spell[] getSpellsBySchool(Skill school) {
        return Arrays.stream(values())
                .filter(spell -> spell.getSchools().contains(school))
                .toArray(Spell[]::new);
    }

    /**
     * Check if player can cast this spell (based on skill levels)
     * Simplified: just check if they have minimum skill in primary school
     */
    public boolean canCast(int primarySchoolLevel) {
        // Rough formula: need skill >= spell level * 2
        return primarySchoolLevel >= (level * 2);
    }
}
