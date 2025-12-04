package com.wasteland.magic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Registry of all available spells in the game.
 * Manages spell definitions and provides lookup functionality.
 */
public class SpellRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, Spell> SPELLS = new HashMap<>();

    // Prevent instantiation
    private SpellRegistry() {}

    /**
     * Register a spell
     */
    public static void register(Spell spell) {
        if (SPELLS.containsKey(spell.getId())) {
            LOGGER.warn("Spell {} is already registered, overwriting", spell.getId());
        }
        SPELLS.put(spell.getId(), spell);
        LOGGER.debug("Registered spell: {} (Level {})", spell.getDisplayName(), spell.getSpellLevel());
    }

    /**
     * Get a spell by ID
     */
    public static Spell getSpell(String id) {
        return SPELLS.get(id);
    }

    /**
     * Get all registered spells
     */
    public static Collection<Spell> getAllSpells() {
        return new ArrayList<>(SPELLS.values());
    }

    /**
     * Get spells by school
     */
    public static List<Spell> getSpellsBySchool(SpellSchool school) {
        return SPELLS.values().stream()
            .filter(spell -> spell.getSchools().contains(school))
            .collect(Collectors.toList());
    }

    /**
     * Get spells by level
     */
    public static List<Spell> getSpellsByLevel(int level) {
        return SPELLS.values().stream()
            .filter(spell -> spell.getSpellLevel() == level)
            .collect(Collectors.toList());
    }

    /**
     * Get spells by level range
     */
    public static List<Spell> getSpellsByLevelRange(int minLevel, int maxLevel) {
        return SPELLS.values().stream()
            .filter(spell -> spell.getSpellLevel() >= minLevel && spell.getSpellLevel() <= maxLevel)
            .sorted(Comparator.comparingInt(Spell::getSpellLevel))
            .collect(Collectors.toList());
    }

    /**
     * Get spells sorted by level
     */
    public static List<Spell> getSpellsSortedByLevel() {
        return SPELLS.values().stream()
            .sorted(Comparator.comparingInt(Spell::getSpellLevel))
            .collect(Collectors.toList());
    }

    /**
     * Get total number of registered spells
     */
    public static int getSpellCount() {
        return SPELLS.size();
    }

    /**
     * Check if a spell exists
     */
    public static boolean hasSpell(String id) {
        return SPELLS.containsKey(id);
    }

    /**
     * Clear all spells (for testing)
     */
    public static void clearAll() {
        SPELLS.clear();
        LOGGER.info("Cleared all registered spells");
    }
}
