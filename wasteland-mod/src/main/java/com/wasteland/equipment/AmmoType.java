package com.wasteland.equipment;

/**
 * Ammunition types - replaces DCSS stones/arrows with wasteland ammo
 */
public enum AmmoType {
    // TRADITIONAL PROJECTILES
    ARROW("Arrow", "Wooden or metal arrows for bows", 1),
    BOLT("Bolt", "Crossbow bolts", 2),

    // GUN AMMUNITION (replaces stones)
    AMMO_22("22LR Ammo", "Small caliber rounds for pistols", 3), // 1 ammo = 1 stone equivalent for now
    AMMO_9MM("9mm Ammo", "Standard pistol ammunition", 4),
    AMMO_45("45 ACP Ammo", "Heavy pistol rounds", 5),
    AMMO_556("5.56mm Ammo", "Rifle ammunition", 6),
    AMMO_762("7.62mm Ammo", "Heavy rifle/machine gun rounds", 7),
    AMMO_SHOTGUN("Shotgun Shell", "12 gauge shells", 8),
    AMMO_ENERGY("Energy Cell", "Power cells for energy weapons", 10),

    // THROWING
    THROWING_KNIFE("Throwing Knife", "Balanced knife", 2),
    GRENADE("Grenade", "Explosive grenade", 15),
    MOLOTOV("Molotov Cocktail", "Fire bomb", 8),
    PIPE_BOMB("Pipe Bomb", "Makeshift explosive", 12);

    private final String displayName;
    private final String description;
    private final int baseDamage;

    AmmoType(String displayName, String description, int baseDamage) {
        this.displayName = displayName;
        this.description = description;
        this.baseDamage = baseDamage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public boolean isGunAmmo() {
        return this == AMMO_22 || this == AMMO_9MM || this == AMMO_45
            || this == AMMO_556 || this == AMMO_762 || this == AMMO_SHOTGUN
            || this == AMMO_ENERGY;
    }
}
