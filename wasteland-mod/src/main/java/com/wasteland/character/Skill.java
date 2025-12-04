package com.wasteland.character;

/**
 * DCSS skills that players can train
 * Skills improve through use and XP investment
 */
public enum Skill {
    // Combat skills
    FIGHTING("Fighting", SkillCategory.COMBAT, "General combat prowess and HP"),
    SHORT_BLADES("Short Blades", SkillCategory.COMBAT, "Daggers and short swords"),
    LONG_BLADES("Long Blades", SkillCategory.COMBAT, "Swords and scimitars"),
    AXES("Axes", SkillCategory.COMBAT, "Hand axes and battleaxes"),
    MACES_FLAILS("Maces & Flails", SkillCategory.COMBAT, "Clubs, maces, and flails"),
    POLEARMS("Polearms", SkillCategory.COMBAT, "Spears, tridents, and halberds"),
    STAVES("Staves", SkillCategory.COMBAT, "Quarterstaves and magical staves"),
    UNARMED_COMBAT("Unarmed Combat", SkillCategory.COMBAT, "Fighting with fists and claws"),

    // Ranged combat
    THROWING("Throwing", SkillCategory.RANGED, "Thrown weapons and explosives"),
    BOWS("Bows", SkillCategory.RANGED, "Bows and arrows"),
    CROSSBOWS("Crossbows", SkillCategory.RANGED, "Crossbows and bolts"),
    GUNS("Guns", SkillCategory.RANGED, "Firearms and energy weapons"),

    // Defense skills
    ARMOUR("Armour", SkillCategory.DEFENSE, "Wearing heavy armor effectively"),
    DODGING("Dodging", SkillCategory.DEFENSE, "Evading attacks"),
    SHIELDS("Shields", SkillCategory.DEFENSE, "Using shields to block"),
    STEALTH("Stealth", SkillCategory.DEFENSE, "Moving silently and unseen"),

    // Magic schools
    SPELLCASTING("Spellcasting", SkillCategory.MAGIC, "General magic power and MP"),
    CONJURATIONS("Conjurations", SkillCategory.MAGIC, "Offensive damage spells"),
    HEXES("Hexes", SkillCategory.MAGIC, "Debuffs and curses"),
    SUMMONINGS("Summonings", SkillCategory.MAGIC, "Summoning creatures"),
    NECROMANCY("Necromancy", SkillCategory.MAGIC, "Death magic and undead"),
    TRANSLOCATIONS("Translocations", SkillCategory.MAGIC, "Teleportation and spatial magic"),
    TRANSMUTATIONS("Transmutations", SkillCategory.MAGIC, "Shape-shifting and transformation"),
    FIRE_MAGIC("Fire Magic", SkillCategory.MAGIC, "Fire spells"),
    ICE_MAGIC("Ice Magic", SkillCategory.MAGIC, "Ice and cold spells"),
    AIR_MAGIC("Air Magic", SkillCategory.MAGIC, "Lightning and wind spells"),
    EARTH_MAGIC("Earth Magic", SkillCategory.MAGIC, "Stone and poison spells"),
    POISON_MAGIC("Poison Magic", SkillCategory.MAGIC, "Poison and venom spells"),

    // Utility skills
    INVOCATIONS("Invocations", SkillCategory.UTILITY, "Using god-given abilities"),
    EVOCATIONS("Evocations", SkillCategory.UTILITY, "Using magical items"),
    SHAPESHIFTING("Shapeshifting", SkillCategory.UTILITY, "Transforming into beasts");

    private final String displayName;
    private final SkillCategory category;
    private final String description;

    Skill(String displayName, SkillCategory category, String description) {
        this.displayName = displayName;
        this.category = category;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SkillCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Skill categories for organization
     */
    public enum SkillCategory {
        COMBAT("Combat"),
        RANGED("Ranged Combat"),
        DEFENSE("Defense"),
        MAGIC("Magic"),
        UTILITY("Utility");

        private final String displayName;

        SkillCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Get skills by category
     */
    public static Skill[] getSkillsByCategory(SkillCategory category) {
        return java.util.Arrays.stream(values())
                .filter(skill -> skill.getCategory() == category)
                .toArray(Skill[]::new);
    }
}
