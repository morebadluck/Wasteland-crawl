package com.wasteland.religion;

import com.wasteland.character.CharacterManager;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.statuseffects.StatusEffect;
import com.wasteland.statuseffects.StatusEffectManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles invocation of divine abilities
 */
public class AbilityInvoker {
    private static final Logger LOGGER = LogManager.getLogger();

    // Cooldowns: playerId -> (abilityId -> ticksRemaining)
    private static final Map<UUID, Map<String, Integer>> COOLDOWNS = new HashMap<>();

    /**
     * Try to invoke a divine ability
     * Returns true if successful, false if failed (with error message sent to player)
     */
    public static boolean invoke(ServerPlayer player, DivineAbility ability) {
        UUID playerId = player.getUUID();

        // Get player's religion
        PlayerReligion religion = ReligionManager.getReligion(playerId);
        God currentGod = religion.getGod();

        // Check if player worships the right god
        if (currentGod == God.NONE) {
            player.displayClientMessage(Component.literal("§cYou must worship a god to use divine abilities!"), true);
            return false;
        }

        // Check if this ability belongs to the player's god
        if (!GodAbilities.getAbilities(currentGod).contains(ability)) {
            player.displayClientMessage(Component.literal("§cThis ability is not granted by your god!"), true);
            return false;
        }

        // Check piety requirement
        int currentPiety = religion.getPiety();
        if (!ability.isAvailableAtPiety(currentPiety)) {
            player.displayClientMessage(Component.literal(
                String.format("§cYou need %d piety to use %s! (Current: %d)",
                    ability.getMinPiety(), ability.getName(), currentPiety)), true);
            return false;
        }

        // Check cooldown
        if (isOnCooldown(playerId, ability.getId())) {
            int remaining = getCooldownTicks(playerId, ability.getId());
            player.displayClientMessage(Component.literal(
                String.format("§c%s is on cooldown! (%d seconds)",
                    ability.getName(), remaining / 20)), true);
            return false;
        }

        // Get player character
        PlayerCharacter character = CharacterManager.getCharacter(playerId);
        if (character == null) {
            player.displayClientMessage(Component.literal("§cCharacter not found!"), true);
            return false;
        }

        // Execute the ability based on type
        boolean success = executeAbility(player, character, religion, ability);

        if (success) {
            // Consume invocation cost (piety)
            if (ability.getInvocationCost() > 0) {
                religion.losePiety(ability.getInvocationCost());
            }

            // Set cooldown based on ability type
            int cooldown = getCooldownForAbility(ability);
            if (cooldown > 0) {
                setCooldown(playerId, ability.getId(), cooldown);
            }

            // Success message
            player.displayClientMessage(Component.literal(
                String.format("§aYou invoke %s!", ability.getName())), true);

            LOGGER.info("Player {} invoked ability: {}", player.getName().getString(), ability.getName());
        }

        return success;
    }

    /**
     * Execute an ability's effect
     */
    private static boolean executeAbility(ServerPlayer player, PlayerCharacter character,
                                         PlayerReligion religion, DivineAbility ability) {
        UUID playerId = player.getUUID();

        switch (ability.getType()) {
            case BERSERK:
                return executeBerserk(player, character);

            case HEAL:
                return executeHeal(player, character, ability);

            case BUFF:
                return executeBuff(player, character, ability);

            case UTILITY:
                return executeUtility(player, character, ability);

            case SUMMON:
                return executeSummon(player, character, ability);

            case ATTACK:
                return executeAttack(player, character, ability);

            default:
                player.displayClientMessage(Component.literal("§cThis ability is not yet implemented!"), true);
                return false;
        }
    }

    /**
     * Execute berserk ability (Trog)
     */
    private static boolean executeBerserk(ServerPlayer player, PlayerCharacter character) {
        UUID playerId = player.getUUID();

        // Check if player can berserk
        if (!StatusEffectManager.canBerserk(playerId)) {
            if (StatusEffectManager.hasEffect(playerId, StatusEffect.BERSERK)) {
                player.displayClientMessage(Component.literal("§cYou are already berserk!"), true);
            } else if (StatusEffectManager.hasEffect(playerId, StatusEffect.EXHAUSTED)) {
                player.displayClientMessage(Component.literal("§cYou are too exhausted to berserk!"), true);
            }
            return false;
        }

        // Apply berserk status
        StatusEffectManager.addEffect(playerId, StatusEffect.BERSERK);
        player.displayClientMessage(Component.literal("§cYou fly into a berserker rage!"), true);
        return true;
    }

    /**
     * Execute heal ability
     */
    private static boolean executeHeal(ServerPlayer player, PlayerCharacter character, DivineAbility ability) {
        int currentHP = character.getCurrentHP();
        int maxHP = character.getMaxHP();

        if (currentHP >= maxHP) {
            player.displayClientMessage(Component.literal("§cYou are already at full health!"), true);
            return false;
        }

        // Determine healing amount based on ability
        int healAmount;
        switch (ability.getId()) {
            case "elyvilon_lesser_healing":
                healAmount = maxHP / 4; // 25% of max HP
                break;
            case "elyvilon_greater_healing":
                healAmount = maxHP / 2; // 50% of max HP
                break;
            case "zin_vitalisation":
                healAmount = maxHP; // Full heal
                character.restoreMP(character.getMaxMP()); // Also restore MP
                break;
            default:
                healAmount = maxHP / 4; // Default 25%
                break;
        }

        character.heal(healAmount);
        player.displayClientMessage(Component.literal(
            String.format("§aYou are healed for %d HP!", healAmount)), true);
        return true;
    }

    /**
     * Execute buff ability
     */
    private static boolean executeBuff(ServerPlayer player, PlayerCharacter character, DivineAbility ability) {
        UUID playerId = player.getUUID();

        // Map ability IDs to status effects
        StatusEffect effect = switch (ability.getId()) {
            case "trog_trogs_hand" -> StatusEffect.TROGS_HAND;
            case "tso_divine_shield" -> StatusEffect.DIVINE_SHIELD;
            case "okawaru_heroism" -> StatusEffect.HEROISM;
            case "okawaru_finesse" -> StatusEffect.FINESSE;
            case "sif_channel_magic" -> StatusEffect.CHANNEL_MAGIC;
            default -> null;
        };

        if (effect == null) {
            player.displayClientMessage(Component.literal("§cUnknown buff ability!"), true);
            return false;
        }

        // Check if already has this effect
        if (StatusEffectManager.hasEffect(playerId, effect)) {
            player.displayClientMessage(Component.literal("§cYou already have this effect!"), true);
            return false;
        }

        // Apply the buff
        StatusEffectManager.addEffect(playerId, effect);
        return true;
    }

    /**
     * Execute utility ability
     */
    private static boolean executeUtility(ServerPlayer player, PlayerCharacter character, DivineAbility ability) {
        UUID playerId = player.getUUID();

        switch (ability.getId()) {
            case "sif_divine_energy":
                // Restore MP
                int mpToRestore = character.getMaxMP() / 2; // 50% of max MP
                character.restoreMP(mpToRestore);
                player.displayClientMessage(Component.literal(
                    String.format("§bYou channel divine energy! (+%d MP)", mpToRestore)), true);
                return true;

            case "vehumet_mp_regeneration":
                // Apply MP regen buff
                StatusEffectManager.addEffect(playerId, StatusEffect.CHANNEL_MAGIC);
                return true;

            default:
                player.displayClientMessage(Component.literal("§cThis utility ability is not yet implemented!"), true);
                return false;
        }
    }

    /**
     * Execute summon ability (placeholder)
     */
    private static boolean executeSummon(ServerPlayer player, PlayerCharacter character, DivineAbility ability) {
        player.displayClientMessage(Component.literal("§eSummon abilities are not yet implemented!"), true);
        // TODO: Implement summoning when mob system is ready
        return false;
    }

    /**
     * Execute attack ability (placeholder)
     */
    private static boolean executeAttack(ServerPlayer player, PlayerCharacter character, DivineAbility ability) {
        player.displayClientMessage(Component.literal("§eAttack abilities are not yet implemented!"), true);
        // TODO: Implement attack abilities
        return false;
    }

    /**
     * Get cooldown duration for an ability (in ticks)
     */
    private static int getCooldownForAbility(DivineAbility ability) {
        return switch (ability.getType()) {
            case BERSERK -> 1200;     // 60 seconds
            case HEAL -> 600;         // 30 seconds
            case BUFF -> 400;         // 20 seconds
            case UTILITY -> 200;      // 10 seconds
            case SUMMON -> 800;       // 40 seconds
            case ATTACK -> 300;       // 15 seconds
            default -> 200;           // 10 seconds default
        };
    }

    /**
     * Check if an ability is on cooldown
     */
    public static boolean isOnCooldown(UUID playerId, String abilityId) {
        Map<String, Integer> playerCooldowns = COOLDOWNS.get(playerId);
        if (playerCooldowns == null) return false;
        Integer remaining = playerCooldowns.get(abilityId);
        return remaining != null && remaining > 0;
    }

    /**
     * Get remaining cooldown ticks
     */
    public static int getCooldownTicks(UUID playerId, String abilityId) {
        Map<String, Integer> playerCooldowns = COOLDOWNS.get(playerId);
        if (playerCooldowns == null) return 0;
        return playerCooldowns.getOrDefault(abilityId, 0);
    }

    /**
     * Set cooldown for an ability
     */
    public static void setCooldown(UUID playerId, String abilityId, int ticks) {
        Map<String, Integer> playerCooldowns = COOLDOWNS.computeIfAbsent(playerId, k -> new HashMap<>());
        playerCooldowns.put(abilityId, ticks);
    }

    /**
     * Update all cooldowns (call from server tick event)
     */
    public static void tickCooldowns() {
        for (Map<String, Integer> playerCooldowns : COOLDOWNS.values()) {
            playerCooldowns.replaceAll((id, ticks) -> Math.max(0, ticks - 1));
        }
    }

    /**
     * Clear cooldowns for a player (cleanup)
     */
    public static void clearCooldowns(UUID playerId) {
        COOLDOWNS.remove(playerId);
    }
}
