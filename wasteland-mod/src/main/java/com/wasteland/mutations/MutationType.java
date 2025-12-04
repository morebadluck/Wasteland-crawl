package com.wasteland.mutations;

/**
 * Type of mutation - determines how it's displayed and if gods care
 */
public enum MutationType {
    GOOD("Good", "§a"),      // Beneficial mutations
    BAD("Bad", "§c"),        // Harmful mutations
    BODY("Body", "§6"),      // Physical body modifications
    MIXED("Mixed", "§e");    // Context-dependent

    private final String displayName;
    private final String colorCode;

    MutationType(String displayName, String colorCode) {
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }
}
