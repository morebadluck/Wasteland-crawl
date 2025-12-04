package com.wasteland.equipment;

import com.wasteland.character.Skill;

/**
 * All weapon types in the Wasteland mod
 * Based on DCSS weapons but adapted for post-apocalyptic setting
 */
public enum WeaponType {
    // ===== SHORT BLADES =====
    SHIV("Shiv", "Makeshift prison knife", WeaponCategory.SHORT_BLADES, 3, 10, 1, 0),
    COMBAT_KNIFE("Combat Knife", "Military-grade fighting knife", WeaponCategory.SHORT_BLADES, 5, 10, 1, 0),
    MACHETE("Machete", "Heavy chopping blade", WeaponCategory.SHORT_BLADES, 7, 12, 1, 0),

    // ===== LONG BLADES =====
    RUSTY_SWORD("Rusty Sword", "Pre-war blade, seen better days", WeaponCategory.LONG_BLADES, 7, 14, 1, 0),
    SCRAP_BLADE("Scrap Blade", "Crafted from salvaged metal", WeaponCategory.LONG_BLADES, 8, 14, 1, 0),
    KATANA("Katana", "Rare pre-war ceremonial sword", WeaponCategory.LONG_BLADES, 10, 12, 2, 0),
    RIPPER("Ripper", "Motorized chainsaw blade", WeaponCategory.LONG_BLADES, 13, 16, 2, 0),

    // ===== AXES =====
    HATCHET("Hatchet", "Small chopping axe", WeaponCategory.AXES, 7, 13, 1, 0),
    FIRE_AXE("Fire Axe", "Heavy firefighter's axe", WeaponCategory.AXES, 11, 15, 2, 0),
    SUPER_SLEDGE("Super Sledge", "Rocket-powered sledgehammer", WeaponCategory.AXES, 18, 18, 3, 0),

    // ===== MACES & FLAILS =====
    PIPE("Lead Pipe", "Heavy metal pipe", WeaponCategory.MACES_FLAILS, 5, 13, 1, 0),
    CROWBAR("Crowbar", "Useful tool and weapon", WeaponCategory.MACES_FLAILS, 6, 13, 1, 0),
    BASEBALL_BAT("Baseball Bat", "Pre-war sports equipment", WeaponCategory.MACES_FLAILS, 7, 13, 1, 0),
    SLEDGEHAMMER("Sledgehammer", "Heavy construction hammer", WeaponCategory.MACES_FLAILS, 15, 17, 3, 0),
    PNEUMATIC_HAMMER("Pneumatic Hammer", "Powered jackhammer", WeaponCategory.MACES_FLAILS, 17, 18, 3, 0),

    // ===== POLEARMS =====
    SPEAR("Spear", "Sharpened metal on a pole", WeaponCategory.POLEARMS, 6, 13, 2, 0),
    PITCHFORK("Pitchfork", "Farming implement", WeaponCategory.POLEARMS, 8, 14, 2, 0),
    HALBERD("Halberd", "Axe-spear combination", WeaponCategory.POLEARMS, 13, 15, 2, 0),

    // ===== STAVES =====
    WOODEN_STAFF("Wooden Staff", "Simple quarterstaff", WeaponCategory.STAVES, 5, 13, 2, 0),
    REBAR_STAFF("Rebar Staff", "Reinforced metal staff", WeaponCategory.STAVES, 8, 14, 2, 0),

    // ===== BOWS =====
    MAKESHIFT_BOW("Makeshift Bow", "Scavenged parts bow", WeaponCategory.BOWS, 9, 13, 2, AmmoType.ARROW),
    COMPOUND_BOW("Compound Bow", "Modern hunting bow", WeaponCategory.BOWS, 15, 15, 2, AmmoType.ARROW),

    // ===== CROSSBOWS =====
    HAND_CROSSBOW("Hand Crossbow", "One-handed crossbow", WeaponCategory.CROSSBOWS, 12, 15, 1, AmmoType.BOLT),
    CROSSBOW("Crossbow", "Standard crossbow", WeaponCategory.CROSSBOWS, 18, 19, 2, AmmoType.BOLT),

    // ===== GUNS (REPLACES SLINGS) =====
    // Tier 1: Pistols (Light)
    PIPE_PISTOL("Pipe Pistol", "Makeshift gun from scrap", WeaponCategory.GUNS, 8, 12, 1, AmmoType.AMMO_22),
    PISTOL_22("22 Pistol", "Light caliber pistol", WeaponCategory.GUNS, 10, 12, 1, AmmoType.AMMO_22),

    // Tier 2: Pistols (Medium)
    PISTOL_9MM("9mm Pistol", "Standard sidearm", WeaponCategory.GUNS, 12, 12, 1, AmmoType.AMMO_9MM),
    PISTOL_45("45 Pistol", "Heavy pistol", WeaponCategory.GUNS, 15, 13, 1, AmmoType.AMMO_45),

    // Tier 3: Pistols (Heavy)
    MAGNUM_44("44 Magnum", "Powerful revolver", WeaponCategory.GUNS, 18, 14, 1, AmmoType.AMMO_45),
    DESERT_EAGLE("Desert Eagle", "High-powered pistol", WeaponCategory.GUNS, 20, 14, 1, AmmoType.AMMO_45),

    // Tier 4: Rifles (Light)
    HUNTING_RIFLE("Hunting Rifle", "Civilian rifle", WeaponCategory.GUNS, 16, 16, 2, AmmoType.AMMO_556),
    VARMINT_RIFLE("Varmint Rifle", "Small game rifle", WeaponCategory.GUNS, 14, 15, 2, AmmoType.AMMO_22),

    // Tier 5: Rifles (Combat)
    ASSAULT_RIFLE("Assault Rifle", "Military automatic rifle", WeaponCategory.GUNS, 20, 15, 2, AmmoType.AMMO_556),
    BATTLE_RIFLE("Battle Rifle", "Heavy combat rifle", WeaponCategory.GUNS, 24, 17, 2, AmmoType.AMMO_762),
    SNIPER_RIFLE("Sniper Rifle", "Long-range precision rifle", WeaponCategory.GUNS, 28, 18, 2, AmmoType.AMMO_762),

    // Tier 6: Shotguns
    SAWED_OFF_SHOTGUN("Sawed-Off Shotgun", "Close-range devastation", WeaponCategory.GUNS, 22, 14, 1, AmmoType.AMMO_SHOTGUN),
    COMBAT_SHOTGUN("Combat Shotgun", "Tactical shotgun", WeaponCategory.GUNS, 26, 15, 2, AmmoType.AMMO_SHOTGUN),

    // Tier 7: Energy Weapons (Rare/Late Game)
    LASER_PISTOL("Laser Pistol", "Pre-war energy sidearm", WeaponCategory.GUNS, 18, 12, 1, AmmoType.AMMO_ENERGY),
    LASER_RIFLE("Laser Rifle", "Military energy weapon", WeaponCategory.GUNS, 26, 14, 2, AmmoType.AMMO_ENERGY),
    PLASMA_RIFLE("Plasma Rifle", "Advanced energy weapon", WeaponCategory.GUNS, 32, 16, 2, AmmoType.AMMO_ENERGY),
    GAUSS_RIFLE("Gauss Rifle", "Electromagnetic railgun", WeaponCategory.GUNS, 35, 18, 2, AmmoType.AMMO_ENERGY),

    // ===== THROWING =====
    THROWING_KNIFE("Throwing Knife", "Balanced blade", WeaponCategory.THROWING, 3, 10, 1, AmmoType.THROWING_KNIFE),
    GRENADE("Grenade", "Explosive device", WeaponCategory.THROWING, 25, 15, 1, AmmoType.GRENADE),
    MOLOTOV("Molotov Cocktail", "Fire bomb", WeaponCategory.THROWING, 15, 13, 1, AmmoType.MOLOTOV);

    private final String displayName;
    private final String description;
    private final WeaponCategory category;
    private final int baseDamage;
    private final int delay; // Attack speed (lower = faster)
    private final int handedness; // 0=unarmed, 1=one-handed, 2=two-handed, 3=heavy two-handed
    private final AmmoType ammoType; // null for melee weapons

    // Melee weapon constructor
    WeaponType(String displayName, String description, WeaponCategory category,
               int baseDamage, int delay, int handedness, int unused) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.baseDamage = baseDamage;
        this.delay = delay;
        this.handedness = handedness;
        this.ammoType = null;
    }

    // Ranged weapon constructor
    WeaponType(String displayName, String description, WeaponCategory category,
               int baseDamage, int delay, int handedness, AmmoType ammoType) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
        this.baseDamage = baseDamage;
        this.delay = delay;
        this.handedness = handedness;
        this.ammoType = ammoType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public WeaponCategory getCategory() {
        return category;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getDelay() {
        return delay;
    }

    public AmmoType getAmmoType() {
        return ammoType;
    }

    public boolean isMelee() {
        return ammoType == null;
    }

    public boolean isRanged() {
        return ammoType != null;
    }

    public boolean requiresAmmo() {
        return ammoType != null;
    }

    public boolean isGun() {
        return category == WeaponCategory.GUNS;
    }

    /**
     * Get gun tier for balancing (1-7, higher = more powerful)
     */
    public int getGunTier() {
        if (!isGun()) return 0;

        return switch (this) {
            case PIPE_PISTOL, PISTOL_22 -> 1;
            case PISTOL_9MM, PISTOL_45 -> 2;
            case MAGNUM_44, DESERT_EAGLE -> 3;
            case HUNTING_RIFLE, VARMINT_RIFLE -> 4;
            case ASSAULT_RIFLE, BATTLE_RIFLE, SNIPER_RIFLE -> 5;
            case SAWED_OFF_SHOTGUN, COMBAT_SHOTGUN -> 6;
            case LASER_PISTOL, LASER_RIFLE, PLASMA_RIFLE, GAUSS_RIFLE -> 7;
            default -> 0;
        };
    }

    // ===== LOOT SYSTEM COMPATIBILITY METHODS =====

    /**
     * Get the skill associated with this weapon
     */
    public Skill getSkill() {
        return switch (category) {
            case SHORT_BLADES -> Skill.SHORT_BLADES;
            case LONG_BLADES -> Skill.LONG_BLADES;
            case AXES -> Skill.AXES;
            case MACES_FLAILS -> Skill.MACES_FLAILS;
            case POLEARMS -> Skill.POLEARMS;
            case STAVES -> Skill.STAVES;
            case UNARMED -> Skill.UNARMED_COMBAT;
            case BOWS -> Skill.BOWS;
            case CROSSBOWS -> Skill.CROSSBOWS;
            case GUNS -> Skill.GUNS;
            case THROWING -> Skill.THROWING;
        };
    }

    /**
     * Get base attack delay (alias for getDelay for loot compatibility)
     */
    public int getBaseDelay() {
        return delay;
    }

    /**
     * Get handedness (0=unarmed, 1=one-handed, 2=two-handed, 3=heavy two-handed)
     */
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
        return (baseDamage * 10.0) / delay;
    }
}
