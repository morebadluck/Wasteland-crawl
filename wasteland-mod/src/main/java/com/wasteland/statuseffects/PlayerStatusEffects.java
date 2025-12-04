package com.wasteland.statuseffects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Tracks a player's active status effects with durations
 */
public class PlayerStatusEffects {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UUID playerId;
    private final Map<StatusEffect, Integer> activeEffects; // Effect -> remaining ticks

    public PlayerStatusEffects(UUID playerId) {
        this.playerId = playerId;
        this.activeEffects = new HashMap<>();
    }

    /**
     * Add a status effect or refresh its duration
     */
    public boolean addEffect(StatusEffect effect, int durationTicks) {
        // Check for conflicts
        for (StatusEffect active : activeEffects.keySet()) {
            if (effect.conflictsWith(active)) {
                LOGGER.debug("Player {} cannot gain {} - conflicts with {}",
                           playerId, effect.getDisplayName(), active.getDisplayName());
                return false;
            }
        }

        // Add or refresh the effect
        activeEffects.put(effect, durationTicks);
        LOGGER.info("Player {} gained {} for {} ticks",
                   playerId, effect.getDisplayName(), durationTicks);
        return true;
    }

    /**
     * Add a status effect with default duration
     */
    public boolean addEffect(StatusEffect effect) {
        return addEffect(effect, effect.getDefaultDuration());
    }

    /**
     * Remove a status effect
     */
    public void removeEffect(StatusEffect effect) {
        if (activeEffects.remove(effect) != null) {
            LOGGER.info("Player {} lost {}", playerId, effect.getDisplayName());
        }
    }

    /**
     * Check if player has an effect
     */
    public boolean hasEffect(StatusEffect effect) {
        return activeEffects.containsKey(effect);
    }

    /**
     * Get remaining duration for an effect (in ticks)
     */
    public int getRemainingDuration(StatusEffect effect) {
        return activeEffects.getOrDefault(effect, 0);
    }

    /**
     * Get all active effects
     */
    public Map<StatusEffect, Integer> getActiveEffects() {
        return new HashMap<>(activeEffects);
    }

    /**
     * Update all effect durations (call every tick)
     * Returns list of effects that expired this tick
     */
    public List<StatusEffect> tick() {
        List<StatusEffect> expired = new ArrayList<>();

        Iterator<Map.Entry<StatusEffect, Integer>> iterator = activeEffects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<StatusEffect, Integer> entry = iterator.next();
            int newDuration = entry.getValue() - 1;

            if (newDuration <= 0) {
                expired.add(entry.getKey());
                iterator.remove();
                LOGGER.debug("Player {} - {} expired", playerId, entry.getKey().getDisplayName());
            } else {
                entry.setValue(newDuration);
            }
        }

        return expired;
    }

    /**
     * Clear all status effects
     */
    public void clearAllEffects() {
        activeEffects.clear();
        LOGGER.info("Cleared all status effects for player {}", playerId);
    }

    /**
     * Get effects formatted for display
     */
    public List<String> getFormattedEffects() {
        List<String> formatted = new ArrayList<>();

        if (activeEffects.isEmpty()) {
            formatted.add("§7No status effects§r");
            return formatted;
        }

        // Sort by type
        Map<EffectType, List<Map.Entry<StatusEffect, Integer>>> byType = new HashMap<>();
        for (Map.Entry<StatusEffect, Integer> entry : activeEffects.entrySet()) {
            EffectType type = entry.getKey().getType();
            byType.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
        }

        // Format each type
        for (EffectType type : EffectType.values()) {
            List<Map.Entry<StatusEffect, Integer>> effectList = byType.get(type);
            if (effectList != null && !effectList.isEmpty()) {
                formatted.add(String.format("%s%s:", type.getColorCode(), type.getDisplayName()));

                for (Map.Entry<StatusEffect, Integer> entry : effectList) {
                    StatusEffect effect = entry.getKey();
                    int remainingTicks = entry.getValue();
                    int remainingSeconds = remainingTicks / 20;

                    formatted.add(String.format("  %s%s §7(%ds)§r",
                        effect.getColorCode(),
                        effect.getDisplayName(),
                        remainingSeconds));
                }
            }
        }

        return formatted;
    }

    /**
     * Get count of effects by type
     */
    public int countEffectsByType(EffectType type) {
        return (int) activeEffects.keySet().stream()
            .filter(e -> e.getType() == type)
            .count();
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
