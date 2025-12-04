package com.wasteland.religion;

/**
 * Divine abilities granted by gods at various piety levels
 */
public class DivineAbility {
    private final String id;
    private final String name;
    private final String description;
    private final int minPiety;
    private final int maxPiety; // -1 means no max
    private final int invocationCost; // 0 means passive ability
    private final AbilityType type;

    public DivineAbility(String id, String name, String description, int minPiety, int maxPiety,
                        int invocationCost, AbilityType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minPiety = minPiety;
        this.maxPiety = maxPiety;
        this.invocationCost = invocationCost;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinPiety() {
        return minPiety;
    }

    public int getMaxPiety() {
        return maxPiety;
    }

    public int getInvocationCost() {
        return invocationCost;
    }

    public AbilityType getType() {
        return type;
    }

    public boolean isPassive() {
        return invocationCost == 0;
    }

    public boolean isAvailableAtPiety(int piety) {
        if (piety < minPiety) return false;
        if (maxPiety == -1) return true;
        return piety <= maxPiety;
    }

    /**
     * Types of divine abilities
     */
    public enum AbilityType {
        PASSIVE,      // Always active when requirements met
        ACTIVE,       // Must be invoked
        BERSERK,      // Berserk rage
        HEAL,         // Healing
        SUMMON,       // Summon allies
        ATTACK,       // Direct attack
        BUFF,         // Buff player
        UTILITY       // Misc utility
    }

    /**
     * Builder for divine abilities
     */
    public static class Builder {
        private String id;
        private String name;
        private String description;
        private int minPiety = 0;
        private int maxPiety = -1;
        private int invocationCost = 0;
        private AbilityType type = AbilityType.ACTIVE;

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder minPiety(int minPiety) {
            this.minPiety = minPiety;
            return this;
        }

        public Builder maxPiety(int maxPiety) {
            this.maxPiety = maxPiety;
            return this;
        }

        public Builder invocationCost(int invocationCost) {
            this.invocationCost = invocationCost;
            return this;
        }

        public Builder type(AbilityType type) {
            this.type = type;
            return this;
        }

        public Builder passive() {
            this.type = AbilityType.PASSIVE;
            this.invocationCost = 0;
            return this;
        }

        public DivineAbility build() {
            return new DivineAbility(id, name, description, minPiety, maxPiety, invocationCost, type);
        }
    }
}
