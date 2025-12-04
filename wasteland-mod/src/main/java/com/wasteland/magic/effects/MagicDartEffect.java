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
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Magic Dart - Level 1 Conjurations spell
 * Fires a magical dart that never misses and deals reliable damage
 */
public class MagicDartEffect implements SpellEffect {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public boolean execute(Player caster, LivingEntity target, BlockPos targetPos, Level level, int spellPower) {
        if (target == null || !target.isAlive()) {
            return false;
        }

        // Calculate damage: 3d4 + power/10
        int baseDamage = rollDice(3, 4);
        int powerBonus = spellPower / 10;
        float totalDamage = baseDamage + powerBonus;

        // Apply damage
        target.hurt(level.damageSources().magic(), totalDamage);

        // Visual and sound effects
        if (level instanceof ServerLevel serverLevel) {
            // Particle trail from caster to target
            spawnParticleTrail(serverLevel, caster, target);
        }

        level.playSound(null, target.blockPosition(), SoundEvents.EVOKER_CAST_SPELL,
                       SoundSource.PLAYERS, 1.0F, 1.2F);

        LOGGER.debug("Magic Dart hit {} for {} damage (power: {})",
                    target.getName().getString(), totalDamage, spellPower);

        return true;
    }

    @Override
    public String getDescription() {
        return "Fires a magical dart that never misses. Damage: 3d4 + power/10";
    }

    @Override
    public boolean canCast(Player caster, LivingEntity target, BlockPos targetPos, Level level) {
        // Requires a valid target
        return target != null && target.isAlive();
    }

    @Override
    public String getFailureMessage() {
        return "You need a valid target!";
    }

    /**
     * Roll dice (e.g., 3d4)
     */
    private int rollDice(int count, int sides) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += 1 + (int) (Math.random() * sides);
        }
        return total;
    }

    /**
     * Spawn particle trail from caster to target
     */
    private void spawnParticleTrail(ServerLevel level, Player caster, LivingEntity target) {
        // Create a line of particles from caster to target
        double steps = 10;
        for (int i = 0; i <= steps; i++) {
            double ratio = i / steps;
            double x = caster.getX() + (target.getX() - caster.getX()) * ratio;
            double y = caster.getEyeY() + (target.getEyeY() - caster.getEyeY()) * ratio;
            double z = caster.getZ() + (target.getZ() - caster.getZ()) * ratio;

            level.sendParticles(ParticleTypes.WITCH,
                x, y, z,
                1, 0, 0, 0, 0.01);
        }
    }
}
