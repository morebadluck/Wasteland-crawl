package com.wasteland.equipment;

/**
 * Equipment slots for DCSS-style character equipment
 */
public enum EquipmentSlot {
    WEAPON("Weapon", 'a'),
    OFFHAND("Offhand", 'b'),      // Shield, second weapon, or book
    BODY("Body", 'c'),
    HELMET("Helmet", 'd'),
    CLOAK("Cloak", 'e'),
    GLOVES("Gloves", 'f'),
    BOOTS("Boots", 'g'),
    AMULET("Amulet", 'h'),
    LEFT_RING("Left Ring", 'i'),
    RIGHT_RING("Right Ring", 'j');

    private final String displayName;
    private final char slotKey;

    EquipmentSlot(String displayName, char slotKey) {
        this.displayName = displayName;
        this.slotKey = slotKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    public char getSlotKey() {
        return slotKey;
    }

    /**
     * Check if this slot can hold weapons
     */
    public boolean canHoldWeapon() {
        return this == WEAPON || this == OFFHAND;
    }

    /**
     * Check if this slot can hold armor
     */
    public boolean canHoldArmor() {
        return this == BODY || this == HELMET || this == CLOAK
            || this == GLOVES || this == BOOTS;
    }

    /**
     * Check if this slot can hold jewelry
     */
    public boolean canHoldJewelry() {
        return this == AMULET || this == LEFT_RING || this == RIGHT_RING;
    }
}
