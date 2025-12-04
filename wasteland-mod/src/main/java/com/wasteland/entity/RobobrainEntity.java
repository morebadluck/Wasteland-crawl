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
 * Robobrain - Human brain in a robotic chassis
 * Tier 5 robot with psionic abilities and high intelligence
 * MEDIUM armor class with special mental attacks
 * Based on Fallout's disturbing Robobrain design
 */
public class RobobrainEntity extends RobotEntity {

    private int psionicPulseCooldown = 0;

    public RobobrainEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 55.0D)      // Good health
            .add(Attributes.MOVEMENT_SPEED, 0.20D)  // Slow movement
            .add(Attributes.ATTACK_DAMAGE, 7.0D)    // Moderate physical damage
            .add(Attributes.ARMOR, 8.0D)            // Decent armor
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.7D)
            .add(Attributes.FOLLOW_RANGE, 48.0D);   // Very high detection (psionic sense)
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Tactical intelligence - keeps distance when possible
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.9D, false));

        // Occasionally backs away to use ranged attacks
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.0D, 1.2D,
            (entity) -> this.psionicPulseCooldown == 0 && this.random.nextFloat() < 0.3F));

        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Intelligent targeting - alerts all nearby robots
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Cooldown for psionic pulse
        if (psionicPulseCooldown > 0) {
            psionicPulseCooldown--;
        }

        // Use psionic pulse on nearby targets
        if (!this.level().isClientSide && this.getTarget() != null && psionicPulseCooldown == 0) {
            double distance = this.distanceToSqr(this.getTarget());

            // If target is within 10 blocks, 10% chance to use psionic pulse
            if (distance < 100.0D && this.random.nextFloat() < 0.1F) {
                // Psionic pulse deals direct damage (bypasses armor)
                this.getTarget().hurt(this.damageSources().magic(), 5.0F);

                // 8 second cooldown
                psionicPulseCooldown = 160;
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 30% chance for psionic feedback (confusion-like effect)
            if (this.random.nextFloat() < 0.3F) {
                // Deal bonus magic damage
                target.hurt(this.damageSources().magic(), 3.0F);
            }
        }
        return hit;
    }
}
