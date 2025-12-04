package com.wasteland.loot;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Loot tables for robot enemies
 * Robots drop tech components, energy cells, and rare robot parts
 */
public class RobotLoot {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Robot loot tier - determines quality and quantity of drops
     */
    public enum RobotTier {
        LIGHT,      // Eyebot, Mr. Handy
        MEDIUM,     // Protectron, Security Bot
        COMBAT,     // Assaultron, Mr. Gusty, Robobrain
        HEAVY,      // Sentry Bot, Experimental Bot
        ELITE,      // Annihilator, Quantum Assaultron
        BOSS        // Overlord Bot
    }

    /**
     * Drop loot for a destroyed robot
     */
    public static void dropLoot(Level level, BlockPos pos, RobotTier tier) {
        if (level.isClientSide) return;

        // Always drop scrap metal (iron ingots)
        dropItem(level, pos, new ItemStack(Items.IRON_INGOT, 1 + RANDOM.nextInt(3)));

        // Energy cells (glowstone dust)
        if (RANDOM.nextFloat() < 0.7F) {
            dropItem(level, pos, new ItemStack(Items.GLOWSTONE_DUST, 1 + RANDOM.nextInt(2)));
        }

        // Circuits (redstone)
        if (RANDOM.nextFloat() < 0.5F) {
            dropItem(level, pos, new ItemStack(Items.REDSTONE, 2 + RANDOM.nextInt(4)));
        }

        // Tier-specific loot
        switch (tier) {
            case LIGHT:
                dropLightRobotLoot(level, pos);
                break;
            case MEDIUM:
                dropMediumRobotLoot(level, pos);
                break;
            case COMBAT:
                dropCombatRobotLoot(level, pos);
                break;
            case HEAVY:
                dropHeavyRobotLoot(level, pos);
                break;
            case ELITE:
                dropEliteRobotLoot(level, pos);
                break;
            case BOSS:
                dropBossRobotLoot(level, pos);
                break;
        }
    }

    private static void dropLightRobotLoot(Level level, BlockPos pos) {
        // Light robots drop basic components
        // 30% chance for sensor module (compass)
        if (RANDOM.nextFloat() < 0.3F) {
            dropItem(level, pos, new ItemStack(Items.COMPASS));
        }
    }

    private static void dropMediumRobotLoot(Level level, BlockPos pos) {
        // Medium robots drop better components
        dropItem(level, pos, new ItemStack(Items.IRON_INGOT, 2 + RANDOM.nextInt(3)));

        // 40% chance for armor plating (iron blocks)
        if (RANDOM.nextFloat() < 0.4F) {
            dropItem(level, pos, new ItemStack(Items.IRON_BLOCK));
        }
    }

    private static void dropCombatRobotLoot(Level level, BlockPos pos) {
        // Combat robots drop weapons components
        dropItem(level, pos, new ItemStack(Items.IRON_INGOT, 3 + RANDOM.nextInt(4)));
        dropItem(level, pos, new ItemStack(Items.GUNPOWDER, 2 + RANDOM.nextInt(3)));

        // 50% chance for targeting system (ender pearl)
        if (RANDOM.nextFloat() < 0.5F) {
            dropItem(level, pos, new ItemStack(Items.ENDER_PEARL));
        }
    }

    private static void dropHeavyRobotLoot(Level level, BlockPos pos) {
        // Heavy robots drop lots of metal and explosives
        dropItem(level, pos, new ItemStack(Items.IRON_BLOCK, 1 + RANDOM.nextInt(2)));
        dropItem(level, pos, new ItemStack(Items.TNT, 1 + RANDOM.nextInt(2)));

        // 60% chance for power core (blaze rod)
        if (RANDOM.nextFloat() < 0.6F) {
            dropItem(level, pos, new ItemStack(Items.BLAZE_ROD));
        }

        // 30% chance for rare energy weapon part (nether star fragment - using ghast tear)
        if (RANDOM.nextFloat() < 0.3F) {
            dropItem(level, pos, new ItemStack(Items.GHAST_TEAR));
        }
    }

    private static void dropEliteRobotLoot(Level level, BlockPos pos) {
        // Elite robots drop high-tier components
        dropItem(level, pos, new ItemStack(Items.IRON_BLOCK, 2 + RANDOM.nextInt(3)));
        dropItem(level, pos, new ItemStack(Items.DIAMOND, 1 + RANDOM.nextInt(2)));
        dropItem(level, pos, new ItemStack(Items.BLAZE_ROD, 1 + RANDOM.nextInt(2)));

        // 70% chance for quantum core (ender eye)
        if (RANDOM.nextFloat() < 0.7F) {
            dropItem(level, pos, new ItemStack(Items.ENDER_EYE));
        }

        // 40% chance for rare tech (nether star)
        if (RANDOM.nextFloat() < 0.4F) {
            dropItem(level, pos, new ItemStack(Items.NETHER_STAR));
        }
    }

    private static void dropBossRobotLoot(Level level, BlockPos pos) {
        // Boss drops MASSIVE loot
        dropItem(level, pos, new ItemStack(Items.IRON_BLOCK, 5 + RANDOM.nextInt(5)));
        dropItem(level, pos, new ItemStack(Items.DIAMOND, 3 + RANDOM.nextInt(4)));
        dropItem(level, pos, new ItemStack(Items.EMERALD, 2 + RANDOM.nextInt(3)));
        dropItem(level, pos, new ItemStack(Items.NETHER_STAR, 1 + RANDOM.nextInt(2)));

        // Always drops unique boss items
        dropItem(level, pos, new ItemStack(Items.BEACON)); // Command core
        dropItem(level, pos, new ItemStack(Items.NETHERITE_INGOT, 2 + RANDOM.nextInt(3))); // Advanced alloy

        // 100% chance for legendary weapon schematic (enchanted book)
        dropItem(level, pos, new ItemStack(Items.ENCHANTED_BOOK));

        LOGGER.info("BOSS ROBOT DEFEATED! Legendary loot dropped at {}", pos);
    }

    /**
     * Helper to spawn item entity at position
     */
    private static void dropItem(Level level, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty()) return;

        double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.5;
        double y = pos.getY() + 0.5;
        double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.5;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }
}
