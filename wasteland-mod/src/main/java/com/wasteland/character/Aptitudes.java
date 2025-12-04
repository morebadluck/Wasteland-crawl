package com.wasteland.character;

import java.util.HashMap;
import java.util.Map;

/**
 * DCSS aptitude system - determines how quickly races learn skills
 * Aptitude ranges: -5 (very poor) to +5 (exceptional)
 * 0 = average learning speed
 */
public class Aptitudes {

    // Aptitude tables: Race -> Skill -> Aptitude value
    private static final Map<Race, Map<Skill, Integer>> APTITUDE_TABLE = new HashMap<>();

    static {
        // Initialize aptitudes for each race
        initHumanAptitudes();
        initHillOrcAptitudes();
        initMinotaurAptitudes();
        initMerfolkAptitudes();
        initGargoyleAptitudes();
        initDraconianAptitudes();
        initDeepElfAptitudes();
        initDeepDwarfAptitudes();
        initTrollAptitudes();
        initOgreAptitudes();
        initSprigganAptitudes();
        initKoboldAptitudes();
        initCentaurAptitudes();
        initNagaAptitudes();
        initVampireAptitudes();
        initGhoulAptitudes();
        initMummyAptitudes();
        initFelidAptitudes();
        initOctopodeAptitudes();
    }

    /**
     * Get aptitude value for a race/skill combination
     * @return Aptitude from -5 to +5, or 0 if not defined
     */
    public static int getAptitude(Race race, Skill skill) {
        Map<Skill, Integer> raceAptitudes = APTITUDE_TABLE.get(race);
        if (raceAptitudes == null) return 0;
        return raceAptitudes.getOrDefault(skill, 0);
    }

    /**
     * Human: Balanced aptitudes (all 0)
     */
    private static void initHumanAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        // Humans have 0 aptitude in everything (average)
        for (Skill skill : Skill.values()) {
            apt.put(skill, 0);
        }
        APTITUDE_TABLE.put(Race.HUMAN, apt);
    }

    /**
     * Hill Orc: Good at melee combat, poor at magic
     */
    private static void initHillOrcAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        // Combat bonuses
        apt.put(Skill.FIGHTING, 2);
        apt.put(Skill.AXES, 1);
        apt.put(Skill.MACES_FLAILS, 1);
        apt.put(Skill.POLEARMS, 1);
        apt.put(Skill.ARMOUR, 2);
        apt.put(Skill.SHIELDS, 1);

        // Magic penalties
        apt.put(Skill.SPELLCASTING, -3);
        apt.put(Skill.CONJURATIONS, -2);
        apt.put(Skill.HEXES, -1);
        apt.put(Skill.FIRE_MAGIC, 1); // Orcs like fire

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.HILL_ORC, apt);
    }

    /**
     * Minotaur: Excellent melee fighters, terrible at magic
     */
    private static void initMinotaurAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        // Excellent melee
        apt.put(Skill.FIGHTING, 3);
        apt.put(Skill.AXES, 2);
        apt.put(Skill.MACES_FLAILS, 2);
        apt.put(Skill.POLEARMS, 2);
        apt.put(Skill.UNARMED_COMBAT, 1);

        // Poor at ranged and magic
        apt.put(Skill.BOWS, -3);
        apt.put(Skill.CROSSBOWS, -3);
        apt.put(Skill.SPELLCASTING, -4);
        apt.put(Skill.CONJURATIONS, -3);
        apt.put(Skill.HEXES, -3);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.MINOTAUR, apt);
    }

    /**
     * Merfolk: Versatile, good with polearms
     */
    private static void initMerfolkAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.POLEARMS, 3);
        apt.put(Skill.THROWING, 2);
        apt.put(Skill.DODGING, 2);
        apt.put(Skill.STEALTH, 1);
        apt.put(Skill.SPELLCASTING, 1);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.MERFOLK, apt);
    }

    /**
     * Gargoyle: Tough, good at earth magic
     */
    private static void initGargoyleAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.MACES_FLAILS, 1);
        apt.put(Skill.ARMOUR, 1);
        apt.put(Skill.EARTH_MAGIC, 2);
        apt.put(Skill.DODGING, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.GARGOYLE, apt);
    }

    /**
     * Draconian: Balanced with some magic affinity
     */
    private static void initDraconianAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.DODGING, 0);
        apt.put(Skill.SPELLCASTING, 1);
        apt.put(Skill.FIRE_MAGIC, 1);
        apt.put(Skill.ICE_MAGIC, 1);
        apt.put(Skill.POISON_MAGIC, 1);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.DRACONIAN, apt);
    }

    /**
     * Deep Elf: Excellent at all magic, poor at melee and defense
     */
    private static void initDeepElfAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        // Magic excellence
        apt.put(Skill.SPELLCASTING, 3);
        apt.put(Skill.CONJURATIONS, 2);
        apt.put(Skill.HEXES, 2);
        apt.put(Skill.SUMMONINGS, 1);
        apt.put(Skill.NECROMANCY, 1);
        apt.put(Skill.TRANSLOCATIONS, 2);
        apt.put(Skill.TRANSMUTATIONS, 1);
        apt.put(Skill.FIRE_MAGIC, 2);
        apt.put(Skill.ICE_MAGIC, 2);
        apt.put(Skill.AIR_MAGIC, 2);
        apt.put(Skill.EARTH_MAGIC, 2);
        apt.put(Skill.POISON_MAGIC, 2);

        // Combat/defense weaknesses
        apt.put(Skill.FIGHTING, -2);
        apt.put(Skill.ARMOUR, -3);
        apt.put(Skill.SHIELDS, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.DEEP_ELF, apt);
    }

    /**
     * Deep Dwarf: Hardy warriors, no healing magic
     */
    private static void initDeepDwarfAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 2);
        apt.put(Skill.AXES, 2);
        apt.put(Skill.MACES_FLAILS, 2);
        apt.put(Skill.CROSSBOWS, 2);
        apt.put(Skill.ARMOUR, 2);
        apt.put(Skill.SHIELDS, 2);
        apt.put(Skill.EARTH_MAGIC, 1);

        apt.put(Skill.SPELLCASTING, -2);
        apt.put(Skill.DODGING, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.DEEP_DWARF, apt);
    }

    /**
     * Troll: Massive melee power, terrible at everything else
     */
    private static void initTrollAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 2);
        apt.put(Skill.UNARMED_COMBAT, 3);
        apt.put(Skill.DODGING, -2);
        apt.put(Skill.STEALTH, -4);
        apt.put(Skill.SPELLCASTING, -5);
        apt.put(Skill.INVOCATIONS, -3);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.TROLL, apt);
    }

    /**
     * Ogre: Strong but clumsy
     */
    private static void initOgreAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 2);
        apt.put(Skill.MACES_FLAILS, 2);
        apt.put(Skill.THROWING, 1);
        apt.put(Skill.DODGING, -2);
        apt.put(Skill.STEALTH, -2);
        apt.put(Skill.SPELLCASTING, -4);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.OGRE, apt);
    }

    /**
     * Spriggan: Fast, stealthy, magically inclined, terrible at melee
     */
    private static void initSprigganAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.SHORT_BLADES, 2);
        apt.put(Skill.DODGING, 3);
        apt.put(Skill.STEALTH, 4);
        apt.put(Skill.SPELLCASTING, 2);
        apt.put(Skill.HEXES, 2);
        apt.put(Skill.TRANSLOCATIONS, 3);

        apt.put(Skill.FIGHTING, -2);
        apt.put(Skill.AXES, -3);
        apt.put(Skill.MACES_FLAILS, -4);
        apt.put(Skill.POLEARMS, -3);
        apt.put(Skill.ARMOUR, -3);
        apt.put(Skill.SHIELDS, -3);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.SPRIGGAN, apt);
    }

    /**
     * Kobold: Small and sneaky
     */
    private static void initKoboldAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.SHORT_BLADES, 2);
        apt.put(Skill.GUNS, 2);
        apt.put(Skill.DODGING, 2);
        apt.put(Skill.STEALTH, 3);
        apt.put(Skill.SHIELDS, -1);
        apt.put(Skill.ARMOUR, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.KOBOLD, apt);
    }

    /**
     * Centaur: Fast and good with ranged weapons
     */
    private static void initCentaurAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.BOWS, 2);
        apt.put(Skill.THROWING, 1);
        apt.put(Skill.DODGING, -2);
        apt.put(Skill.ARMOUR, -2);
        apt.put(Skill.SPELLCASTING, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.CENTAUR, apt);
    }

    /**
     * Naga: Slow but poison-specialized
     */
    private static void initNagaAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.POLEARMS, 1);
        apt.put(Skill.SPELLCASTING, 1);
        apt.put(Skill.POISON_MAGIC, 2);
        apt.put(Skill.SHIELDS, -2);
        apt.put(Skill.DODGING, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.NAGA, apt);
    }

    /**
     * Vampire: Undead with some magic affinity
     */
    private static void initVampireAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.SHORT_BLADES, 1);
        apt.put(Skill.UNARMED_COMBAT, 2);
        apt.put(Skill.DODGING, 1);
        apt.put(Skill.STEALTH, 2);
        apt.put(Skill.SPELLCASTING, 1);
        apt.put(Skill.NECROMANCY, 2);
        apt.put(Skill.ICE_MAGIC, 2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.VAMPIRE, apt);
    }

    /**
     * Ghoul: Undead melee fighter
     */
    private static void initGhoulAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, 1);
        apt.put(Skill.UNARMED_COMBAT, 1);
        apt.put(Skill.NECROMANCY, 1);
        apt.put(Skill.ICE_MAGIC, 1);
        apt.put(Skill.SPELLCASTING, -2);
        apt.put(Skill.DODGING, -1);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.GHOUL, apt);
    }

    /**
     * Mummy: Ancient undead, no healing
     */
    private static void initMummyAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, -1);
        apt.put(Skill.SPELLCASTING, 1);
        apt.put(Skill.NECROMANCY, 2);
        apt.put(Skill.INVOCATIONS, -2);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.MUMMY, apt);
    }

    /**
     * Felid: Cat with multiple lives, poor at most combat
     */
    private static void initFelidAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.FIGHTING, -2);
        apt.put(Skill.UNARMED_COMBAT, 2);
        apt.put(Skill.DODGING, 3);
        apt.put(Skill.STEALTH, 4);
        apt.put(Skill.SPELLCASTING, 2);
        apt.put(Skill.HEXES, 2);
        apt.put(Skill.TRANSLOCATIONS, 2);

        // Can't use weapons/armor
        apt.put(Skill.SHORT_BLADES, -5);
        apt.put(Skill.LONG_BLADES, -5);
        apt.put(Skill.AXES, -5);
        apt.put(Skill.MACES_FLAILS, -5);
        apt.put(Skill.POLEARMS, -5);
        apt.put(Skill.ARMOUR, -5);
        apt.put(Skill.SHIELDS, -5);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.FELID, apt);
    }

    /**
     * Octopode: Eight-tentacled creature
     */
    private static void initOctopodeAptitudes() {
        Map<Skill, Integer> apt = new HashMap<>();
        apt.put(Skill.UNARMED_COMBAT, 1);
        apt.put(Skill.DODGING, 1);
        apt.put(Skill.STEALTH, 2);
        apt.put(Skill.SPELLCASTING, 1);
        apt.put(Skill.POISON_MAGIC, 2);

        apt.put(Skill.ARMOUR, -3);
        apt.put(Skill.SHIELDS, -3);

        fillDefaults(apt);
        APTITUDE_TABLE.put(Race.OCTOPODE, apt);
    }

    /**
     * Fill in any missing skills with 0 aptitude
     */
    private static void fillDefaults(Map<Skill, Integer> aptitudes) {
        for (Skill skill : Skill.values()) {
            aptitudes.putIfAbsent(skill, 0);
        }
    }
}
