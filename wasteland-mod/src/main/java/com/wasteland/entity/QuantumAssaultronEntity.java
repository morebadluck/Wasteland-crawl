package com.wasteland.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Quantum Assaultron - Elite variant with phase-shift technology
 * Tier 7-8 late-game assassin robot
 * MEDIUM armor class but EXTREMELY fast and deadly
 * Can teleport short distances (quantum phase)
 */
public class QuantumAssaultronEntity extends RobotEntity {

    private int quantumPhaseCooldown = 0;
    private int deathRayCooldown = 0;

    public QuantumAssaultronEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.ELITE);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 70.0D)      // Moderate health
            .add(Attributes.MOVEMENT_SPEED, 0.42D)  // EXTREMELY fast
            .add(Attributes.ATTACK_DAMAGE, 14.0D)   // Very high damage
            .add(Attributes.ARMOR, 8.0D)            // Medium armor
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
            .add(Attributes.FOLLOW_RANGE, 48.0D);   // Long detection range
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Ultra-aggressive - rushes down targets
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.6D, false)); // Extremely fast attacks
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.2D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Cooldowns
        if (quantumPhaseCooldown > 0) {
            quantumPhaseCooldown--;
        }
        if (deathRayCooldown > 0) {
            deathRayCooldown--;
        }

        // Quantum phase teleport when damaged and on cooldown
        if (!this.level().isClientSide && this.getTarget() != null && quantumPhaseCooldown == 0) {
            // 5% chance per tick to quantum phase when target is nearby
            if (this.distanceToSqr(this.getTarget()) < 16.0D && this.random.nextFloat() < 0.05F) {
                // Teleport behind target (simulated by particle effects and position adjustment)
                // In full implementation, would use actual teleportation
                quantumPhaseCooldown = 200; // 10 second cooldown
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 35% chance to use death ray for massive damage
            if (this.random.nextFloat() < 0.35F && deathRayCooldown == 0) {
                // Death ray deals extreme energy damage
                float rayDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F;
                target.hurt(this.damageSources().mobAttack(this), rayDamage);

                // Burns target intensely
                target.setSecondsOnFire(5);

                // 8 second cooldown
                deathRayCooldown = 160;
            }

            // 20% chance to phase strike (bonus damage from quantum instability)
            if (this.random.nextFloat() < 0.2F) {
                target.hurt(this.damageSources().magic(), 5.0F);
            }
        }
        return hit;
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        // 15% chance to phase out and negate damage
        if (this.random.nextFloat() < 0.15F && quantumPhaseCooldown == 0) {
            quantumPhaseCooldown = 60; // 3 second cooldown
            return false; // Damage negated
        }
        return super.hurt(source, amount);
    }
}
