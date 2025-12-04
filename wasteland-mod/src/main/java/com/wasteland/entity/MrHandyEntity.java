package com.wasteland.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

/**
 * Mr. Handy - Early game utility robot (Fallout reference)
 *
 * Characteristics:
 * - LIGHT armor class (no bullet deflection)
 * - Moderate health, low-moderate damage
 * - Multiple attack types (buzz saw + flamer)
 * - Floats/hovers (no fall damage)
 * - Fire damage on attacks
 * - No self-destruct
 *
 * Tier: Early Game (1-2)
 * Fallout Reference: Pre-war utility robots with multiple arms and thrusters
 */
public class MrHandyEntity extends RobotEntity {

    public MrHandyEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.LIGHT, false, com.wasteland.loot.RobotLoot.RobotTier.LIGHT);
    }

    /**
     * Create Mr. Handy attributes - versatile utility robot
     */
    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 25.0D)      // Moderate health
            .add(Attributes.MOVEMENT_SPEED, 0.25D)  // Moderate speed (hovers)
            .add(Attributes.ATTACK_DAMAGE, 4.0D)    // Moderate damage
            .add(Attributes.ARMOR, 2.0D)            // Light armor (LIGHT class)
            .add(Attributes.FOLLOW_RANGE, 30.0D);
    }

    /**
     * Mr. Handy attacks have a chance to deal fire damage
     */
    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit) {
            // 30% chance to set target on fire (flamer arm)
            if (this.random.nextFloat() < 0.3F) {
                target.setSecondsOnFire(3);
            }
        }

        return hit;
    }

    /**
     * Mr. Handy can hover (immune to fall damage)
     */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }
}
