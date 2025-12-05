package com.wasteland.worldgen;

import com.wasteland.structures.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Manages natural generation of surface structures across the wasteland.
 * Creates neighborhoods of housing, commercial buildings, and rare landmarks.
 */
public class StructureManager {
    private static final Logger LOGGER = LogManager.getLogger();

    // Structure tracking
    private static final List<StructureInstance> ALL_STRUCTURES = new ArrayList<>();
    private static final List<BlockPos> NEIGHBORHOOD_CENTERS = new ArrayList<>();

    // Configuration
    private static final int NEIGHBORHOOD_SPACING = 600;  // Min blocks between neighborhoods
    private static final int NEIGHBORHOOD_SIZE = 250;      // Radius of a neighborhood
    private static final int HOUSES_PER_NEIGHBORHOOD = 8;  // Average houses per neighborhood
    private static final int MALL_SPACING = 2000;          // Min blocks between malls

    /**
     * Generate all surface structures for the world
     */
    public static void generateStructures(ServerLevel level, long worldSeed) {
        LOGGER.info("=== Generating surface structures for world seed {} ===", worldSeed);
        Random random = new Random(worldSeed);

        // First, generate neighborhoods across suitable biomes
        generateNeighborhoods(level, worldSeed, random);

        // Then, place commercial structures near neighborhoods
        generateCommercialStructures(level, random);

        // Finally, place rare landmark structures
        generateLandmarks(level, random);

        LOGGER.info("Generated {} total structures", ALL_STRUCTURES.size());
        LOGGER.info("  - {} neighborhoods", NEIGHBORHOOD_CENTERS.size());
        LOGGER.info("  - {} houses", countByType(StructureType.HOUSE));
        LOGGER.info("  - {} apartments", countByType(StructureType.APARTMENT));
        LOGGER.info("  - {} trailers", countByType(StructureType.TRAILER));
        LOGGER.info("  - {} grocery stores", countByType(StructureType.GROCERY));
        LOGGER.info("  - {} malls", countByType(StructureType.MALL));
    }

    /**
     * Generate neighborhoods with housing clusters
     */
    private static void generateNeighborhoods(ServerLevel level, long worldSeed, Random random) {
        // Generate neighborhoods across the map
        // Use a grid-based approach to distribute them evenly
        int gridSize = 1000;  // Check every 1000 blocks
        int searchRadius = 5000;  // Search within 5000 blocks of spawn

        for (int gridX = -searchRadius; gridX <= searchRadius; gridX += gridSize) {
            for (int gridZ = -searchRadius; gridZ <= searchRadius; gridZ += gridSize) {
                // Random chance for neighborhood in this grid cell (10%)
                if (random.nextFloat() > 0.1f) continue;

                // Random offset within grid cell
                int x = gridX + random.nextInt(gridSize) - gridSize / 2;
                int z = gridZ + random.nextInt(gridSize) - gridSize / 2;

                BlockPos centerPos = new BlockPos(x, 64, z);
                BlockPos groundPos = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, centerPos);

                // Check if valid for neighborhood
                if (!isValidNeighborhoodLocation(level, groundPos)) {
                    continue;
                }

                // Check spacing from existing neighborhoods
                if (!hasNeighborhoodSpacing(groundPos)) {
                    continue;
                }

                // Create neighborhood
                generateNeighborhood(level, groundPos, random);
                NEIGHBORHOOD_CENTERS.add(groundPos);
            }
        }
    }

    /**
     * Generate a single neighborhood with housing
     */
    private static void generateNeighborhood(ServerLevel level, BlockPos center, Random random) {
        int numHouses = HOUSES_PER_NEIGHBORHOOD + random.nextInt(5) - 2;  // 6-10 houses

        for (int i = 0; i < numHouses; i++) {
            // Random position within neighborhood radius
            int offsetX = random.nextInt(NEIGHBORHOOD_SIZE * 2) - NEIGHBORHOOD_SIZE;
            int offsetZ = random.nextInt(NEIGHBORHOOD_SIZE * 2) - NEIGHBORHOOD_SIZE;

            BlockPos housePos = center.offset(offsetX, 0, offsetZ);
            housePos = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, housePos);

            // Check spacing between houses (at least 40 blocks apart)
            if (!hasMinimumSpacing(housePos, 40)) {
                continue;
            }

            // Choose housing type based on probabilities
            StructureType type;
            float roll = random.nextFloat();
            if (roll < 0.5f) {
                type = StructureType.HOUSE;  // 50% suburban houses
            } else if (roll < 0.75f) {
                type = StructureType.APARTMENT;  // 25% apartments
            } else {
                type = StructureType.TRAILER;  // 25% trailers
            }

            // Place structure
            placeStructure(level, type, housePos, random);
        }
    }

    /**
     * Generate commercial structures near neighborhoods
     */
    private static void generateCommercialStructures(ServerLevel level, Random random) {
        // Place grocery stores near neighborhoods
        for (BlockPos neighborhoodCenter : NEIGHBORHOOD_CENTERS) {
            // 70% chance for grocery store per neighborhood
            if (random.nextFloat() > 0.7f) continue;

            // Place at edge of neighborhood
            int angle = random.nextInt(360);
            double radians = Math.toRadians(angle);
            int offsetX = (int) (NEIGHBORHOOD_SIZE * 1.5 * Math.cos(radians));
            int offsetZ = (int) (NEIGHBORHOOD_SIZE * 1.5 * Math.sin(radians));

            BlockPos storePos = neighborhoodCenter.offset(offsetX, 0, offsetZ);
            storePos = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, storePos);

            // Check spacing
            if (!hasMinimumSpacing(storePos, 100)) {
                continue;
            }

            placeStructure(level, StructureType.GROCERY, storePos, random);
        }
    }

    /**
     * Generate rare landmark structures (malls)
     */
    private static void generateLandmarks(ServerLevel level, Random random) {
        // Malls are rare and require large flat areas
        int searchRadius = 5000;
        int gridSize = 2000;

        for (int gridX = -searchRadius; gridX <= searchRadius; gridX += gridSize) {
            for (int gridZ = -searchRadius; gridZ <= searchRadius; gridZ += gridSize) {
                // Very low chance (2%)
                if (random.nextFloat() > 0.02f) continue;

                int x = gridX + random.nextInt(gridSize) - gridSize / 2;
                int z = gridZ + random.nextInt(gridSize) - gridSize / 2;

                BlockPos mallPos = new BlockPos(x, 64, z);
                mallPos = level.getHeightmapPos(
                    net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, mallPos);

                // Check if area is flat enough for mall (60x40 footprint)
                if (!isFlatArea(level, mallPos, 60, 40)) {
                    continue;
                }

                // Check spacing from other malls
                if (!hasStructureSpacing(mallPos, StructureType.MALL, MALL_SPACING)) {
                    continue;
                }

                placeStructure(level, StructureType.MALL, mallPos, random);
            }
        }
    }

    /**
     * Place a structure at the given position
     */
    private static void placeStructure(ServerLevel level, StructureType type, BlockPos pos, Random random) {
        try {
            switch (type) {
                case HOUSE:
                    HousingStructure.generate(level, pos, random);
                    break;
                case APARTMENT:
                    ApartmentStructure.generate(level, pos, random);
                    break;
                case TRAILER:
                    TrailerStructure.generate(level, pos, random);
                    break;
                case GROCERY:
                    GroceryStoreStructure.generate(level, pos, random);
                    break;
                case MALL:
                    MallStructure.generate(level, pos, random);
                    break;
            }

            // Track structure
            ALL_STRUCTURES.add(new StructureInstance(type, pos));
            LOGGER.debug("Placed {} at {}", type.name(), pos);
        } catch (Exception e) {
            LOGGER.error("Failed to place {} at {}: {}", type.name(), pos, e.getMessage());
        }
    }

    /**
     * Check if location is valid for a neighborhood
     */
    private static boolean isValidNeighborhoodLocation(ServerLevel level, BlockPos pos) {
        // Check biome - prefer plains, forests, meadows, taiga
        Biome biome = level.getBiome(pos).value();

        // Simple biome check based on temperature/downfall
        Biome.ClimateSettings climate = biome.getModifiedClimateSettings();
        float temp = climate.temperature();

        // Temperate areas (not too hot or cold)
        if (temp < -0.5f || temp > 2.0f) {
            return false;  // Too cold or too hot
        }

        // Check if relatively flat (for housing)
        return isFlatArea(level, pos, 30, 30);
    }

    /**
     * Check if area is flat enough for construction
     */
    private static boolean isFlatArea(ServerLevel level, BlockPos center, int width, int depth) {
        int centerY = center.getY();
        int maxVariation = 5;  // Allow up to 5 blocks of height difference

        // Sample corners and center
        BlockPos[] checkPoints = {
            center,
            center.offset(width / 2, 0, 0),
            center.offset(-width / 2, 0, 0),
            center.offset(0, 0, depth / 2),
            center.offset(0, 0, -depth / 2),
            center.offset(width / 2, 0, depth / 2),
            center.offset(-width / 2, 0, -depth / 2)
        };

        for (BlockPos checkPos : checkPoints) {
            BlockPos groundPos = level.getHeightmapPos(
                net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, checkPos);

            if (Math.abs(groundPos.getY() - centerY) > maxVariation) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check spacing from existing neighborhoods
     */
    private static boolean hasNeighborhoodSpacing(BlockPos pos) {
        for (BlockPos existing : NEIGHBORHOOD_CENTERS) {
            double distance = Math.sqrt(existing.distSqr(pos));
            if (distance < NEIGHBORHOOD_SPACING) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check minimum spacing from all structures
     */
    private static boolean hasMinimumSpacing(BlockPos pos, int minDistance) {
        for (StructureInstance structure : ALL_STRUCTURES) {
            double distance = Math.sqrt(structure.pos.distSqr(pos));
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check spacing from structures of a specific type
     */
    private static boolean hasStructureSpacing(BlockPos pos, StructureType type, int minDistance) {
        for (StructureInstance structure : ALL_STRUCTURES) {
            if (structure.type == type) {
                double distance = Math.sqrt(structure.pos.distSqr(pos));
                if (distance < minDistance) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Count structures by type
     */
    private static int countByType(StructureType type) {
        int count = 0;
        for (StructureInstance structure : ALL_STRUCTURES) {
            if (structure.type == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * Save all structures to NBT
     */
    public static CompoundTag save(CompoundTag tag) {
        ListTag structuresList = new ListTag();

        for (StructureInstance structure : ALL_STRUCTURES) {
            CompoundTag structureTag = new CompoundTag();
            structure.save(structureTag);
            structuresList.add(structureTag);
        }

        tag.put("Structures", structuresList);
        tag.putInt("StructureCount", ALL_STRUCTURES.size());

        // Save neighborhood centers
        ListTag neighborhoodsList = new ListTag();
        for (BlockPos center : NEIGHBORHOOD_CENTERS) {
            CompoundTag centerTag = new CompoundTag();
            centerTag.putInt("X", center.getX());
            centerTag.putInt("Y", center.getY());
            centerTag.putInt("Z", center.getZ());
            neighborhoodsList.add(centerTag);
        }
        tag.put("Neighborhoods", neighborhoodsList);

        LOGGER.info("Saved {} structures to world data", ALL_STRUCTURES.size());
        return tag;
    }

    /**
     * Load all structures from NBT
     */
    public static void load(CompoundTag tag) {
        clearAll();

        if (tag.contains("Structures")) {
            ListTag structuresList = tag.getList("Structures", Tag.TAG_COMPOUND);

            for (int i = 0; i < structuresList.size(); i++) {
                CompoundTag structureTag = structuresList.getCompound(i);
                StructureInstance structure = StructureInstance.load(structureTag);
                ALL_STRUCTURES.add(structure);
            }

            LOGGER.info("Loaded {} structures from world data", ALL_STRUCTURES.size());
        }

        if (tag.contains("Neighborhoods")) {
            ListTag neighborhoodsList = tag.getList("Neighborhoods", Tag.TAG_COMPOUND);

            for (int i = 0; i < neighborhoodsList.size(); i++) {
                CompoundTag centerTag = neighborhoodsList.getCompound(i);
                BlockPos center = new BlockPos(
                    centerTag.getInt("X"),
                    centerTag.getInt("Y"),
                    centerTag.getInt("Z")
                );
                NEIGHBORHOOD_CENTERS.add(center);
            }

            LOGGER.info("Loaded {} neighborhoods from world data", NEIGHBORHOOD_CENTERS.size());
        }
    }

    /**
     * Clear all structure data
     */
    public static void clearAll() {
        ALL_STRUCTURES.clear();
        NEIGHBORHOOD_CENTERS.clear();
        LOGGER.info("Cleared all structure data");
    }

    /**
     * Get all structures
     */
    public static List<StructureInstance> getAllStructures() {
        return new ArrayList<>(ALL_STRUCTURES);
    }

    /**
     * Structure type enum
     */
    public enum StructureType {
        HOUSE,
        APARTMENT,
        TRAILER,
        GROCERY,
        MALL
    }

    /**
     * Structure instance data
     */
    public static class StructureInstance {
        public final StructureType type;
        public final BlockPos pos;

        public StructureInstance(StructureType type, BlockPos pos) {
            this.type = type;
            this.pos = pos;
        }

        public void save(CompoundTag tag) {
            tag.putString("Type", type.name());
            tag.putInt("X", pos.getX());
            tag.putInt("Y", pos.getY());
            tag.putInt("Z", pos.getZ());
        }

        public static StructureInstance load(CompoundTag tag) {
            StructureType type = StructureType.valueOf(tag.getString("Type"));
            BlockPos pos = new BlockPos(
                tag.getInt("X"),
                tag.getInt("Y"),
                tag.getInt("Z")
            );
            return new StructureInstance(type, pos);
        }
    }
}
