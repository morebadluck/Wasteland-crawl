package com.wasteland.religion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * Tracks a player's religious affiliation and piety
 */
public class PlayerReligion {
    private static final Logger LOGGER = LogManager.getLogger();

    // Piety constants from DCSS
    public static final int MIN_PIETY = 0;
    public static final int MAX_PIETY = 200;
    public static final int PIETY_RANK_1 = 30;   // "*"     - First power
    public static final int PIETY_RANK_2 = 50;   // "**"    - Second power
    public static final int PIETY_RANK_3 = 75;   // "***"   - Third power
    public static final int PIETY_RANK_4 = 100;  // "****"  - Fourth power
    public static final int PIETY_RANK_5 = 120;  // "*****" - Fifth power
    public static final int PIETY_RANK_6 = 160;  // "******" - Sixth power (max)

    private final UUID playerId;
    private God god;
    private int piety;
    private boolean abandoned; // Has player abandoned a god?

    public PlayerReligion(UUID playerId) {
        this.playerId = playerId;
        this.god = God.NONE;
        this.piety = 0;
        this.abandoned = false;
    }

    /**
     * Join a god's religion
     */
    public boolean joinGod(God newGod) {
        if (newGod == god) {
            LOGGER.warn("Player {} already worships {}", playerId, god.getDisplayName());
            return false;
        }

        if (god != God.NONE) {
            LOGGER.warn("Player {} tried to join {} while worshipping {}",
                       playerId, newGod.getDisplayName(), god.getDisplayName());
            return false; // Must abandon current god first
        }

        god = newGod;
        piety = newGod.usesPiety() ? PIETY_RANK_1 : 0; // Start with some piety (except Xom)
        abandoned = false;

        LOGGER.info("Player {} joined {}", playerId, god.getDisplayName());
        return true;
    }

    /**
     * Abandon current god
     */
    public void abandonGod() {
        if (god == God.NONE) return;

        LOGGER.info("Player {} abandoned {}", playerId, god.getDisplayName());

        god = God.NONE;
        piety = 0;
        abandoned = true;
    }

    /**
     * Gain piety
     */
    public void gainPiety(int amount) {
        if (!god.usesPiety()) return;

        int oldRank = getPietyRank();
        piety = Math.min(piety + amount, MAX_PIETY);
        int newRank = getPietyRank();

        LOGGER.debug("Player {} gained {} piety with {} (now {}/{})",
                    playerId, amount, god.getDisplayName(), piety, MAX_PIETY);

        if (newRank > oldRank) {
            LOGGER.info("Player {} reached piety rank {} with {}",
                       playerId, newRank, god.getDisplayName());
        }
    }

    /**
     * Lose piety
     */
    public void losePiety(int amount) {
        if (!god.usesPiety()) return;

        int oldRank = getPietyRank();
        piety = Math.max(piety - amount, MIN_PIETY);
        int newRank = getPietyRank();

        LOGGER.debug("Player {} lost {} piety with {} (now {}/{})",
                    playerId, amount, god.getDisplayName(), piety, MAX_PIETY);

        if (newRank < oldRank) {
            LOGGER.warn("Player {} dropped to piety rank {} with {}",
                       playerId, newRank, god.getDisplayName());
        }

        // Auto-abandon if piety drops to 0
        if (piety <= 0 && god != God.NONE) {
            LOGGER.warn("Player {} was excommunicated by {}", playerId, god.getDisplayName());
            abandonGod();
        }
    }

    /**
     * Get current piety rank (0-6)
     */
    public int getPietyRank() {
        if (!god.usesPiety() || god == God.NONE) return 0;

        if (piety >= PIETY_RANK_6) return 6;
        if (piety >= PIETY_RANK_5) return 5;
        if (piety >= PIETY_RANK_4) return 4;
        if (piety >= PIETY_RANK_3) return 3;
        if (piety >= PIETY_RANK_2) return 2;
        if (piety >= PIETY_RANK_1) return 1;
        return 0;
    }

    /**
     * Get piety rank as stars
     */
    public String getPietyStars() {
        int rank = getPietyRank();
        if (rank == 0) return "";
        return "§e" + "*".repeat(rank) + "§r";
    }

    /**
     * Check if a divine ability is available at current piety
     */
    public boolean canUseAbility(DivineAbility ability) {
        if (god == God.NONE) return false;
        if (!god.usesPiety()) return god == God.XOM; // Xom abilities always available (randomly)
        return ability.isAvailableAtPiety(piety);
    }

    // Getters
    public UUID getPlayerId() {
        return playerId;
    }

    public God getGod() {
        return god;
    }

    public int getPiety() {
        return piety;
    }

    public boolean hasAbandoned() {
        return abandoned;
    }

    public boolean hasGod() {
        return god != God.NONE;
    }

    /**
     * Get piety as percentage for UI
     */
    public float getPietyPercentage() {
        return (float) piety / MAX_PIETY;
    }
}
