package com.wasteland.magic;

import com.wasteland.character.PlayerCharacter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Represents a spell that can be cast by players.
 * Based on DCSS spell system.
 */
public class Spell {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String id;
    private final String displayName;
    private final String description;
    private final List<SpellSchool> schools;
    private final int spellLevel; // 1-9 in DCSS
    private final int baseMPCost;
    private final TargetType targetType;
    private final SpellEffect effect;
    private final int baseRange;
    private final int basePower;

    /**
     * Builder for spells
     */
    public static class Builder {
        private String id;
        private String displayName;
        private String description = "";
        private final List<SpellSchool> schools = new ArrayList<>();
        private int spellLevel = 1;
        private int baseMPCost = 1;
        private TargetType targetType = TargetType.SINGLE_TARGET;
        private SpellEffect effect;
        private int baseRange = 6; // Default range
        private int basePower = 10;

        public Builder(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public Builder description(String desc) {
            this.description = desc;
            return this;
        }

        public Builder school(SpellSchool school) {
            this.schools.add(school);
            return this;
        }

        public Builder schools(SpellSchool... schools) {
            this.schools.addAll(Arrays.asList(schools));
            return this;
        }

        public Builder level(int level) {
            this.spellLevel = level;
            return this;
        }

        public Builder mpCost(int cost) {
            this.baseMPCost = cost;
            return this;
        }

        public Builder targetType(TargetType type) {
            this.targetType = type;
            return this;
        }

        public Builder effect(SpellEffect effect) {
            this.effect = effect;
            return this;
        }

        public Builder range(int range) {
            this.baseRange = range;
            return this;
        }

        public Builder power(int power) {
            this.basePower = power;
            return this;
        }

        public Spell build() {
            if (effect == null) {
                throw new IllegalStateException("Spell must have an effect");
            }
            if (schools.isEmpty()) {
                throw new IllegalStateException("Spell must have at least one school");
            }
            return new Spell(this);
        }
    }

    private Spell(Builder builder) {
        this.id = builder.id;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.schools = new ArrayList<>(builder.schools);
        this.spellLevel = builder.spellLevel;
        this.baseMPCost = builder.baseMPCost;
        this.targetType = builder.targetType;
        this.effect = builder.effect;
        this.baseRange = builder.baseRange;
        this.basePower = builder.basePower;
    }

    /**
     * Cast the spell
     * @param caster The player casting the spell
     * @param target The targeted entity (may be null)
     * @param targetPos The targeted position (may be null)
     * @param level The world level
     * @return true if spell was cast successfully
     */
    public boolean cast(Player caster, LivingEntity target, BlockPos targetPos, Level level) {
        // Get character
        com.wasteland.character.PlayerCharacter character =
            com.wasteland.character.CharacterManager.getCharacter(caster.getUUID());

        if (character == null) {
            LOGGER.error("Cannot cast spell - character not found");
            return false;
        }

        // Check MP cost
        int mpCost = calculateMPCost(character);
        if (character.getCurrentMP() < mpCost) {
            caster.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cNot enough MP! Need " + mpCost + " MP."),
                true
            );
            return false;
        }

        // Check if spell can be cast
        if (!effect.canCast(caster, target, targetPos, level)) {
            caster.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§c" + effect.getFailureMessage()),
                true
            );
            return false;
        }

        // Calculate spell power
        int spellPower = calculateSpellPower(character);

        // Execute spell effect
        boolean success = effect.execute(caster, target, targetPos, level, spellPower);

        if (success) {
            // Consume MP
            character.consumeMP(mpCost);

            // Grant spell casting XP
            grantCastingXP(character);

            LOGGER.info("Player {} cast {} (MP: {}, Power: {})",
                caster.getName().getString(), displayName, mpCost, spellPower);

            return true;
        } else {
            // Spell failed but still costs MP (reduced)
            int failureCost = mpCost / 2;
            character.consumeMP(failureCost);

            caster.displayClientMessage(
                net.minecraft.network.chat.Component.literal("§cSpell fizzled!"),
                true
            );

            return false;
        }
    }

    /**
     * Calculate MP cost based on character skills
     */
    public int calculateMPCost(PlayerCharacter character) {
        int cost = baseMPCost;

        // Reduce cost based on relevant spell school skills
        int totalSkillReduction = 0;
        for (SpellSchool school : schools) {
            // Get skill for this school (e.g., FIRE -> FIRE_MAGIC skill)
            com.wasteland.character.Skill skill = getSkillForSchool(school);
            if (skill != null) {
                int skillLevel = character.getSkillLevel(skill);
                totalSkillReduction += skillLevel;
            }
        }

        // Reduce cost by 1% per skill level (averaged across schools)
        if (!schools.isEmpty()) {
            int avgSkillLevel = totalSkillReduction / schools.size();
            cost = Math.max(1, cost - (cost * avgSkillLevel / 100));
        }

        // Also factor in Spellcasting skill (general MP cost reduction)
        int spellcasting = character.getSkillLevel(com.wasteland.character.Skill.SPELLCASTING);
        cost = Math.max(1, cost - (spellcasting / 5));

        return cost;
    }

    /**
     * Calculate effective spell power
     */
    public int calculateSpellPower(PlayerCharacter character) {
        int power = basePower;

        // Intelligence bonus
        int intelligence = character.getIntelligence();
        power += (intelligence - 8) * 2; // +2 power per INT above 8

        // Spell school skill bonuses
        int totalSkillBonus = 0;
        for (SpellSchool school : schools) {
            com.wasteland.character.Skill skill = getSkillForSchool(school);
            if (skill != null) {
                int skillLevel = character.getSkillLevel(skill);
                totalSkillBonus += skillLevel * 3; // +3 power per skill level
            }
        }

        if (!schools.isEmpty()) {
            power += totalSkillBonus / schools.size();
        }

        // Spellcasting skill bonus
        int spellcasting = character.getSkillLevel(com.wasteland.character.Skill.SPELLCASTING);
        power += spellcasting * 2;

        return Math.max(1, power);
    }

    /**
     * Grant XP for casting this spell
     */
    private void grantCastingXP(PlayerCharacter character) {
        // Grant XP based on spell level
        int xpGain = spellLevel * 5;

        // Grant to spell schools
        for (SpellSchool school : schools) {
            com.wasteland.character.Skill skill = getSkillForSchool(school);
            if (skill != null) {
                character.trainSkill(skill, xpGain);
            }
        }

        // Also grant Spellcasting XP
        character.trainSkill(com.wasteland.character.Skill.SPELLCASTING, xpGain / 2);
    }

    /**
     * Map spell school to character skill
     */
    private com.wasteland.character.Skill getSkillForSchool(SpellSchool school) {
        return switch (school) {
            case FIRE -> com.wasteland.character.Skill.FIRE_MAGIC;
            case ICE -> com.wasteland.character.Skill.ICE_MAGIC;
            case AIR -> com.wasteland.character.Skill.AIR_MAGIC;
            case EARTH -> com.wasteland.character.Skill.EARTH_MAGIC;
            case POISON -> com.wasteland.character.Skill.POISON_MAGIC;
            case CONJURATIONS -> com.wasteland.character.Skill.CONJURATIONS;
            case HEXES -> com.wasteland.character.Skill.HEXES;
            case SUMMONINGS -> com.wasteland.character.Skill.SUMMONINGS;
            case NECROMANCY -> com.wasteland.character.Skill.NECROMANCY;
            case TRANSLOCATIONS -> com.wasteland.character.Skill.TRANSLOCATIONS;
            case TRANSMUTATIONS -> com.wasteland.character.Skill.TRANSMUTATIONS;
            default -> null;
        };
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<SpellSchool> getSchools() {
        return new ArrayList<>(schools);
    }

    public int getSpellLevel() {
        return spellLevel;
    }

    public int getBaseMPCost() {
        return baseMPCost;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public int getBaseRange() {
        return baseRange;
    }

    public int getBasePower() {
        return basePower;
    }

    /**
     * Get formatted spell info
     */
    public String getFormattedInfo(PlayerCharacter character) {
        StringBuilder sb = new StringBuilder();

        sb.append("§e").append(displayName).append("§r\n");
        sb.append("Level ").append(spellLevel).append(" ");

        // Schools
        for (int i = 0; i < schools.size(); i++) {
            sb.append(schools.get(i).getDisplayName());
            if (i < schools.size() - 1) sb.append("/");
        }
        sb.append("\n");

        if (character != null) {
            sb.append("MP Cost: ").append(calculateMPCost(character)).append("\n");
            sb.append("Power: ").append(calculateSpellPower(character)).append("\n");
        } else {
            sb.append("MP Cost: ").append(baseMPCost).append("\n");
            sb.append("Power: ").append(basePower).append("\n");
        }

        sb.append("Target: ").append(targetType.getDisplayName()).append("\n");
        sb.append("Range: ").append(baseRange).append("\n\n");
        sb.append(description);

        return sb.toString();
    }
}
