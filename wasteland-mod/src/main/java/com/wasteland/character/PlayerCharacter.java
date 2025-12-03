package com.wasteland.character;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Represents a player character with DCSS-style attributes
 * Tracks race, skills, spells, and progression
 */
public class PlayerCharacter {
    private static final Logger LOGGER = LogManager.getLogger();

    // Core attributes
    private final UUID playerId;
    private Race race;
    private String characterName;

    // Skills: Skill -> current level (0-27 in DCSS)
    private final Map<Skill, Integer> skillLevels;
    private final Map<Skill, Double> skillProgress; // XP progress toward next level (0.0-1.0)

    // Spells: Known spells
    private final Set<Spell> knownSpells;
    private final List<Spell> memorizedSpells; // Limited slots (based on XL and INT)

    // Experience
    private int experienceLevel; // Character level (1-27)
    private int totalXP;

    // Stats (affected by race)
    private int maxHP;
    private int currentHP;
    private int maxMP;
    private int currentMP;

    /**
     * Create a new character
     */
    public PlayerCharacter(UUID playerId, Race race, String characterName) {
        this.playerId = playerId;
        this.race = race;
        this.characterName = characterName;

        this.skillLevels = new HashMap<>();
        this.skillProgress = new HashMap<>();
        this.knownSpells = new HashSet<>();
        this.memorizedSpells = new ArrayList<>();

        // Initialize all skills to 0
        for (Skill skill : Skill.values()) {
            skillLevels.put(skill, 0);
            skillProgress.put(skill, 0.0);
        }

        // Starting stats
        this.experienceLevel = 1;
        this.totalXP = 0;
        this.maxHP = 15 + race.getHpModifier() * 2;
        this.currentHP = maxHP;
        this.maxMP = 5 + race.getMpModifier() * 2;
        this.currentMP = maxMP;

        LOGGER.info("Created new character: {} ({}) for player {}",
                   characterName, race.getDisplayName(), playerId);
    }

    // ===== Getters =====

    public UUID getPlayerId() {
        return playerId;
    }

    public Race getRace() {
        return race;
    }

    public String getCharacterName() {
        return characterName;
    }

    public int getExperienceLevel() {
        return experienceLevel;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public int getMaxHP() {
        return maxHP;
    }

    public int getCurrentHP() {
        return currentHP;
    }

    public int getMaxMP() {
        return maxMP;
    }

    public int getCurrentMP() {
        return currentMP;
    }

    public int getSkillLevel(Skill skill) {
        return skillLevels.getOrDefault(skill, 0);
    }

    public double getSkillProgress(Skill skill) {
        return skillProgress.getOrDefault(skill, 0.0);
    }

    public Set<Spell> getKnownSpells() {
        return new HashSet<>(knownSpells);
    }

    public List<Spell> getMemorizedSpells() {
        return new ArrayList<>(memorizedSpells);
    }

    // ===== Skill Training =====

    /**
     * Train a skill by adding XP
     * Takes aptitude into account
     */
    public void trainSkill(Skill skill, int xp) {
        int currentLevel = skillLevels.get(skill);
        if (currentLevel >= 27) {
            LOGGER.debug("Skill {} already at max level (27)", skill.getDisplayName());
            return;
        }

        // Apply aptitude modifier
        int aptitude = Aptitudes.getAptitude(race, skill);
        double aptitudeMultiplier = 1.0 + (aptitude * 0.1); // Each aptitude point = 10% faster/slower
        double effectiveXP = xp * aptitudeMultiplier;

        // Add to progress
        double progress = skillProgress.get(skill);
        progress += effectiveXP / 100.0; // Simplified: 100 XP per level

        // Level up if necessary
        while (progress >= 1.0 && currentLevel < 27) {
            progress -= 1.0;
            currentLevel++;
            skillLevels.put(skill, currentLevel);
            LOGGER.info("Skill improved: {} is now level {}!", skill.getDisplayName(), currentLevel);

            // Update derived stats
            updateDerivedStats();
        }

        skillProgress.put(skill, progress);
    }

    /**
     * Set skill level directly (for testing/debugging)
     */
    public void setSkillLevel(Skill skill, int level) {
        level = Math.max(0, Math.min(27, level));
        skillLevels.put(skill, level);
        skillProgress.put(skill, 0.0);
        updateDerivedStats();
    }

    // ===== Spell Management =====

    /**
     * Learn a new spell (add to spellbook)
     */
    public boolean learnSpell(Spell spell) {
        if (knownSpells.contains(spell)) {
            LOGGER.debug("Already know spell: {}", spell.getDisplayName());
            return false;
        }

        knownSpells.add(spell);
        LOGGER.info("Learned new spell: {}!", spell.getDisplayName());
        return true;
    }

    /**
     * Memorize a spell (add to active spell slots)
     */
    public boolean memorizeSpell(Spell spell) {
        if (!knownSpells.contains(spell)) {
            LOGGER.warn("Cannot memorize unknown spell: {}", spell.getDisplayName());
            return false;
        }

        if (memorizedSpells.contains(spell)) {
            LOGGER.debug("Spell already memorized: {}", spell.getDisplayName());
            return false;
        }

        if (memorizedSpells.size() >= getMaxSpellSlots()) {
            LOGGER.warn("No more spell slots available (max: {})", getMaxSpellSlots());
            return false;
        }

        memorizedSpells.add(spell);
        LOGGER.info("Memorized spell: {}!", spell.getDisplayName());
        return true;
    }

    /**
     * Forget a memorized spell
     */
    public boolean forgetSpell(Spell spell) {
        if (memorizedSpells.remove(spell)) {
            LOGGER.info("Forgot spell: {}", spell.getDisplayName());
            return true;
        }
        return false;
    }

    /**
     * Check if player can cast a spell
     */
    public boolean canCast(Spell spell) {
        if (!memorizedSpells.contains(spell)) {
            return false;
        }

        // Check if we have enough MP
        int mpCost = spell.getLevel() * 2; // Simplified: level * 2 MP
        if (currentMP < mpCost) {
            return false;
        }

        // Check if we have sufficient skill
        Skill primarySchool = spell.getPrimarySchool();
        int skillLevel = getSkillLevel(primarySchool);
        return spell.canCast(skillLevel);
    }

    /**
     * Cast a spell (deduct MP)
     */
    public boolean castSpell(Spell spell) {
        if (!canCast(spell)) {
            return false;
        }

        int mpCost = spell.getLevel() * 2;
        currentMP -= mpCost;
        LOGGER.debug("Cast {}, {} MP remaining", spell.getDisplayName(), currentMP);
        return true;
    }

    // ===== Derived Stats =====

    /**
     * Calculate max spell slots based on XL and spellcasting skill
     */
    public int getMaxSpellSlots() {
        int spellcastingLevel = getSkillLevel(Skill.SPELLCASTING);
        return 5 + experienceLevel + (spellcastingLevel / 2);
    }

    /**
     * Update HP/MP when skills or XL change
     */
    private void updateDerivedStats() {
        // HP increases with Fighting skill and XL
        int fightingLevel = getSkillLevel(Skill.FIGHTING);
        maxHP = 15 + race.getHpModifier() * 2
                + experienceLevel * 2
                + fightingLevel;

        // MP increases with Spellcasting skill and XL
        int spellcastingLevel = getSkillLevel(Skill.SPELLCASTING);
        maxMP = 5 + race.getMpModifier() * 2
                + experienceLevel
                + spellcastingLevel * 2;

        // Don't let current values exceed max
        currentHP = Math.min(currentHP, maxHP);
        currentMP = Math.min(currentMP, maxMP);
    }

    /**
     * Gain experience and possibly level up
     */
    public void gainXP(int xp) {
        totalXP += xp;

        // Check for level up (simplified: 100 XP per level)
        int xpForNextLevel = experienceLevel * 100;
        if (totalXP >= xpForNextLevel && experienceLevel < 27) {
            experienceLevel++;
            LOGGER.info("═══════════════════════════════════════════════════════");
            LOGGER.info("  LEVEL UP! You are now level {}!", experienceLevel);
            LOGGER.info("═══════════════════════════════════════════════════════");
            updateDerivedStats();

            // Fully heal on level up
            currentHP = maxHP;
            currentMP = maxMP;
        }
    }

    /**
     * Heal HP
     */
    public void heal(int amount) {
        currentHP = Math.min(currentHP + amount, maxHP);
    }

    /**
     * Restore MP
     */
    public void restoreMP(int amount) {
        currentMP = Math.min(currentMP + amount, maxMP);
    }

    /**
     * Take damage
     */
    public void takeDamage(int damage) {
        currentHP = Math.max(0, currentHP - damage);
        if (currentHP == 0) {
            LOGGER.warn("Character {} has died!", characterName);
        }
    }

    /**
     * Check if character is alive
     */
    public boolean isAlive() {
        return currentHP > 0;
    }

    /**
     * Get character summary for display
     */
    public String getSummary() {
        return String.format("%s the %s - Level %d | HP: %d/%d | MP: %d/%d | XP: %d",
                characterName,
                race.getDisplayName(),
                experienceLevel,
                currentHP, maxHP,
                currentMP, maxMP,
                totalXP);
    }
}
