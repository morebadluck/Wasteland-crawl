package com.wasteland;

import com.wasteland.player.RuneInventory;
import com.wasteland.worldgen.RuneType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * The gate to the Realm of Zot (final level).
 * Requires player to have collected 3+ different runes to enter.
 *
 * TODO: Rename "Realm of Zot" to wasteland-appropriate name
 */
public class ZotGate {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Build the Zot gate structure at the specified location
     */
    public static void placeZotGate(ServerLevel level, BlockPos pos) {
        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Building Zot Gate Structure");
        LOGGER.info("  Location: {}", pos);
        LOGGER.info("═══════════════════════════════════════════════════════");

        // Build an imposing gate structure
        int width = 15;
        int depth = 15;
        int height = 7;

        // Floor and walls
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int z = 0; z < depth; z++) {
                    BlockPos buildPos = pos.offset(x, y, z);
                    boolean isWall = (x == 0 || x == width - 1 || z == 0 || z == depth - 1);
                    boolean isFloor = (y == 0);

                    if (isFloor) {
                        // Obsidian floor
                        level.setBlock(buildPos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    } else if (isWall && y < height - 1) {
                        // Nether brick walls
                        level.setBlock(buildPos, Blocks.NETHER_BRICKS.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Create archway entrance
        BlockPos archway = pos.offset(7, 1, 0);
        level.setBlock(archway, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(archway.above(), Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(archway.above(2), Blocks.AIR.defaultBlockState(), 3);

        // Central portal - purple wool (requires runes)
        BlockPos portalPos = pos.offset(7, 1, 7);
        level.setBlock(portalPos, Blocks.PURPLE_WOOL.defaultBlockState(), 3);

        // Rune pedestals (end rods)
        level.setBlock(pos.offset(4, 1, 7), Blocks.END_ROD.defaultBlockState(), 3);
        level.setBlock(pos.offset(10, 1, 7), Blocks.END_ROD.defaultBlockState(), 3);
        level.setBlock(pos.offset(7, 1, 4), Blocks.END_ROD.defaultBlockState(), 3);

        // Register portal (restricted)
        PortalManager.registerPortal(portalPos, PortalManager.PortalType.ZOT_GATE, null);

        // Sign
        level.setBlock(pos.offset(7, 2, 1), Blocks.OAK_SIGN.defaultBlockState(), 3);

        LOGGER.info("  Zot Gate complete!");
        LOGGER.info("  Requires {} runes to enter", RuneType.getRunesRequiredForZot());
        LOGGER.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Check if player can enter Zot (has enough runes)
     */
    public static boolean canPlayerEnterZot(Player player) {
        return RuneInventory.canAccessZot(player.getUUID());
    }

    /**
     * Attempt to let player through Zot gate
     * Returns true if allowed, false if not enough runes
     */
    public static boolean attemptZotEntry(Player player) {
        if (canPlayerEnterZot(player)) {
            Set<RuneType> runes = RuneInventory.getRunes(player.getUUID());
            player.sendSystemMessage(Component.literal("The gate recognizes your " + runes.size() + " runes and opens...")
                .withStyle(ChatFormatting.GOLD));
            return true;
        } else {
            int runesNeeded = RuneType.getRunesRequiredForZot();
            int runesHave = RuneInventory.getRuneCount(player.getUUID());

            player.sendSystemMessage(Component.literal("The gate is sealed. You need " + runesNeeded + " runes to enter.")
                .withStyle(ChatFormatting.RED));
            player.sendSystemMessage(Component.literal("You currently have " + runesHave + " rune(s).")
                .withStyle(ChatFormatting.YELLOW));

            // Show which runes they have
            if (runesHave > 0) {
                player.sendSystemMessage(Component.literal("Collected runes:").withStyle(ChatFormatting.GRAY));
                for (String runeLine : RuneInventory.getRuneList(player.getUUID())) {
                    player.sendSystemMessage(Component.literal("  " + runeLine));
                }
            }

            return false;
        }
    }
}
