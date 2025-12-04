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
 * Overlord Bot - Boss-tier command robot
 * Tier 9+ ultra-rare boss encounter
 * HEAVY armor class with command abilities
 * Summons other robots and coordinates attacks
 * Does NOT self-destruct (too valuable)
 */
public class OverlordBotEntity extends RobotEntity {

    private int commandPulseCooldown = 0;
    private int orbitalStrikeCooldown = 0;
    private boolean enraged = false;

    public OverlordBotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.HEAVY, false, com.wasteland.loot.RobotLoot.RobotTier.BOSS); // Boss doesn't self-destruct
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 200.0D)     // Boss-tier health
            .add(Attributes.MOVEMENT_SPEED, 0.18D)  // Moderate speed
            .add(Attributes.ATTACK_DAMAGE, 20.0D)   // Devastating damage
            .add(Attributes.ARMOR, 25.0D)           // Maximum armor
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D) // Immune to knockback
            .add(Attributes.FOLLOW_RANGE, 64.0D);   // Massive detection range
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Boss behavior - strategic and commanding
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, true));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Alerts ALL robots when attacked
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Check for enrage at 50% health
        if (!enraged && this.getHealth() < this.getMaxHealth() * 0.5F) {
            enraged = true;
            // Increase movement speed when enraged
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        }

        // Cooldowns
        if (commandPulseCooldown > 0) {
            commandPulseCooldown--;
        }
        if (orbitalStrikeCooldown > 0) {
            orbitalStrikeCooldown--;
        }

        // Command pulse - buffs nearby robots
        if (!this.level().isClientSide && commandPulseCooldown == 0) {
            // Every 15 seconds, command pulse
            if (this.random.nextFloat() < 0.05F) {
                // TODO: In full implementation, buff nearby robots
                // For now, just set cooldown
                commandPulseCooldown = 300; // 15 seconds
            }
        }

        // Orbital strike at range
        if (!this.level().isClientSide && this.getTarget() != null && orbitalStrikeCooldown == 0) {
            double distance = this.distanceToSqr(this.getTarget());

            // If target is 10-30 blocks away, 3% chance for orbital strike
            if (distance > 100.0D && distance < 900.0D && this.random.nextFloat() < 0.03F) {
                // Orbital strike deals massive explosive damage
                this.getTarget().hurt(this.damageSources().explosion(this, this), 20.0F);
                this.getTarget().setSecondsOnFire(5);

                // 20 second cooldown
                orbitalStrikeCooldown = 400;
            }
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 50% chance for plasma cannon burst
            if (this.random.nextFloat() < 0.5F) {
                float plasmaDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.7F;
                target.hurt(this.damageSources().mobAttack(this), plasmaDamage);
                target.setSecondsOnFire(4);
            }

            // 30% chance to knock back with force
            if (this.random.nextFloat() < 0.3F) {
                // Massive knockback effect
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                target.push(dx * 0.5, 0.4, dz * 0.5);
            }
        }
        return hit;
    }

    @Override
    public boolean fireImmune() {
        // Boss is immune to fire
        return true;
    }

    @Override
    public int getExperienceReward() {
        // Boss drops massive XP
        return 100;
    }
}
