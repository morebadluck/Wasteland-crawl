package com.wasteland.statuseffects;

/**
 * Status effects - temporary buffs and debuffs
 * Similar to DCSS status effects from spells, abilities, and items
 */
public enum StatusEffect {
    // DIVINE ABILITY BUFFS
    BERSERK(
        "Berserk",
        "You are in a berserker rage! (+50% damage, +50% speed, -50% defense)",
        EffectType.BUFF,
        200  // 10 seconds at 20 ticks/second
    ),
    TROGS_HAND(
        "Trog's Hand",
        "Trog's hand aids you in battle. (+20% damage, +2 AC)",
        EffectType.BUFF,
        600  // 30 seconds
    ),
    DIVINE_SHIELD(
        "Divine Shield",
        "You are protected by divine power. (+5 AC, +2 magic resistance)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    HEROISM(
        "Heroism",
        "You fight with heroic strength! (+3 STR, +3 DEX)",
        EffectType.BUFF,
        600  // 30 seconds
    ),
    FINESSE(
        "Finesse",
        "Your actions are supernaturally swift. (+50% attack speed)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    DIVINE_VIGOR(
        "Divine Vigor",
        "Divine energy fills you. (Regenerate HP and MP rapidly)",
        EffectType.BUFF,
        300  // 15 seconds
    ),
    BRILLIANCE(
        "Brilliance",
        "Your mind is enhanced. (+5 INT, -50% spell costs)",
        EffectType.BUFF,
        600  // 30 seconds
    ),
    CHANNEL_MAGIC(
        "Channel Magic",
        "You channel magical energy. (+50% MP regeneration)",
        EffectType.BUFF,
        1200  // 60 seconds
    ),

    // SPELL BUFFS
    HASTE(
        "Haste",
        "You are moving and acting faster. (+50% speed)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    REGENERATION(
        "Regeneration",
        "You are regenerating health rapidly.",
        EffectType.BUFF,
        600  // 30 seconds
    ),
    STONESKIN(
        "Stoneskin",
        "Your skin is hard as stone. (+5 AC)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    RESISTANCE(
        "Resistance",
        "You resist the elements. (+1 to all resistances)",
        EffectType.BUFF,
        800  // 40 seconds
    ),
    INVISIBILITY(
        "Invisibility",
        "You are invisible.",
        EffectType.BUFF,
        300  // 15 seconds
    ),
    MIGHT(
        "Might",
        "You are supernaturally strong. (+5 STR)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    AGILITY(
        "Agility",
        "You are supernaturally agile. (+5 DEX)",
        EffectType.BUFF,
        400  // 20 seconds
    ),
    BRILLIANCE_SPELL(
        "Brilliance",
        "You are supernaturally clever. (+5 INT)",
        EffectType.BUFF,
        400  // 20 seconds
    ),

    // DEBUFFS
    SLOW(
        "Slow",
        "You are moving and acting slower. (-50% speed)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),
    CONFUSION(
        "Confusion",
        "You are confused and cannot control your actions.",
        EffectType.DEBUFF,
        200  // 10 seconds
    ),
    PARALYSIS(
        "Paralysis",
        "You are paralyzed and cannot move or act!",
        EffectType.DEBUFF,
        100  // 5 seconds
    ),
    POISON(
        "Poison",
        "You are poisoned. (Take damage over time)",
        EffectType.DEBUFF,
        600  // 30 seconds
    ),
    BURNING(
        "Burning",
        "You are on fire! (Take fire damage)",
        EffectType.DEBUFF,
        200  // 10 seconds
    ),
    WEAKNESS(
        "Weakness",
        "You feel weak. (-3 STR)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),
    CLUMSINESS(
        "Clumsiness",
        "You feel clumsy. (-3 DEX)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),
    STUPIDITY(
        "Stupidity",
        "You feel stupid. (-3 INT)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),
    PETRIFYING(
        "Petrifying",
        "You are turning to stone! (-50% speed, will become paralyzed)",
        EffectType.DEBUFF,
        100  // 5 seconds
    ),
    CORROSION(
        "Corrosion",
        "You are corroded. (-4 AC)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),

    // SPECIAL STATES
    EXHAUSTED(
        "Exhausted",
        "You are exhausted from berserking. (-20% speed, no berserking)",
        EffectType.DEBUFF,
        400  // 20 seconds
    ),
    TELEPORTING(
        "Teleporting",
        "You are about to teleport.",
        EffectType.SPECIAL,
        60  // 3 seconds delay
    ),
    MESMERIZED(
        "Mesmerized",
        "You are mesmerized and cannot move away.",
        EffectType.DEBUFF,
        200  // 10 seconds
    ),
    SILENCED(
        "Silenced",
        "You cannot speak or cast spells!",
        EffectType.DEBUFF,
        300  // 15 seconds
    );

    private final String displayName;
    private final String description;
    private final EffectType type;
    private final int defaultDuration; // in ticks (20 ticks = 1 second)

    StatusEffect(String displayName, String description, EffectType type, int defaultDuration) {
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.defaultDuration = defaultDuration;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public EffectType getType() {
        return type;
    }

    public int getDefaultDuration() {
        return defaultDuration;
    }

    /**
     * Check if this effect conflicts with another
     */
    public boolean conflictsWith(StatusEffect other) {
        // Opposite effects conflict
        if (this == HASTE && other == SLOW) return true;
        if (this == SLOW && other == HASTE) return true;
        if (this == MIGHT && other == WEAKNESS) return true;
        if (this == WEAKNESS && other == MIGHT) return true;
        if (this == AGILITY && other == CLUMSINESS) return true;
        if (this == CLUMSINESS && other == AGILITY) return true;
        if (this == BRILLIANCE_SPELL && other == STUPIDITY) return true;
        if (this == STUPIDITY && other == BRILLIANCE_SPELL) return true;

        // Berserk conflicts with exhaustion
        if (this == BERSERK && other == EXHAUSTED) return true;
        if (this == EXHAUSTED && other == BERSERK) return true;

        return false;
    }

    /**
     * Get color code for this effect type
     */
    public String getColorCode() {
        return type.getColorCode();
    }
}
