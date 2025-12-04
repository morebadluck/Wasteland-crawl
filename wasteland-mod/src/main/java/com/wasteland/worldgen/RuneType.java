package com.wasteland.worldgen;

/**
 * Types of runes that can be found in max-level dungeons.
 * Based on DCSS runes - each rune type is thematically tied to dungeon types.
 * Players need to collect 3+ different runes to access the Realm of Zot.
 *
 * TODO: Rename "Realm of Zot" to a wasteland-appropriate name (e.g., "The Core", "Ground Zero", etc.)
 */
public enum RuneType {
    // Standard runes from classic DCSS branches
    SLIMY("Slimy Rune", "A rune covered in acidic slime", 0x00FF00),          // Swamp/Lair
    SERPENTINE("Serpentine Rune", "A rune shaped like a coiled snake", 0xFFFF00), // Snake Pit
    BARNACLED("Barnacled Rune", "A rune encrusted with sea barnacles", 0x00FFFF), // Shoals
    GOSSAMER("Gossamer Rune", "A rune wrapped in spider silk", 0xCCCCCC),    // Spider Nest

    // Civilization ruins runes
    IRON("Iron Rune", "A heavy iron rune", 0x888888),                         // Orc Mines
    SILVER("Silver Rune", "An elegant silver rune", 0xC0C0C0),                // Elf Halls
    GOLDEN("Golden Rune", "A gleaming golden rune", 0xFFD700),                // Vaults

    // Death/undead runes
    BONE("Bone Rune", "A rune carved from ancient bone", 0xFFFFEE),           // Crypt
    DARK("Dark Rune", "A rune that absorbs light", 0x000000),                 // Tomb

    // Deep/endgame runes
    ABYSSAL("Abyssal Rune", "A rune from the deepest depths", 0x4B0082),      // Depths

    // Special/rare runes
    GLOWING("Glowing Rune", "A rune that pulses with radiation", 0x00FF00),   // Special wasteland-themed
    RUSTED("Rusted Rune", "A rune corroded by the wasteland", 0x8B4513);      // Special wasteland-themed

    private final String displayName;
    private final String description;
    private final int color;  // RGB color for UI display

    RuneType(String displayName, String description, int color) {
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
     * Get the number of runes required to access Zot
     */
    public static int getRunesRequiredForZot() {
        return 3;  // Like DCSS, need 3+ runes for Zot
    }

    /**
     * Check if player has enough runes to access Zot
     */
    public static boolean hasEnoughRunesForZot(java.util.Set<RuneType> collectedRunes) {
        return collectedRunes.size() >= getRunesRequiredForZot();
    }
}
