package com.wasteland.loot;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates loot for dungeon chests based on depth/tier.
 * Implements DCSS-style loot distribution with depth-based quality scaling.
 */
public class LootGenerator {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Loot tier determines quality and quantity of loot
     */
    public enum LootTier {
        EARLY(1, 3),      // Depth 1-5: Mostly common/uncommon
        MID(4, 7),        // Depth 6-12: Uncommon/rare
        LATE(6, 10),      // Depth 13-20: Rare/epic
        END_GAME(8, 12);  // Depth 21+: Epic/legendary/artifacts

        private final int minItems;
        private final int maxItems;

        LootTier(int minItems, int maxItems) {
            this.minItems = minItems;
            this.maxItems = maxItems;
        }

        public int getMinItems() {
            return minItems;
        }

        public int getMaxItems() {
            return maxItems;
        }

        /**
         * Get loot tier based on dungeon depth
         */
        public static LootTier fromDepth(int depth) {
            if (depth <= 5) return EARLY;
            if (depth <= 12) return MID;
            if (depth <= 20) return LATE;
            return END_GAME;
        }
    }

    /**
     * Generate loot for a chest at a specific depth
     * @param depth Dungeon depth (1-30)
     * @return List of ItemStacks to place in chest
     */
    public static List<ItemStack> generateChestLoot(int depth) {
        LootTier tier = LootTier.fromDepth(depth);
        int itemCount = tier.getMinItems() + RANDOM.nextInt(tier.getMaxItems() - tier.getMinItems() + 1);

        List<ItemStack> loot = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            ItemStack item = generateSingleItem(depth, tier);
            if (!item.isEmpty()) {
                loot.add(item);
            }
        }

        LOGGER.debug("Generated {} items for depth {} ({} tier)", loot.size(), depth, tier);
        return loot;
    }

    /**
     * Generate a single loot item
     */
    private static ItemStack generateSingleItem(int depth, LootTier tier) {
        // Determine item type
        int typeRoll = RANDOM.nextInt(100);

        if (typeRoll < 40) {
            // 40% weapon
            return generateWeapon(depth, tier);
        } else if (typeRoll < 75) {
            // 35% armor
            return generateArmor(depth, tier);
        } else if (typeRoll < 90) {
            // 15% consumable (potions/scrolls)
            return generateConsumable(depth, tier);
        } else {
            // 10% gold/gems
            return generateTreasure(depth, tier);
        }
    }

    /**
     * Generate a weapon with appropriate rarity for depth
     */
    private static ItemStack generateWeapon(int depth, LootTier tier) {
        // Get base item (iron sword as placeholder)
        ItemStack itemStack = new ItemStack(Items.IRON_SWORD);

        // Determine rarity based on depth
        ItemRarity rarity = getDepthAppropriateRarity(depth);

        // Get random weapon type
        WeaponType weaponType = RandartGenerator.getRandomWeaponType();

        // Generate enchantment level
        int enchantment = RandartGenerator.getRandomEnchantment(rarity);

        // Check if should upgrade to randart
        if (RandartGenerator.shouldUpgradeToRandart(rarity)) {
            // Generate randart
            WastelandWeapon weapon = RandartGenerator.generateWeapon(weaponType, itemStack);
            LOGGER.info("Generated randart weapon: {}", weapon.getCustomName());
            return weapon.getItemStack();
        } else {
            // Generate normal weapon
            WastelandWeapon weapon = new WastelandWeapon(itemStack, weaponType, rarity, enchantment);

            // Add artifact properties for rare+ items
            if (rarity.getArtifactPropertyCount() > 0) {
                addRandomPropertiesForRarity(weapon, rarity);
            }

            return weapon.getItemStack();
        }
    }

    /**
     * Generate armor with appropriate rarity for depth
     */
    private static ItemStack generateArmor(int depth, LootTier tier) {
        // Get base item (iron chestplate as placeholder)
        ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);

        // Determine rarity based on depth
        ItemRarity rarity = getDepthAppropriateRarity(depth);

        // Get random armor type
        ArmorType armorType = RandartGenerator.getRandomArmorType();

        // Generate enchantment level
        int enchantment = RandartGenerator.getRandomEnchantment(rarity);

        // Check if should upgrade to randart
        if (RandartGenerator.shouldUpgradeToRandart(rarity)) {
            // Generate randart
            WastelandArmor armor = RandartGenerator.generateArmor(armorType, itemStack);
            LOGGER.info("Generated randart armor: {}", armor.getCustomName());
            return armor.getItemStack();
        } else {
            // Generate normal armor
            WastelandArmor armor = new WastelandArmor(itemStack, armorType, rarity, enchantment);

            // Add artifact properties for rare+ items
            if (rarity.getArtifactPropertyCount() > 0) {
                addRandomPropertiesForRarity(armor, rarity);
            }

            return armor.getItemStack();
        }
    }

    /**
     * Add random properties to an item based on its rarity
     */
    private static void addRandomPropertiesForRarity(WastelandItem item, ItemRarity rarity) {
        int propertyCount = rarity.getArtifactPropertyCount();
        if (propertyCount == 0) return;

        // Get all non-curse properties
        List<ArtifactProperty> availableProps = new ArrayList<>();
        for (ArtifactProperty prop : ArtifactProperty.values()) {
            if (!prop.isCurse()) {
                availableProps.add(prop);
            }
        }

        // Add random properties
        for (int i = 0; i < propertyCount && !availableProps.isEmpty(); i++) {
            ArtifactProperty prop = availableProps.remove(RANDOM.nextInt(availableProps.size()));
            int value = 1 + RANDOM.nextInt(Math.min(3, prop.getMaxValue())); // 1-3 value
            item.addArtifactProperty(prop, value);
        }
    }

    /**
     * Generate consumable items (potions, scrolls, food)
     */
    private static ItemStack generateConsumable(int depth, LootTier tier) {
        int typeRoll = RANDOM.nextInt(100);

        if (typeRoll < 50) {
            // Potion
            return new ItemStack(Items.POTION, 1 + RANDOM.nextInt(3));
        } else if (typeRoll < 90) {
            // Food
            return new ItemStack(Items.BREAD, 2 + RANDOM.nextInt(4));
        } else {
            // Book/scroll (rare)
            return new ItemStack(Items.BOOK, 1);
        }
    }

    /**
     * Generate treasure (gold nuggets, gems)
     */
    private static ItemStack generateTreasure(int depth, LootTier tier) {
        int baseAmount = switch (tier) {
            case EARLY -> 3 + RANDOM.nextInt(8);
            case MID -> 8 + RANDOM.nextInt(16);
            case LATE -> 16 + RANDOM.nextInt(32);
            case END_GAME -> 32 + RANDOM.nextInt(64);
        };

        int typeRoll = RANDOM.nextInt(100);
        if (typeRoll < 70) {
            // Gold nuggets
            return new ItemStack(Items.GOLD_NUGGET, baseAmount);
        } else if (typeRoll < 90) {
            // Gold ingots (more valuable)
            return new ItemStack(Items.GOLD_INGOT, baseAmount / 4);
        } else {
            // Diamonds (rare)
            return new ItemStack(Items.DIAMOND, 1 + RANDOM.nextInt(3));
        }
    }

    /**
     * Get depth-appropriate rarity with weighted distribution
     * Deeper dungeons have higher chance of better loot
     */
    private static ItemRarity getDepthAppropriateRarity(int depth) {
        // Base rarity distribution
        ItemRarity baseRarity = RandartGenerator.getRandomRarity();

        // Depth bonus: higher depths have chance to upgrade rarity
        int depthBonus = depth / 5; // Every 5 levels increases upgrade chance

        for (int i = 0; i < depthBonus; i++) {
            if (RANDOM.nextDouble() < 0.3) { // 30% chance per bonus level
                baseRarity = upgradeRarity(baseRarity);
            }
        }

        return baseRarity;
    }

    /**
     * Upgrade rarity to next tier
     */
    private static ItemRarity upgradeRarity(ItemRarity current) {
        return switch (current) {
            case COMMON -> ItemRarity.UNCOMMON;
            case UNCOMMON -> ItemRarity.RARE;
            case RARE -> ItemRarity.EPIC;
            case EPIC -> ItemRarity.LEGENDARY;
            case LEGENDARY -> ItemRarity.LEGENDARY; // Max non-artifact
            default -> current;
        };
    }

    /**
     * Generate loot for enemy drops
     * Less generous than chests
     */
    public static List<ItemStack> generateEnemyLoot(int enemyLevel) {
        List<ItemStack> loot = new ArrayList<>();

        // 30% chance to drop nothing
        if (RANDOM.nextDouble() < 0.3) {
            return loot;
        }

        // 50% chance for treasure
        if (RANDOM.nextDouble() < 0.5) {
            LootTier tier = LootTier.fromDepth(enemyLevel);
            loot.add(generateTreasure(enemyLevel, tier));
        }

        // 30% chance for equipment
        if (RANDOM.nextDouble() < 0.3) {
            LootTier tier = LootTier.fromDepth(enemyLevel);
            if (RANDOM.nextBoolean()) {
                loot.add(generateWeapon(enemyLevel, tier));
            } else {
                loot.add(generateArmor(enemyLevel, tier));
            }
        }

        // 20% chance for consumable
        if (RANDOM.nextDouble() < 0.2) {
            LootTier tier = LootTier.fromDepth(enemyLevel);
            loot.add(generateConsumable(enemyLevel, tier));
        }

        return loot;
    }

    /**
     * Generate starting equipment for a new character
     */
    public static List<ItemStack> generateStartingEquipment(com.wasteland.character.Race race) {
        List<ItemStack> equipment = new ArrayList<>();

        // Starting weapon based on race
        WeaponType startWeapon = getStartingWeapon(race);
        ItemStack weaponStack = new ItemStack(Items.IRON_SWORD);
        WastelandWeapon weapon = new WastelandWeapon(weaponStack, startWeapon, ItemRarity.COMMON, 0);
        equipment.add(weapon.getItemStack());

        // Starting armor (leather armor)
        ItemStack armorStack = new ItemStack(Items.LEATHER_CHESTPLATE);
        WastelandArmor armor = new WastelandArmor(armorStack, ArmorType.LEATHER_ARMOR, ItemRarity.COMMON, 0);
        equipment.add(armor.getItemStack());

        // Some starting food
        equipment.add(new ItemStack(Items.BREAD, 5));

        LOGGER.info("Generated starting equipment for {}: {} and {}",
                   race.getDisplayName(), startWeapon.getDisplayName(), ArmorType.LEATHER_ARMOR.getDisplayName());

        return equipment;
    }

    /**
     * Get starting weapon based on race
     */
    private static WeaponType getStartingWeapon(com.wasteland.character.Race race) {
        // Strong races prefer maces
        if (race == com.wasteland.character.Race.MINOTAUR ||
            race == com.wasteland.character.Race.TROLL ||
            race == com.wasteland.character.Race.OGRE) {
            return WeaponType.MACE;
        }

        // Elves prefer finesse weapons
        if (race == com.wasteland.character.Race.DEEP_ELF) {
            return WeaponType.RAPIER;
        }

        // Dwarves prefer axes
        if (race == com.wasteland.character.Race.DEEP_DWARF) {
            return WeaponType.HAND_AXE;
        }

        // Small races use daggers
        if (race == com.wasteland.character.Race.SPRIGGAN ||
            race == com.wasteland.character.Race.KOBOLD) {
            return WeaponType.DAGGER;
        }

        // Reptilian races use polearms
        if (race == com.wasteland.character.Race.NAGA ||
            race == com.wasteland.character.Race.DRACONIAN) {
            return WeaponType.SPEAR;
        }

        // Centaurs get a spear (good ranged backup)
        if (race == com.wasteland.character.Race.CENTAUR) {
            return WeaponType.SPEAR;
        }

        // Default starting weapon
        return WeaponType.SHORT_SWORD;
    }
}
