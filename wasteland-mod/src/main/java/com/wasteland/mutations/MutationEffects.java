package com.wasteland.mutations;

import com.wasteland.character.PlayerCharacter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * Calculates and applies stat modifiers from mutations
 */
public class MutationEffects {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Calculate total HP modifier from mutations (percentage)
     */
    public static double getHPModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        double modifier = 1.0;

        // ROBUST: +10% HP per level
        int robustLevel = mutations.getMutationLevel(Mutation.ROBUST);
        modifier += robustLevel * 0.10;

        // FRAIL: -10% HP per level
        int frailLevel = mutations.getMutationLevel(Mutation.FRAIL);
        modifier -= frailLevel * 0.10;

        return modifier;
    }

    /**
     * Calculate total MP modifier from mutations (percentage)
     */
    public static double getMPModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        double modifier = 1.0;

        // HIGH_MP: +10% MP per level
        int highMpLevel = mutations.getMutationLevel(Mutation.HIGH_MP);
        modifier += highMpLevel * 0.10;

        // LOW_MP: -10% MP per level
        int lowMpLevel = mutations.getMutationLevel(Mutation.LOW_MP);
        modifier -= lowMpLevel * 0.10;

        return modifier;
    }

    /**
     * Calculate strength modifier from mutations
     */
    public static int getStrengthModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        int modifier = 0;

        // WEAK: -1 STR per level
        int weakLevel = mutations.getMutationLevel(Mutation.WEAK);
        modifier -= weakLevel;

        return modifier;
    }

    /**
     * Calculate dexterity modifier from mutations
     */
    public static int getDexterityModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        int modifier = 0;

        // CLUMSY: -1 DEX per level
        int clumsyLevel = mutations.getMutationLevel(Mutation.CLUMSY);
        modifier -= clumsyLevel;

        return modifier;
    }

    /**
     * Calculate intelligence modifier from mutations
     */
    public static int getIntelligenceModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        int modifier = 0;

        // DOPEY: -1 INT per level
        int dopeyLevel = mutations.getMutationLevel(Mutation.DOPEY);
        modifier -= dopeyLevel;

        return modifier;
    }

    /**
     * Calculate armor class (AC) bonus from mutations
     */
    public static int getArmorBonus(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        int bonus = 0;

        // SCALES: +1 AC per level
        int scalesLevel = mutations.getMutationLevel(Mutation.SCALES);
        bonus += scalesLevel;

        // FUR: +1 AC per level (cold resistance too)
        int furLevel = mutations.getMutationLevel(Mutation.FUR);
        bonus += furLevel;

        return bonus;
    }

    /**
     * Calculate melee damage bonus from mutations
     */
    public static int getMeleeDamageBonus(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        int bonus = 0;

        // CLAWS: +2 damage per level
        int clawsLevel = mutations.getMutationLevel(Mutation.CLAWS);
        bonus += clawsLevel * 2;

        // HORNS: +1 damage per level (aux attack)
        int hornsLevel = mutations.getMutationLevel(Mutation.HORNS);
        bonus += hornsLevel;

        // FANGS: +1 damage per level (aux attack)
        int fangsLevel = mutations.getMutationLevel(Mutation.FANGS);
        bonus += fangsLevel;

        // TAIL: +1 damage per level (aux attack)
        int tailLevel = mutations.getMutationLevel(Mutation.TAIL);
        bonus += tailLevel;

        // TENTACLES: +3 damage per level (but replaces hands)
        int tentaclesLevel = mutations.getMutationLevel(Mutation.TENTACLES);
        bonus += tentaclesLevel * 3;

        return bonus;
    }

    /**
     * Calculate health regeneration rate modifier
     */
    public static double getRegenModifier(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        double modifier = 1.0;

        // REGENERATION: +50% regen per level
        int regenLevel = mutations.getMutationLevel(Mutation.REGENERATION);
        modifier += regenLevel * 0.5;

        // SLOW_HEALING: -33% regen per level
        int slowHealLevel = mutations.getMutationLevel(Mutation.SLOW_HEALING);
        modifier -= slowHealLevel * 0.33;

        return Math.max(0.1, modifier); // Never go below 10%
    }

    /**
     * Calculate poison resistance (0-3)
     */
    public static int getPoisonResistance(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.getMutationLevel(Mutation.POISON_RESISTANCE);
    }

    /**
     * Calculate magic resistance (0-3)
     */
    public static int getMagicResistance(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.getMutationLevel(Mutation.MAGIC_RESISTANCE);
    }

    /**
     * Check if player has clarity (confusion immunity)
     */
    public static boolean hasClarity(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.hasMutation(Mutation.CLARITY);
    }

    /**
     * Check if player has teleportitis
     */
    public static boolean hasTeleportitis(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.hasMutation(Mutation.TELEPORTITIS);
    }

    /**
     * Get teleportitis level (determines frequency)
     */
    public static int getTeleportitisLevel(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.getMutationLevel(Mutation.TELEPORTITIS);
    }

    /**
     * Check if player has berserk rage mutation
     */
    public static boolean hasBerserkRage(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.hasMutation(Mutation.BERSERK_RAGE);
    }

    /**
     * Get berserk rage level (determines trigger chance)
     */
    public static int getBerserkRageLevel(UUID playerId) {
        PlayerMutations mutations = MutationManager.getMutations(playerId);
        return mutations.getMutationLevel(Mutation.BERSERK_RAGE);
    }

    /**
     * Apply all mutation effects to a player character
     * This should be called whenever mutations change
     */
    public static void applyMutationEffects(PlayerCharacter character) {
        UUID playerId = character.getPlayerId();

        // Log the application
        LOGGER.debug("Applying mutation effects to player {}", playerId);

        // Note: The actual stat modifications are calculated on-the-fly when stats are retrieved
        // This method is here for future expansion (status effects, etc.)
    }

    /**
     * Get a summary of all active mutation effects
     */
    public static String getEffectsSummary(UUID playerId) {
        StringBuilder summary = new StringBuilder();

        double hpMod = getHPModifier(playerId);
        if (hpMod != 1.0) {
            summary.append(String.format("HP: %+.0f%% ", (hpMod - 1.0) * 100));
        }

        double mpMod = getMPModifier(playerId);
        if (mpMod != 1.0) {
            summary.append(String.format("MP: %+.0f%% ", (mpMod - 1.0) * 100));
        }

        int strMod = getStrengthModifier(playerId);
        if (strMod != 0) {
            summary.append(String.format("STR: %+d ", strMod));
        }

        int dexMod = getDexterityModifier(playerId);
        if (dexMod != 0) {
            summary.append(String.format("DEX: %+d ", dexMod));
        }

        int intMod = getIntelligenceModifier(playerId);
        if (intMod != 0) {
            summary.append(String.format("INT: %+d ", intMod));
        }

        int acBonus = getArmorBonus(playerId);
        if (acBonus > 0) {
            summary.append(String.format("AC: +%d ", acBonus));
        }

        int dmgBonus = getMeleeDamageBonus(playerId);
        if (dmgBonus > 0) {
            summary.append(String.format("DMG: +%d ", dmgBonus));
        }

        double regenMod = getRegenModifier(playerId);
        if (regenMod != 1.0) {
            summary.append(String.format("REGEN: %+.0f%% ", (regenMod - 1.0) * 100));
        }

        if (summary.length() == 0) {
            return "No mutation effects";
        }

        return summary.toString().trim();
    }
}
