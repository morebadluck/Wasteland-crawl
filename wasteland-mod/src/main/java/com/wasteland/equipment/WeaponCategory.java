package com.wasteland.equipment;

/**
 * Weapon categories - based on DCSS but adapted for Wasteland
 * Guns replace Slings from DCSS
 */
public enum WeaponCategory {
    // MELEE WEAPONS
    SHORT_BLADES("Short Blades", "Fast, low damage, good for stealth kills", true),
    LONG_BLADES("Long Blades", "Balanced speed and damage", true),
    AXES("Axes", "High damage, can cleave multiple enemies", true),
    MACES_FLAILS("Maces & Flails", "Crushing damage, good vs armor", true),
    POLEARMS("Polearms", "Reach attacks, control positioning", true),
    STAVES("Staves", "Quarterstaff combat, some with elemental effects", true),
    UNARMED("Unarmed", "Bare hands, claws, mutations", true),

    // RANGED WEAPONS
    BOWS("Bows", "Silent, renewable ammo (arrows)", false),
    CROSSBOWS("Crossbows", "Powerful, slower reload", false),
    GUNS("Guns", "Powerful wasteland firearms, limited ammo", false), // REPLACES SLINGS
    THROWING("Throwing", "Thrown weapons and explosives", false);

    private final String displayName;
    private final String description;
    private final boolean isMelee;

    WeaponCategory(String displayName, String description, boolean isMelee) {
        this.displayName = displayName;
        this.description = description;
        this.isMelee = isMelee;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public boolean isRanged() {
        return !isMelee;
    }
}
