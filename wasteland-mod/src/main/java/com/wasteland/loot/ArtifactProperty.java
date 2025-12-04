package com.wasteland.loot;

/**
 * Special properties that can appear on artifacts and randarts.
 * Based on DCSS artifact properties.
 */
public enum ArtifactProperty {
    // Stat bonuses
    STRENGTH("Str", "Strength bonus", 1, 5),
    DEXTERITY("Dex", "Dexterity bonus", 1, 5),
    INTELLIGENCE("Int", "Intelligence bonus", 1, 5),

    // Combat bonuses
    SLAYING("Slay", "Damage and accuracy bonus", 1, 9),
    ACCURACY("Acc", "To-hit bonus", 1, 9),

    // Defensive bonuses
    ARMOR_CLASS("AC", "Armor class bonus", 1, 8),
    EVASION("EV", "Evasion bonus", 1, 8),
    STEALTH("Stealth", "Stealth bonus", 1, 3),

    // Resistances (can stack)
    RESIST_FIRE("rF", "Fire resistance", 1, 3),
    RESIST_COLD("rC", "Cold resistance", 1, 3),
    RESIST_ELECTRICITY("rElec", "Electricity resistance", 1, 1),
    RESIST_POISON("rPois", "Poison resistance", 1, 1),
    RESIST_NEGATIVE("rN", "Negative energy resistance", 1, 3),
    RESIST_MAGIC("MR", "Magic resistance", 1, 3),
    RESIST_CORROSION("rCorr", "Corrosion resistance", 1, 1),

    // Special abilities
    SEE_INVISIBLE("SInv", "See invisible", 1, 1),
    REGENERATION("Regen", "Regeneration", 1, 1),
    CLARITY("Clar", "Clarity (resist confusion)", 1, 1),
    WILLPOWER("Will", "Willpower bonus", 1, 3),

    // Magical bonuses
    MANA_REGENERATION("MP Regen", "Mana regeneration", 1, 1),
    SPELL_POWER("Spell Power", "Spellpower bonus", 1, 5),
    CAST_SUCCESS("Cast", "Spell success bonus", 1, 5),

    // Speed
    ATTACK_SPEED("Attack Speed", "Faster attacks", 1, 2),

    // Negative properties (curses)
    CURSE_DRAIN("*Drain", "Drains on hit", 1, 1),
    CURSE_NOISE("*Noise", "Makes noise", 1, 1),
    CURSE_SLOW("*Slow", "Slows movement", 1, 1),
    CURSE_FRAGILE("*Fragile", "Reduced durability", 1, 1);

    private final String abbreviation;
    private final String description;
    private final int minValue;
    private final int maxValue;

    ArtifactProperty(String abbreviation, String description, int minValue, int maxValue) {
        this.abbreviation = abbreviation;
        this.description = description;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getDescription() {
        return description;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    /**
     * Get display string with value
     */
    public String formatValue(int value) {
        if (value == 1 && maxValue == 1) {
            // Binary property (just show abbreviation)
            return abbreviation;
        } else if (value > 0) {
            // Positive bonus
            return abbreviation + " +" + value;
        } else if (value < 0) {
            // Negative property/curse
            return abbreviation + " " + value;
        }
        return "";
    }

    /**
     * Check if this is a curse/negative property
     */
    public boolean isCurse() {
        return name().startsWith("CURSE_");
    }

    /**
     * Get power level for artifact generation (higher = rarer)
     */
    public int getPowerLevel() {
        return switch (this) {
            // Common properties (power 1)
            case ARMOR_CLASS, EVASION, ACCURACY, SLAYING -> 1;

            // Uncommon properties (power 2)
            case STRENGTH, DEXTERITY, INTELLIGENCE,
                 RESIST_FIRE, RESIST_COLD, STEALTH -> 2;

            // Rare properties (power 3)
            case RESIST_ELECTRICITY, RESIST_POISON, RESIST_NEGATIVE,
                 RESIST_MAGIC, WILLPOWER -> 3;

            // Very rare properties (power 4)
            case SEE_INVISIBLE, REGENERATION, CLARITY,
                 MANA_REGENERATION, SPELL_POWER, ATTACK_SPEED -> 4;

            // Extremely rare (power 5)
            case CAST_SUCCESS, RESIST_CORROSION -> 5;

            // Curses (negative power)
            case CURSE_DRAIN, CURSE_NOISE, CURSE_SLOW, CURSE_FRAGILE -> -1;
        };
    }
}
