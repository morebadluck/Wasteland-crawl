package com.wasteland.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.wasteland.structures.ApartmentStructure;
import com.wasteland.structures.GroceryStoreStructure;
import com.wasteland.structures.HousingStructure;
import com.wasteland.structures.MallStructure;
import com.wasteland.structures.TrailerStructure;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

import java.util.Random;

/**
 * Test commands for spawning structures
 */
public class StructureTestCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("spawn")
            .then(Commands.literal("house")
                .executes(StructureTestCommand::spawnHouse))
            .then(Commands.literal("apartment")
                .executes(StructureTestCommand::spawnApartment))
            .then(Commands.literal("trailer")
                .executes(StructureTestCommand::spawnTrailer))
            .then(Commands.literal("grocery")
                .executes(StructureTestCommand::spawnGrocery))
            .then(Commands.literal("mall")
                .executes(StructureTestCommand::spawnMall))
        );
    }

    private static int spawnHouse(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.literal("Command must be run in a server world"));
            return 0;
        }

        ServerLevel level = (ServerLevel) source.getLevel();

        // Get player position (or command block position)
        BlockPos pos = BlockPos.containing(source.getPosition());
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);

        // Generate house at position
        HousingStructure.generate(level, spawnPos, new Random());

        source.sendSuccess(() -> Component.literal("Spawned suburban house at " + spawnPos.toShortString()), true);
        return 1;
    }

    private static int spawnApartment(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.literal("Command must be run in a server world"));
            return 0;
        }

        ServerLevel level = (ServerLevel) source.getLevel();

        // Get player position (or command block position)
        BlockPos pos = BlockPos.containing(source.getPosition());
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);

        // Generate apartment building at position
        ApartmentStructure.generate(level, spawnPos, new Random());

        source.sendSuccess(() -> Component.literal("Spawned apartment building at " + spawnPos.toShortString()), true);
        return 1;
    }

    private static int spawnTrailer(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.literal("Command must be run in a server world"));
            return 0;
        }

        ServerLevel level = (ServerLevel) source.getLevel();

        // Get player position (or command block position)
        BlockPos pos = BlockPos.containing(source.getPosition());
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);

        // Generate trailer at position
        TrailerStructure.generate(level, spawnPos, new Random());

        source.sendSuccess(() -> Component.literal("Spawned trailer at " + spawnPos.toShortString()), true);
        return 1;
    }

    private static int spawnGrocery(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.literal("Command must be run in a server world"));
            return 0;
        }

        ServerLevel level = (ServerLevel) source.getLevel();

        // Get player position (or command block position)
        BlockPos pos = BlockPos.containing(source.getPosition());
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);

        // Generate grocery store at position
        GroceryStoreStructure.generate(level, spawnPos, new Random());

        source.sendSuccess(() -> Component.literal("Spawned grocery store at " + spawnPos.toShortString()), true);
        return 1;
    }

    private static int spawnMall(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        if (!(source.getLevel() instanceof ServerLevel)) {
            source.sendFailure(Component.literal("Command must be run in a server world"));
            return 0;
        }

        ServerLevel level = (ServerLevel) source.getLevel();

        // Get player position (or command block position)
        BlockPos pos = BlockPos.containing(source.getPosition());
        BlockPos spawnPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, pos);

        // Generate mall at position
        MallStructure.generate(level, spawnPos, new Random());

        source.sendSuccess(() -> Component.literal("Spawned mall at " + spawnPos.toShortString()), true);
        return 1;
    }
}
