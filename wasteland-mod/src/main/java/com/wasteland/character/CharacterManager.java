package com.wasteland.character;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages player characters across the server
 * Similar to DungeonProgression, but tracks character data
 */
public class CharacterManager {
    private static final Logger LOGGER = LogManager.getLogger();

    // Track all player characters by UUID
    private static final Map<UUID, PlayerCharacter> CHARACTERS = new HashMap<>();

    /**
     * Get a player's character, or null if they don't have one
     */
    public static PlayerCharacter getCharacter(UUID playerId) {
        return CHARACTERS.get(playerId);
    }

    /**
     * Create a new character for a player
     */
    public static PlayerCharacter createCharacter(UUID playerId, Race race, String characterName) {
        PlayerCharacter character = new PlayerCharacter(playerId, race, characterName);
        CHARACTERS.put(playerId, character);

        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  New Character Created!");
        LOGGER.info("  Player: {}", playerId);
        LOGGER.info("  Name: {}", characterName);
        LOGGER.info("  Race: {}", race.getDisplayName());
        LOGGER.info("  Description: {}", race.getDescription());
        LOGGER.info("  Starting HP: {}", character.getMaxHP());
        LOGGER.info("  Starting MP: {}", character.getMaxMP());
        LOGGER.info("═══════════════════════════════════════════════════════");

        return character;
    }

    /**
     * Check if player has a character
     */
    public static boolean hasCharacter(UUID playerId) {
        return CHARACTERS.containsKey(playerId);
    }

    /**
     * Delete a character
     */
    public static void deleteCharacter(UUID playerId) {
        PlayerCharacter removed = CHARACTERS.remove(playerId);
        if (removed != null) {
            LOGGER.info("Deleted character: {} ({})",
                       removed.getCharacterName(),
                       removed.getRace().getDisplayName());
        }
    }

    /**
     * Clear all characters (for server reload/testing)
     */
    public static void clearAll() {
        CHARACTERS.clear();
        LOGGER.info("Cleared all player characters");
    }

    /**
     * Get or create a default character for testing
     * If player doesn't have a character, creates a Human
     */
    public static PlayerCharacter getOrCreateDefault(UUID playerId) {
        PlayerCharacter character = getCharacter(playerId);
        if (character == null) {
            LOGGER.info("Player {} has no character, creating default Human", playerId);
            character = createCharacter(playerId, Race.HUMAN, "Adventurer");
        }
        return character;
    }

    /**
     * Get summary of all active characters
     */
    public static String getAllCharactersSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Active Characters: ").append(CHARACTERS.size()).append("\n");

        for (PlayerCharacter character : CHARACTERS.values()) {
            sb.append("  - ").append(character.getSummary()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Train a skill for a player (convenience method)
     */
    public static void trainSkill(UUID playerId, Skill skill, int xp) {
        PlayerCharacter character = getCharacter(playerId);
        if (character != null) {
            character.trainSkill(skill, xp);
        }
    }

    /**
     * Give XP to a player (convenience method)
     */
    public static void giveXP(UUID playerId, int xp) {
        PlayerCharacter character = getCharacter(playerId);
        if (character != null) {
            character.gainXP(xp);
        }
    }

    /**
     * Learn a spell (convenience method)
     */
    public static boolean learnSpell(UUID playerId, Spell spell) {
        PlayerCharacter character = getCharacter(playerId);
        if (character != null) {
            return character.learnSpell(spell);
        }
        return false;
    }

    /**
     * Memorize a spell (convenience method)
     */
    public static boolean memorizeSpell(UUID playerId, Spell spell) {
        PlayerCharacter character = getCharacter(playerId);
        if (character != null) {
            return character.memorizeSpell(spell);
        }
        return false;
    }
}
