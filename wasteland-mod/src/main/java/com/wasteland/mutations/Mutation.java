package com.wasteland.mutations;

/**
 * Mutations from DCSS - permanent character modifications
 * Can be good, bad, or body modifications
 */
public enum Mutation {
    // GOOD MUTATIONS
    REGENERATION(
        "Regeneration",
        "You regenerate health rapidly.",
        MutationType.GOOD,
        3  // Max levels
    ),
    CLARITY(
        "Clarity",
        "You are immune to confusion.",
        MutationType.GOOD,
        1
    ),
    POISON_RESISTANCE(
        "Poison Resistance",
        "You are resistant to poison.",
        MutationType.GOOD,
        3
    ),
    MAGIC_RESISTANCE(
        "Magic Resistance",
        "You are resistant to hostile enchantments.",
        MutationType.GOOD,
        3
    ),
    ROBUST(
        "Robust",
        "You have increased maximum health.",
        MutationType.GOOD,
        3
    ),
    HIGH_MP(
        "High MP",
        "You have increased maximum magic.",
        MutationType.GOOD,
        3
    ),
    STRONG_LEGS(
        "Strong Legs",
        "You can jump higher.",
        MutationType.GOOD,
        2
    ),

    // BAD MUTATIONS
    TELEPORTITIS(
        "Teleportitis",
        "You randomly teleport.",
        MutationType.BAD,
        3
    ),
    DETERIORATION(
        "Deterioration",
        "Your body deteriorates over time.",
        MutationType.BAD,
        3
    ),
    FRAIL(
        "Frail",
        "You have reduced maximum health.",
        MutationType.BAD,
        3
    ),
    LOW_MP(
        "Low MP",
        "You have reduced maximum magic.",
        MutationType.BAD,
        3
    ),
    SLOW_HEALING(
        "Slow Healing",
        "You heal slowly.",
        MutationType.BAD,
        3
    ),
    WEAK(
        "Weak",
        "You are physically weak.",
        MutationType.BAD,
        2
    ),
    CLUMSY(
        "Clumsy",
        "You are clumsy.",
        MutationType.BAD,
        2
    ),
    DOPEY(
        "Dopey",
        "You are slow-witted.",
        MutationType.BAD,
        2
    ),
    BLURRY_VISION(
        "Blurry Vision",
        "Your vision is blurred.",
        MutationType.BAD,
        3
    ),
    SCREAMING(
        "Screaming",
        "You occasionally scream uncontrollably.",
        MutationType.BAD,
        3
    ),

    // BODY MUTATIONS
    CLAWS(
        "Claws",
        "You have sharp claws for hands.",
        MutationType.BODY,
        3
    ),
    HORNS(
        "Horns",
        "You have horns on your head.",
        MutationType.BODY,
        3
    ),
    HOOVES(
        "Hooves",
        "You have hooves for feet.",
        MutationType.BODY,
        3
    ),
    FANGS(
        "Fangs",
        "You have elongated fangs.",
        MutationType.BODY,
        3
    ),
    TAIL(
        "Tail",
        "You have a tail.",
        MutationType.BODY,
        3
    ),
    WINGS(
        "Wings",
        "You have wings (non-functional).",
        MutationType.BODY,
        3
    ),
    SCALES(
        "Scales",
        "You are partially covered in scales.",
        MutationType.BODY,
        3
    ),
    FUR(
        "Fur",
        "You are covered in thick fur.",
        MutationType.BODY,
        3
    ),
    TENTACLES(
        "Tentacles",
        "You have tentacles for arms.",
        MutationType.BODY,
        2
    ),

    // MIXED MUTATIONS
    BERSERK_RAGE(
        "Berserk Rage",
        "You tend to lose your temper in combat.",
        MutationType.MIXED,
        3
    ),
    FAST_METABOLISM(
        "Fast Metabolism",
        "You have a fast metabolism.",
        MutationType.MIXED,
        3
    ),
    SLOW_METABOLISM(
        "Slow Metabolism",
        "You have a slow metabolism.",
        MutationType.MIXED,
        2
    );

    private final String displayName;
    private final String description;
    private final MutationType type;
    private final int maxLevel;

    Mutation(String displayName, String description, MutationType type, int maxLevel) {
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.maxLevel = maxLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public MutationType getType() {
        return type;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Get description with level
     */
    public String getDescriptionWithLevel(int level) {
        if (level <= 0 || level > maxLevel) {
            return description;
        }

        // Some mutations scale with level
        return switch (this) {
            case REGENERATION -> String.format("You regenerate health %s rapidly.",
                level == 3 ? "very" : level == 2 ? "quite" : "");
            case POISON_RESISTANCE -> level == 3 ? "You are immune to poison." :
                                     level == 2 ? "You are highly resistant to poison." :
                                     "You are resistant to poison.";
            case MAGIC_RESISTANCE -> String.format("You are %s resistant to hostile enchantments.",
                level == 3 ? "very" : level == 2 ? "quite" : "");
            case ROBUST -> String.format("You have %d%% increased maximum health.", level * 10);
            case HIGH_MP -> String.format("You have %d%% increased maximum magic.", level * 10);
            case FRAIL -> String.format("You have %d%% reduced maximum health.", level * 10);
            case LOW_MP -> String.format("You have %d%% reduced maximum magic.", level * 10);
            case CLAWS -> String.format("You have %s claws for hands.",
                level == 3 ? "razor-sharp" : level == 2 ? "sharp" : "");
            case HORNS -> String.format("You have %s horns on your head.",
                level == 3 ? "large" : level == 2 ? "medium" : "small");
            case SCALES -> String.format("You are %s covered in scales (AC +%d).",
                level == 3 ? "completely" : level == 2 ? "mostly" : "partially", level);
            default -> description;
        };
    }

    /**
     * Check if this is a conflicting mutation
     */
    public boolean conflictsWith(Mutation other) {
        // Opposite mutations conflict
        if (this == ROBUST && other == FRAIL) return true;
        if (this == FRAIL && other == ROBUST) return true;
        if (this == HIGH_MP && other == LOW_MP) return true;
        if (this == LOW_MP && other == HIGH_MP) return true;
        if (this == REGENERATION && other == SLOW_HEALING) return true;
        if (this == SLOW_HEALING && other == REGENERATION) return true;
        if (this == FAST_METABOLISM && other == SLOW_METABOLISM) return true;
        if (this == SLOW_METABOLISM && other == FAST_METABOLISM) return true;

        // Body part conflicts
        if (this == CLAWS && (other == TENTACLES)) return true;
        if (this == TENTACLES && (other == CLAWS)) return true;
        if (this == HOOVES && (other == TAIL)) return true;
        if (this == TAIL && (other == HOOVES)) return true;

        return false;
    }

    /**
     * Get all good mutations
     */
    public static Mutation[] getGoodMutations() {
        return java.util.Arrays.stream(values())
            .filter(m -> m.type == MutationType.GOOD)
            .toArray(Mutation[]::new);
    }

    /**
     * Get all bad mutations
     */
    public static Mutation[] getBadMutations() {
        return java.util.Arrays.stream(values())
            .filter(m -> m.type == MutationType.BAD)
            .toArray(Mutation[]::new);
    }

    /**
     * Get all body mutations
     */
    public static Mutation[] getBodyMutations() {
        return java.util.Arrays.stream(values())
            .filter(m -> m.type == MutationType.BODY)
            .toArray(Mutation[]::new);
    }
}
