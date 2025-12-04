package com.wasteland.statuseffects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages status effects for all players
 */
public class StatusEffectManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, PlayerStatusEffects> EFFECTS = new HashMap<>();

    /**
     * Get or create player status effects
     */
    public static PlayerStatusEffects getEffects(UUID playerId) {
        return EFFECTS.computeIfAbsent(playerId, PlayerStatusEffects::new);
    }

    /**
     * Add a status effect to a player
     */
    public static boolean addEffect(UUID playerId, StatusEffect effect, int durationTicks) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.addEffect(effect, durationTicks);
    }

    /**
     * Add a status effect with default duration
     */
    public static boolean addEffect(UUID playerId, StatusEffect effect) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.addEffect(effect);
    }

    /**
     * Remove a status effect from a player
     */
    public static void removeEffect(UUID playerId, StatusEffect effect) {
        PlayerStatusEffects effects = getEffects(playerId);
        effects.removeEffect(effect);
    }

    /**
     * Check if player has an effect
     */
    public static boolean hasEffect(UUID playerId, StatusEffect effect) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.hasEffect(effect);
    }

    /**
     * Get remaining duration for an effect
     */
    public static int getRemainingDuration(UUID playerId, StatusEffect effect) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.getRemainingDuration(effect);
    }

    /**
     * Get all active effects for a player
     */
    public static Map<StatusEffect, Integer> getActiveEffects(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.getActiveEffects();
    }

    /**
     * Update all effects for a player (call every tick)
     */
    public static List<StatusEffect> tick(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.tick();
    }

    /**
     * Clear all effects for a player
     */
    public static void clearAllEffects(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        effects.clearAllEffects();
    }

    /**
     * Remove player effects (cleanup)
     */
    public static void removeEffects(UUID playerId) {
        EFFECTS.remove(playerId);
        LOGGER.debug("Removed status effect data for player {}", playerId);
    }

    /**
     * Update all player effects (call from server tick event)
     */
    public static void tickAllPlayers() {
        for (PlayerStatusEffects effects : EFFECTS.values()) {
            List<StatusEffect> expired = effects.tick();

            // Handle special expiration effects
            for (StatusEffect effect : expired) {
                handleExpiration(effects.getPlayerId(), effect);
            }
        }
    }

    /**
     * Handle special logic when an effect expires
     */
    private static void handleExpiration(UUID playerId, StatusEffect effect) {
        switch (effect) {
            case BERSERK:
                // Berserking causes exhaustion when it ends
                addEffect(playerId, StatusEffect.EXHAUSTED);
                LOGGER.info("Player {} is exhausted after berserking", playerId);
                break;

            case PETRIFYING:
                // Petrifying leads to paralysis
                addEffect(playerId, StatusEffect.PARALYSIS);
                LOGGER.info("Player {} turned to stone!", playerId);
                break;

            case TELEPORTING:
                // Teleporting effect triggers the actual teleport
                LOGGER.info("Player {} teleported!", playerId);
                // TODO: Implement actual teleport logic
                break;

            default:
                // Most effects just expire normally
                break;
        }
    }

    /**
     * Check if player can berserk (not already berserk or exhausted)
     */
    public static boolean canBerserk(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return !effects.hasEffect(StatusEffect.BERSERK)
            && !effects.hasEffect(StatusEffect.EXHAUSTED);
    }

    /**
     * Check if player can cast spells (not silenced)
     */
    public static boolean canCastSpells(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return !effects.hasEffect(StatusEffect.SILENCED);
    }

    /**
     * Check if player can move (not paralyzed or mesmerized)
     */
    public static boolean canMove(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return !effects.hasEffect(StatusEffect.PARALYSIS);
    }

    /**
     * Check if player is confused
     */
    public static boolean isConfused(UUID playerId) {
        PlayerStatusEffects effects = getEffects(playerId);
        return effects.hasEffect(StatusEffect.CONFUSION);
    }
}
