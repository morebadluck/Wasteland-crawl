package com.wasteland.religion;

import com.wasteland.WastelandMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles player interaction with altars
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID)
public class AltarEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Handle right-click on altar blocks
     */
    @SubscribeEvent
    public static void onBlockRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        BlockPos clickedPos = event.getPos();

        // Check if this is an altar
        God altarGod = AltarManager.getAltarGod(clickedPos);
        if (altarGod == null) return;

        // Cancel the event so we don't place blocks
        event.setCanceled(true);

        // Get player's current religion
        PlayerReligion religion = ReligionManager.getReligion(player.getUUID());
        God currentGod = religion.getGod();

        // Already worshipping this god
        if (currentGod == altarGod) {
            showGodInfo(serverPlayer, altarGod, religion);
            return;
        }

        // Already worshipping another god
        if (currentGod != God.NONE) {
            serverPlayer.displayClientMessage(
                Component.literal(String.format(
                    "§cYou already worship %s! You must abandon them first.",
                    currentGod.getDisplayName()
                )),
                false
            );
            return;
        }

        // Has abandoned a god before
        if (religion.hasAbandoned()) {
            serverPlayer.displayClientMessage(
                Component.literal("§cThe gods remember your betrayal. None will accept you now."),
                false
            );
            return;
        }

        // Join the god!
        boolean success = religion.joinGod(altarGod);
        if (success) {
            serverPlayer.displayClientMessage(
                Component.literal("§e╔════════════════════════════════════════════╗"),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal(String.format("§e§l  You now worship %s!", altarGod.getDisplayName())),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal("§e╚════════════════════════════════════════════╝"),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal(""),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal(String.format("§7%s", altarGod.getDescription())),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal(""),
                false
            );

            // Show what the god likes
            serverPlayer.displayClientMessage(
                Component.literal("§a§lLikes:"),
                false
            );
            for (String like : altarGod.getLikes()) {
                serverPlayer.displayClientMessage(
                    Component.literal(String.format("§a  • %s", like)),
                    false
                );
            }

            // Show what the god dislikes
            serverPlayer.displayClientMessage(
                Component.literal("§c§lDislikes:"),
                false
            );
            for (String dislike : altarGod.getDislikes()) {
                serverPlayer.displayClientMessage(
                    Component.literal(String.format("§c  • %s", dislike)),
                    false
                );
            }

            // Show initial piety
            serverPlayer.displayClientMessage(
                Component.literal(""),
                false
            );
            serverPlayer.displayClientMessage(
                Component.literal(String.format("§6Piety: %s %d/%d",
                    religion.getPietyStars(),
                    religion.getPiety(),
                    PlayerReligion.MAX_PIETY
                )),
                false
            );

            LOGGER.info("Player {} joined {}", player.getName().getString(), altarGod.getDisplayName());
        }
    }

    /**
     * Show god information and abilities to a worshipper
     */
    private static void showGodInfo(ServerPlayer player, God god, PlayerReligion religion) {
        player.displayClientMessage(
            Component.literal(String.format("§e§l%s", god.getDisplayName())),
            false
        );
        player.displayClientMessage(
            Component.literal(String.format("§7%s", god.getDescription())),
            false
        );
        player.displayClientMessage(
            Component.literal(""),
            false
        );
        player.displayClientMessage(
            Component.literal(String.format("§6Piety: %s %d/%d (Rank %d)",
                religion.getPietyStars(),
                religion.getPiety(),
                PlayerReligion.MAX_PIETY,
                religion.getPietyRank()
            )),
            false
        );
        player.displayClientMessage(
            Component.literal(""),
            false
        );

        // Show available abilities
        player.displayClientMessage(
            Component.literal("§e§lDivine Powers:"),
            false
        );

        java.util.List<DivineAbility> abilities = GodAbilities.getAvailableAbilities(god, religion.getPiety());
        if (abilities.isEmpty()) {
            player.displayClientMessage(
                Component.literal("§7  (None available yet - gain more piety!)"),
                false
            );
        } else {
            for (DivineAbility ability : abilities) {
                String prefix = ability.isPassive() ? "§a[Passive]" : "§b[Active]";
                player.displayClientMessage(
                    Component.literal(String.format("%s §f%s", prefix, ability.getName())),
                    false
                );
                player.displayClientMessage(
                    Component.literal(String.format("§7    %s", ability.getDescription())),
                    false
                );
            }
        }

        // Show next milestone
        int nextRank = religion.getPietyRank() + 1;
        if (nextRank <= 6) {
            int nextMilestone = switch (nextRank) {
                case 1 -> PlayerReligion.PIETY_RANK_1;
                case 2 -> PlayerReligion.PIETY_RANK_2;
                case 3 -> PlayerReligion.PIETY_RANK_3;
                case 4 -> PlayerReligion.PIETY_RANK_4;
                case 5 -> PlayerReligion.PIETY_RANK_5;
                case 6 -> PlayerReligion.PIETY_RANK_6;
                default -> -1;
            };

            if (nextMilestone > 0) {
                int needed = nextMilestone - religion.getPiety();
                player.displayClientMessage(
                    Component.literal(""),
                    false
                );
                player.displayClientMessage(
                    Component.literal(String.format("§7Next power in %d piety", needed)),
                    false
                );
            }
        }
    }
}
