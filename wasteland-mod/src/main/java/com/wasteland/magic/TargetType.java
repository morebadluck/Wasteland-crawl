package com.wasteland.magic;

/**
 * Spell targeting types.
 * Determines how a spell selects its target(s).
 */
public enum TargetType {
    /**
     * Targets a single enemy (requires line of sight)
     */
    SINGLE_TARGET("Single Target", "Targets one enemy", true, false),

    /**
     * Targets self only
     */
    SELF("Self", "Affects only the caster", false, false),

    /**
     * Targets a location for area effect
     */
    LOCATION("Location", "Targets a location", false, true),

    /**
     * Area of effect around caster
     */
    AREA_AROUND_CASTER("Area (Caster)", "Affects area around caster", false, false),

    /**
     * Beam/projectile in a direction
     */
    BEAM("Beam", "Fires a beam in a direction", true, false),

    /**
     * Cloud effect at location
     */
    CLOUD("Cloud", "Creates a lingering cloud", false, true),

    /**
     * All enemies in radius
     */
    ALL_ENEMIES("All Enemies", "Affects all nearby enemies", false, false),

    /**
     * Smite targeting (ignores LOS)
     */
    SMITE("Smite", "Targets enemy ignoring obstacles", true, false);

    private final String displayName;
    private final String description;
    private final boolean requiresTarget;
    private final boolean requiresLocation;

    TargetType(String displayName, String description, boolean requiresTarget, boolean requiresLocation) {
        this.displayName = displayName;
        this.description = description;
        this.requiresTarget = requiresTarget;
        this.requiresLocation = requiresLocation;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Does this spell require selecting an enemy target?
     */
    public boolean requiresTarget() {
        return requiresTarget;
    }

    /**
     * Does this spell require selecting a location?
     */
    public boolean requiresLocation() {
        return requiresLocation;
    }

    /**
     * Does this spell require any targeting at all?
     */
    public boolean requiresTargeting() {
        return requiresTarget || requiresLocation;
    }
}
