package com.wasteland.loot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;

/**
 * Base class for all Wasteland mod items.
 * Wraps a Minecraft ItemStack and stores custom properties in NBT.
 */
public abstract class WastelandItem {
    // NBT keys
    private static final String KEY_RARITY = "WastelandRarity";
    private static final String KEY_ENCHANTMENT = "WastelandEnchant";
    private static final String KEY_ARTIFACT_PROPS = "WastelandArtProps";
    private static final String KEY_ARTIFACT_NAME = "WastelandArtName";
    private static final String KEY_IDENTIFIED = "WastelandIdentified";

    protected ItemStack itemStack;
    protected String customName;
    protected ItemRarity rarity;
    protected int enchantmentLevel;
    protected Map<ArtifactProperty, Integer> artifactProperties;
    protected boolean identified;

    /**
     * Create a new Wasteland item from an ItemStack
     */
    public WastelandItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.rarity = ItemRarity.COMMON;
        this.enchantmentLevel = 0;
        this.artifactProperties = new HashMap<>();
        this.identified = false;
        this.customName = null;

        // Load properties from NBT if they exist
        loadFromNBT();
    }

    /**
     * Create a new Wasteland item with specific properties
     */
    public WastelandItem(ItemStack itemStack, ItemRarity rarity, int enchantmentLevel) {
        this.itemStack = itemStack;
        this.rarity = rarity;
        this.enchantmentLevel = enchantmentLevel;
        this.artifactProperties = new HashMap<>();
        this.identified = false;
        this.customName = null;

        // Save to NBT
        saveToNBT();
    }

    /**
     * Load properties from ItemStack NBT
     */
    protected void loadFromNBT() {
        CompoundTag tag = itemStack.getTag();
        if (tag == null) return;

        // Load rarity
        if (tag.contains(KEY_RARITY)) {
            try {
                this.rarity = ItemRarity.valueOf(tag.getString(KEY_RARITY));
            } catch (IllegalArgumentException e) {
                this.rarity = ItemRarity.COMMON;
            }
        }

        // Load enchantment
        if (tag.contains(KEY_ENCHANTMENT)) {
            this.enchantmentLevel = tag.getInt(KEY_ENCHANTMENT);
        }

        // Load artifact properties
        if (tag.contains(KEY_ARTIFACT_PROPS)) {
            ListTag propsList = tag.getList(KEY_ARTIFACT_PROPS, Tag.TAG_COMPOUND);
            for (int i = 0; i < propsList.size(); i++) {
                CompoundTag propTag = propsList.getCompound(i);
                try {
                    ArtifactProperty prop = ArtifactProperty.valueOf(propTag.getString("Property"));
                    int value = propTag.getInt("Value");
                    artifactProperties.put(prop, value);
                } catch (IllegalArgumentException e) {
                    // Skip invalid properties
                }
            }
        }

        // Load custom name
        if (tag.contains(KEY_ARTIFACT_NAME)) {
            this.customName = tag.getString(KEY_ARTIFACT_NAME);
        }

        // Load identified status
        if (tag.contains(KEY_IDENTIFIED)) {
            this.identified = tag.getBoolean(KEY_IDENTIFIED);
        }
    }

    /**
     * Save properties to ItemStack NBT
     */
    public void saveToNBT() {
        CompoundTag tag = itemStack.getOrCreateTag();

        // Save rarity
        tag.putString(KEY_RARITY, rarity.name());

        // Save enchantment
        tag.putInt(KEY_ENCHANTMENT, enchantmentLevel);

        // Save artifact properties
        if (!artifactProperties.isEmpty()) {
            ListTag propsList = new ListTag();
            for (Map.Entry<ArtifactProperty, Integer> entry : artifactProperties.entrySet()) {
                CompoundTag propTag = new CompoundTag();
                propTag.putString("Property", entry.getKey().name());
                propTag.putInt("Value", entry.getValue());
                propsList.add(propTag);
            }
            tag.put(KEY_ARTIFACT_PROPS, propsList);
        }

        // Save custom name
        if (customName != null) {
            tag.putString(KEY_ARTIFACT_NAME, customName);
        }

        // Save identified status
        tag.putBoolean(KEY_IDENTIFIED, identified);

        // Update display name
        updateDisplayName();
    }

    /**
     * Update the ItemStack's display name with rarity and enchantment
     */
    protected void updateDisplayName() {
        StringBuilder name = new StringBuilder();

        // Add rarity color
        name.append(rarity.getFormattingCode());

        // Add enchantment prefix if positive
        if (enchantmentLevel > 0) {
            name.append("+").append(enchantmentLevel).append(" ");
        } else if (enchantmentLevel < 0) {
            name.append(enchantmentLevel).append(" ");
        }

        // Add custom name for artifacts, or base name
        if (customName != null && identified) {
            name.append(customName);
        } else if (!identified && rarity.isArtifact()) {
            name.append("Unidentified ").append(getBaseItemName());
        } else {
            name.append(getBaseItemName());
        }

        // Set the display name
        itemStack.setHoverName(Component.literal(name.toString()));
    }

    /**
     * Get base item name (without enchantment/rarity)
     */
    protected abstract String getBaseItemName();

    /**
     * Add an artifact property
     */
    public void addArtifactProperty(ArtifactProperty property, int value) {
        artifactProperties.put(property, value);
        saveToNBT();
    }

    /**
     * Get artifact property value (0 if not present)
     */
    public int getArtifactProperty(ArtifactProperty property) {
        return artifactProperties.getOrDefault(property, 0);
    }

    /**
     * Check if item has an artifact property
     */
    public boolean hasArtifactProperty(ArtifactProperty property) {
        return artifactProperties.containsKey(property);
    }

    /**
     * Get all artifact properties
     */
    public Map<ArtifactProperty, Integer> getArtifactProperties() {
        return new HashMap<>(artifactProperties);
    }

    /**
     * Set custom name (for artifacts)
     */
    public void setCustomName(String name) {
        this.customName = name;
        saveToNBT();
    }

    /**
     * Identify the item (reveal properties)
     */
    public void identify() {
        this.identified = true;
        saveToNBT();
    }

    /**
     * Get formatted tooltip lines for this item
     */
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();

        // Show rarity
        tooltip.add(rarity.getFormattingCode() + rarity.getDisplayName());

        // Show enchantment if any
        if (enchantmentLevel != 0) {
            String sign = enchantmentLevel > 0 ? "+" : "";
            tooltip.add("§7Enchantment: " + sign + enchantmentLevel);
        }

        // Show artifact properties if identified
        if (identified && !artifactProperties.isEmpty()) {
            tooltip.add("§6Special Properties:");
            for (Map.Entry<ArtifactProperty, Integer> entry : artifactProperties.entrySet()) {
                ArtifactProperty prop = entry.getKey();
                int value = entry.getValue();
                String color = prop.isCurse() ? "§c" : "§a";
                tooltip.add(color + "  " + prop.formatValue(value));
            }
        } else if (!identified && !artifactProperties.isEmpty()) {
            tooltip.add("§8(Unidentified - properties unknown)");
        }

        return tooltip;
    }

    // Getters
    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

    public boolean isIdentified() {
        return identified;
    }

    public String getCustomName() {
        return customName;
    }

    /**
     * Check if this item is a wrapper for the given ItemStack
     */
    public static boolean isWastelandItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(KEY_RARITY);
    }

    /**
     * Get total power level of all artifact properties
     */
    public int getTotalPowerLevel() {
        int total = 0;
        for (Map.Entry<ArtifactProperty, Integer> entry : artifactProperties.entrySet()) {
            total += entry.getKey().getPowerLevel() * entry.getValue();
        }
        return total;
    }
}
