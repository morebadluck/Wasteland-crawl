package com.wasteland;

import com.wasteland.magic.Spells;
import com.wasteland.religion.AltarManager;
import com.wasteland.religion.GodAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus()
            .addListener(this::setup);

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

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity().level() instanceof ServerLevel level) {
                // Initialize player progression (starts at depth 0 = surface)
                DungeonProgression.setDepth(event.getEntity().getUUID(), 0);

                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Wasteland Crawl - Player Spawned in Overworld");
                LOGGER.info("  Find a dungeon entrance to begin your adventure!");
                LOGGER.info("═══════════════════════════════════════════════════════");

                // Place a dungeon entrance structure near spawn
                BlockPos spawnPos = event.getEntity().blockPosition();
                BlockPos entrancePos = spawnPos.offset(20, 0, 20);

                // Find ground level
                BlockPos groundPos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, entrancePos);

                LOGGER.info("Placing initial dungeon entrance at: {}", groundPos);
                DungeonEntrance.placeRandomEntrance(level, groundPos);

                // Place test temple with altars for testing
                BlockPos templePos = spawnPos.offset(-20, 0, 0);
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
    }
}
