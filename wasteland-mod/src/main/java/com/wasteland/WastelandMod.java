package com.wasteland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
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
                // Select a random DCSS wasteland vault!
                String vaultPath = VaultSelector.getRandomVault();

                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Wasteland Crawl - Random Vault Selection");
                LOGGER.info("  Vault: {}", vaultPath);
                LOGGER.info("═══════════════════════════════════════════════════════");

                // Render the vault
                BlockPos playerPos = event.getEntity().blockPosition();
                BlockPos safePos = DungeonRenderer.renderRoomFromJson(level, playerPos, vaultPath);

                // Teleport player to center of room
                event.getEntity().teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

                LOGGER.info("Player teleported to DCSS vault: {}", safePos);
            }
        }
    }
}
