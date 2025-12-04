package com.wasteland.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

/**
 * Protectron - Early game security robot (Fallout reference)
 *
 * Characteristics:
 * - MEDIUM armor class (30% bullet deflection, 50% damage reduction)
 * - Moderate health and damage
 * - Slow movement (lumbering gait)
 * - Basic security programming
 * - No self-destruct
 *
 * Tier: Early Game (1-2)
 * Fallout Reference: Basic bipedal security robots, slow but durable
 */
public class ProtectronEntity extends RobotEntity {

    public ProtectronEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.MEDIUM);
    }

    /**
     * Create Protectron attributes - durable but slow
     */
    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 30.0D)      // Moderate health (3 hearts)
            .add(Attributes.MOVEMENT_SPEED, 0.18D)  // Slow movement
            .add(Attributes.ATTACK_DAMAGE, 5.0D)    // Moderate damage
            .add(Attributes.ARMOR, 6.0D)            // Good armor (MEDIUM class)
            .add(Attributes.FOLLOW_RANGE, 25.0D)    // Standard detection
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D); // Hard to knock back
    }
}
