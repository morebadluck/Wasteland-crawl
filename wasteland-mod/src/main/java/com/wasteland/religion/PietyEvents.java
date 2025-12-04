package com.wasteland.religion;

import com.wasteland.WastelandMod;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles piety gain and loss based on player actions
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID)
public class PietyEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Handle entity death - grant piety for kills
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();

        // Check if killed by a player
        if (!(sourceEntity instanceof Player player)) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        // Get player's god
        PlayerReligion religion = ReligionManager.getReligion(player.getUUID());
        God god = religion.getGod();
        if (god == God.NONE) return;

        LivingEntity victim = event.getEntity();
        handleKill(serverPlayer, religion, god, victim);
    }

    /**
     * Handle kills for piety
     */
    private static void handleKill(ServerPlayer player, PlayerReligion religion, God god, LivingEntity victim) {
        int pietyGain = 0;
        String message = null;

        switch (god) {
            case TROG:
                // Trog likes killing, especially in melee
                pietyGain = 2;
                if (pietyGain > 0) {
                    message = "§6Trog accepts your kill.";
                }
                break;

            case THE_SHINING_ONE:
                // TSO likes killing evil/undead/demons
                if (victim instanceof Enemy) {
                    MobType mobType = victim.getMobType();
                    if (mobType == MobType.UNDEAD) {
                        pietyGain = 3;
                        message = "§6The Shining One approves of your destruction of the undead.";
                    } else if (mobType == MobType.ILLAGER) { // Using illager as proxy for evil
                        pietyGain = 2;
                        message = "§6The Shining One smiles upon your holy crusade.";
                    } else {
                        pietyGain = 1;
                        message = "§6The Shining One accepts your offering.";
                    }
                }
                break;

            case ZIN:
                // Zin likes killing chaotic/unclean creatures
                if (victim instanceof Enemy) {
                    pietyGain = 2;
                    message = "§6Zin appreciates your cleansing of chaos.";
                }
                break;

            case ELYVILON:
                // Elyvilon DISLIKES unnecessary killing
                religion.losePiety(3);
                player.displayClientMessage(
                    Component.literal("§cElyvilon is displeased by this killing!"),
                    true
                );
                return;

            case OKAWARU:
                // Okawaru likes all combat kills
                pietyGain = 2;
                if (victim instanceof Enemy) {
                    pietyGain = 3; // Extra for killing monsters
                    message = "§6Okawaru is pleased by your victory!";
                }
                break;

            case VEHUMET:
                // Vehumet likes kills with spells (TODO: track spell kills)
                // For now, give small amount for any kill
                pietyGain = 1;
                break;

            case KIKUBAAQUDGHA:
                // Kikubaaqudgha likes killing living creatures
                if (victim.getMobType() != MobType.UNDEAD) {
                    pietyGain = 2;
                    message = "§6Kikubaaqudgha appreciates the death.";
                }
                break;

            case MAKHLEB:
                // Makhleb likes killing anything
                pietyGain = 2;
                message = "§6Makhleb revels in destruction!";
                break;

            case YREDELEMNUL:
                // Yredelemnul likes killing living creatures
                if (victim.getMobType() != MobType.UNDEAD) {
                    pietyGain = 2;
                    message = "§6Yredelemnul feeds on death.";
                }
                break;

            case XOM:
                // Xom doesn't use piety
                return;

            case GOZAG:
                // Gozag doesn't care about kills
                return;

            case SIF_MUNA:
                // Sif Muna doesn't care much about kills
                pietyGain = 1;
                break;
        }

        if (pietyGain > 0) {
            int oldRank = religion.getPietyRank();
            religion.gainPiety(pietyGain);
            int newRank = religion.getPietyRank();

            if (message != null) {
                player.displayClientMessage(Component.literal(message), true);
            }

            // Notify on rank up
            if (newRank > oldRank) {
                player.displayClientMessage(
                    Component.literal(String.format("§e§lYou have gained divine favour! %s",
                                                   religion.getPietyStars())),
                    false
                );
            }
        }
    }

    /**
     * Handle player leaving - save religion data
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        // Religion data is kept in memory for reconnection
        // In a full implementation, this would save to disk/database
        LOGGER.debug("Player {} logged out, religion data retained",
                    event.getEntity().getUUID());
    }

    /**
     * Grant piety for exploration (called from elsewhere)
     */
    public static void grantExplorationPiety(Player player) {
        PlayerReligion religion = ReligionManager.getReligion(player.getUUID());
        God god = religion.getGod();

        switch (god) {
            case SIF_MUNA:
                religion.gainPiety(1);
                player.displayClientMessage(
                    Component.literal("§6Sif Muna appreciates your pursuit of knowledge."),
                    true
                );
                break;
            case ELYVILON:
                religion.gainPiety(1);
                player.displayClientMessage(
                    Component.literal("§6Elyvilon smiles upon your peaceful exploration."),
                    true
                );
                break;
        }
    }

    /**
     * Grant piety for casting spells (called from spell system)
     */
    public static void grantSpellPiety(Player player, boolean isDestructive) {
        PlayerReligion religion = ReligionManager.getReligion(player.getUUID());
        God god = religion.getGod();

        switch (god) {
            case TROG:
                // Trog HATES magic
                religion.losePiety(5);
                player.displayClientMessage(
                    Component.literal("§c§lTrog is furious at your use of magic!"),
                    false
                );
                break;

            case SIF_MUNA:
                religion.gainPiety(1);
                break;

            case VEHUMET:
                if (isDestructive) {
                    religion.gainPiety(2);
                    player.displayClientMessage(
                        Component.literal("§6Vehumet is pleased by your destructive magic!"),
                        true
                    );
                }
                break;
        }
    }

    /**
     * Penalize for attacking allies
     */
    public static void penalizeAllyAttack(Player player) {
        PlayerReligion religion = ReligionManager.getReligion(player.getUUID());
        God god = religion.getGod();

        // Most good gods penalize attacking allies
        switch (god) {
            case THE_SHINING_ONE:
            case ZIN:
            case ELYVILON:
                religion.losePiety(10);
                player.displayClientMessage(
                    Component.literal(String.format("§c§l%s is furious at your betrayal!",
                                                   god.getDisplayName())),
                    false
                );
                break;
        }
    }
}
