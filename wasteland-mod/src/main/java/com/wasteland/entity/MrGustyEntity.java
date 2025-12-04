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
 * Mr. Gutsy - Military variant of Mr. Handy
 * Tier 4 floating combat robot with flamers and ballistic weapons
 * MEDIUM armor class with fire immunity
 * Based on Fallout's Mr. Gutsy military robot
 */
public class MrGustyEntity extends RobotEntity {

    public MrGustyEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 45.0D)      // Tougher than Mr. Handy
            .add(Attributes.MOVEMENT_SPEED, 0.25D)  // Moderate speed (floats)
            .add(Attributes.ATTACK_DAMAGE, 8.0D)    // Solid damage
            .add(Attributes.ARMOR, 7.0D)            // Military-grade plating
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
            .add(Attributes.FOLLOW_RANGE, 36.0D);   // Good detection
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // Military tactics - more coordinated than Mr. Handy
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Alerts other military robots
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(MrGustyEntity.class, SecurityBotEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 50% chance to use flamer (higher than Mr. Handy's 30%)
            if (this.random.nextFloat() < 0.5F) {
                target.setSecondsOnFire(4);
            }

            // 20% chance for machine gun burst (bonus damage)
            if (this.random.nextFloat() < 0.2F) {
                float burstDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.4F;
                target.hurt(this.damageSources().mobAttack(this), burstDamage);
            }
        }
        return hit;
    }

    @Override
    public boolean fireImmune() {
        // Mr. Gutsy is immune to fire (military flamer unit)
        return true;
    }
}
