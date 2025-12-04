package com.wasteland.loot;

/**
 * Equipment slots for armor and accessories.
 * Based on DCSS equipment slots.
 */
public enum ArmorSlot {
    WEAPON("Weapon"),
    OFFHAND("Offhand"), // Shield or second weapon
    BODY("Body Armor"),
    HELMET("Helmet"),
    CLOAK("Cloak"),
    GLOVES("Gloves"),
    BOOTS("Boots"),
    AMULET("Amulet"),
    LEFT_RING("Left Ring"),
    RIGHT_RING("Right Ring");

    private final String displayName;

    ArmorSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if this slot accepts armor
     */
    public boolean isArmorSlot() {
        return this == BODY || this == HELMET || this == CLOAK ||
               this == GLOVES || this == BOOTS || this == OFFHAND;
    }

    /**
     * Check if this slot accepts weapons
     */
    public boolean isWeaponSlot() {
        return this == WEAPON || this == OFFHAND;
    }

    /**
     * Check if this slot accepts jewelry
     */
    public boolean isJewelrySlot() {
        return this == AMULET || this == LEFT_RING || this == RIGHT_RING;
    }
}
