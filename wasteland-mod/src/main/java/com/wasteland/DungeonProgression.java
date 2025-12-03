package com.wasteland;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks player progression through dungeon levels
 */
public class DungeonProgression {
    private static final Logger LOGGER = LogManager.getLogger();

    // Track each player's current dungeon depth
    private static final Map<UUID, Integer> PLAYER_DEPTHS = new HashMap<>();

    /**
     * Get player's current dungeon depth (0 = surface, 1+ = underground)
     */
    public static int getDepth(UUID playerId) {
        return PLAYER_DEPTHS.getOrDefault(playerId, 0);
    }

    /**
     * Set player's dungeon depth
     */
    public static void setDepth(UUID playerId, int depth) {
        PLAYER_DEPTHS.put(playerId, depth);
        LOGGER.info("Player {} now at depth {}", playerId, depth);
    }

    /**
     * Move player deeper (stairs down)
     */
    public static int goDeeper(UUID playerId) {
        int currentDepth = getDepth(playerId);
        int newDepth = currentDepth + 1;
        setDepth(playerId, newDepth);
        LOGGER.info("Player descended: depth {} → {}", currentDepth, newDepth);
        return newDepth;
    }

    /**
     * Move player up (stairs up)
     */
    public static int goUp(UUID playerId) {
        int currentDepth = getDepth(playerId);
        int newDepth = Math.max(0, currentDepth - 1); // Don't go above surface (depth 0)
        setDepth(playerId, newDepth);
        LOGGER.info("Player ascended: depth {} → {}", currentDepth, newDepth);
        return newDepth;
    }

    /**
     * Get appropriate vault tier for current depth
     */
    public static VaultSelector.VaultTier getTierForDepth(int depth) {
        if (depth <= 5) {
            return VaultSelector.VaultTier.EARLY;  // Depths 1-5: easy vaults
        } else if (depth <= 10) {
            return VaultSelector.VaultTier.MID;    // Depths 6-10: medium vaults
        } else {
            return VaultSelector.VaultTier.LATE;   // Depths 11+: hard vaults
        }
    }

    /**
     * Get vault for player based on their current depth
     */
    public static String getVaultForDepth(UUID playerId) {
        int depth = getDepth(playerId);
        VaultSelector.VaultTier tier = getTierForDepth(depth);

        String vault = VaultSelector.getRandomVault(tier);

        LOGGER.info("Selected {} tier vault for depth {}: {}", tier, depth, vault);
        return vault;
    }

    /**
     * Reset player to surface (depth 0)
     */
    public static void resetToSurface(UUID playerId) {
        setDepth(playerId, 0);
        LOGGER.info("Player returned to surface (depth 0)");
    }

    /**
     * Clear all progression data (for world reload)
     */
    public static void clearAll() {
        PLAYER_DEPTHS.clear();
        LOGGER.info("Cleared all dungeon progression data");
    }
}
