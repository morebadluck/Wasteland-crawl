package com.wasteland.worldgen;

import com.wasteland.DungeonEntrance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages all dungeon instances in the world.
 * Tracks dungeon locations, types, and generates dungeons spread across biomes.
 */
public class DungeonManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, DungeonInstance> DUNGEONS = new HashMap<>();
    private static final Map<BlockPos, UUID> ENTRANCE_POSITIONS = new HashMap<>();

    // Configuration
    private static final int DUNGEON_SPACING = 500;  // Min blocks between dungeons
    private static final int DUNGEONS_PER_REGION = 0;  // Average dungeons per USA region (TEMPORARILY DISABLED FOR TESTING)

    /**
     * Register a new dungeon
     */
    public static DungeonInstance registerDungeon(DungeonType type, BlockPos entrancePos) {
        DungeonInstance dungeon = new DungeonInstance(type, entrancePos);
        DUNGEONS.put(dungeon.getId(), dungeon);
        ENTRANCE_POSITIONS.put(entrancePos, dungeon.getId());

        LOGGER.info("Registered dungeon: {} at {} ({} levels, max={})",
            type.getDisplayName(), entrancePos, dungeon.getNumLevels(), dungeon.hasMaxLevels());

        return dungeon;
    }

    /**
     * Get dungeon by ID
     */
    public static DungeonInstance getDungeon(UUID id) {
        return DUNGEONS.get(id);
    }

    /**
     * Get dungeon at entrance position
     */
    public static DungeonInstance getDungeonAtPosition(BlockPos pos) {
        UUID id = ENTRANCE_POSITIONS.get(pos);
        return id != null ? DUNGEONS.get(id) : null;
    }

    /**
     * Get all dungeons
     */
    public static Collection<DungeonInstance> getAllDungeons() {
        return new ArrayList<>(DUNGEONS.values());
    }

    /**
     * Get all max-level dungeons
     */
    public static List<DungeonInstance> getMaxLevelDungeons() {
        List<DungeonInstance> maxDungeons = new ArrayList<>();
        for (DungeonInstance dungeon : DUNGEONS.values()) {
            if (dungeon.hasMaxLevels()) {
                maxDungeons.add(dungeon);
            }
        }
        return maxDungeons;
    }

    /**
     * Get all dungeons with uncollected runes
     */
    public static List<DungeonInstance> getDungeonsWithRunes() {
        List<DungeonInstance> runeDungeons = new ArrayList<>();
        for (DungeonInstance dungeon : DUNGEONS.values()) {
            if (dungeon.hasMaxLevels() && !dungeon.isRuneCollected()) {
                runeDungeons.add(dungeon);
            }
        }
        return runeDungeons;
    }

    /**
     * Generate dungeons across the overworld based on regions
     */
    public static void generateDungeons(ServerLevel level, long worldSeed) {
        LOGGER.info("=== Generating dungeons for world seed {} ===", worldSeed);
        Random random = new Random(worldSeed);

        // Generate dungeons for each USA region
        for (USARegion region : USARegion.values()) {
            int numDungeons = DUNGEONS_PER_REGION + random.nextInt(3);  // 3-5 dungeons per region

            for (int i = 0; i < numDungeons; i++) {
                // Random position within region bounds
                int x = region.getCenterX() + (random.nextInt(DUNGEON_SPACING * 2) - DUNGEON_SPACING);
                int z = region.getCenterZ() + (random.nextInt(DUNGEON_SPACING * 2) - DUNGEON_SPACING);

                // Find ground level for dungeon entrance
                BlockPos searchPos = new BlockPos(x, 64, z);
                BlockPos groundPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, searchPos);

                // Check spacing from existing dungeons
                if (!isValidDungeonLocation(groundPos)) {
                    continue;
                }

                // Get random dungeon type suitable for this region
                DungeonType type = DungeonType.getRandomForRegion(region, random);

                // Register the dungeon
                DungeonInstance dungeon = registerDungeon(type, groundPos);

                // Place the physical entrance structure in the world
                DungeonEntrance.placeRandomEntrance(level, groundPos, dungeon.getId());

                LOGGER.debug("Placed {} entrance at {}", type.getDisplayName(), groundPos);
            }
        }

        LOGGER.info("Generated {} dungeons total", DUNGEONS.size());
        LOGGER.info("Max-level dungeons: {}", getMaxLevelDungeons().size());

        // Log rune distribution
        Map<RuneType, Integer> runeCounts = new HashMap<>();
        for (DungeonInstance dungeon : getMaxLevelDungeons()) {
            runeCounts.put(dungeon.getRune(), runeCounts.getOrDefault(dungeon.getRune(), 0) + 1);
        }
        LOGGER.info("Rune distribution: {}", runeCounts);
    }

    /**
     * Check if a location is valid for a dungeon (spacing check)
     */
    private static boolean isValidDungeonLocation(BlockPos pos) {
        for (BlockPos existingPos : ENTRANCE_POSITIONS.keySet()) {
            double distance = Math.sqrt(existingPos.distSqr(pos));
            if (distance < DUNGEON_SPACING) {
                return false;
            }
        }
        return true;
    }

    /**
     * Save all dungeons to NBT
     */
    public static CompoundTag save(CompoundTag tag) {
        ListTag dungeonsList = new ListTag();

        for (DungeonInstance dungeon : DUNGEONS.values()) {
            CompoundTag dungeonTag = new CompoundTag();
            dungeon.save(dungeonTag);
            dungeonsList.add(dungeonTag);
        }

        tag.put("Dungeons", dungeonsList);
        tag.putInt("DungeonCount", DUNGEONS.size());

        LOGGER.info("Saved {} dungeons to world data", DUNGEONS.size());
        return tag;
    }

    /**
     * Load all dungeons from NBT
     */
    public static void load(CompoundTag tag) {
        clearAll();

        if (tag.contains("Dungeons")) {
            ListTag dungeonsList = tag.getList("Dungeons", Tag.TAG_COMPOUND);

            for (int i = 0; i < dungeonsList.size(); i++) {
                CompoundTag dungeonTag = dungeonsList.getCompound(i);
                DungeonInstance dungeon = DungeonInstance.load(dungeonTag);
                DUNGEONS.put(dungeon.getId(), dungeon);
                ENTRANCE_POSITIONS.put(dungeon.getEntrancePos(), dungeon.getId());
            }

            LOGGER.info("Loaded {} dungeons from world data", DUNGEONS.size());
        }
    }

    /**
     * Clear all dungeons (for world reload)
     */
    public static void clearAll() {
        DUNGEONS.clear();
        ENTRANCE_POSITIONS.clear();
        LOGGER.info("Cleared all dungeon data");
    }

    /**
     * Get statistics about dungeon distribution
     */
    public static String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Dungeon Statistics ===\n");
        stats.append("Total dungeons: ").append(DUNGEONS.size()).append("\n");
        stats.append("Max-level dungeons: ").append(getMaxLevelDungeons().size()).append("\n");
        stats.append("Dungeons with uncollected runes: ").append(getDungeonsWithRunes().size()).append("\n");

        // Count by type
        Map<DungeonType, Integer> typeCounts = new HashMap<>();
        for (DungeonInstance dungeon : DUNGEONS.values()) {
            typeCounts.put(dungeon.getType(), typeCounts.getOrDefault(dungeon.getType(), 0) + 1);
        }

        stats.append("\nDungeons by type:\n");
        for (Map.Entry<DungeonType, Integer> entry : typeCounts.entrySet()) {
            stats.append("  ").append(entry.getKey().getDisplayName()).append(": ").append(entry.getValue()).append("\n");
        }

        return stats.toString();
    }
}
