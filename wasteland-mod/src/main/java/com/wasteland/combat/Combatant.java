package com.wasteland.combat;

import net.minecraft.world.entity.LivingEntity;

/**
 * Wrapper class for entities participating in combat.
 * Tracks combat-specific state for each combatant.
 */
public class Combatant {
    private final LivingEntity entity;
    private final boolean isPlayer;
    private int actionPointsRemaining = 1; // For future multi-action turns
    private boolean hasActedThisTurn = false;

    public Combatant(LivingEntity entity, boolean isPlayer) {
        this.entity = entity;
        this.isPlayer = isPlayer;
    }

    /**
     * Get the wrapped entity
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * Check if this is the player
     */
    public boolean isPlayer() {
        return isPlayer;
    }

    /**
     * Get combatant speed (for turn order)
     * TODO: Integrate with DCSS stat system
     */
    public int getSpeed() {
        // Default speed is 10
        // Haste effects would increase this
        // Slow effects would decrease this
        return 10;
    }

    /**
     * Get current HP
     */
    public float getCurrentHP() {
        return entity.getHealth();
    }

    /**
     * Get max HP
     */
    public float getMaxHP() {
        return entity.getMaxHealth();
    }

    /**
     * Check if combatant is alive
     */
    public boolean isAlive() {
        return entity.isAlive() && entity.getHealth() > 0;
    }

    /**
     * Check if combatant is dead
     */
    public boolean isDead() {
        return !isAlive();
    }

    /**
     * Get action points remaining this turn
     */
    public int getActionPoints() {
        return actionPointsRemaining;
    }

    /**
     * Use action points
     */
    public void useActionPoints(int amount) {
        actionPointsRemaining -= amount;
    }

    /**
     * Reset for new turn
     */
    public void startTurn() {
        actionPointsRemaining = 1; // Standard 1 action per turn
        hasActedThisTurn = false;
    }

    /**
     * Mark turn as complete
     */
    public void endTurn() {
        hasActedThisTurn = true;
        actionPointsRemaining = 0;
    }

    /**
     * Check if has acted this turn
     */
    public boolean hasActed() {
        return hasActedThisTurn;
    }

    /**
     * Get display name
     */
    public String getName() {
        return entity.getName().getString();
    }

    @Override
    public String toString() {
        return getName() + " (HP: " + getCurrentHP() + "/" + getMaxHP() + ")";
    }
}
