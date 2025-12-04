package com.wasteland.religion;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages altar locations and god assignments
 * Uses existing Minecraft blocks as placeholder altars until custom models are created
 */
public class AltarManager {
    private static final Logger LOGGER = LogManager.getLogger();

    // Map of altar positions to gods
    private static final Map<BlockPos, God> ALTARS = new HashMap<>();

    /**
     * Register an altar at a position
     */
    public static void registerAltar(BlockPos pos, God god) {
        ALTARS.put(pos.immutable(), god);
        LOGGER.info("Registered {} altar at {}", god.getDisplayName(), pos);
    }

    /**
     * Get the god for an altar at a position
     */
    public static God getAltarGod(BlockPos pos) {
        return ALTARS.get(pos);
    }

    /**
     * Check if a position has an altar
     */
    public static boolean isAltar(BlockPos pos) {
        return ALTARS.containsKey(pos);
    }

    /**
     * Remove an altar
     */
    public static void removeAltar(BlockPos pos) {
        God god = ALTARS.remove(pos);
        if (god != null) {
            LOGGER.info("Removed {} altar at {}", god.getDisplayName(), pos);
        }
    }

    /**
     * Place an altar in the world (using placeholder blocks)
     * In the future, this will use custom BlockBench models
     */
    public static void placeAltar(ServerLevel level, BlockPos pos, God god) {
        Block altarBlock = getAltarBlock(god);

        // Place the altar block
        level.setBlock(pos, altarBlock.defaultBlockState(), 3);

        // Register it
        registerAltar(pos, god);

        LOGGER.info("Placed {} altar at {} using block {}",
                   god.getDisplayName(), pos, altarBlock.getDescriptionId());
    }

    /**
     * Get the placeholder block for a god's altar
     * TODO: Replace with custom BlockBench models
     */
    private static Block getAltarBlock(God god) {
        return switch (god) {
            case THE_SHINING_ONE -> Blocks.GLOWSTONE;        // Good - Holy light
            case ZIN -> Blocks.QUARTZ_BLOCK;                 // Good - Law and order
            case ELYVILON -> Blocks.PINK_WOOL;               // Good - Healing
            case TROG -> Blocks.NETHERRACK;                  // Neutral - Anger
            case OKAWARU -> Blocks.IRON_BLOCK;               // Neutral - War
            case SIF_MUNA -> Blocks.BOOKSHELF;               // Neutral - Magic/Knowledge
            case VEHUMET -> Blocks.MAGMA_BLOCK;              // Neutral - Destructive magic
            case KIKUBAAQUDGHA -> Blocks.SOUL_SAND;          // Evil - Necromancy
            case MAKHLEB -> Blocks.CRYING_OBSIDIAN;          // Evil - Chaos
            case YREDELEMNUL -> Blocks.WITHER_SKELETON_SKULL;// Evil - Death
            case XOM -> Blocks.PURPLE_CONCRETE;              // Chaotic - Chaos
            case GOZAG -> Blocks.GOLD_BLOCK;                 // Neutral - Gold
            default -> Blocks.STONE;                         // Fallback
        };
    }

    /**
     * Place a random selection of altars near a position
     * Useful for temple rooms in dungeons
     */
    public static void placeTemple(ServerLevel level, BlockPos centerPos, int count) {
        God[] worshipableGods = God.getWorshipableGods();

        // Shuffle and select random gods
        java.util.List<God> selectedGods = new java.util.ArrayList<>();
        for (God god : worshipableGods) {
            selectedGods.add(god);
        }
        java.util.Collections.shuffle(selectedGods);

        // Place altars in a circle
        int radius = 4;
        for (int i = 0; i < Math.min(count, selectedGods.size()); i++) {
            double angle = (2 * Math.PI * i) / count;
            int x = (int) (Math.cos(angle) * radius);
            int z = (int) (Math.sin(angle) * radius);

            BlockPos altarPos = centerPos.offset(x, 0, z);
            placeAltar(level, altarPos, selectedGods.get(i));
        }

        LOGGER.info("Placed temple with {} altars at {}", count, centerPos);
    }

    /**
     * Get all altars (for debugging)
     */
    public static Map<BlockPos, God> getAllAltars() {
        return new HashMap<>(ALTARS);
    }

    /**
     * Clear all altars (for world reload)
     */
    public static void clearAllAltars() {
        ALTARS.clear();
        LOGGER.info("Cleared all altar registrations");
    }
}
