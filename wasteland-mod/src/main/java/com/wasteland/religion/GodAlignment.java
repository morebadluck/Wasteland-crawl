package com.wasteland.religion;

/**
 * God alignment categories from DCSS
 */
public enum GodAlignment {
    GOOD("Good", "§a"),      // Green
    NEUTRAL("Neutral", "§7"), // Gray
    EVIL("Evil", "§c"),       // Red
    CHAOTIC("Chaotic", "§5"); // Purple

    private final String displayName;
    private final String colorCode;

    GodAlignment(String displayName, String colorCode) {
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
