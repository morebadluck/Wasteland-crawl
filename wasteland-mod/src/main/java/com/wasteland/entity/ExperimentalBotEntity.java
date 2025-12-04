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
 * Experimental Bot - Prototype with unstable technology
 * Tier 6-7 rare encounter with unpredictable abilities
 * MEDIUM armor class with random effects
 * Can overload and explode (like mini-nuke)
 * Based on failed military experiments
 */
public class ExperimentalBotEntity extends RobotEntity {

    private int overloadCharge = 0;
    private boolean isOverloading = false;
    private int randomAbilityCooldown = 0;

    public ExperimentalBotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.HEAVY);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 60.0D)      // Moderate health
            .add(Attributes.MOVEMENT_SPEED, 0.28D)  // Good speed
            .add(Attributes.ATTACK_DAMAGE, 12.0D)   // High damage (unstable)
            .add(Attributes.ARMOR, 10.0D)           // Good armor
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
            .add(Attributes.FOLLOW_RANGE, 40.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Erratic behavior - unpredictable
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Cooldown
        if (randomAbilityCooldown > 0) {
            randomAbilityCooldown--;
        }

        // Overload mechanic - builds up when taking damage
        if (isOverloading) {
            overloadCharge++;

            // After 5 seconds of overload, explode
            if (overloadCharge >= 100) {
                this.explode();
                this.kill(); // Kill self after explosion
                return;
            }
        }

        // Random experimental ability trigger
        if (!this.level().isClientSide && this.getTarget() != null && randomAbilityCooldown == 0) {
            if (this.random.nextFloat() < 0.03F) {
                int ability = this.random.nextInt(4);

                switch (ability) {
                    case 0: // Energy discharge
                        this.getTarget().hurt(this.damageSources().magic(), 8.0F);
                        break;
                    case 1: // Healing pulse (self-repair)
                        this.heal(10.0F);
                        break;
                    case 2: // Speed boost
                        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.40D);
                        break;
                    case 3: // Damage boost
                        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(18.0D);
                        break;
                }

                randomAbilityCooldown = 200; // 10 second cooldown
            }
        }
    }

    @Override
    public boolean hurt(net.minecraft.world.damagesource.DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);

        // 20% chance to start overloading when damaged
        if (result && !isOverloading && this.random.nextFloat() < 0.2F) {
            isOverloading = true;
            overloadCharge = 0;
        }

        return result;
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // Random effect on hit
            int effect = this.random.nextInt(5);

            switch (effect) {
                case 0: // Electric shock
                    target.hurt(this.damageSources().magic(), 4.0F);
                    break;
                case 1: // Fire burst
                    target.setSecondsOnFire(3);
                    break;
                case 2: // Toxic leak
                    // Would apply poison effect in full implementation
                    target.hurt(this.damageSources().magic(), 2.0F);
                    break;
                case 3: // Cryo blast
                    // Would apply slowness in full implementation
                    break;
                case 4: // Nothing (malfunction)
                    break;
            }
        }
        return hit;
    }

    private void explode() {
        if (!this.level().isClientSide) {
            // Massive experimental explosion (bigger than Sentry Bot)
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                5.0F, Level.ExplosionInteraction.MOB);
        }
    }

    @Override
    public void die(net.minecraft.world.damagesource.DamageSource damageSource) {
        super.die(damageSource);

        // 30% chance to explode on death (unstable core)
        if (!this.level().isClientSide && this.random.nextFloat() < 0.3F) {
            this.explode();
        }
    }
}
