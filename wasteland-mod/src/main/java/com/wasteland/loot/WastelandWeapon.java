package com.wasteland.loot;

import net.minecraft.world.item.ItemStack;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.equipment.WeaponType;

/**
 * Weapon item with DCSS-style damage and speed stats.
 * Extends WastelandItem to inherit rarity, enchantment, and artifact properties.
 */
public class WastelandWeapon extends WastelandItem {
    private WeaponType weaponType;
    private int bonusAccuracy;  // To-hit bonus (separate from enchantment)
    private int bonusSlaying;   // Damage bonus (separate from enchantment)

    /**
     * Create weapon from existing ItemStack (loads from NBT)
     */
    public WastelandWeapon(ItemStack itemStack, WeaponType weaponType) {
        super(itemStack);
        this.weaponType = weaponType;
        this.bonusAccuracy = 0;
        this.bonusSlaying = 0;
        loadWeaponFromNBT();
    }

    /**
     * Create new weapon with specific properties
     */
    public WastelandWeapon(ItemStack itemStack, WeaponType weaponType, ItemRarity rarity, int enchantmentLevel) {
        super(itemStack, rarity, enchantmentLevel);
        this.weaponType = weaponType;
        this.bonusAccuracy = 0;
        this.bonusSlaying = 0;
        saveWeaponToNBT();
    }

    /**
     * Load weapon-specific properties from NBT
     */
    private void loadWeaponFromNBT() {
        var tag = itemStack.getTag();
        if (tag == null) return;

        if (tag.contains("WastelandWeaponType")) {
            try {
                this.weaponType = WeaponType.valueOf(tag.getString("WastelandWeaponType"));
            } catch (IllegalArgumentException e) {
                // Keep default
            }
        }

        if (tag.contains("WastelandBonusAccuracy")) {
            this.bonusAccuracy = tag.getInt("WastelandBonusAccuracy");
        }

        if (tag.contains("WastelandBonusSlaying")) {
            this.bonusSlaying = tag.getInt("WastelandBonusSlaying");
        }
    }

    /**
     * Save weapon-specific properties to NBT
     */
    private void saveWeaponToNBT() {
        var tag = itemStack.getOrCreateTag();
        tag.putString("WastelandWeaponType", weaponType.name());
        tag.putInt("WastelandBonusAccuracy", bonusAccuracy);
        tag.putInt("WastelandBonusSlaying", bonusSlaying);
    }

    @Override
    public void saveToNBT() {
        super.saveToNBT();
        saveWeaponToNBT();
    }

    @Override
    protected String getBaseItemName() {
        return weaponType.getDisplayName();
    }

    /**
     * Calculate total damage for this weapon with a character
     * @param character The character wielding the weapon
     * @return Total damage (including stats, skills, enchantment, and properties)
     */
    public int calculateDamage(PlayerCharacter character) {
        // Base weapon damage
        int damage = weaponType.getBaseDamage();

        // Enchantment bonus (direct damage increase)
        damage += enchantmentLevel;

        // Slaying bonus from weapon
        damage += bonusSlaying;

        // Artifact SLAYING property
        if (hasArtifactProperty(ArtifactProperty.SLAYING)) {
            damage += getArtifactProperty(ArtifactProperty.SLAYING);
        }

        // Strength bonus: +1 damage per 2 STR above 8
        int strength = character.getStrength();
        int strBonus = Math.max(0, (strength - 8) / 2);
        damage += strBonus;

        // Weapon skill bonus: +1 damage per 3 skill levels
        int skillLevel = character.getSkillLevel(weaponType.getSkill());
        int skillBonus = skillLevel / 3;
        damage += skillBonus;

        return Math.max(1, damage); // Minimum 1 damage
    }

    /**
     * Calculate accuracy (to-hit) for this weapon with a character
     * @param character The character wielding the weapon
     * @return Total accuracy bonus
     */
    public int calculateAccuracy(PlayerCharacter character) {
        int accuracy = 0;

        // Base accuracy from enchantment
        accuracy += enchantmentLevel;

        // Bonus accuracy from weapon
        accuracy += bonusAccuracy;

        // Artifact ACCURACY property
        if (hasArtifactProperty(ArtifactProperty.ACCURACY)) {
            accuracy += getArtifactProperty(ArtifactProperty.ACCURACY);
        }

        // Artifact SLAYING property (affects both damage and accuracy)
        if (hasArtifactProperty(ArtifactProperty.SLAYING)) {
            accuracy += getArtifactProperty(ArtifactProperty.SLAYING);
        }

        // Dexterity bonus: +1 accuracy per 2 DEX above 8
        int dexterity = character.getDexterity();
        int dexBonus = Math.max(0, (dexterity - 8) / 2);
        accuracy += dexBonus;

        // Weapon skill bonus: +1 accuracy per 2 skill levels
        int skillLevel = character.getSkillLevel(weaponType.getSkill());
        int skillAccuracy = skillLevel / 2;
        accuracy += skillAccuracy;

        return accuracy;
    }

    /**
     * Get attack delay (lower = faster)
     * Modified by artifact properties and enchantment
     */
    public int getAttackDelay() {
        int delay = weaponType.getBaseDelay();

        // Attack speed artifact property reduces delay
        if (hasArtifactProperty(ArtifactProperty.ATTACK_SPEED)) {
            int speedBonus = getArtifactProperty(ArtifactProperty.ATTACK_SPEED);
            delay = Math.max(5, delay - speedBonus); // Minimum delay of 5
        }

        return delay;
    }

    /**
     * Get weapon type
     */
    public WeaponType getWeaponType() {
        return weaponType;
    }

    /**
     * Set bonus accuracy (for special weapon generation)
     */
    public void setBonusAccuracy(int bonus) {
        this.bonusAccuracy = bonus;
        saveToNBT();
    }

    /**
     * Set bonus slaying (for special weapon generation)
     */
    public void setBonusSlaying(int bonus) {
        this.bonusSlaying = bonus;
        saveToNBT();
    }

    /**
     * Get bonus accuracy
     */
    public int getBonusAccuracy() {
        return bonusAccuracy;
    }

    /**
     * Get bonus slaying
     */
    public int getBonusSlaying() {
        return bonusSlaying;
    }

    @Override
    public java.util.List<String> getTooltip() {
        var tooltip = super.getTooltip();

        // Show weapon stats
        tooltip.add("§7Damage: " + weaponType.getBaseDamage() +
                   (enchantmentLevel != 0 ? " (" + (enchantmentLevel > 0 ? "+" : "") + enchantmentLevel + ")" : ""));

        tooltip.add("§7Attack Delay: " + getAttackDelay());

        if (bonusAccuracy != 0) {
            tooltip.add("§7Accuracy: " + (bonusAccuracy > 0 ? "+" : "") + bonusAccuracy);
        }

        if (bonusSlaying != 0) {
            tooltip.add("§7Slaying: " + (bonusSlaying > 0 ? "+" : "") + bonusSlaying);
        }

        // Show handedness
        if (weaponType.isTwoHanded()) {
            tooltip.add("§7(Two-handed)");
        } else if (weaponType.isOneHanded()) {
            tooltip.add("§7(One-handed)");
        }

        // Show required skill
        tooltip.add("§7Skill: " + weaponType.getSkill().getDisplayName());

        return tooltip;
    }
}
