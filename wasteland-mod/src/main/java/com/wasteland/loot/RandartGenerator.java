package com.wasteland.loot;

import net.minecraft.world.item.ItemStack;
import java.util.*;

/**
 * Generates random artifacts (randarts) with balanced properties.
 * Based on DCSS's randart generation algorithm.
 */
public class RandartGenerator {
    private static final Random RANDOM = new Random();

    // Power budgets for different item types
    private static final int WEAPON_BASE_POWER = 30;
    private static final int ARMOR_BASE_POWER = 25;
    private static final int JEWELRY_BASE_POWER = 40;

    // Artifact naming components (DCSS-style)
    private static final String[] PREFIXES = {
        "Flaming", "Freezing", "Electrifying", "Venomous", "Radiant",
        "Shadow", "Ethereal", "Celestial", "Infernal", "Ancient",
        "Cursed", "Blessed", "Enchanted", "Mystic", "Arcane",
        "Divine", "Demonic", "Spectral", "Ghostly", "Vampiric"
    };

    private static final String[] SUFFIXES = {
        "of the Phoenix", "of the Storm", "of the Abyss", "of the Stars",
        "of Destruction", "of Protection", "of Power", "of Wisdom",
        "of the Ancients", "of the Gods", "of Chaos", "of Order",
        "of Fire", "of Ice", "of Lightning", "of Shadows",
        "of Death", "of Life", "of the Void", "of Eternity"
    };

    /**
     * Generate a random artifact weapon
     * @param baseWeaponType The weapon type to make into a randart
     * @param itemStack The Minecraft ItemStack to wrap
     * @return A randomly generated artifact weapon
     */
    public static WastelandWeapon generateWeapon(WeaponType baseWeaponType, ItemStack itemStack) {
        // Create base randart weapon
        int enchantment = 4 + RANDOM.nextInt(6); // +4 to +9
        WastelandWeapon weapon = new WastelandWeapon(itemStack, baseWeaponType, ItemRarity.RANDART, enchantment);

        // Set custom artifact name
        String name = generateName(baseWeaponType.getDisplayName());
        weapon.setCustomName(name);

        // Calculate power budget based on weapon tier
        int basePower = WEAPON_BASE_POWER + (baseWeaponType.getBaseDamage() / 2);
        int powerBudget = basePower + RANDOM.nextInt(20) - 10; // ±10 variance

        // Add random properties
        addRandomProperties(weapon, powerBudget, true);

        // Small chance for curses (makes artifact more powerful but with drawbacks)
        if (RANDOM.nextDouble() < 0.2) {
            addCurse(weapon);
        }

        return weapon;
    }

    /**
     * Generate a random artifact armor piece
     * @param baseArmorType The armor type to make into a randart
     * @param itemStack The Minecraft ItemStack to wrap
     * @return A randomly generated artifact armor
     */
    public static WastelandArmor generateArmor(ArmorType baseArmorType, ItemStack itemStack) {
        // Create base randart armor
        int enchantment = 4 + RANDOM.nextInt(6); // +4 to +9
        WastelandArmor armor = new WastelandArmor(itemStack, baseArmorType, ItemRarity.RANDART, enchantment);

        // Set custom artifact name
        String name = generateName(baseArmorType.getDisplayName());
        armor.setCustomName(name);

        // Calculate power budget based on armor tier
        int basePower = ARMOR_BASE_POWER + (baseArmorType.getBaseAC() * 2);
        int powerBudget = basePower + RANDOM.nextInt(15) - 7; // ±7 variance

        // Add random properties
        addRandomProperties(armor, powerBudget, false);

        // Small chance for curses
        if (RANDOM.nextDouble() < 0.15) {
            addCurse(armor);
        }

        return armor;
    }

    /**
     * Generate a random artifact name
     */
    private static String generateName(String baseName) {
        int style = RANDOM.nextInt(4);

        return switch (style) {
            case 0 -> PREFIXES[RANDOM.nextInt(PREFIXES.length)] + " " + baseName;
            case 1 -> baseName + " " + SUFFIXES[RANDOM.nextInt(SUFFIXES.length)];
            case 2 -> PREFIXES[RANDOM.nextInt(PREFIXES.length)] + " " + baseName + " " +
                     SUFFIXES[RANDOM.nextInt(SUFFIXES.length)];
            default -> "\"" + generateUniqueName() + "\"";
        };
    }

    /**
     * Generate a unique artifact name (DCSS-style)
     */
    private static String generateUniqueName() {
        String[] starts = {"Zong", "Morg", "Sulf", "Korg", "Aeth", "Xom", "Neme", "Dith"};
        String[] middles = {"ur", "al", "or", "as", "is", "ok", "un"};
        String[] ends = {"as", "os", "us", "ath", "oth", "agh", "ius", "ian"};

        return starts[RANDOM.nextInt(starts.length)] +
               middles[RANDOM.nextInt(middles.length)] +
               ends[RANDOM.nextInt(ends.length)];
    }

    /**
     * Add random artifact properties within power budget
     */
    private static void addRandomProperties(WastelandItem item, int powerBudget, boolean isWeapon) {
        List<ArtifactProperty> availableProps = new ArrayList<>();

        // Build list of appropriate properties
        for (ArtifactProperty prop : ArtifactProperty.values()) {
            if (prop.isCurse()) continue; // Curses added separately

            // Weapons favor combat/offensive properties
            if (isWeapon) {
                if (prop == ArtifactProperty.SLAYING ||
                    prop == ArtifactProperty.ACCURACY ||
                    prop == ArtifactProperty.STRENGTH ||
                    prop == ArtifactProperty.DEXTERITY ||
                    prop == ArtifactProperty.ATTACK_SPEED) {
                    availableProps.add(prop);
                }
            }

            // All items can have resistances and utility properties
            if (prop.getPowerLevel() >= 2) {
                availableProps.add(prop);
            }
        }

        // Shuffle for randomness
        Collections.shuffle(availableProps);

        int remainingBudget = powerBudget;
        int propertiesAdded = 0;
        int maxProperties = 4 + RANDOM.nextInt(3); // 4-6 properties

        // Add properties until budget exhausted or max properties reached
        for (ArtifactProperty prop : availableProps) {
            if (propertiesAdded >= maxProperties) break;
            if (remainingBudget <= 0) break;

            int powerCost = prop.getPowerLevel();
            if (powerCost > remainingBudget) continue;

            // Determine property value (1 to max, weighted toward lower values)
            int maxValue = Math.min(prop.getMaxValue(), remainingBudget / powerCost);
            if (maxValue < 1) continue;

            int value;
            if (maxValue == 1) {
                value = 1;
            } else {
                // Weighted toward lower values (more common)
                value = 1 + (int) (Math.pow(RANDOM.nextDouble(), 1.5) * (maxValue - 1));
            }

            item.addArtifactProperty(prop, value);
            remainingBudget -= powerCost * value;
            propertiesAdded++;
        }
    }

    /**
     * Add a random curse to an item (increases power but adds drawback)
     */
    private static void addCurse(WastelandItem item) {
        List<ArtifactProperty> curses = new ArrayList<>();
        for (ArtifactProperty prop : ArtifactProperty.values()) {
            if (prop.isCurse()) {
                curses.add(prop);
            }
        }

        if (!curses.isEmpty()) {
            ArtifactProperty curse = curses.get(RANDOM.nextInt(curses.size()));
            item.addArtifactProperty(curse, 1);
        }
    }

    /**
     * Check if an item should be upgraded to a randart
     * Based on rarity and random chance
     * @param rarity Current item rarity
     * @return true if should upgrade to randart
     */
    public static boolean shouldUpgradeToRandart(ItemRarity rarity) {
        return switch (rarity) {
            case LEGENDARY -> RANDOM.nextDouble() < 0.3; // 30% chance
            case EPIC -> RANDOM.nextDouble() < 0.1;      // 10% chance
            case RARE -> RANDOM.nextDouble() < 0.03;     // 3% chance
            default -> false;
        };
    }

    /**
     * Get weighted random enchantment level for a rarity
     */
    public static int getRandomEnchantment(ItemRarity rarity) {
        int min = rarity.getMinEnchantment();
        int max = rarity.getMaxEnchantment();

        // Weighted toward middle values
        int range = max - min + 1;
        double roll = RANDOM.nextGaussian() * 0.3 + 0.5; // Mean at 0.5, std dev 0.3
        roll = Math.max(0.0, Math.min(1.0, roll)); // Clamp to [0, 1]

        return min + (int) (roll * range);
    }

    /**
     * Get random rarity for loot generation
     * Uses weighted random based on drop weights
     */
    public static ItemRarity getRandomRarity() {
        // Calculate total weight (excluding artifacts)
        int totalWeight = 0;
        for (ItemRarity rarity : ItemRarity.values()) {
            if (!rarity.isArtifact()) {
                totalWeight += rarity.getDropWeight();
            }
        }

        // Roll weighted random
        int roll = RANDOM.nextInt(totalWeight);
        int cumulative = 0;

        for (ItemRarity rarity : ItemRarity.values()) {
            if (rarity.isArtifact()) continue;

            cumulative += rarity.getDropWeight();
            if (roll < cumulative) {
                return rarity;
            }
        }

        return ItemRarity.COMMON; // Fallback
    }

    /**
     * Get random weapon type weighted by tier
     * Lower tier weapons are more common
     */
    public static WeaponType getRandomWeaponType() {
        List<WeaponType> weapons = new ArrayList<>(Arrays.asList(WeaponType.values()));
        weapons.remove(WeaponType.UNARMED); // Don't generate unarmed weapons

        // Weight by inverse of base damage (weaker weapons more common)
        List<WeaponType> weightedList = new ArrayList<>();
        for (WeaponType type : weapons) {
            int weight = Math.max(1, 20 - type.getBaseDamage()); // Higher damage = lower weight
            for (int i = 0; i < weight; i++) {
                weightedList.add(type);
            }
        }

        return weightedList.get(RANDOM.nextInt(weightedList.size()));
    }

    /**
     * Get random armor type weighted by tier
     */
    public static ArmorType getRandomArmorType() {
        List<ArmorType> armors = Arrays.asList(ArmorType.values());

        // Weight by inverse of AC (weaker armor more common)
        List<ArmorType> weightedList = new ArrayList<>();
        for (ArmorType type : armors) {
            int weight = Math.max(1, 15 - type.getBaseAC());
            for (int i = 0; i < weight; i++) {
                weightedList.add(type);
            }
        }

        return weightedList.get(RANDOM.nextInt(weightedList.size()));
    }
}
