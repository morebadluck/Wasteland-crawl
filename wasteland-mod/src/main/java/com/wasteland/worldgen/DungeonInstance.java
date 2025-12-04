package com.wasteland.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Represents a single dungeon instance in the world.
 * Each dungeon has:
 * - A type (Swamp, Lair, etc.)
 * - A random number of levels (1-15)
 * - Whether it has max levels (only small % do)
 * - Which rune it drops (if it's a max-level dungeon)
 * - Entrance location
 */
public class DungeonInstance {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    // Max level chances - only small % of dungeons get max levels
    private static final double MAX_LEVEL_CHANCE_COMMON = 0.10;    // 10% for common dungeons
    private static final double MAX_LEVEL_CHANCE_UNCOMMON = 0.15;  // 15% for uncommon
    private static final double MAX_LEVEL_CHANCE_RARE = 0.25;      // 25% for rare
    private static final double MAX_LEVEL_CHANCE_VERY_RARE = 0.35; // 35% for very rare

    // Level depth ranges
    private static final int MIN_LEVELS = 1;
    private static final int NORMAL_MAX_LEVELS = 10;  // Most dungeons: 1-10 levels
    private static final int MAX_LEVELS = 15;         // Max-level dungeons: up to 15 levels

    private UUID id;
    private DungeonType type;
    private BlockPos entrancePos;
    private int numLevels;
    private boolean hasMaxLevels;
    private RuneType rune;  // null if not a max-level dungeon
    private boolean runeCollected;

    /**
     * Create a new dungeon instance
     */
    public DungeonInstance(DungeonType type, BlockPos entrancePos) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.entrancePos = entrancePos;
        this.hasMaxLevels = determineIfMaxLevel();
        this.numLevels = generateNumLevels();
        this.rune = hasMaxLevels ? assignRune() : null;
        this.runeCollected = false;

        LOGGER.info("Created {} dungeon at {}: {} levels{}",
            type.getDisplayName(), entrancePos, numLevels,
            hasMaxLevels ? " (MAX LEVEL - drops " + rune + " rune)" : "");
    }

    /**
     * Determine if this dungeon should have max levels based on rarity
     */
    private boolean determineIfMaxLevel() {
        int weight = type.getRarityWeight();
        double chance;

        if (weight >= 80) {
            chance = MAX_LEVEL_CHANCE_COMMON;
        } else if (weight >= 50) {
            chance = MAX_LEVEL_CHANCE_UNCOMMON;
        } else if (weight >= 20) {
            chance = MAX_LEVEL_CHANCE_RARE;
        } else {
            chance = MAX_LEVEL_CHANCE_VERY_RARE;
        }

        return RANDOM.nextDouble() < chance;
    }

    /**
     * Generate random number of levels
     */
    private int generateNumLevels() {
        if (hasMaxLevels) {
            // Max-level dungeons: 10-15 levels
            return 10 + RANDOM.nextInt(6);
        } else {
            // Normal dungeons: 1-10 levels (weighted toward middle)
            // Use triangular distribution for more realistic variety
            int a = RANDOM.nextInt(NORMAL_MAX_LEVELS) + 1;
            int b = RANDOM.nextInt(NORMAL_MAX_LEVELS) + 1;
            return Math.min(a, b) + 1;  // Favors shorter dungeons slightly
        }
    }

    /**
     * Assign a random rune type to this dungeon
     * Makes sure runes are distributed across different dungeon types
     */
    private RuneType assignRune() {
        // Prefer runes that match the dungeon type thematically
        List<RuneType> suitableRunes = getSuitableRunes();

        if (!suitableRunes.isEmpty() && RANDOM.nextDouble() < 0.7) {
            // 70% chance to use a thematically appropriate rune
            return suitableRunes.get(RANDOM.nextInt(suitableRunes.size()));
        } else {
            // 30% chance for any random rune
            RuneType[] allRunes = RuneType.values();
            return allRunes[RANDOM.nextInt(allRunes.length)];
        }
    }

    /**
     * Get runes that are thematically appropriate for this dungeon type
     */
    private List<RuneType> getSuitableRunes() {
        List<RuneType> suitable = new ArrayList<>();

        switch (type) {
            case SWAMP -> suitable.add(RuneType.SLIMY);
            case SHOALS -> suitable.add(RuneType.BARNACLED);
            case SNAKE_PIT -> suitable.add(RuneType.SERPENTINE);
            case SPIDER -> suitable.add(RuneType.GOSSAMER);
            case ORC_MINES -> suitable.add(RuneType.IRON);
            case ELF_HALLS -> suitable.add(RuneType.SILVER);
            case VAULTS -> suitable.add(RuneType.GOLDEN);
            case CRYPT -> suitable.add(RuneType.BONE);
            case TOMB -> suitable.add(RuneType.DARK);
            case DEPTHS -> suitable.add(RuneType.ABYSSAL);
            case LAIR -> {
                suitable.add(RuneType.SLIMY);
                suitable.add(RuneType.SERPENTINE);
                suitable.add(RuneType.GOSSAMER);
            }
        }

        return suitable;
    }

    // Getters
    public UUID getId() { return id; }
    public DungeonType getType() { return type; }
    public BlockPos getEntrancePos() { return entrancePos; }
    public int getNumLevels() { return numLevels; }
    public boolean hasMaxLevels() { return hasMaxLevels; }
    public RuneType getRune() { return rune; }
    public boolean isRuneCollected() { return runeCollected; }

    /**
     * Mark the rune as collected
     */
    public void collectRune() {
        if (hasMaxLevels && !runeCollected) {
            runeCollected = true;
            LOGGER.info("Rune collected from {}: {}", type.getDisplayName(), rune);
        }
    }

    /**
     * Save to NBT
     */
    public CompoundTag save(CompoundTag tag) {
        tag.putUUID("id", id);
        tag.putString("type", type.name());
        tag.putInt("entranceX", entrancePos.getX());
        tag.putInt("entranceY", entrancePos.getY());
        tag.putInt("entranceZ", entrancePos.getZ());
        tag.putInt("numLevels", numLevels);
        tag.putBoolean("hasMaxLevels", hasMaxLevels);
        if (rune != null) {
            tag.putString("rune", rune.name());
        }
        tag.putBoolean("runeCollected", runeCollected);
        return tag;
    }

    /**
     * Load from NBT
     */
    public static DungeonInstance load(CompoundTag tag) {
        DungeonType type = DungeonType.valueOf(tag.getString("type"));
        BlockPos entrancePos = new BlockPos(
            tag.getInt("entranceX"),
            tag.getInt("entranceY"),
            tag.getInt("entranceZ")
        );

        DungeonInstance instance = new DungeonInstance(type, entrancePos);

        // Override generated values with saved values
        instance.id = tag.getUUID("id");
        instance.numLevels = tag.getInt("numLevels");
        instance.hasMaxLevels = tag.getBoolean("hasMaxLevels");
        instance.rune = tag.contains("rune") ? RuneType.valueOf(tag.getString("rune")) : null;
        instance.runeCollected = tag.getBoolean("runeCollected");

        return instance;
    }
}
