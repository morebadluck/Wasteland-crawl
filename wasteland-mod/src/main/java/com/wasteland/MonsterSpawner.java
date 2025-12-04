package com.wasteland;

import com.wasteland.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

/**
 * Spawns Minecraft monsters based on DCSS monster tiers
 * Maps wasteland creatures to Minecraft equivalents
 */
public class MonsterSpawner {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random RANDOM = new Random();

    /**
     * Spawn a monster at the given position based on DCSS tier
     *
     * @param level Server level to spawn in
     * @param pos Position to spawn at
     * @param tier DCSS monster tier (1-9, or "1", "2", etc. as string)
     */
    public static void spawnMonster(ServerLevel level, BlockPos pos, String tier) {
        int tierNum;
        try {
            tierNum = Integer.parseInt(tier);
        } catch (NumberFormatException e) {
            LOGGER.warn("Invalid monster tier: {}, defaulting to tier 1", tier);
            tierNum = 1;
        }

        EntityType<? extends Mob> mobType = getMobTypeForTier(tierNum);
        spawnMobAtPosition(level, pos, mobType);
    }

    /**
     * Map DCSS tier to Minecraft mob type
     * Tiers follow DCSS depth progression
     * Now includes expanded Fallout-style robot roster
     */
    private static EntityType<? extends Mob> getMobTypeForTier(int tier) {
        // Tier 1-3: Early game (Depths 1-5) - Weak wasteland scavengers + light robots
        if (tier <= 3) {
            EntityType<?>[] earlyMobs = {
                EntityType.ZOMBIE,          // Tier 1: Basic undead
                EntityType.SKELETON,        // Tier 1: Ranged threat
                EntityType.SPIDER,          // Tier 2: Fast melee
                EntityType.CAVE_SPIDER,     // Tier 2: Poisonous
                EntityType.HUSK,            // Tier 3: Wasteland zombie variant
                ModEntities.EYEBOT.get(),   // Tier 1: Surveillance drone (ROBOT)
                ModEntities.PROTECTRON.get(),// Tier 2: Security robot (ROBOT)
                ModEntities.MR_HANDY.get(), // Tier 2: Utility robot (ROBOT)
                ModEntities.SECURITY_BOT.get() // Tier 3: Improved security (ROBOT)
            };
            return (EntityType<? extends Mob>) earlyMobs[RANDOM.nextInt(earlyMobs.length)];
        }
        // Tier 4-6: Mid game (Depths 6-10) - Dangerous mutants + combat robots
        else if (tier <= 6) {
            EntityType<?>[] midMobs = {
                EntityType.CREEPER,         // Tier 4: Explosive threat
                EntityType.STRAY,           // Tier 4: Ranged slowness
                EntityType.WITCH,           // Tier 5: Potion thrower
                EntityType.ZOMBIFIED_PIGLIN,// Tier 5: Tough melee
                EntityType.PHANTOM,         // Tier 6: Flying enemy
                ModEntities.SENTRY_BOT.get(), // Tier 5: Heavy combat robot (ROBOT)
                ModEntities.ASSAULTRON.get(), // Tier 4: Fast killer robot (ROBOT)
                ModEntities.MR_GUSTY.get(),   // Tier 4: Military robot (ROBOT)
                ModEntities.ROBOBRAIN.get(),  // Tier 5: Psionic robot (ROBOT)
                ModEntities.EXPERIMENTAL_BOT.get() // Tier 6: Unstable prototype (ROBOT)
            };
            return (EntityType<? extends Mob>) midMobs[RANDOM.nextInt(midMobs.length)];
        }
        // Tier 7-9: Late game (Depths 11+) - Elite wasteland horrors + boss robots
        else {
            EntityType<?>[] lateMobs = {
                EntityType.WITHER_SKELETON, // Tier 7: Wither effect
                EntityType.ENDERMAN,        // Tier 8: Teleporting threat
                EntityType.BLAZE,           // Tier 8: Flying ranged
                EntityType.VINDICATOR,      // Tier 9: High damage melee
                EntityType.RAVAGER,         // Tier 9: Tank enemy
                ModEntities.ANNIHILATOR_SENTRY_BOT.get(), // Tier 7: Elite heavy (ROBOT)
                ModEntities.QUANTUM_ASSAULTRON.get(), // Tier 8: Phase assassin (ROBOT)
                ModEntities.OVERLORD_BOT.get() // Tier 9: BOSS ROBOT (very rare)
            };
            return (EntityType<? extends Mob>) lateMobs[RANDOM.nextInt(lateMobs.length)];
        }
    }

    /**
     * Spawn a mob at the specified position
     */
    private static void spawnMobAtPosition(ServerLevel level, BlockPos pos, EntityType<? extends Mob> mobType) {
        try {
            Mob mob = mobType.create(level);
            if (mob != null) {
                // Position mob at center of block, standing on floor
                mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);

                // Add mob to world
                level.addFreshEntity(mob);

                LOGGER.debug("Spawned {} at {}", mobType.getDescription().getString(), pos);
            } else {
                LOGGER.error("Failed to create mob of type {}", mobType.getDescription().getString());
            }
        } catch (Exception e) {
            LOGGER.error("Error spawning mob {}: {}", mobType.getDescription().getString(), e.getMessage());
        }
    }

    /**
     * Get description of mob type for tier (for logging/debugging)
     */
    public static String getMobDescriptionForTier(int tier) {
        if (tier <= 3) {
            return "EARLY tier (Zombies, Skeletons, Spiders, Eyebots, Protectrons, Mr. Handys, Security Bots)";
        } else if (tier <= 6) {
            return "MID tier (Creepers, Witches, Phantoms, Sentry Bots, Assaultrons, Mr. Gustys, Robobrains, Experimental Bots)";
        } else {
            return "LATE tier (Wither Skeletons, Endermen, Blazes, Annihilator Sentry Bots, Quantum Assaultrons, OVERLORD BOSS)";
        }
    }
}
