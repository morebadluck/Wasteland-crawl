package com.wasteland.religion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player religion and piety
 */
public class ReligionManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, PlayerReligion> RELIGIONS = new HashMap<>();

    /**
     * Get or create player religion
     */
    public static PlayerReligion getReligion(UUID playerId) {
        return RELIGIONS.computeIfAbsent(playerId, PlayerReligion::new);
    }

    /**
     * Join a god
     */
    public static boolean joinGod(UUID playerId, God god) {
        PlayerReligion religion = getReligion(playerId);
        return religion.joinGod(god);
    }

    /**
     * Abandon current god
     */
    public static void abandonGod(UUID playerId) {
        PlayerReligion religion = getReligion(playerId);
        religion.abandonGod();
    }

    /**
     * Gain piety
     */
    public static void gainPiety(UUID playerId, int amount) {
        PlayerReligion religion = getReligion(playerId);
        religion.gainPiety(amount);
    }

    /**
     * Lose piety
     */
    public static void losePiety(UUID playerId, int amount) {
        PlayerReligion religion = getReligion(playerId);
        religion.losePiety(amount);
    }

    /**
     * Get current god
     */
    public static God getGod(UUID playerId) {
        PlayerReligion religion = getReligion(playerId);
        return religion.getGod();
    }

    /**
     * Get current piety
     */
    public static int getPiety(UUID playerId) {
        PlayerReligion religion = getReligion(playerId);
        return religion.getPiety();
    }

    /**
     * Get piety rank
     */
    public static int getPietyRank(UUID playerId) {
        PlayerReligion religion = getReligion(playerId);
        return religion.getPietyRank();
    }

    /**
     * Check if player has a god
     */
    public static boolean hasGod(UUID playerId) {
        PlayerReligion religion = getReligion(playerId);
        return religion.hasGod();
    }

    /**
     * Remove player religion (cleanup)
     */
    public static void removeReligion(UUID playerId) {
        RELIGIONS.remove(playerId);
        LOGGER.debug("Removed religion data for player {}", playerId);
    }

    /**
     * Get all religions (for debugging)
     */
    public static Map<UUID, PlayerReligion> getAllReligions() {
        return new HashMap<>(RELIGIONS);
    }
}
