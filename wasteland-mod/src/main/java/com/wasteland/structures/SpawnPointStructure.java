package com.wasteland.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom spawn point structure that creates a safe starting area
 * near a road on flat terrain
 */
public class SpawnPointStructure {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Generate the spawn point structure
     * This creates:
     * - A flat platform (if needed)
     * - A simple shelter
     * - A road leading away from spawn
     * - Some basic supplies
     */
    public static void generate(ServerLevel level, BlockPos spawnPos, RandomSource random) {
        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Building Spawn Point Structure");
        LOGGER.info("  Location: {}", spawnPos);
        LOGGER.info("═══════════════════════════════════════════════════════");

        // Find the ground level at spawn point
        BlockPos groundPos = level.getHeightmapPos(
            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
            spawnPos
        );

        // Create a flat platform (15x15) at spawn level
        int platformSize = 15;
        int platformRadius = platformSize / 2;

        for (int x = -platformRadius; x <= platformRadius; x++) {
            for (int z = -platformRadius; z <= platformRadius; z++) {
                BlockPos platformPos = groundPos.offset(x, -1, z);
                // Use gray concrete for the platform base
                level.setBlock(platformPos, Blocks.GRAY_CONCRETE.defaultBlockState(), 3);

                // Clear the area above
                for (int y = 0; y < 5; y++) {
                    level.setBlock(platformPos.above(y + 1), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        // Build a simple shelter (5x5) in the center
        int shelterSize = 5;
        int shelterRadius = shelterSize / 2;
        BlockPos shelterBase = groundPos.offset(-shelterRadius, 0, -shelterRadius);

        // Shelter floor
        for (int x = 0; x < shelterSize; x++) {
            for (int z = 0; z < shelterSize; z++) {
                level.setBlock(shelterBase.offset(x, 0, z),
                    Blocks.STONE_BRICKS.defaultBlockState(), 3);
            }
        }

        // Shelter walls (3 blocks high)
        for (int x = 0; x < shelterSize; x++) {
            for (int y = 1; y <= 3; y++) {
                // North and south walls
                level.setBlock(shelterBase.offset(x, y, 0),
                    Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(shelterBase.offset(x, y, shelterSize - 1),
                    Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        for (int z = 0; z < shelterSize; z++) {
            for (int y = 1; y <= 3; y++) {
                // East and west walls
                level.setBlock(shelterBase.offset(0, y, z),
                    Blocks.BRICKS.defaultBlockState(), 3);
                level.setBlock(shelterBase.offset(shelterSize - 1, y, z),
                    Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        // Add doorway (south side, center)
        BlockPos doorPos = shelterBase.offset(shelterSize / 2, 1, shelterSize - 1);
        level.setBlock(doorPos, Blocks.AIR.defaultBlockState(), 3);
        level.setBlock(doorPos.above(), Blocks.AIR.defaultBlockState(), 3);

        // Shelter roof
        for (int x = 0; x < shelterSize; x++) {
            for (int z = 0; z < shelterSize; z++) {
                level.setBlock(shelterBase.offset(x, 4, z),
                    Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
            }
        }

        // Interior furnishings
        // Crafting table
        level.setBlock(shelterBase.offset(1, 1, 1),
            Blocks.CRAFTING_TABLE.defaultBlockState(), 3);

        // Chest with basic supplies
        BlockPos chestPos = shelterBase.offset(3, 1, 1);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        // Fill chest with starter items
        fillStarterChest(level, chestPos);

        // Bed
        level.setBlock(shelterBase.offset(1, 1, 3),
            Blocks.RED_BED.defaultBlockState(), 3);

        // Torch for light
        level.setBlock(shelterBase.offset(2, 2, 2),
            Blocks.TORCH.defaultBlockState(), 3);

        // Build a road leading away from spawn (heading south)
        buildRoad(level, groundPos.offset(0, 0, platformRadius + 1), 0, 1, 20);

        // Add another road (heading east)
        buildRoad(level, groundPos.offset(platformRadius + 1, 0, 0), 1, 0, 20);

        // Add a sign near the door
        level.setBlock(shelterBase.offset(shelterSize / 2 + 1, 1, shelterSize),
            Blocks.OAK_SIGN.defaultBlockState(), 3);

        // Add some decorative elements
        // Barrel outside
        level.setBlock(groundPos.offset(4, 0, 4),
            Blocks.BARREL.defaultBlockState(), 3);

        // Lantern on a fence post
        level.setBlock(groundPos.offset(-4, 0, 4),
            Blocks.OAK_FENCE.defaultBlockState(), 3);
        level.setBlock(groundPos.offset(-4, 1, 4),
            Blocks.LANTERN.defaultBlockState(), 3);

        LOGGER.info("  Spawn point structure complete!");
        LOGGER.info("═══════════════════════════════════════════════════════");
    }

    /**
     * Fill the starter chest with basic equipment and supplies
     */
    private static void fillStarterChest(ServerLevel level, BlockPos chestPos) {
        LOGGER.info("Attempting to fill starter chest at: {}", chestPos);

        // Wait a tick for block entity to be created
        level.getServer().execute(() -> {
            // Get the chest block entity
            var blockEntity = level.getBlockEntity(chestPos);
            LOGGER.info("Chest block entity: {}", blockEntity);

            if (!(blockEntity instanceof net.minecraft.world.level.block.entity.ChestBlockEntity chestEntity)) {
                LOGGER.warn("Failed to find chest block entity at {}", chestPos);
                return;
            }

            // Create starter items
            net.minecraft.world.item.ItemStack starterWeapon = createStarterWeapon();
            net.minecraft.world.item.ItemStack starterArmor = createStarterArmor();

            LOGGER.info("Adding items to chest: weapon={}, armor={}", starterWeapon, starterArmor);

            // Add items to chest
            chestEntity.setItem(0, starterWeapon);
            chestEntity.setItem(1, starterArmor);

            // Add some basic supplies
            chestEntity.setItem(2, new net.minecraft.world.item.ItemStack(Blocks.TORCH, 16));
            chestEntity.setItem(3, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BREAD, 8));
            chestEntity.setItem(4, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.COOKED_BEEF, 4));

            chestEntity.setChanged();
            LOGGER.info("Chest filled successfully");
        });
    }

    /**
     * Create a starter weapon (Shiv)
     */
    private static net.minecraft.world.item.ItemStack createStarterWeapon() {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(
            com.wasteland.item.ModItems.SHIV.get()
        );

        // Initialize with common rarity, no enchantment
        com.wasteland.loot.WastelandWeapon weapon = new com.wasteland.loot.WastelandWeapon(
            stack,
            com.wasteland.equipment.WeaponType.SHIV,
            com.wasteland.loot.ItemRarity.COMMON,
            0
        );
        weapon.identify();
        weapon.saveToNBT();

        return stack;
    }

    /**
     * Create starter armor (Robe)
     */
    private static net.minecraft.world.item.ItemStack createStarterArmor() {
        net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(
            com.wasteland.item.ModItems.ROBE.get()
        );

        // Initialize with common rarity, no enchantment
        com.wasteland.loot.WastelandArmor armor = new com.wasteland.loot.WastelandArmor(
            stack,
            com.wasteland.loot.ArmorType.ROBE,
            com.wasteland.loot.ItemRarity.COMMON,
            0
        );
        armor.identify();
        armor.saveToNBT();

        return stack;
    }

    /**
     * Build a simple road made of stone/concrete
     */
    private static void buildRoad(ServerLevel level, BlockPos startPos, int dirX, int dirZ, int length) {
        // Road is 3 blocks wide
        for (int i = 0; i < length; i++) {
            BlockPos roadCenter = startPos.offset(i * dirX, 0, i * dirZ);

            // Find ground level at this position
            BlockPos groundPos = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                roadCenter
            );

            // Place road blocks (3 wide)
            if (dirX != 0) {
                // Road running east-west, make it wide north-south
                level.setBlock(groundPos.offset(0, -1, -1), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(groundPos.offset(0, -1, 0), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(groundPos.offset(0, -1, 1), Blocks.STONE.defaultBlockState(), 3);
            } else {
                // Road running north-south, make it wide east-west
                level.setBlock(groundPos.offset(-1, -1, 0), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(groundPos.offset(0, -1, 0), Blocks.STONE.defaultBlockState(), 3);
                level.setBlock(groundPos.offset(1, -1, 0), Blocks.STONE.defaultBlockState(), 3);
            }
        }
    }

    /**
     * Check if a position is suitable for spawning
     * (relatively flat terrain)
     */
    public static boolean isSuitableSpawnLocation(ServerLevel level, BlockPos pos) {
        // Check height variance in a 15x15 area
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                BlockPos checkPos = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                    pos.offset(x, 0, z)
                );
                minY = Math.min(minY, checkPos.getY());
                maxY = Math.max(maxY, checkPos.getY());
            }
        }

        // Terrain should be relatively flat (max 5 block variance)
        return (maxY - minY) <= 5;
    }
}
