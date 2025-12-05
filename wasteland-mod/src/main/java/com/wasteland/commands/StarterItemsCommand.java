package com.wasteland.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/**
 * Command to give player starter items for testing
 */
public class StarterItemsCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("starteritems")
            .requires(source -> source.hasPermission(2))
            .executes(StarterItemsCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("This command can only be used by players"));
            return 0;
        }

        try {
            // Create starter weapon (Common Shiv)
            ItemStack starterWeapon = new ItemStack(com.wasteland.item.ModItems.SHIV.get());
            com.wasteland.loot.WastelandWeapon weapon = new com.wasteland.loot.WastelandWeapon(
                starterWeapon,
                com.wasteland.equipment.WeaponType.SHIV,
                com.wasteland.loot.ItemRarity.COMMON,
                0
            );
            weapon.identify();
            weapon.saveToNBT();

            // Create starter armor (Common Robe)
            ItemStack starterArmor = new ItemStack(com.wasteland.item.ModItems.ROBE.get());
            com.wasteland.loot.WastelandArmor armor = new com.wasteland.loot.WastelandArmor(
                starterArmor,
                com.wasteland.loot.ArmorType.ROBE,
                com.wasteland.loot.ItemRarity.COMMON,
                0
            );
            armor.identify();
            armor.saveToNBT();

            // Give items to player
            player.addItem(starterWeapon);
            player.addItem(starterArmor);

            source.sendSuccess(() -> Component.literal("Given starter items: Shiv and Robe"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to give starter items: " + e.getMessage()));
            return 0;
        }
    }
}
