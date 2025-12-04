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
 * Annihilator Sentry Bot - Elite variant of Sentry Bot
 * Tier 7+ late-game heavy combat robot
 * HEAVY armor class with massive firepower
 * Self-destructs like regular Sentry Bot
 */
public class AnnihilatorSentryBotEntity extends RobotEntity {

    private int missileCooldown = 0;

    public AnnihilatorSentryBotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.HEAVY, true, com.wasteland.loot.RobotLoot.RobotTier.ELITE); // CAN self-destruct
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 120.0D)     // Massive health
            .add(Attributes.MOVEMENT_SPEED, 0.12D)  // Very slow
            .add(Attributes.ATTACK_DAMAGE, 18.0D)   // Devastating damage
            .add(Attributes.ARMOR, 20.0D)           // Extremely heavy armor
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D) // Immune to knockback
            .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Relentless assault - never backs down
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true)); // Always tracking
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Cooldown for missile barrage
        if (missileCooldown > 0) {
            missileCooldown--;
        }

        // Missile barrage at range
        if (!this.level().isClientSide && this.getTarget() != null && missileCooldown == 0) {
            double distance = this.distanceToSqr(this.getTarget());

            // If target is 8-20 blocks away, 5% chance to launch missiles
            if (distance > 64.0D && distance < 400.0D && this.random.nextFloat() < 0.05F) {
                // Missile barrage deals area damage
                this.getTarget().hurt(this.damageSources().explosion(this, this), 12.0F);
                this.getTarget().setSecondsOnFire(3);

                // 12 second cooldown
                missileCooldown = 240;
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 40% chance for minigun burst (higher than regular Sentry Bot)
            if (this.random.nextFloat() < 0.4F) {
                float burstDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6F;
                target.hurt(this.damageSources().mobAttack(this), burstDamage);
            }

            // 20% chance to stun with massive impact
            if (this.random.nextFloat() < 0.2F) {
                target.hurt(this.damageSources().mobAttack(this), 3.0F);
            }
        }
        return hit;
    }

    @Override
    public boolean fireImmune() {
        // Annihilator is immune to fire
        return true;
    }
}
