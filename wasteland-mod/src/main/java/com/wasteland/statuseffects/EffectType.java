package com.wasteland.statuseffects;

/**
 * Categories of status effects
 */
public enum EffectType {
    BUFF("Buff", "§a"),      // Green
    DEBUFF("Debuff", "§c"),  // Red
    SPECIAL("Special", "§e"); // Yellow

    private final String displayName;
    private final String colorCode;

    EffectType(String displayName, String colorCode) {
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
