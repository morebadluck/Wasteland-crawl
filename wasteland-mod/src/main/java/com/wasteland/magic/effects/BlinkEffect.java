package com.wasteland.magic.effects;

import com.wasteland.magic.SpellEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Blink - Level 2 Translocations spell
 * Randomly teleports the caster a short distance
 */
public class BlinkEffect implements SpellEffect {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MIN_DISTANCE = 2;
    private static final int MAX_DISTANCE = 6;

    @Override
    public boolean execute(Player caster, LivingEntity target, BlockPos targetPos, Level level, int spellPower) {
        Vec3 originalPos = caster.position();

        // Find a valid teleport destination
        BlockPos destination = findBlinkDestination(caster, level, spellPower);

        if (destination == null) {
            caster.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cCouldn't find a safe place to blink!"),
                true
            );
            return false;
        }

        // Particles at origin
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                originalPos.x, originalPos.y + 1, originalPos.z,
                30, 0.5, 0.5, 0.5, 0.5);
        }

        // Teleport
        caster.teleportTo(destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5);

        // Particles at destination
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                caster.getX(), caster.getY() + 1, caster.getZ(),
                30, 0.5, 0.5, 0.5, 0.5);
        }

        // Sound effects
        level.playSound(null, BlockPos.containing(originalPos),
                       SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        level.playSound(null, destination,
                       SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.2F);

        LOGGER.info("Player {} blinked from {} to {}", caster.getName().getString(), originalPos, destination);

        return true;
    }

    @Override
    public String getDescription() {
        return "Randomly teleports you a short distance (2-6 blocks)";
    }

    @Override
    public boolean canCast(Player caster, LivingEntity target, BlockPos targetPos, Level level) {
        // Can always attempt to cast
        return true;
    }

    @Override
    public String getFailureMessage() {
        return "Couldn't find a safe place to blink!";
    }

    /**
     * Find a valid blink destination
     */
    private BlockPos findBlinkDestination(Player caster, Level level, int spellPower) {
        BlockPos startPos = caster.blockPosition();

        // Power increases max distance slightly
        int maxDist = Math.min(MAX_DISTANCE + spellPower / 20, 10);

        // Try multiple random locations
        for (int attempt = 0; attempt < 20; attempt++) {
            // Random angle and distance
            double angle = Math.random() * Math.PI * 2;
            int distance = MIN_DISTANCE + (int) (Math.random() * (maxDist - MIN_DISTANCE));

            int dx = (int) (Math.cos(angle) * distance);
            int dz = (int) (Math.sin(angle) * distance);

            BlockPos candidate = startPos.offset(dx, 0, dz);

            // Find ground level
            for (int dy = -3; dy <= 3; dy++) {
                BlockPos testPos = candidate.offset(0, dy, 0);

                if (isValidBlinkDestination(level, testPos)) {
                    return testPos;
                }
            }
        }

        return null;
    }

    /**
     * Check if position is valid for teleportation
     */
    private boolean isValidBlinkDestination(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockPos head = pos.above();

        // Need solid ground below and air at feet and head
        return !level.isEmptyBlock(below) &&
               level.isEmptyBlock(pos) &&
               level.isEmptyBlock(head);
    }
}
