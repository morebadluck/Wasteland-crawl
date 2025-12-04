package com.wasteland.loot;

import com.wasteland.character.Skill;

/**
 * Weapon types based on DCSS weapon categories.
 * Each type has base damage, attack speed, and associated skill.
 */
public enum WeaponType {
    // ===== SHORT BLADES (fast, low damage) =====
    DAGGER("Dagger", 4, 10, Skill.SHORT_BLADES, 1),
    QUICK_BLADE("Quick Blade", 5, 7, Skill.SHORT_BLADES, 2),
    SHORT_SWORD("Short Sword", 7, 11, Skill.SHORT_BLADES, 1),
    RAPIER("Rapier", 8, 12, Skill.SHORT_BLADES, 1),

    // ===== LONG BLADES (balanced) =====
    LONG_SWORD("Long Sword", 10, 14, Skill.LONG_BLADES, 1),
    SCIMITAR("Scimitar", 12, 14, Skill.LONG_BLADES, 1),
    GREAT_SWORD("Great Sword", 15, 17, Skill.LONG_BLADES, 2),
    DOUBLE_SWORD("Double Sword", 15, 15, Skill.LONG_BLADES, 2),
    TRIPLE_SWORD("Triple Sword", 17, 17, Skill.LONG_BLADES, 3),

    // ===== AXES (high damage, slower) =====
    HAND_AXE("Hand Axe", 7, 13, Skill.AXES, 1),
    WAR_AXE("War Axe", 11, 15, Skill.AXES, 1),
    BROAD_AXE("Broad Axe", 13, 16, Skill.AXES, 2),
    BATTLEAXE("Battleaxe", 15, 17, Skill.AXES, 2),
    EXECUTIONERS_AXE("Executioner's Axe", 18, 20, Skill.AXES, 3),

    // ===== MACES & FLAILS (crushing damage) =====
    CLUB("Club", 5, 13, Skill.MACES_FLAILS, 1),
    WHIP("Whip", 6, 11, Skill.MACES_FLAILS, 1),
    MACE("Mace", 8, 14, Skill.MACES_FLAILS, 1),
    MORNINGSTAR("Morningstar", 13, 15, Skill.MACES_FLAILS, 1),
    DIRE_FLAIL("Dire Flail", 13, 16, Skill.MACES_FLAILS, 2),
    GREAT_MACE("Great Mace", 17, 18, Skill.MACES_FLAILS, 2),

    // ===== POLEARMS (reach weapons) =====
    SPEAR("Spear", 6, 11, Skill.POLEARMS, 1),
    TRIDENT("Trident", 9, 13, Skill.POLEARMS, 1),
    HALBERD("Halberd", 13, 15, Skill.POLEARMS, 2),
    GLAIVE("Glaive", 15, 17, Skill.POLEARMS, 2),
    BARDICHE("Bardiche", 18, 19, Skill.POLEARMS, 3),

    // ===== STAVES (two-handed, int-based) =====
    QUARTERSTAFF("Quarterstaff", 10, 13, Skill.STAVES, 2),
    LAJATANG("Lajatang", 14, 14, Skill.STAVES, 3),

    // ===== UNARMED (scales with UC skill) =====
    UNARMED("Unarmed", 3, 10, Skill.UNARMED_COMBAT, 0);

    private final String displayName;
    private final int baseDamage;        // Base damage roll
    private final int baseDelay;         // Attack delay (lower = faster)
    private final Skill skill;           // Associated weapon skill
    private final int handedness;        // 0=unarmed, 1=one-handed, 2=two-handed, 3=two-handed heavy

    WeaponType(String displayName, int baseDamage, int baseDelay, Skill skill, int handedness) {
        this.displayName = displayName;
        this.baseDamage = baseDamage;
        this.baseDelay = baseDelay;
        this.skill = skill;
        this.handedness = handedness;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getBaseDelay() {
        return baseDelay;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getHandedness() {
        return handedness;
    }

    /**
     * Check if weapon is one-handed
     */
    public boolean isOneHanded() {
        return handedness == 1;
    }

    /**
     * Check if weapon is two-handed
     */
    public boolean isTwoHanded() {
        return handedness >= 2;
    }

    /**
     * Get minimum character level to use effectively
     */
    public int getMinLevel() {
        return switch (handedness) {
            case 0 -> 1;  // Unarmed
            case 1 -> 1;  // One-handed
            case 2 -> 5;  // Two-handed
            case 3 -> 10; // Heavy two-handed
            default -> 1;
        };
    }

    /**
     * Get damage per turn (accounting for speed)
     * Higher is better
     */
    public double getDamagePerTurn() {
        // Normalize to delay 10 (base speed)
        return (baseDamage * 10.0) / baseDelay;
    }
}
