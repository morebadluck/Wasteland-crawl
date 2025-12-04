package com.wasteland.item;

import com.wasteland.worldgen.RuneType;
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
 * Quest item representing a rune found in max-level dungeons.
 * Players need 3+ different runes to access the Realm of Zot (final level).
 *
 * TODO: Rename "Realm of Zot" to wasteland-appropriate name
 */
public class RuneItem extends Item {
    private final RuneType runeType;

    public RuneItem(RuneType runeType) {
        super(new Properties()
            .stacksTo(1)  // Runes don't stack
            .rarity(Rarity.EPIC)  // Always epic rarity
            .fireResistant());  // Can't be destroyed by fire
        this.runeType = runeType;
    }

    public RuneType getRuneType() {
        return runeType;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(runeType.getDisplayName())
            .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);

        // Add rune description
        tooltip.add(Component.literal(runeType.getDescription())
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

        // Add quest hint
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Quest Item")
            .withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.literal("Collect " + RuneType.getRunesRequiredForZot() + " different runes to access the final level")
            .withStyle(ChatFormatting.AQUA));
    }

    /**
     * Runes glow like enchanted items
     */
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    /**
     * Get rune type from ItemStack NBT
     */
    public static RuneType getRuneType(ItemStack stack) {
        if (stack.getItem() instanceof RuneItem runeItem) {
            return runeItem.getRuneType();
        }
        return null;
    }
}
