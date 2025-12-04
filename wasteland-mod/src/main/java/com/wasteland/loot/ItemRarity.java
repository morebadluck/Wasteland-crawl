package com.wasteland.loot;

/**
 * Item rarity levels (DCSS-style)
 */
public enum ItemRarity {
    COMMON("Common", 0xFFFFFF, 100),           // White
    UNCOMMON("Uncommon", 0x55FF55, 30),        // Green
    RARE("Rare", 0x5555FF, 10),                // Blue
    EPIC("Epic", 0xAA00AA, 3),                 // Purple
    LEGENDARY("Legendary", 0xFFAA00, 1),       // Orange
    ARTIFACT("Artifact", 0xFF5555, 0),         // Red (fixed artifacts)
    RANDART("Random Artifact", 0xFFFF55, 0);   // Yellow (random artifacts)

    private final String displayName;
    private final int color;
    private final int dropWeight; // For loot generation (0 = special generation only)

    ItemRarity(String displayName, int color, int dropWeight) {
        this.displayName = displayName;
        this.color = color;
        this.dropWeight = dropWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColor() {
        return color;
    }

    public int getDropWeight() {
        return dropWeight;
    }

    /**
     * Get Minecraft formatting code for this rarity
     */
    public String getFormattingCode() {
        return switch (this) {
            case COMMON -> "§f";           // White
            case UNCOMMON -> "§a";         // Green
            case RARE -> "§9";             // Blue
            case EPIC -> "§5";             // Dark Purple
            case LEGENDARY -> "§6";        // Gold
            case ARTIFACT -> "§c";         // Red
            case RANDART -> "§e";          // Yellow
        };
    }

    /**
     * Check if this is an artifact-level item
     */
    public boolean isArtifact() {
        return this == ARTIFACT || this == RANDART;
    }

    /**
     * Get enchantment bonus range for this rarity
     */
    public int getMinEnchantment() {
        return switch (this) {
            case COMMON -> 0;
            case UNCOMMON -> 1;
            case RARE -> 2;
            case EPIC -> 3;
            case LEGENDARY, ARTIFACT, RANDART -> 4;
        };
    }

    public int getMaxEnchantment() {
        return switch (this) {
            case COMMON -> 2;
            case UNCOMMON -> 4;
            case RARE -> 6;
            case EPIC -> 8;
            case LEGENDARY, ARTIFACT, RANDART -> 9;
        };
    }

    /**
     * Get number of artifact properties this rarity can have
     */
    public int getArtifactPropertyCount() {
        return switch (this) {
            case COMMON, UNCOMMON -> 0;
            case RARE -> 1;
            case EPIC -> 2;
            case LEGENDARY -> 3;
            case ARTIFACT, RANDART -> 5; // 4-6 properties
        };
    }
}
