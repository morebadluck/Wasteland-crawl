package com.wasteland;

import com.wasteland.commands.StructureTestCommand;
import com.wasteland.entity.ModEntities;
import com.wasteland.item.ModItems;
import com.wasteland.magic.Spells;
import com.wasteland.monsters.MonsterScalingSystem;
import com.wasteland.religion.AltarManager;
import com.wasteland.religion.GodAbilities;
import com.wasteland.worldgen.AreaDifficultyManager;
import com.wasteland.worldgen.VisualDangerCues;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("wasteland")
public class WastelandMod {
    public static final String MOD_ID = "wasteland";
    private static final Logger LOGGER = LogManager.getLogger();

    public WastelandMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register entities
        ModEntities.register(modEventBus);

        // Register items
        ModItems.register(modEventBus);

        modEventBus.addListener(this::setup);

        LOGGER.info("═══════════════════════════════════════════════════════");
        LOGGER.info("  Wasteland Crawl - DCSS Integration Mod Loading...");
        LOGGER.info("═══════════════════════════════════════════════════════");
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Register all spells
        Spells.registerSpells();

        // Register all divine abilities
        GodAbilities.registerAbilities();

        LOGGER.info("Wasteland Crawl - Setup Complete!");
        LOGGER.info("  Version: 0.1.0 (Phase 2 - Proof of Concept)");
        LOGGER.info("  DCSS Backend: Ready for integration");
        LOGGER.info("  Minecraft Frontend: Active");
    }

    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class Events {

        private static boolean worldInitialized = false;
        private static final java.util.Set<Long> corruptedChunks = new java.util.HashSet<>();

        @SubscribeEvent
        public static void onCommandsRegister(RegisterCommandsEvent event) {
            StructureTestCommand.register(event.getDispatcher());
            LOGGER.info("Registered structure test commands");
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level() instanceof ServerLevel level) {
                // Load or initialize world data
                com.wasteland.worldgen.WastelandSavedData.get(level);

                // Initialize player progression (starts at depth 0 = surface)
                DungeonProgression.setDepth(event.getEntity().getUUID(), 0);

                // Give starter equipment to new players
                if (!com.wasteland.player.StarterEquipment.hasReceivedStarterEquipment(event.getEntity())) {
                    com.wasteland.player.StarterEquipment.giveStarterEquipment(event.getEntity());
                    LOGGER.info("Gave starter equipment to new player");
                }

                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Wasteland Crawl - Player Spawned in Overworld");
                LOGGER.info("  Find a dungeon entrance to begin your adventure!");
                LOGGER.info("═══════════════════════════════════════════════════════");

                // One-time world initialization
                if (!worldInitialized) {
                    long worldSeed = level.getSeed();
                    BlockPos worldSpawn = level.getSharedSpawnPos();

                    // Initialize organic difficulty system
                    LOGGER.info("Initializing organic difficulty system...");
                    AreaDifficultyManager.initialize(worldSeed, worldSpawn);
                    LOGGER.info("  Noise-based difficulty active!");
                    LOGGER.info("  Spawn protection: 200 blocks radius");

                    // Generate all dungeons across the world if they don't exist yet
                    if (com.wasteland.worldgen.DungeonManager.getAllDungeons().isEmpty()) {
                        LOGGER.info("Generating dungeons across the wasteland...");
                        com.wasteland.worldgen.DungeonManager.generateDungeons(level, worldSeed);

                        // Generate surface structures across the world
                        LOGGER.info("Generating surface structures...");
                        com.wasteland.worldgen.StructureManager.generateStructures(level, worldSeed);

                        // Generate roads between nearby dungeons
                        LOGGER.info("Generating wasteland roads...");
                        com.wasteland.worldgen.RoadGenerator.generateRoads(level, worldSeed);

                        // Mark data as dirty to save the newly generated dungeons and structures
                        com.wasteland.worldgen.WastelandSavedData.markDirty(level);
                    }

                    worldInitialized = true;
                }

                // Find and teleport player to a safe spawn location
                BlockPos currentPos = event.getEntity().blockPosition();
                BlockPos safeSpawn = com.wasteland.worldgen.SafeSpawnFinder.findSafeSpawn(level, currentPos);
                if (!safeSpawn.equals(currentPos)) {
                    event.getEntity().teleportTo(safeSpawn.getX() + 0.5, safeSpawn.getY(), safeSpawn.getZ() + 0.5);
                    LOGGER.info("Moved player to safe spawn: {}", safeSpawn);
                }

                // Place test temple with altars for testing (near spawn)
                BlockPos templePos = safeSpawn.offset(-20, 0, 0);
                BlockPos templeGroundPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, templePos);
                LOGGER.info("Placing test temple at: {}", templeGroundPos);
                AltarManager.placeTemple(level, templeGroundPos, 6);
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            // Only run on server, once per tick (END phase)
            if (event.phase != TickEvent.Phase.END) return;
            if (!(event.player.level() instanceof ServerLevel level)) return;

            // Check if player is standing on a portal block (wool markers)
            BlockPos playerPos = event.player.blockPosition();
            BlockPos belowPos = playerPos.below();

            // Check if there's a portal registered at this position
            PortalManager.PortalDestination portal = PortalManager.getPortal(belowPos);
            if (portal != null) {
                // Player is standing on a portal! Try to use it
                PortalManager.usePortal(level, belowPos, event.player);
            }
        }

        @SubscribeEvent
        public static void onEntitySpawn(EntityJoinLevelEvent event) {
            // Only scale entities on server side
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            // Only scale living entities (monsters, animals, etc.)
            if (!(event.getEntity() instanceof LivingEntity entity)) return;

            // Skip players
            if (entity instanceof Player) return;

            // Skip if AreaDifficultyManager not initialized yet
            try {
                if (AreaDifficultyManager.getInstance() == null) return;
            } catch (IllegalStateException e) {
                return; // Not initialized yet
            }

            // Apply monster scaling based on spawn location
            BlockPos spawnPos = entity.blockPosition();
            MonsterScalingSystem.scaleMonster(entity, spawnPos, level);

            // Log scaling for debugging (only first few)
            if (level.getRandom().nextFloat() < 0.1f) { // 10% sample rate
                LOGGER.debug(MonsterScalingSystem.getStatBreakdown(entity));
            }
        }

        @SubscribeEvent
        public static void onChunkLoad(ChunkEvent.Load event) {
            // Only apply corruption on server side
            if (!(event.getLevel() instanceof ServerLevel level)) return;

            // Skip if AreaDifficultyManager not initialized yet
            try {
                if (AreaDifficultyManager.getInstance() == null) return;
            } catch (IllegalStateException e) {
                return; // Not initialized yet
            }

            // Get chunk
            LevelChunk chunk = (LevelChunk) event.getChunk();
            long chunkKey = chunk.getPos().toLong();

            // Skip if already corrupted
            if (corruptedChunks.contains(chunkKey)) {
                return;
            }

            // Get chunk center position
            BlockPos chunkCenter = chunk.getPos().getMiddleBlockPosition(64);

            // Calculate area difficulty
            var biome = level.getBiome(chunkCenter).value();
            int difficulty = AreaDifficultyManager.getInstance()
                .calculateDifficultyWithSpawnProtection(chunkCenter, biome);

            // Only apply corruption if dangerous enough (10+)
            if (difficulty >= 10) {
                VisualDangerCues.applyCorruption(level, chunkCenter, difficulty, 16);

                // Mark chunk as corrupted
                corruptedChunks.add(chunkKey);

                // Log corruption for debugging
                if (level.getRandom().nextFloat() < 0.05f) { // 5% sample rate
                    LOGGER.debug("Applied level {} corruption to chunk at {}", difficulty, chunkCenter);
                }
            }
        }
    }
}
