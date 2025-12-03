package com.wasteland.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.wasteland.WastelandMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles detection of enemies and triggers combat mode.
 * Supports both manual (attack to trigger) and auto (proximity) modes.
 * Runs on client side only.
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CombatDetection {

    private static int tickCounter = 0;
    private static final int CHECK_INTERVAL = 20; // Check every second (20 ticks)

    // Combat mode settings
    private static boolean autoCombatMode = false; // Toggle for proximity-based combat
    private static long lastCombatEndTime = 0; // Track when combat last ended
    private static final long COMBAT_COOLDOWN_MS = 5000; // 5 second cooldown after combat ends

    /**
     * Check for nearby enemies every second (only if auto-combat mode is enabled)
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        // Only check periodically to save performance
        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) {
            return;
        }
        tickCounter = 0;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        Player player = minecraft.player;
        Level level = minecraft.level;
        CombatManager combat = CombatManager.getInstance();

        // Skip if already in combat
        if (combat.isInCombat()) {
            return;
        }

        // Only auto-detect if auto-combat mode is enabled
        if (!autoCombatMode) {
            return;
        }

        // Check cooldown to prevent immediate re-trigger after combat ends
        if (System.currentTimeMillis() - lastCombatEndTime < COMBAT_COOLDOWN_MS) {
            return;
        }

        // Check for nearby hostile entities
        List<LivingEntity> nearbyEnemies = detectNearbyEnemies(player, level);

        if (!nearbyEnemies.isEmpty()) {
            // Trigger combat!
            System.out.println("[Auto-Combat] Found " + nearbyEnemies.size() + " enemies within range");
            startCombat(player, nearbyEnemies, level, minecraft);
        }
    }

    /**
     * Manual combat trigger - when player attacks an entity
     */
    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.level().isClientSide && event.getTarget() instanceof LivingEntity) {
            LivingEntity target = (LivingEntity) event.getTarget();

            CombatManager combat = CombatManager.getInstance();

            // Skip if already in combat
            if (combat.isInCombat()) {
                return;
            }

            // Check cooldown
            if (System.currentTimeMillis() - lastCombatEndTime < COMBAT_COOLDOWN_MS) {
                return;
            }

            // Check if target is hostile
            if (isHostile(target, player)) {
                System.out.println("[Manual Combat] Player attacked " + target.getName().getString());

                // Gather nearby enemies
                List<LivingEntity> enemies = new ArrayList<>();
                enemies.add(target);

                // Include other nearby hostiles
                List<LivingEntity> nearby = detectNearbyEnemies(player, player.level());
                for (LivingEntity enemy : nearby) {
                    if (!enemies.contains(enemy)) {
                        enemies.add(enemy);
                    }
                }

                Minecraft minecraft = Minecraft.getInstance();
                startCombat(player, enemies, player.level(), minecraft);

                // Cancel the attack event - combat system handles damage
                event.setCanceled(true);
            }
        }
    }

    /**
     * Start combat and open UI
     */
    private static void startCombat(Player player, List<LivingEntity> enemies, Level level, Minecraft minecraft) {
        CombatManager combat = CombatManager.getInstance();
        combat.startCombat(player, enemies, level);
        minecraft.setScreen(new com.wasteland.client.gui.CombatScreen());
    }

    /**
     * Detect all hostile entities within detection radius
     */
    private static List<LivingEntity> detectNearbyEnemies(Player player, Level level) {
        List<LivingEntity> enemies = new ArrayList<>();

        double radius = CombatManager.DETECTION_RADIUS;

        // Create bounding box around player
        AABB searchBox = player.getBoundingBox().inflate(radius);

        // Find all living entities in range
        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(
            LivingEntity.class,
            searchBox,
            entity -> entity != player // Exclude player
        );

        // Filter for hostile mobs
        for (LivingEntity entity : nearbyEntities) {
            if (isHostile(entity, player)) {
                enemies.add(entity);
            }
        }

        return enemies;
    }

    /**
     * Check if an entity is hostile to the player
     */
    private static boolean isHostile(LivingEntity entity, Player player) {
        // For now, consider all Mobs (except passive animals) as hostile
        // TODO: Implement proper faction/hostility system

        if (!(entity instanceof Mob)) {
            return false;
        }

        Mob mob = (Mob) entity;

        // Check if mob is targeting player
        if (mob.getTarget() == player) {
            return true;
        }

        // Check mob type - consider most mobs hostile in dungeons
        // TODO: Add whitelist for friendly NPCs, merchants, etc.
        String mobType = entity.getType().toString();

        // Don't attack passive animals
        if (mobType.contains("cow") || mobType.contains("sheep") ||
            mobType.contains("chicken") || mobType.contains("pig")) {
            return false;
        }

        // Most other mobs in dungeons are hostile
        return true;
    }

    /**
     * Manually trigger combat (for testing or player-initiated attacks)
     */
    public static void triggerCombat(Player player, LivingEntity target) {
        Level level = player.level();
        CombatManager combat = CombatManager.getInstance();

        if (combat.isInCombat()) {
            return; // Already in combat
        }

        List<LivingEntity> enemies = new ArrayList<>();
        enemies.add(target);

        // Also include nearby enemies
        double radius = 10.0;
        AABB searchBox = target.getBoundingBox().inflate(radius);

        List<LivingEntity> nearby = level.getEntitiesOfClass(
            LivingEntity.class,
            searchBox,
            entity -> entity != player && entity != target && isHostile(entity, player)
        );

        enemies.addAll(nearby);

        System.out.println("Manually triggered combat with " + enemies.size() + " enemies");
        combat.startCombat(player, enemies, level);
    }

    /**
     * Toggle auto-combat mode on/off
     */
    public static void toggleAutoCombatMode() {
        autoCombatMode = !autoCombatMode;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            String status = autoCombatMode ? "ENABLED" : "DISABLED";
            minecraft.player.displayClientMessage(
                Component.literal("§6[Combat Mode] §f" + status),
                true // Display as action bar
            );
            System.out.println("Auto-combat mode: " + status);
        }
    }

    /**
     * Get current auto-combat mode state
     */
    public static boolean isAutoCombatEnabled() {
        return autoCombatMode;
    }

    /**
     * Set the cooldown timer (called when combat ends)
     */
    public static void setCombatCooldown() {
        lastCombatEndTime = System.currentTimeMillis();
        System.out.println("Combat cooldown started (5 seconds)");
    }
}
