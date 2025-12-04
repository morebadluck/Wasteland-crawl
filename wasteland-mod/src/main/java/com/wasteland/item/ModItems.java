package com.wasteland.item;

import com.wasteland.WastelandMod;
import com.wasteland.worldgen.RuneType;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for all Wasteland mod items
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, WastelandMod.MOD_ID);

    // === RUNES ===
    // Quest items from max-level dungeons - need 3+ to access Zot

    public static final RegistryObject<Item> SLIMY_RUNE = ITEMS.register("slimy_rune",
        () -> new RuneItem(RuneType.SLIMY));

    public static final RegistryObject<Item> SERPENTINE_RUNE = ITEMS.register("serpentine_rune",
        () -> new RuneItem(RuneType.SERPENTINE));

    public static final RegistryObject<Item> BARNACLED_RUNE = ITEMS.register("barnacled_rune",
        () -> new RuneItem(RuneType.BARNACLED));

    public static final RegistryObject<Item> GOSSAMER_RUNE = ITEMS.register("gossamer_rune",
        () -> new RuneItem(RuneType.GOSSAMER));

    public static final RegistryObject<Item> IRON_RUNE = ITEMS.register("iron_rune",
        () -> new RuneItem(RuneType.IRON));

    public static final RegistryObject<Item> SILVER_RUNE = ITEMS.register("silver_rune",
        () -> new RuneItem(RuneType.SILVER));

    public static final RegistryObject<Item> GOLDEN_RUNE = ITEMS.register("golden_rune",
        () -> new RuneItem(RuneType.GOLDEN));

    public static final RegistryObject<Item> BONE_RUNE = ITEMS.register("bone_rune",
        () -> new RuneItem(RuneType.BONE));

    public static final RegistryObject<Item> DARK_RUNE = ITEMS.register("dark_rune",
        () -> new RuneItem(RuneType.DARK));

    public static final RegistryObject<Item> ABYSSAL_RUNE = ITEMS.register("abyssal_rune",
        () -> new RuneItem(RuneType.ABYSSAL));

    public static final RegistryObject<Item> GLOWING_RUNE = ITEMS.register("glowing_rune",
        () -> new RuneItem(RuneType.GLOWING));

    public static final RegistryObject<Item> RUSTED_RUNE = ITEMS.register("rusted_rune",
        () -> new RuneItem(RuneType.RUSTED));

    /**
     * Get the rune item for a specific rune type
     */
    public static Item getRuneItem(RuneType runeType) {
        return switch (runeType) {
            case SLIMY -> SLIMY_RUNE.get();
            case SERPENTINE -> SERPENTINE_RUNE.get();
            case BARNACLED -> BARNACLED_RUNE.get();
            case GOSSAMER -> GOSSAMER_RUNE.get();
            case IRON -> IRON_RUNE.get();
            case SILVER -> SILVER_RUNE.get();
            case GOLDEN -> GOLDEN_RUNE.get();
            case BONE -> BONE_RUNE.get();
            case DARK -> DARK_RUNE.get();
            case ABYSSAL -> ABYSSAL_RUNE.get();
            case GLOWING -> GLOWING_RUNE.get();
            case RUSTED -> RUSTED_RUNE.get();
        };
    }

    /**
     * Register items to the event bus
     */
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
