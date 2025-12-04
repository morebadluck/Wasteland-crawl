package com.wasteland.magic.effects;

import com.wasteland.magic.SpellEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Fireball - Level 5 Fire/Conjurations spell
 * Fires an exploding ball of fire that deals damage in an area
 */
public class FireballEffect implements SpellEffect {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final double EXPLOSION_RADIUS = 3.0;

    @Override
    public boolean execute(Player caster, LivingEntity target, BlockPos targetPos, Level level, int spellPower) {
        Vec3 explosionPos;

        // Determine explosion location
        if (targetPos != null) {
            explosionPos = Vec3.atCenterOf(targetPos);
        } else if (target != null) {
            explosionPos = target.position();
        } else {
            return false;
        }

        // Calculate damage: 3d6 + power/6
        int baseDamage = rollDice(3, 6);
        int powerBonus = spellPower / 6;
        float totalDamage = baseDamage + powerBonus;

        // Create explosion visual and sound
        level.playSound(null, BlockPos.containing(explosionPos),
                       SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS,
                       4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

        if (level instanceof ServerLevel serverLevel) {
            // Explosion particles
            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                explosionPos.x, explosionPos.y + 0.5, explosionPos.z,
                10, 0.5, 0.5, 0.5, 0.1);

            serverLevel.sendParticles(ParticleTypes.FLAME,
                explosionPos.x, explosionPos.y + 0.5, explosionPos.z,
                50, EXPLOSION_RADIUS * 0.5, EXPLOSION_RADIUS * 0.5, EXPLOSION_RADIUS * 0.5, 0.05);

            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                explosionPos.x, explosionPos.y + 0.5, explosionPos.z,
                20, EXPLOSION_RADIUS * 0.4, EXPLOSION_RADIUS * 0.4, EXPLOSION_RADIUS * 0.4, 0.02);
        }

        // Damage all entities in radius
        AABB explosionBox = new AABB(explosionPos.x - EXPLOSION_RADIUS, explosionPos.y - EXPLOSION_RADIUS, explosionPos.z - EXPLOSION_RADIUS,
                                      explosionPos.x + EXPLOSION_RADIUS, explosionPos.y + EXPLOSION_RADIUS, explosionPos.z + EXPLOSION_RADIUS);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, explosionBox);

        int hitCount = 0;
        for (LivingEntity entity : entities) {
            if (entity == caster) {
                continue; // Don't damage caster
            }

            // Calculate distance-based damage falloff
            double distance = entity.position().distanceTo(explosionPos);
            if (distance > EXPLOSION_RADIUS) {
                continue;
            }

            double damageFactor = 1.0 - (distance / EXPLOSION_RADIUS) * 0.5; // 50% falloff at edge
            float actualDamage = (float) (totalDamage * damageFactor);

            entity.hurt(level.damageSources().magic(), actualDamage);

            // Set on fire
            entity.setSecondsOnFire(3);

            hitCount++;
            LOGGER.debug("Fireball hit {} for {} damage (distance: {}, power: {})",
                        entity.getName().getString(), actualDamage, distance, spellPower);
        }

        LOGGER.info("Fireball exploded at {}, hit {} entities", explosionPos, hitCount);

        return true;
    }

    @Override
    public String getDescription() {
        return "Fires an exploding ball of fire. Damage: 3d6 + power/6, 3-block radius";
    }

    @Override
    public boolean canCast(Player caster, LivingEntity target, BlockPos targetPos, Level level) {
        // Requires either a target or a location
        return target != null || targetPos != null;
    }

    @Override
    public String getFailureMessage() {
        return "You need to select a target location!";
    }

    /**
     * Roll dice (e.g., 3d6)
     */
    private int rollDice(int count, int sides) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += 1 + (int) (Math.random() * sides);
        }
        return total;
    }
}
