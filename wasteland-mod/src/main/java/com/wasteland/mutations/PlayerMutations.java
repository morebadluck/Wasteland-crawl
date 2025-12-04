package com.wasteland.mutations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Tracks a player's mutations
 */
public class PlayerMutations {
    private static final Logger LOGGER = LogManager.getLogger();

    private final UUID playerId;
    private final Map<Mutation, Integer> mutations; // Mutation -> Level

    public PlayerMutations(UUID playerId) {
        this.playerId = playerId;
        this.mutations = new HashMap<>();
    }

    /**
     * Add a mutation or increase its level
     */
    public boolean addMutation(Mutation mutation) {
        int currentLevel = mutations.getOrDefault(mutation, 0);

        // Already at max level
        if (currentLevel >= mutation.getMaxLevel()) {
            LOGGER.warn("Player {} already has {} at max level", playerId, mutation.getDisplayName());
            return false;
        }

        // Check for conflicts
        for (Map.Entry<Mutation, Integer> entry : mutations.entrySet()) {
            if (mutation.conflictsWith(entry.getKey()) && entry.getValue() > 0) {
                LOGGER.warn("Player {} cannot gain {} - conflicts with {}",
                           playerId, mutation.getDisplayName(), entry.getKey().getDisplayName());
                return false;
            }
        }

        // Add/increase mutation
        mutations.put(mutation, currentLevel + 1);
        LOGGER.info("Player {} gained {} (level {})", playerId, mutation.getDisplayName(), currentLevel + 1);
        return true;
    }

    /**
     * Remove a mutation level or fully remove it
     */
    public boolean removeMutation(Mutation mutation) {
        int currentLevel = mutations.getOrDefault(mutation, 0);

        if (currentLevel <= 0) {
            LOGGER.warn("Player {} does not have {} to remove", playerId, mutation.getDisplayName());
            return false;
        }

        if (currentLevel == 1) {
            mutations.remove(mutation);
            LOGGER.info("Player {} lost {}", playerId, mutation.getDisplayName());
        } else {
            mutations.put(mutation, currentLevel - 1);
            LOGGER.info("Player {} reduced {} to level {}", playerId, mutation.getDisplayName(), currentLevel - 1);
        }

        return true;
    }

    /**
     * Get mutation level
     */
    public int getMutationLevel(Mutation mutation) {
        return mutations.getOrDefault(mutation, 0);
    }

    /**
     * Check if player has mutation
     */
    public boolean hasMutation(Mutation mutation) {
        return mutations.getOrDefault(mutation, 0) > 0;
    }

    /**
     * Get all active mutations
     */
    public Map<Mutation, Integer> getAllMutations() {
        return new HashMap<>(mutations);
    }

    /**
     * Count total mutations by type
     */
    public int countMutationsByType(MutationType type) {
        return (int) mutations.entrySet().stream()
            .filter(e -> e.getKey().getType() == type && e.getValue() > 0)
            .count();
    }

    /**
     * Get total mutation count
     */
    public int getTotalMutationCount() {
        return mutations.values().stream()
            .mapToInt(Integer::intValue)
            .sum();
    }

    /**
     * Clear all mutations
     */
    public void clearAllMutations() {
        mutations.clear();
        LOGGER.info("Cleared all mutations for player {}", playerId);
    }

    /**
     * Get mutations formatted for display
     */
    public List<String> getFormattedMutations() {
        List<String> formatted = new ArrayList<>();

        // Sort by type
        Map<MutationType, List<Map.Entry<Mutation, Integer>>> byType = new HashMap<>();
        for (Map.Entry<Mutation, Integer> entry : mutations.entrySet()) {
            if (entry.getValue() > 0) {
                MutationType type = entry.getKey().getType();
                byType.computeIfAbsent(type, k -> new ArrayList<>()).add(entry);
            }
        }

        // Format each type
        for (MutationType type : MutationType.values()) {
            List<Map.Entry<Mutation, Integer>> mutList = byType.get(type);
            if (mutList != null && !mutList.isEmpty()) {
                formatted.add(String.format("%s%s Mutations:",
                    type.getColorCode(), type.getDisplayName()));

                for (Map.Entry<Mutation, Integer> entry : mutList) {
                    Mutation mut = entry.getKey();
                    int level = entry.getValue();
                    String stars = "*".repeat(level);

                    formatted.add(String.format("  %s%s %s§r",
                        type.getColorCode(),
                        mut.getDisplayName(),
                        stars));
                    formatted.add(String.format("    §7%s§r",
                        mut.getDescriptionWithLevel(level)));
                }
            }
        }

        if (formatted.isEmpty()) {
            formatted.add("§7No mutations§r");
        }

        return formatted;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
