package com.wasteland.worldgen;

/**
 * Types of DCSS dungeons that can be placed in the wasteland.
 * Each has preferred biome/region types.
 */
public enum DungeonType {
    // Aquatic dungeon - coastal areas
    SHOALS("Shoals", true, false, false, false, false),

    // Swamp dungeon - swamp biomes
    SWAMP("Swamp", false, false, false, false, true),

    // Lair - forests and natural areas
    LAIR("Lair of Beasts", false, true, false, false, false),

    // Snake Pit - desert areas
    SNAKE_PIT("Snake Pit", false, false, true, false, false),

    // Spider Nest - dense forests
    SPIDER("Spider Nest", false, true, false, false, false),

    // Orc Mines - mountains
    ORC_MINES("Orcish Mines", false, false, false, true, false),

    // Elven Halls - ancient forests
    ELF_HALLS("Elven Halls", false, true, false, false, false),

    // Vaults - any location (ruined cities)
    VAULTS("Vaults", false, false, false, false, false),

    // Crypt - scattered locations
    CRYPT("Crypt", false, false, false, false, false),

    // Tomb - desert areas
    TOMB("Tomb of the Ancients", false, false, true, false, false),

    // Depths - rare, any location
    DEPTHS("Depths", false, false, false, false, false),

    // Zot - very rare, special locations
    REALM_OF_ZOT("Realm of Zot", false, false, false, false, false);

    private final String displayName;
    private final boolean prefersCoastal;
    private final boolean prefersForest;
    private final boolean prefersDesert;
    private final boolean prefersMountain;
    private final boolean prefersSwamp;

    DungeonType(String displayName, boolean coastal, boolean forest,
                boolean desert, boolean mountain, boolean swamp) {
        this.displayName = displayName;
        this.prefersCoastal = coastal;
        this.prefersForest = forest;
        this.prefersDesert = desert;
        this.prefersMountain = mountain;
        this.prefersSwamp = swamp;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this dungeon type is suitable for a given region
     */
    public boolean isSuitableFor(USARegion region) {
        if (prefersCoastal && region.isCoastal()) return true;
        if (prefersForest && region.isForest()) return true;
        if (prefersDesert && region.isDesert()) return true;
        if (prefersMountain && region.isMountain()) return true;
        if (prefersSwamp && region.isSwamp()) return true;

        // Vaults, Crypt, Depths, Zot can spawn anywhere
        if (this == VAULTS || this == CRYPT || this == DEPTHS || this == REALM_OF_ZOT) {
            return true;
        }

        return false;
    }

    /**
     * Get a random suitable dungeon type for a region
     */
    public static DungeonType getRandomForRegion(USARegion region, java.util.Random random) {
        java.util.List<DungeonType> suitable = new java.util.ArrayList<>();

        for (DungeonType type : values()) {
            if (type.isSuitableFor(region)) {
                suitable.add(type);
            }
        }

        if (suitable.isEmpty()) {
            return VAULTS; // Default fallback
        }

        return suitable.get(random.nextInt(suitable.size()));
    }

    /**
     * Get rarity weight for this dungeon type
     * Higher = more common
     */
    public int getRarityWeight() {
        return switch (this) {
            case LAIR, ORC_MINES -> 100; // Common
            case SWAMP, SHOALS, SNAKE_PIT, SPIDER -> 80; // Fairly common
            case ELF_HALLS, VAULTS, CRYPT -> 50; // Uncommon
            case TOMB -> 30; // Rare
            case DEPTHS -> 15; // Very rare
            case REALM_OF_ZOT -> 5; // Extremely rare
        };
    }
}
