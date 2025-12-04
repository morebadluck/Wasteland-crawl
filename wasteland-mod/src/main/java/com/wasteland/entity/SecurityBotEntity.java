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
 * Security Bot - Improved Protectron variant for mid-game
 * Tier 3-4 robot with better speed and coordination
 * MEDIUM armor class with 30% bullet deflection
 */
public class SecurityBotEntity extends RobotEntity {

    public SecurityBotEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.MEDIUM);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createRobotAttributes()
            .add(Attributes.MAX_HEALTH, 40.0D)      // More HP than Protectron
            .add(Attributes.MOVEMENT_SPEED, 0.23D)  // Faster than Protectron (0.18)
            .add(Attributes.ATTACK_DAMAGE, 6.0D)    // Slightly stronger
            .add(Attributes.ARMOR, 8.0D)            // Better armor than Protectron
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
            .add(Attributes.FOLLOW_RANGE, 32.0D);   // Better detection range
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // More aggressive than Protectron
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Better coordination - alerts nearby robots
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(SecurityBotEntity.class, ProtectronEntity.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit) {
            // 15% chance to stun target briefly (represents taser function)
            if (this.random.nextFloat() < 0.15F) {
                target.hurt(this.damageSources().mobAttack(this), 1.0F);
                // Note: Actual stun/slowness effect would require status effect implementation
            }
        }
        return hit;
    }
}
