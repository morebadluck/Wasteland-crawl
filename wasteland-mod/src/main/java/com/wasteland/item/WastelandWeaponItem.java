package com.wasteland.item;

import com.wasteland.equipment.WeaponType;
import com.wasteland.loot.ItemRarity;
import com.wasteland.loot.WastelandWeapon;
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
 * Minecraft item representing a DCSS-style weapon.
 * Stores weapon type and can create WastelandWeapon instances for stat calculations.
 */
public class WastelandWeaponItem extends Item {
    private final WeaponType weaponType;

    public WastelandWeaponItem(WeaponType weaponType) {
        super(new Properties()
            .stacksTo(1)  // Weapons don't stack
            .rarity(Rarity.COMMON));
        this.weaponType = weaponType;
    }

    /**
     * Get the weapon type for this item
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Create a WastelandWeapon instance from this ItemStack for stat calculations
     */
    public static WastelandWeapon createWeapon(ItemStack stack) {
        if (stack.getItem() instanceof WastelandWeaponItem weaponItem) {
            return new WastelandWeapon(stack, weaponItem.getWeaponType());
        }
        return null;
    }

    /**
     * Initialize a new weapon with starting properties
     */
    public static ItemStack createStack(WeaponType type, ItemRarity rarity, int enchantmentLevel) {
        WastelandWeaponItem item = getItemForType(type);
        if (item == null) return ItemStack.EMPTY;

        ItemStack stack = new ItemStack(item);
        WastelandWeapon weapon = new WastelandWeapon(stack, type, rarity, enchantmentLevel);
        weapon.saveToNBT();

        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        WastelandWeapon weapon = createWeapon(stack);
        if (weapon != null) {
            // Build name with enchantment level and rarity
            StringBuilder name = new StringBuilder();
            if (weapon.getEnchantmentLevel() > 0) {
                name.append("+").append(weapon.getEnchantmentLevel()).append(" ");
            } else if (weapon.getEnchantmentLevel() < 0) {
                name.append(weapon.getEnchantmentLevel()).append(" ");
            }
            name.append(weaponType.getDisplayName());

            return Component.literal(name.toString())
                .withStyle(weapon.getRarity().getChatFormatting());
        }
        return Component.literal(weaponType.getDisplayName());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        WastelandWeapon weapon = createWeapon(stack);
        if (weapon == null) {
            // Uninitialized weapon - show basic stats
            tooltip.add(Component.literal("Damage: " + weaponType.getBaseDamage())
                .withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.literal("Delay: " + weaponType.getBaseDelay())
                .withStyle(ChatFormatting.GRAY));
            return;
        }

        // Get formatted tooltip from WastelandWeapon
        List<String> weaponTooltip = weapon.getTooltip();
        for (String line : weaponTooltip) {
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
        WastelandWeapon weapon = createWeapon(stack);
        if (weapon != null) {
            // Enchanted or artifact weapons have foil effect
            return weapon.getEnchantmentLevel() > 0 || weapon.getRarity().isArtifact();
        }
        return false;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        WastelandWeapon weapon = createWeapon(stack);
        if (weapon != null) {
            return weapon.getRarity().getMinecraftRarity();
        }
        return Rarity.COMMON;
    }

    /**
     * Helper to get the registered item for a weapon type
     * TODO: This will be implemented once weapons are registered
     */
    private static WastelandWeaponItem getItemForType(WeaponType type) {
        // For now, return null - this will be implemented when we register items
        return null;
    }
}
