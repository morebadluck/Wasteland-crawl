package com.wasteland.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wasteland.structures.SpawnPointStructure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

/**
 * Command to manually build spawn structure for testing
 */
public class SpawnStructureCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("buildspawn")
            .requires(source -> source.hasPermission(2))
            .executes(SpawnStructureCommand::execute));
    }

    private static int execute(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerLevel level = source.getLevel();

        BlockPos playerPos = BlockPos.containing(source.getPosition());

        source.sendSuccess(() -> Component.literal("Building spawn structure at your location..."), true);

        try {
            SpawnPointStructure.generate(level, playerPos, level.getRandom());
            source.sendSuccess(() -> Component.literal("Spawn structure built successfully!"), true);
            return 1;
        } catch (Exception e) {
            source.sendFailure(Component.literal("Failed to build spawn structure: " + e.getMessage()));
            return 0;
        }
    }
}
