package com.wasteland.entity;

import com.wasteland.loot.RobotLoot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all robot enemies in the Wasteland mod.
 * Implements Fallout-style robot mechanics with resistance/vulnerability system.
 *
 * Robot Properties:
 * - IMMUNE: Poison, Bleeding, Disease, Drowning
 * - RESISTANT: Physical damage (25-50%), Fire (25%)
 * - VULNERABLE: Electrical damage (150-200%)
 * - SPECIAL: Bullet deflection (varies by armor class)
 */
public abstract class RobotEntity extends Monster {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Robot armor class determines bullet deflection and physical resistance
     */
    public enum RobotArmorClass {
        LIGHT(0.0, 0.0, 0.25),      // No deflection, 25% physical resistance
        MEDIUM(0.30, 0.50, 0.35),   // 30% deflection, 50% damage reduction, 35% physical resistance
        HEAVY(0.60, 0.75, 0.50);    // 60% deflection, 75% damage reduction, 50% physical resistance

        private final double deflectionChance;
        private final double damageReduction;
        private final double physicalResistance;

        RobotArmorClass(double deflectionChance, double damageReduction, double physicalResistance) {
            this.deflectionChance = deflectionChance;
            this.damageReduction = damageReduction;
            this.physicalResistance = physicalResistance;
        }

        public double getDeflectionChance() {
            return deflectionChance;
        }

        public double getDamageReduction() {
            return damageReduction;
        }

        public double getPhysicalResistance() {
            return physicalResistance;
        }
    }

    protected final RobotArmorClass armorClass;
    protected final boolean canSelfDestruct;
    protected final RobotLoot.RobotTier lootTier;

    public RobotEntity(EntityType<? extends Monster> entityType, Level level, RobotArmorClass armorClass, boolean canSelfDestruct, RobotLoot.RobotTier lootTier) {
        super(entityType, level);
        this.armorClass = armorClass;
        this.canSelfDestruct = canSelfDestruct;
        this.lootTier = lootTier;
    }

    /**
     * Register AI goals for robot behavior
     */
    @Override
    protected void registerGoals() {
        // Base AI goals for robots
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        // Target selection
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Base robot attributes
     */
    public static AttributeSupplier.Builder createRobotAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.FOLLOW_RANGE, 35.0D)
            .add(Attributes.MOVEMENT_SPEED, 0.23D)
            .add(Attributes.ATTACK_DAMAGE, 3.0D)
            .add(Attributes.ARMOR, 2.0D);
    }

    /**
     * Handle incoming damage with robot resistance/vulnerability mechanics
     */
    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        float modifiedDamage = amount;

        // Check damage type and apply modifiers
        String damageType = source.getMsgId();

        // VULNERABILITY: Electrical damage (150-200%)
        if (isElectricalDamage(damageType)) {
            modifiedDamage *= 1.5 + (this.random.nextFloat() * 0.5); // 150-200%
            LOGGER.debug("{} takes {}% electrical damage", this.getName().getString(), (modifiedDamage / amount) * 100);
        }
        // RESISTANCE: Fire damage (25%)
        else if (damageType.contains("fire") || damageType.contains("lava") || damageType.contains("onFire")) {
            modifiedDamage *= 0.75; // 25% resistance
            LOGGER.debug("{} resists {}% fire damage", this.getName().getString(), 25);
        }
        // RESISTANCE: Physical damage (varies by armor class)
        else if (isPhysicalDamage(damageType)) {
            // Check for bullet deflection
            if (source.getEntity() instanceof Player && isBulletDamage(damageType)) {
                if (this.random.nextDouble() < armorClass.getDeflectionChance()) {
                    // Bullet deflected
                    modifiedDamage *= (1.0 - armorClass.getDamageReduction());
                    LOGGER.debug("{} deflected bullet, reduced damage by {}%",
                        this.getName().getString(), armorClass.getDamageReduction() * 100);

                    // Heavy robots have chance to reflect back
                    if (armorClass == RobotArmorClass.HEAVY && this.random.nextDouble() < 0.20) {
                        // TODO: Implement ricochet damage back to player
                        LOGGER.info("{} reflected bullet back at attacker!", this.getName().getString());
                    }
                }
            }

            // Apply physical resistance
            modifiedDamage *= (1.0 - armorClass.getPhysicalResistance());
        }

        return super.hurt(source, modifiedDamage);
    }

    /**
     * Handle robot death
     */
    @Override
    public void die(DamageSource damageSource) {
        // Drop loot before death
        if (!this.level().isClientSide) {
            RobotLoot.dropLoot(this.level(), this.blockPosition(), lootTier);
        }

        // Self-destruct explosion (only for sentry bots)
        if (canSelfDestruct && !this.level().isClientSide) {
            LOGGER.info("{} self-destructing!", this.getName().getString());
            // Create explosion
            this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                3.0F, Level.ExplosionInteraction.MOB);
        }

        super.die(damageSource);
    }

    /**
     * Robots are immune to drowning
     */
    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    /**
     * Robots don't need air
     */
    @Override
    protected int decreaseAirSupply(int air) {
        return air; // Never decrease
    }

    /**
     * Check if damage type is electrical
     */
    private boolean isElectricalDamage(String damageType) {
        return damageType.contains("lightning") ||
               damageType.contains("electric") ||
               damageType.contains("shock") ||
               damageType.contains("emp");
    }

    /**
     * Check if damage type is physical
     */
    private boolean isPhysicalDamage(String damageType) {
        return damageType.equals("mob") ||
               damageType.equals("player") ||
               damageType.contains("arrow") ||
               damageType.contains("bullet") ||
               damageType.contains("projectile");
    }

    /**
     * Check if damage is from bullets/guns
     */
    private boolean isBulletDamage(String damageType) {
        return damageType.contains("bullet") ||
               damageType.contains("gun") ||
               damageType.contains("arrow") ||
               damageType.contains("projectile");
    }

    /**
     * Get robot armor class
     */
    public RobotArmorClass getArmorClass() {
        return armorClass;
    }

    /**
     * Check if this robot can self-destruct
     */
    public boolean canSelfDestruct() {
        return canSelfDestruct;
    }
}
