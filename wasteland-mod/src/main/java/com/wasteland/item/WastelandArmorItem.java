package com.wasteland.item;

import com.wasteland.loot.ArmorType;
import com.wasteland.loot.ItemRarity;
import com.wasteland.loot.WastelandArmor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Minecraft item representing DCSS-style armor.
 * Stores armor type and can create WastelandArmor instances for stat calculations.
 */
public class WastelandArmorItem extends Item {
    private final ArmorType armorType;

    public WastelandArmorItem(ArmorType armorType) {
        super(new Properties()
            .stacksTo(1)  // Armor doesn't stack
            .rarity(Rarity.COMMON));
        this.armorType = armorType;
    }

    /**
     * Get the armor type for this item
     */
    public ArmorType getArmorType() {
        return armorType;
    }

    /**
     * Create a WastelandArmor instance from this ItemStack for stat calculations
     */
    public static WastelandArmor createArmor(ItemStack stack) {
        if (stack.getItem() instanceof WastelandArmorItem armorItem) {
            return new WastelandArmor(stack, armorItem.getArmorType());
        }
        return null;
    }

    /**
     * Initialize a new armor piece with starting properties
     */
    public static ItemStack createStack(ArmorType type, ItemRarity rarity, int enchantmentLevel) {
        WastelandArmorItem item = getItemForType(type);
        if (item == null) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(item);
        WastelandArmor armor = new WastelandArmor(stack, type, rarity, enchantmentLevel);
        armor.saveToNBT();

        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        WastelandArmor armor = createArmor(stack);
        if (armor != null) {
            // Build name with enchantment level and rarity
            StringBuilder name = new StringBuilder();
            if (armor.getEnchantmentLevel() > 0) {
                name.append("+").append(armor.getEnchantmentLevel()).append(" ");
            } else if (armor.getEnchantmentLevel() < 0) {
                name.append(armor.getEnchantmentLevel()).append(" ");
            }
            name.append(armorType.getDisplayName());

            return Component.literal(name.toString())
                .withStyle(armor.getRarity().getChatFormatting());
        }
        return Component.literal(armorType.getDisplayName());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        WastelandArmor armor = createArmor(stack);
        if (armor == null) {
            // Uninitialized armor - show basic stats
            tooltip.add(Component.literal("AC: +" + armorType.getBaseAC())
                .withStyle(ChatFormatting.GRAY));
            int evPenalty = armorType.getBaseEVPenalty();
            if (evPenalty != 0) {
                tooltip.add(Component.literal("EV: " + (evPenalty > 0 ? "+" : "") + evPenalty)
                    .withStyle(ChatFormatting.GRAY));
            }
            return;
        }

        // Get formatted tooltip from WastelandArmor
        List<String> armorTooltip = armor.getTooltip();
        for (String line : armorTooltip) {
            tooltip.add(Component.literal(line));
        }

        // Add comparison tooltip if on client side with a player
        if (level != null && level.isClientSide()) {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                var comparison = com.wasteland.loot.ItemComparison.getComparisonTooltip(mc.player, stack);
                if (comparison != null) {
                    tooltip.addAll(comparison);
                }
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        WastelandArmor armor = createArmor(stack);
        if (armor != null) {
            // Enchanted or artifact armor has foil effect
            return armor.getEnchantmentLevel() > 0 || armor.getRarity().isArtifact();
        }
        return false;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        WastelandArmor armor = createArmor(stack);
        if (armor != null) {
            return armor.getRarity().getMinecraftRarity();
        }
        return Rarity.COMMON;
    }

    /**
     * Helper to get the registered item for an armor type
     * TODO: This will be implemented once armors are registered
     */
    private static WastelandArmorItem getItemForType(ArmorType type) {
        // For now, return null - this will be implemented when we register items
        return null;
    }
}
