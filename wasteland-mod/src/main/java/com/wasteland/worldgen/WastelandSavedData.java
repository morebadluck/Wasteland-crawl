package com.wasteland.worldgen;

import com.wasteland.DungeonProgression;
import com.wasteland.player.RuneInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Saves and loads Wasteland mod data with the world.
 * This includes:
 * - All dungeon instances and their state
 * - Player rune collections
 * - Player dungeon progression
 */
public class WastelandSavedData extends SavedData {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DATA_NAME = "wasteland_data";

    /**
     * Get or create the saved data for a level
     */
    public static WastelandSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
            WastelandSavedData::load,
            WastelandSavedData::new,
            DATA_NAME
        );
    }

    /**
     * Load data from NBT
     */
    public static WastelandSavedData load(CompoundTag tag) {
        WastelandSavedData data = new WastelandSavedData();

        // Load dungeon data
        if (tag.contains("Dungeons")) {
            DungeonManager.load(tag.getCompound("Dungeons"));
        }

        // Load structure data
        if (tag.contains("Structures")) {
            StructureManager.load(tag.getCompound("Structures"));
        }

        // Load player rune data
        if (tag.contains("PlayerRunes")) {
            RuneInventory.load(tag.getCompound("PlayerRunes"));
        }

        // Load player progression data
        if (tag.contains("PlayerProgression")) {
            DungeonProgression.load(tag.getCompound("PlayerProgression"));
        }

        LOGGER.info("Loaded Wasteland saved data");
        return data;
    }

    /**
     * Save data to NBT
     */
    @Override
    public CompoundTag save(CompoundTag tag) {
        // Save dungeon data
        CompoundTag dungeonsTag = new CompoundTag();
        DungeonManager.save(dungeonsTag);
        tag.put("Dungeons", dungeonsTag);

        // Save structure data
        CompoundTag structuresTag = new CompoundTag();
        StructureManager.save(structuresTag);
        tag.put("Structures", structuresTag);

        // Save player rune data
        CompoundTag runesTag = new CompoundTag();
        RuneInventory.save(runesTag);
        tag.put("PlayerRunes", runesTag);

        // Save player progression data
        CompoundTag progressionTag = new CompoundTag();
        DungeonProgression.save(progressionTag);
        tag.put("PlayerProgression", progressionTag);

        LOGGER.info("Saved Wasteland data to world");
        return tag;
    }

    /**
     * Mark data as dirty (needs to be saved)
     */
    public static void markDirty(ServerLevel level) {
        get(level).setDirty();
    }
}
