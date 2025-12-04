package com.wasteland.magic;

import com.wasteland.magic.effects.BlinkEffect;
import com.wasteland.magic.effects.FireballEffect;
import com.wasteland.magic.effects.MagicDartEffect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Initializes and registers all spells in the game
 */
public class Spells {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Register all spells
     */
    public static void registerSpells() {
        LOGGER.info("Registering spells...");

        // Level 1 spells
        registerMagicDart();

        // Level 2 spells
        registerBlink();

        // Level 5 spells
        registerFireball();

        LOGGER.info("Registered {} spells", SpellRegistry.getSpellCount());
    }

    /**
     * Magic Dart - Level 1 Conjurations
     */
    private static void registerMagicDart() {
        Spell magicDart = new Spell.Builder("magic_dart", "Magic Dart")
                .description("Fires a magical dart that never misses")
                .school(SpellSchool.CONJURATIONS)
                .level(1)
                .mpCost(1)
                .power(10)
                .range(6)
                .targetType(TargetType.SINGLE_TARGET)
                .effect(new MagicDartEffect())
                .build();

        SpellRegistry.register(magicDart);
    }

    /**
     * Blink - Level 2 Translocations
     */
    private static void registerBlink() {
        Spell blink = new Spell.Builder("blink", "Blink")
                .description("Randomly teleports you a short distance")
                .school(SpellSchool.TRANSLOCATIONS)
                .level(2)
                .mpCost(2)
                .power(15)
                .range(0) // Self spell
                .targetType(TargetType.SELF)
                .effect(new BlinkEffect())
                .build();

        SpellRegistry.register(blink);
    }

    /**
     * Fireball - Level 5 Fire/Conjurations
     */
    private static void registerFireball() {
        Spell fireball = new Spell.Builder("fireball", "Fireball")
                .description("Fires an exploding ball of fire")
                .schools(SpellSchool.FIRE, SpellSchool.CONJURATIONS)
                .level(5)
                .mpCost(5)
                .power(25)
                .range(8)
                .targetType(TargetType.LOCATION)
                .effect(new FireballEffect())
                .build();

        SpellRegistry.register(fireball);
    }
}
