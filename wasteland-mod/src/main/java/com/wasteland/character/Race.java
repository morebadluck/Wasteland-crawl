package com.wasteland.character;

/**
 * DCSS player races (species)
 * Each race has different stat modifiers and aptitudes
 */
public enum Race {
    // Simple races (good for beginners)
    // Format: name, description, HP, MP, Speed, STR, DEX, INT
    HUMAN("Human", "Versatile and balanced", 0, 0, 0, 0, 0, 0),
    HILL_ORC("Hill Orc", "Strong warriors with fire resistance", 1, -1, 0, 2, -1, -1),
    MINOTAUR("Minotaur", "Powerful melee fighters", 3, -2, 0, 4, -2, -2),
    MERFOLK("Merfolk", "Agile fighters, bonus in water", 0, 0, 1, 0, 2, 1),
    GARGOYLE("Gargoyle", "Stone-skinned, resistant to damage", 2, -2, 0, 2, -2, 0),
    DRACONIAN("Draconian", "Dragon-like humanoids with breath weapons", 0, 0, 1, 2, 0, 0),

    // Advanced races
    DEEP_ELF("Deep Elf", "Magical specialists, fragile", -2, 3, 2, -3, 2, 4),
    DEEP_DWARF("Deep Dwarf", "Hardy but cannot regenerate naturally", 2, -1, -1, 3, -2, 0),
    TROLL("Troll", "Regenerating hulks", 4, -3, -2, 5, -3, -4),
    OGRE("Ogre", "Large and strong", 3, -3, -1, 4, -3, -3),
    SPRIGGAN("Spriggan", "Tiny and fast", -3, 0, 3, -4, 4, 2),
    KOBOLD("Kobold", "Small and sneaky", -2, 0, 2, -2, 3, 0),
    CENTAUR("Centaur", "Fast with natural aptitude for ranged combat", 1, -1, 1, 2, 2, -2),

    // Monster races
    NAGA("Naga", "Serpentine, slow but poison resistant", 1, 0, -2, 2, 0, 0),
    VAMPIRE("Vampire", "Undead bloodsucker", 0, 1, 1, 0, 2, 2),
    GHOUL("Ghoul", "Undead flesh-eater", 2, -2, -1, 3, -1, -2),
    MUMMY("Mummy", "Ancient undead, no regeneration", 0, 0, -2, 0, 0, -2),
    FELID("Felid", "Cat with nine lives", -4, 1, 3, -5, 4, 2),
    OCTOPODE("Octopode", "Eight-tentacled aquatic creature", -2, 1, 2, -2, 2, 3);

    private final String displayName;
    private final String description;
    private final int hpModifier;      // Base HP bonus/penalty
    private final int mpModifier;      // Base MP bonus/penalty
    private final int speedModifier;   // Movement speed modifier
    private final int strengthModifier; // STR bonus/penalty
    private final int dexterityModifier; // DEX bonus/penalty
    private final int intelligenceModifier; // INT bonus/penalty

    Race(String displayName, String description, int hpModifier, int mpModifier, int speedModifier,
         int strengthModifier, int dexterityModifier, int intelligenceModifier) {
        this.displayName = displayName;
        this.description = description;
        this.hpModifier = hpModifier;
        this.mpModifier = mpModifier;
        this.speedModifier = speedModifier;
        this.strengthModifier = strengthModifier;
        this.dexterityModifier = dexterityModifier;
        this.intelligenceModifier = intelligenceModifier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getHpModifier() {
        return hpModifier;
    }

    public int getMpModifier() {
        return mpModifier;
    }

    public int getSpeedModifier() {
        return speedModifier;
    }

    public int getStrengthModifier() {
        return strengthModifier;
    }

    public int getDexterityModifier() {
        return dexterityModifier;
    }

    public int getIntelligenceModifier() {
        return intelligenceModifier;
    }

    /**
     * Get beginner-friendly races
     */
    public static Race[] getBeginnerRaces() {
        return new Race[]{HUMAN, HILL_ORC, MINOTAUR, MERFOLK, GARGOYLE, DRACONIAN};
    }

    /**
     * Get all playable races
     */
    public static Race[] getAllRaces() {
        return values();
    }
}
