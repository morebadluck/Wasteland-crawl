package com.wasteland.monsters;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import com.wasteland.worldgen.AreaDifficultyManager;

/**
 * Scales monster stats based on area difficulty.
 * Creates organic difficulty progression across the wasteland.
 */
public class MonsterScalingSystem {

    // Scaling factors per level
    private static final double HP_PER_LEVEL = 2.0;      // +2 HP per level
    private static final double DAMAGE_PER_LEVEL = 0.5;   // +0.5 damage per level
    private static final double ARMOR_PER_LEVEL = 0.3;    // +0.3 armor per level
    private static final double SPEED_PER_LEVEL = 0.002;  // +0.2% speed per level

    /**
     * Scale a monster based on its spawn location difficulty
     */
    public static void scaleMonster(LivingEntity entity, BlockPos spawnPos, Level level) {
        // Get area difficulty
        int areaDifficulty = calculateAreaDifficulty(spawnPos, level);

        // Apply scaling
        scaleHealth(entity, areaDifficulty);
        scaleDamage(entity, areaDifficulty);
        scaleArmor(entity, areaDifficulty);
        scaleSpeed(entity, areaDifficulty);

        // Store level for later reference (experience, loot, etc.)
        entity.getPersistentData().putInt("AreaLevel", areaDifficulty);

        // Apply visual variant if configured
        applyVisualVariant(entity, areaDifficulty);
    }

    /**
     * Calculate area difficulty (with fallback if AreaDifficultyManager not initialized)
     */
    private static int calculateAreaDifficulty(BlockPos pos, Level level) {
        try {
            if (AreaDifficultyManager.getInstance() != null) {
                // Get biome at position
                var biome = level.getBiome(pos).value();
                return AreaDifficultyManager.getInstance().calculateDifficulty(pos, biome);
            }
        } catch (IllegalStateException e) {
            // AreaDifficultyManager not initialized yet
        }

        // Fallback: Use distance from spawn
        BlockPos spawnPos = new BlockPos(level.getLevelData().getXSpawn(),
                                         level.getLevelData().getYSpawn(),
                                         level.getLevelData().getZSpawn());
        double distance = Math.sqrt(pos.distSqr(spawnPos));
        return Math.max(1, Math.min(30, (int)(distance / 500.0) + 1));
    }

    /**
     * Scale entity health based on level
     */
    private static void scaleHealth(LivingEntity entity, int level) {
        AttributeInstance maxHealth = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth != null) {
            double baseHealth = maxHealth.getBaseValue();
            double newHealth = baseHealth + (HP_PER_LEVEL * (level - 1));
            maxHealth.setBaseValue(newHealth);
            entity.setHealth((float)newHealth); // Set to full health
        }
    }

    /**
     * Scale entity damage based on level
     */
    private static void scaleDamage(LivingEntity entity, int level) {
        AttributeInstance attackDamage = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamage != null) {
            double baseDamage = attackDamage.getBaseValue();
            double newDamage = baseDamage + (DAMAGE_PER_LEVEL * (level - 1));
            attackDamage.setBaseValue(newDamage);
        }
    }

    /**
     * Scale entity armor based on level
     */
    private static void scaleArmor(LivingEntity entity, int level) {
        AttributeInstance armor = entity.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            double baseArmor = armor.getBaseValue();
            double newArmor = baseArmor + (ARMOR_PER_LEVEL * (level - 1));
            armor.setBaseValue(newArmor);
        }
    }

    /**
     * Scale entity speed based on level
     */
    private static void scaleSpeed(LivingEntity entity, int level) {
        AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            double baseSpeed = speed.getBaseValue();
            double newSpeed = baseSpeed + (SPEED_PER_LEVEL * (level - 1));
            speed.setBaseValue(newSpeed);
        }
    }

    /**
     * Apply visual variant based on level (color/model changes)
     */
    private static void applyVisualVariant(LivingEntity entity, int level) {
        // Store variant tier for rendering
        int variantTier = getVariantTier(level);
        entity.getPersistentData().putInt("VisualVariant", variantTier);

        // Variants:
        // Tier 0 (1-5): Normal colors (gray, brown)
        // Tier 1 (6-10): Slightly darker, yellow eyes
        // Tier 2 (11-15): Dark, red accents
        // Tier 3 (16-20): Very dark, red/purple glow
        // Tier 4 (21-25): Black with purple aura
        // Tier 5 (26+): Nightmare - black with reality distortion

        // Note: Actual rendering changes would be implemented in entity renderer
    }

    /**
     * Get visual variant tier based on level
     */
    public static int getVariantTier(int level) {
        if (level <= 5) return 0;
        if (level <= 10) return 1;
        if (level <= 15) return 2;
        if (level <= 20) return 3;
        if (level <= 25) return 4;
        return 5; // Nightmare tier
    }

    /**
     * Get scaled experience drop for monster
     */
    public static int getScaledExperience(LivingEntity entity) {
        int level = entity.getPersistentData().getInt("AreaLevel");
        if (level == 0) level = 1; // Default if not set

        // Base XP * level multiplier
        int baseXP = 5;
        return baseXP + (level * 2);
    }

    /**
     * Get display name suffix for monster (shows level)
     */
    public static String getLevelSuffix(LivingEntity entity) {
        int level = entity.getPersistentData().getInt("AreaLevel");
        if (level == 0) return "";

        // Color code based on level
        if (level <= 5) return " §7[Lv" + level + "]§r";       // Gray (safe)
        if (level <= 10) return " §e[Lv" + level + "]§r";      // Yellow (moderate)
        if (level <= 15) return " §6[Lv" + level + "]§r";      // Gold (dangerous)
        if (level <= 20) return " §c[Lv" + level + "]§r";      // Red (very dangerous)
        if (level <= 25) return " §4[Lv" + level + "]§r";      // Dark red (deadly)
        return " §5[Lv" + level + "]§r";                        // Purple (nightmare)
    }

    /**
     * Check if player should be warned about this monster
     */
    public static boolean isHighThreat(LivingEntity entity, LivingEntity player) {
        int monsterLevel = entity.getPersistentData().getInt("AreaLevel");
        int playerLevel = player.getPersistentData().getInt("PlayerLevel");

        // If player level not set, use equipment as rough estimate
        if (playerLevel == 0) {
            playerLevel = estimatePlayerLevel(player);
        }

        // Warn if monster is 5+ levels higher
        return monsterLevel >= (playerLevel + 5);
    }

    /**
     * Estimate player level based on equipment/stats
     */
    private static int estimatePlayerLevel(LivingEntity player) {
        // Simple estimate based on max health
        double maxHealth = player.getMaxHealth();

        if (maxHealth <= 20) return 1;
        if (maxHealth <= 30) return 5;
        if (maxHealth <= 40) return 10;
        if (maxHealth <= 60) return 15;
        if (maxHealth <= 80) return 20;
        return 25;
    }

    /**
     * Get stat breakdown for debugging
     */
    public static String getStatBreakdown(LivingEntity entity) {
        int level = entity.getPersistentData().getInt("AreaLevel");
        double hp = entity.getMaxHealth();

        // Some entities don't have attack_damage attribute (e.g., passive mobs)
        double damage = 0.0;
        if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            damage = entity.getAttributeValue(Attributes.ATTACK_DAMAGE);
        }

        double armor = entity.getAttributeValue(Attributes.ARMOR);
        int variant = entity.getPersistentData().getInt("VisualVariant");

        return String.format("%s [Lv%d]: HP=%.1f, DMG=%.1f, ARM=%.1f, Variant=%d",
            entity.getName().getString(), level, hp, damage, armor, variant);
    }
}
