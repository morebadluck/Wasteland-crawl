package com.wasteland;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages portal destinations and teleportation between vaults
 */
public class PortalManager {
    private static final Logger LOGGER = LogManager.getLogger();

    // Portal destination registry: portal position -> destination info
    private static final Map<String, PortalDestination> PORTAL_REGISTRY = new HashMap<>();

    // Player cooldown to prevent portal spam
    private static final Map<UUID, Long> PLAYER_COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_MS = 1000; // 1 second cooldown

    /**
     * Register a portal with its destination
     */
    public static void registerPortal(BlockPos portalPos, PortalType type, String destinationVault) {
        String key = posToKey(portalPos);
        PortalDestination dest = new PortalDestination(type, destinationVault);
        PORTAL_REGISTRY.put(key, dest);

        LOGGER.debug("Registered portal at {} -> {} ({})", portalPos, destinationVault, type);
    }

    /**
     * Get portal destination at a position
     */
    public static PortalDestination getPortal(BlockPos pos) {
        return PORTAL_REGISTRY.get(posToKey(pos));
    }

    /**
     * Check if player can use portal (cooldown check)
     */
    public static boolean canUsePortal(UUID playerId) {
        Long lastUse = PLAYER_COOLDOWNS.get(playerId);
        if (lastUse == null) return true;

        long now = System.currentTimeMillis();
        return (now - lastUse) > COOLDOWN_MS;
    }

    /**
     * Mark player as having used a portal (start cooldown)
     */
    public static void markPortalUse(UUID playerId) {
        PLAYER_COOLDOWNS.put(playerId, System.currentTimeMillis());
    }

    /**
     * Teleport player through portal
     */
    public static boolean usePortal(ServerLevel level, BlockPos portalPos, net.minecraft.world.entity.player.Player player) {
        // Check cooldown
        if (!canUsePortal(player.getUUID())) {
            return false;
        }

        PortalDestination dest = getPortal(portalPos);
        if (dest == null) {
            LOGGER.warn("No portal found at {}", portalPos);
            return false;
        }

        // Determine destination based on portal type and player depth
        String vaultPath;
        int oldDepth = DungeonProgression.getDepth(player.getUUID());
        int newDepth;

        switch (dest.type) {
            case STAIRS_DOWN:
                newDepth = DungeonProgression.goDeeper(player.getUUID());
                vaultPath = DungeonProgression.getVaultForDepth(player.getUUID());
                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Player descending: depth {} → {}", oldDepth, newDepth);
                LOGGER.info("  Vault tier: {}", DungeonProgression.getTierForDepth(newDepth));
                LOGGER.info("  Selected vault: {}", vaultPath);
                LOGGER.info("═══════════════════════════════════════════════════════");
                break;

            case STAIRS_UP:
                newDepth = DungeonProgression.goUp(player.getUUID());

                // If we've reached the surface (depth 0), return to overworld
                if (newDepth == 0) {
                    LOGGER.info("═══════════════════════════════════════════════════════");
                    LOGGER.info("  Player ascending: depth {} → 0 (surface)", oldDepth);
                    LOGGER.info("  Returning to overworld");
                    LOGGER.info("═══════════════════════════════════════════════════════");

                    // Teleport to world spawn (overworld)
                    BlockPos spawnPoint = level.getSharedSpawnPos();
                    BlockPos surfacePos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, spawnPoint);
                    player.teleportTo(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5);

                    // Mark portal use
                    markPortalUse(player.getUUID());

                    LOGGER.info("  Player teleported to overworld: {}", surfacePos);
                    LOGGER.info("═══════════════════════════════════════════════════════");
                    return true;
                }

                // Still underground, load appropriate vault
                vaultPath = DungeonProgression.getVaultForDepth(player.getUUID());
                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Player ascending: depth {} → {}", oldDepth, newDepth);
                LOGGER.info("  Vault tier: {}", DungeonProgression.getTierForDepth(newDepth));
                LOGGER.info("  Selected vault: {}", vaultPath);
                LOGGER.info("═══════════════════════════════════════════════════════");
                break;

            case DUNGEON_ENTRANCE:
                DungeonProgression.setDepth(player.getUUID(), 1);
                vaultPath = DungeonProgression.getVaultForDepth(player.getUUID());
                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Player entering dungeon at depth 1");
                LOGGER.info("  Selected vault: {}", vaultPath);
                LOGGER.info("═══════════════════════════════════════════════════════");
                break;

            case DUNGEON_EXIT:
                // Return player to overworld spawn
                DungeonProgression.resetToSurface(player.getUUID());
                LOGGER.info("═══════════════════════════════════════════════════════");
                LOGGER.info("  Player exiting to surface (depth 0)");
                LOGGER.info("═══════════════════════════════════════════════════════");

                // Teleport to world spawn (overworld)
                BlockPos spawnPoint = level.getSharedSpawnPos();
                BlockPos surfacePos = level.getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, spawnPoint);
                player.teleportTo(surfacePos.getX() + 0.5, surfacePos.getY(), surfacePos.getZ() + 0.5);

                // Mark portal use
                markPortalUse(player.getUUID());

                LOGGER.info("  Player teleported to overworld: {}", surfacePos);
                LOGGER.info("═══════════════════════════════════════════════════════");
                return true;

            default:
                LOGGER.error("Unknown portal type: {}", dest.type);
                return false;
        }

        // Generate new spawn location (offset from current to avoid overlap)
        BlockPos newSpawn = player.blockPosition().offset(100, 0, 100);

        // Render destination vault
        BlockPos safePos = DungeonRenderer.renderRoomFromJson(level, newSpawn, vaultPath);

        // Teleport player
        player.teleportTo(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5);

        // Mark portal use
        markPortalUse(player.getUUID());

        LOGGER.info("  Player teleported to: {}", safePos);
        LOGGER.info("═══════════════════════════════════════════════════════");

        return true;
    }

    /**
     * Clear all portal registrations (for world reload)
     */
    public static void clearPortals() {
        PORTAL_REGISTRY.clear();
        LOGGER.info("Cleared all portal registrations");
    }

    /**
     * Convert BlockPos to string key for map
     */
    private static String posToKey(BlockPos pos) {
        return pos.getX() + "," + pos.getY() + "," + pos.getZ();
    }

    /**
     * Portal destination information
     */
    public static class PortalDestination {
        public final PortalType type;
        public final String vaultName;

        public PortalDestination(PortalType type, String vaultName) {
            this.type = type;
            this.vaultName = vaultName;
        }
    }

    /**
     * Types of portals
     */
    public enum PortalType {
        STAIRS_UP,          // < - Go to previous level
        STAIRS_DOWN,        // > - Go to next level
        DUNGEON_ENTRANCE,   // { - Enter dungeon from surface
        DUNGEON_EXIT        // } - Exit dungeon to surface
    }
}
