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
 * Assaultron - Fast, deadly humanoid combat robot
 * Tier 4-5 robot with high speed and powerful melee attacks
 * MEDIUM armor class with emphasis on speed over defense
 * Based on Fallout 4's Assaultron
 */
public class AssaultronEntity extends RobotEntity {

    private int headLaserCooldown = 0;

    public AssaultronEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 50.0D)      // Moderate health
            .add(Attributes.MOVEMENT_SPEED, 0.35D)  // VERY fast for a robot
            .add(Attributes.ATTACK_DAMAGE, 10.0D)   // High melee damage
            .add(Attributes.ARMOR, 6.0D)            // Lower armor (speed over defense)
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D)
            .add(Attributes.FOLLOW_RANGE, 40.0D);   // Long detection range
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Very aggressive - charges at enemies
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4D, false)); // Fast attacks
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Cooldown for head laser
        if (headLaserCooldown > 0) {
            headLaserCooldown--;
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 25% chance to use head laser for bonus damage
            if (this.random.nextFloat() < 0.25F && headLaserCooldown == 0) {
                // Head laser deals extra energy damage
                float laserDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.6F;
                target.hurt(this.damageSources().mobAttack(this), laserDamage);

                // Set target on fire briefly (energy burn)
                target.setSecondsOnFire(2);

                // 5 second cooldown
                headLaserCooldown = 100;
            }
        }
        return hit;
    }
}
