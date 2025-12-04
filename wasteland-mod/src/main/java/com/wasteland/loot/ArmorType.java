package com.wasteland.loot;

import com.wasteland.character.Skill;

/**
 * Armor types based on DCSS armor categories.
 * Each piece provides AC (armor class) and affects EV (evasion).
 * Heavier armor has higher AC but penalties to EV.
 */
public enum ArmorType {
    // ===== BODY ARMOR =====
    // Light (high EV, low AC)
    ROBE("Robe", ArmorSlot.BODY, 2, 0, Skill.ARMOUR, 0),
    LEATHER_ARMOR("Leather Armor", ArmorSlot.BODY, 3, -1, Skill.ARMOUR, 1),
    RING_MAIL("Ring Mail", ArmorSlot.BODY, 5, -2, Skill.ARMOUR, 2),

    // Medium (balanced)
    SCALE_MAIL("Scale Mail", ArmorSlot.BODY, 6, -3, Skill.ARMOUR, 2),
    CHAIN_MAIL("Chain Mail", ArmorSlot.BODY, 8, -4, Skill.ARMOUR, 3),
    PLATE_ARMOR("Plate Armor", ArmorSlot.BODY, 10, -5, Skill.ARMOUR, 4),

    // Heavy (high AC, low EV)
    CRYSTAL_PLATE("Crystal Plate Armor", ArmorSlot.BODY, 14, -7, Skill.ARMOUR, 5),

    // ===== HELMETS =====
    HAT("Hat", ArmorSlot.HELMET, 0, 0, Skill.ARMOUR, 0),
    HELMET("Helmet", ArmorSlot.HELMET, 1, 0, Skill.ARMOUR, 1),

    // ===== GLOVES =====
    GLOVES("Gloves", ArmorSlot.GLOVES, 1, 0, Skill.ARMOUR, 1),

    // ===== BOOTS =====
    BOOTS("Boots", ArmorSlot.BOOTS, 1, 0, Skill.ARMOUR, 1),

    // ===== CLOAKS =====
    CLOAK("Cloak", ArmorSlot.CLOAK, 1, 0, Skill.ARMOUR, 0),

    // ===== SHIELDS =====
    BUCKLER("Buckler", ArmorSlot.OFFHAND, 3, 0, Skill.SHIELDS, 1),
    KITE_SHIELD("Kite Shield", ArmorSlot.OFFHAND, 8, -2, Skill.SHIELDS, 2),
    TOWER_SHIELD("Tower Shield", ArmorSlot.OFFHAND, 13, -5, Skill.SHIELDS, 3);

    private final String displayName;
    private final ArmorSlot slot;
    private final int baseAC;           // Armor class bonus
    private final int baseEVPenalty;    // Evasion penalty (negative values)
    private final Skill skill;          // Required skill (Armour or Shields)
    private final int encumbrance;      // Weight class (0-5, higher = heavier)

    ArmorType(String displayName, ArmorSlot slot, int baseAC, int baseEVPenalty,
              Skill skill, int encumbrance) {
        this.displayName = displayName;
        this.slot = slot;
        this.baseAC = baseAC;
        this.baseEVPenalty = baseEVPenalty;
        this.skill = skill;
        this.encumbrance = encumbrance;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ArmorSlot getSlot() {
        return slot;
    }

    public int getBaseAC() {
        return baseAC;
    }

    public int getBaseEVPenalty() {
        return baseEVPenalty;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getEncumbrance() {
        return encumbrance;
    }

    /**
     * Check if this is a shield
     */
    public boolean isShield() {
        return slot == ArmorSlot.OFFHAND;
    }

    /**
     * Check if this is body armor
     */
    public boolean isBodyArmor() {
        return slot == ArmorSlot.BODY;
    }

    /**
     * Get minimum character level to use effectively
     */
    public int getMinLevel() {
        return switch (encumbrance) {
            case 0, 1 -> 1;
            case 2 -> 3;
            case 3 -> 7;
            case 4 -> 12;
            case 5 -> 18;
            default -> 1;
        };
    }
}
