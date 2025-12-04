package com.wasteland.magic;

/**
 * Magic schools/types in DCSS style.
 * Each spell belongs to one or more schools.
 */
public enum SpellSchool {
    // Elemental schools
    FIRE("Fire Magic", "Spells of flame and heat", 0xFF5500),
    ICE("Ice Magic", "Spells of frost and cold", 0x00DDFF),
    AIR("Air Magic", "Spells of lightning and wind", 0xCCCCFF),
    EARTH("Earth Magic", "Spells of stone and gravity", 0x885522),
    POISON("Poison Magic", "Spells of venom and toxins", 0x00CC00),

    // Core magic schools
    CONJURATIONS("Conjurations", "Direct damage spells", 0xFF0000),
    HEXES("Hexes", "Debuffs and curses", 0xAA00AA),
    CHARMS("Charms", "Buffs and enhancements", 0xFFAA00),
    SUMMONINGS("Summonings", "Summoning creatures", 0x00AAFF),
    NECROMANCY("Necromancy", "Death and undead magic", 0x444444),
    TRANSLOCATIONS("Translocations", "Teleportation and space", 0xFF00FF),
    TRANSMUTATIONS("Transmutations", "Shape-shifting and transformation", 0xFFFF00),

    // Special
    DIVINATIONS("Divinations", "Information and detection", 0xCCCCCC);

    private final String displayName;
    private final String description;
    private final int color;

    SpellSchool(String displayName, String description, int color) {
        this.displayName = displayName;
        this.description = description;
        this.color = color;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    /**
     * Get formatting code for display
     */
    public String getFormattingCode() {
        return String.format("§%x", (color >> 16) & 0xFF);
    }
}
