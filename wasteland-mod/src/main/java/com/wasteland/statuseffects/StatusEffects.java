package com.wasteland.statuseffects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

/**
 * Calculates and applies stat modifiers from status effects
 * Similar to MutationEffects but for temporary buffs/debuffs
 */
public class StatusEffects {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Calculate strength modifier from status effects
     */
    public static int getStrengthModifier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int modifier = 0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.MIGHT)) modifier += 5;
        if (effects.hasEffect(StatusEffect.HEROISM)) modifier += 3;

        // DEBUFFS
        if (effects.hasEffect(StatusEffect.WEAKNESS)) modifier -= 3;

        return modifier;
    }

    /**
     * Calculate dexterity modifier from status effects
     */
    public static int getDexterityModifier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int modifier = 0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.AGILITY)) modifier += 5;
        if (effects.hasEffect(StatusEffect.HEROISM)) modifier += 3;

        // DEBUFFS
        if (effects.hasEffect(StatusEffect.CLUMSINESS)) modifier -= 3;

        return modifier;
    }

    /**
     * Calculate intelligence modifier from status effects
     */
    public static int getIntelligenceModifier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int modifier = 0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.BRILLIANCE)) modifier += 5;
        if (effects.hasEffect(StatusEffect.BRILLIANCE_SPELL)) modifier += 5;

        // DEBUFFS
        if (effects.hasEffect(StatusEffect.STUPIDITY)) modifier -= 3;

        return modifier;
    }

    /**
     * Calculate armor class (AC) bonus from status effects
     */
    public static int getArmorBonus(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int bonus = 0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.STONESKIN)) bonus += 5;
        if (effects.hasEffect(StatusEffect.DIVINE_SHIELD)) bonus += 5;
        if (effects.hasEffect(StatusEffect.TROGS_HAND)) bonus += 2;

        // DEBUFFS
        if (effects.hasEffect(StatusEffect.CORROSION)) bonus -= 4;

        return bonus;
    }

    /**
     * Calculate melee damage bonus from status effects
     */
    public static int getMeleeDamageBonus(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int bonus = 0;

        if (effects.hasEffect(StatusEffect.TROGS_HAND)) {
            bonus += 4; // +20% damage bonus
        }

        return bonus;
    }

    /**
     * Calculate damage multiplier from status effects
     */
    public static double getDamageMultiplier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double multiplier = 1.0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.BERSERK)) {
            multiplier *= 1.5; // +50% damage
        }
        if (effects.hasEffect(StatusEffect.TROGS_HAND)) {
            multiplier *= 1.2; // +20% damage
        }

        return multiplier;
    }

    /**
     * Calculate defense multiplier from status effects
     */
    public static double getDefenseMultiplier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double multiplier = 1.0;

        // DEBUFFS (reduce defense)
        if (effects.hasEffect(StatusEffect.BERSERK)) {
            multiplier *= 0.5; // -50% defense while berserking
        }

        return multiplier;
    }

    /**
     * Calculate speed multiplier from status effects
     */
    public static double getSpeedMultiplier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double multiplier = 1.0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.HASTE)) {
            multiplier *= 1.5; // +50% speed
        }
        if (effects.hasEffect(StatusEffect.BERSERK)) {
            multiplier *= 1.5; // +50% speed
        }
        if (effects.hasEffect(StatusEffect.FINESSE)) {
            multiplier *= 1.5; // +50% attack speed
        }

        // DEBUFFS
        if (effects.hasEffect(StatusEffect.SLOW)) {
            multiplier *= 0.5; // -50% speed
        }
        if (effects.hasEffect(StatusEffect.PETRIFYING)) {
            multiplier *= 0.5; // -50% speed
        }
        if (effects.hasEffect(StatusEffect.EXHAUSTED)) {
            multiplier *= 0.8; // -20% speed
        }

        return multiplier;
    }

    /**
     * Calculate health regeneration rate modifier
     */
    public static double getRegenModifier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double modifier = 1.0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.REGENERATION)) {
            modifier += 2.0; // +200% regen
        }
        if (effects.hasEffect(StatusEffect.DIVINE_VIGOR)) {
            modifier += 1.0; // +100% regen
        }

        return modifier;
    }

    /**
     * Calculate mana regeneration rate modifier
     */
    public static double getMPRegenModifier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double modifier = 1.0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.CHANNEL_MAGIC)) {
            modifier += 0.5; // +50% MP regen
        }
        if (effects.hasEffect(StatusEffect.DIVINE_VIGOR)) {
            modifier += 0.5; // +50% MP regen
        }

        return modifier;
    }

    /**
     * Calculate spell cost multiplier
     */
    public static double getSpellCostMultiplier(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        double multiplier = 1.0;

        // BUFFS (reduce cost)
        if (effects.hasEffect(StatusEffect.BRILLIANCE)) {
            multiplier *= 0.5; // -50% spell costs
        }

        return multiplier;
    }

    /**
     * Calculate magic resistance bonus
     */
    public static int getMagicResistance(UUID playerId) {
        PlayerStatusEffects effects = StatusEffectManager.getEffects(playerId);
        int bonus = 0;

        // BUFFS
        if (effects.hasEffect(StatusEffect.DIVINE_SHIELD)) {
            bonus += 2;
        }
        if (effects.hasEffect(StatusEffect.RESISTANCE)) {
            bonus += 1;
        }

        return bonus;
    }

    /**
     * Get a summary of all active status effect modifiers
     */
    public static String getEffectsSummary(UUID playerId) {
        StringBuilder summary = new StringBuilder();

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
        if (acBonus != 0) {
            summary.append(String.format("AC: %+d ", acBonus));
        }

        double damageMultiplier = getDamageMultiplier(playerId);
        if (damageMultiplier != 1.0) {
            summary.append(String.format("DMG: %.0f%% ", damageMultiplier * 100));
        }

        double speedMultiplier = getSpeedMultiplier(playerId);
        if (speedMultiplier != 1.0) {
            summary.append(String.format("SPD: %.0f%% ", speedMultiplier * 100));
        }

        if (summary.length() == 0) {
            return "No status effects";
        }

        return summary.toString().trim();
    }
}
