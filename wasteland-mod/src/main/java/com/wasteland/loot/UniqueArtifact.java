package com.wasteland.loot;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a unique artifact definition (fixed artifact).
 * Unlike randarts which are procedurally generated, unique artifacts have
 * fixed names, properties, and lore.
 */
public class UniqueArtifact {
    private final String id;
    private final String displayName;
    private final String loreText;
    private final ArtifactType type;
    private final int enchantmentLevel;
    private final Map<ArtifactProperty, Integer> properties;

    // For weapons
    private final WeaponType weaponType;

    // For armor
    private final ArmorType armorType;

    // Generation settings
    private final int minDepth; // Minimum dungeon depth to spawn
    private final double spawnWeight; // Relative spawn chance

    /**
     * Builder for unique artifacts
     */
    public static class Builder {
        private String id;
        private String displayName;
        private String loreText = "";
        private ArtifactType type;
        private int enchantmentLevel = 0;
        private final Map<ArtifactProperty, Integer> properties = new HashMap<>();
        private WeaponType weaponType;
        private ArmorType armorType;
        private int minDepth = 1;
        private double spawnWeight = 1.0;

        public Builder(String id, String displayName, ArtifactType type) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
        }

        public Builder lore(String lore) {
            this.loreText = lore;
            return this;
        }

        public Builder enchantment(int level) {
            this.enchantmentLevel = level;
            return this;
        }

        public Builder property(ArtifactProperty prop, int value) {
            this.properties.put(prop, value);
            return this;
        }

        public Builder weaponType(WeaponType type) {
            this.weaponType = type;
            return this;
        }

        public Builder armorType(ArmorType type) {
            this.armorType = type;
            return this;
        }

        public Builder minDepth(int depth) {
            this.minDepth = depth;
            return this;
        }

        public Builder spawnWeight(double weight) {
            this.spawnWeight = weight;
            return this;
        }

        public UniqueArtifact build() {
            if (type == ArtifactType.WEAPON && weaponType == null) {
                throw new IllegalStateException("Weapon artifact must have weaponType");
            }
            if (type == ArtifactType.ARMOR && armorType == null) {
                throw new IllegalStateException("Armor artifact must have armorType");
            }
            return new UniqueArtifact(this);
        }
    }

    private UniqueArtifact(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.loreText = builder.loreText;
        this.type = builder.type;
        this.enchantmentLevel = builder.enchantmentLevel;
        this.properties = new HashMap<>(builder.properties);
        this.weaponType = builder.weaponType;
        this.armorType = builder.armorType;
        this.minDepth = builder.minDepth;
        this.spawnWeight = builder.spawnWeight;
    }

    /**
     * Generate an instance of this unique artifact
     */
    public ItemStack generate(ItemStack baseStack) {
        WastelandItem item;

        if (type == ArtifactType.WEAPON) {
            WastelandWeapon weapon = new WastelandWeapon(baseStack, weaponType, ItemRarity.ARTIFACT, enchantmentLevel);
            weapon.setCustomName(displayName);

            // Add all properties
            for (Map.Entry<ArtifactProperty, Integer> entry : properties.entrySet()) {
                weapon.addArtifactProperty(entry.getKey(), entry.getValue());
            }

            item = weapon;
        } else if (type == ArtifactType.ARMOR) {
            WastelandArmor armor = new WastelandArmor(baseStack, armorType, ItemRarity.ARTIFACT, enchantmentLevel);
            armor.setCustomName(displayName);

            // Add all properties
            for (Map.Entry<ArtifactProperty, Integer> entry : properties.entrySet()) {
                armor.addArtifactProperty(entry.getKey(), entry.getValue());
            }

            item = armor;
        } else {
            throw new UnsupportedOperationException("Jewelry artifacts not yet implemented");
        }

        return item.getItemStack();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLoreText() {
        return loreText;
    }

    public ArtifactType getType() {
        return type;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

    public Map<ArtifactProperty, Integer> getProperties() {
        return new HashMap<>(properties);
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public ArmorType getArmorType() {
        return armorType;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public double getSpawnWeight() {
        return spawnWeight;
    }

    /**
     * Artifact type enum
     */
    public enum ArtifactType {
        WEAPON,
        ARMOR,
        JEWELRY
    }
}
