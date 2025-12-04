package com.wasteland.magic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Interface for spell effects.
 * Defines how a spell affects the world when cast.
 */
public interface SpellEffect {
    /**
     * Execute the spell effect
     * @param caster The player casting the spell
     * @param target The targeted entity (may be null for location/self spells)
     * @param targetPos The targeted position (may be null for entity-targeted spells)
     * @param level The world level
     * @param spellPower The effective spell power
     * @return true if the spell effect succeeded, false otherwise
     */
    boolean execute(Player caster, LivingEntity target, BlockPos targetPos, Level level, int spellPower);

    /**
     * Get a description of what this effect does
     */
    String getDescription();

    /**
     * Can this effect be cast in the current context?
     * @param caster The player attempting to cast
     * @param target The targeted entity (may be null)
     * @param targetPos The targeted position (may be null)
     * @param level The world level
     * @return true if the spell can be cast, false otherwise
     */
    default boolean canCast(Player caster, LivingEntity target, BlockPos targetPos, Level level) {
        return true; // By default, all spells can be cast
    }

    /**
     * Get message to display when spell cannot be cast
     */
    default String getFailureMessage() {
        return "You cannot cast this spell right now.";
    }
}
