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
 * Eyebot - Early game surveillance robot (Fallout reference)
 *
 * Characteristics:
 * - LIGHT armor class (no bullet deflection)
 * - Low health and damage
 * - Fast movement
 * - Calls for help when damaged (alerts nearby robots)
 * - No self-destruct
 *
 * Tier: Early Game (1-2)
 * Fallout Reference: Small floating surveillance drones
 */
public class EyebotEntity extends RobotEntity {

    public EyebotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.LIGHT, false, com.wasteland.loot.RobotLoot.RobotTier.LIGHT);
    }

    /**
     * Create Eyebot attributes - weak but fast
     */
    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 15.0D)      // Low health (1.5 hearts)
            .add(Attributes.MOVEMENT_SPEED, 0.35D)  // Fast (flies)
            .add(Attributes.ATTACK_DAMAGE, 2.0D)    // Weak attack
            .add(Attributes.ARMOR, 0.0D)            // No armor (LIGHT class)
            .add(Attributes.FOLLOW_RANGE, 40.0D);   // Long detection range
    }

    @Override
    protected void registerGoals() {
        // Eyebots flee when low health
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 6.0F, 1.2D, 1.5D,
            (entity) -> this.getHealth() < this.getMaxHealth() * 0.5));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        // Aggressive targeting
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers()); // Calls for help
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Eyebots can fly (immune to fall damage)
     */
    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, net.minecraft.world.damagesource.DamageSource damageSource) {
        return false;
    }
}
