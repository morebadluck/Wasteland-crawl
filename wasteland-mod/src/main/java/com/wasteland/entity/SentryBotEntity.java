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
 * Sentry Bot - Heavy combat robot (Fallout reference)
 *
 * Characteristics:
 * - HEAVY armor class (60% bullet deflection, 75% damage reduction, 20% reflect)
 * - High health and damage
 * - Slow but powerful
 * - Self-destruct explosion on death (3.0 explosion radius)
 * - Minigun/missile attacks
 *
 * Tier: Mid-Late Game (3-5)
 * Fallout Reference: Heavy military combat robots with devastating firepower
 */
public class SentryBotEntity extends RobotEntity {

    public SentryBotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.HEAVY, true, com.wasteland.loot.RobotLoot.RobotTier.HEAVY); // CAN self-destruct
    }

    /**
     * Create Sentry Bot attributes - tank-like combat robot
     */
    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 80.0D)      // Very high health (8 hearts)
            .add(Attributes.MOVEMENT_SPEED, 0.15D)  // Very slow
            .add(Attributes.ATTACK_DAMAGE, 12.0D)   // High damage
            .add(Attributes.ARMOR, 15.0D)           // Very high armor (HEAVY class)
            .add(Attributes.FOLLOW_RANGE, 35.0D)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D) // Immune to knockback
            .add(Attributes.ATTACK_KNOCKBACK, 2.0D);    // Strong knockback on hit
    }

    @Override
    protected void registerGoals() {
        // Sentry bots are relentless and never flee
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.8D, true)); // Slower attack
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Aggressive targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Sentry Bots have a chance to deal extra damage (minigun burst)
     */
    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);

        if (hit) {
            // 20% chance for minigun burst (double damage tick)
            if (this.random.nextFloat() < 0.2F) {
                target.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F);
            }
        }

        return hit;
    }
}
