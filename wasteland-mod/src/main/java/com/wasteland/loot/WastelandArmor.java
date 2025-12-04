package com.wasteland.loot;

import net.minecraft.world.item.ItemStack;
import com.wasteland.character.PlayerCharacter;

/**
 * Armor item with DCSS-style AC and EV stats.
 * Extends WastelandItem to inherit rarity, enchantment, and artifact properties.
 */
public class WastelandArmor extends WastelandItem {
    private ArmorType armorType;

    /**
     * Create armor from existing ItemStack (loads from NBT)
     */
    public WastelandArmor(ItemStack itemStack, ArmorType armorType) {
        super(itemStack);
        this.armorType = armorType;
        loadArmorFromNBT();
    }

    /**
     * Create new armor with specific properties
     */
    public WastelandArmor(ItemStack itemStack, ArmorType armorType, ItemRarity rarity, int enchantmentLevel) {
        super(itemStack, rarity, enchantmentLevel);
        this.armorType = armorType;
        saveArmorToNBT();
    }

    /**
     * Load armor-specific properties from NBT
     */
    private void loadArmorFromNBT() {
        var tag = itemStack.getTag();
        if (tag == null) return;

        if (tag.contains("WastelandArmorType")) {
            try {
                this.armorType = ArmorType.valueOf(tag.getString("WastelandArmorType"));
            } catch (IllegalArgumentException e) {
                // Keep default
            }
        }
    }

    /**
     * Save armor-specific properties to NBT
     */
    private void saveArmorToNBT() {
        var tag = itemStack.getOrCreateTag();
        tag.putString("WastelandArmorType", armorType.name());
    }

    @Override
    public void saveToNBT() {
        super.saveToNBT();
        saveArmorToNBT();
    }

    @Override
    protected String getBaseItemName() {
        return armorType.getDisplayName();
    }

    /**
     * Calculate total AC (armor class) for this armor
     * Higher AC = better physical damage reduction
     * @param character The character wearing the armor
     * @return Total AC
     */
    public int calculateAC(PlayerCharacter character) {
        // Base AC from armor type
        int ac = armorType.getBaseAC();

        // Enchantment bonus (direct AC increase)
        ac += enchantmentLevel;

        // Artifact AC property
        if (hasArtifactProperty(ArtifactProperty.ARMOR_CLASS)) {
            ac += getArtifactProperty(ArtifactProperty.ARMOR_CLASS);
        }

        // Armor skill reduces encumbrance penalty
        int armorSkill = character.getSkillLevel(armorType.getSkill());
        int encumbrance = Math.max(0, armorType.getEncumbrance() - (armorSkill / 5));

        // High encumbrance slightly reduces effective AC if skill is low
        ac = Math.max(1, ac - (encumbrance / 3));

        return ac;
    }

    /**
     * Calculate EV (evasion) modifier for this armor
     * Negative EV = harder to dodge
     * @param character The character wearing the armor
     * @return EV modifier (usually negative for heavy armor)
     */
    public int calculateEVModifier(PlayerCharacter character) {
        // Base EV penalty from armor type
        int evMod = armorType.getBaseEVPenalty();

        // Enchantment reduces EV penalty by half (rounded down)
        // e.g., +3 enchantment reduces -4 EV to -2 EV
        if (enchantmentLevel > 0) {
            evMod = Math.min(0, evMod + (enchantmentLevel / 2));
        }

        // Artifact EV property
        if (hasArtifactProperty(ArtifactProperty.EVASION)) {
            evMod += getArtifactProperty(ArtifactProperty.EVASION);
        }

        // Armor skill reduces EV penalty
        int armorSkill = character.getSkillLevel(armorType.getSkill());
        int skillBonus = armorSkill / 3; // +1 EV per 3 skill levels
        evMod = Math.min(0, evMod + skillBonus);

        return evMod;
    }

    /**
     * Get total stat bonuses from this armor
     * @return [STR, DEX, INT] bonuses
     */
    public int[] getStatBonuses() {
        int str = 0, dex = 0, intel = 0;

        // Artifact stat properties
        if (hasArtifactProperty(ArtifactProperty.STRENGTH)) {
            str += getArtifactProperty(ArtifactProperty.STRENGTH);
        }
        if (hasArtifactProperty(ArtifactProperty.DEXTERITY)) {
            dex += getArtifactProperty(ArtifactProperty.DEXTERITY);
        }
        if (hasArtifactProperty(ArtifactProperty.INTELLIGENCE)) {
            intel += getArtifactProperty(ArtifactProperty.INTELLIGENCE);
        }

        return new int[]{str, dex, intel};
    }

    /**
     * Get armor type
     */
    public ArmorType getArmorType() {
        return armorType;
    }

    /**
     * Get equipment slot for this armor
     */
    public ArmorSlot getSlot() {
        return armorType.getSlot();
    }

    @Override
    public java.util.List<String> getTooltip() {
        var tooltip = super.getTooltip();

        // Show armor stats
        int displayAC = armorType.getBaseAC() + enchantmentLevel;
        tooltip.add("§7AC: +" + displayAC);

        int evPenalty = armorType.getBaseEVPenalty();
        if (evPenalty != 0) {
            // Show enchantment benefit for EV
            int displayEV = evPenalty;
            if (enchantmentLevel > 0) {
                displayEV = Math.min(0, evPenalty + (enchantmentLevel / 2));
            }

            if (displayEV < 0) {
                tooltip.add("§7EV: " + displayEV);
            } else if (displayEV > 0) {
                tooltip.add("§7EV: +" + displayEV);
            }
        }

        // Show encumbrance for body armor
        if (armorType.isBodyArmor() && armorType.getEncumbrance() > 0) {
            tooltip.add("§7Encumbrance: " + armorType.getEncumbrance());
        }

        // Show required skill
        tooltip.add("§7Skill: " + armorType.getSkill().getDisplayName());

        // Show slot
        tooltip.add("§7Slot: " + armorType.getSlot().getDisplayName());

        return tooltip;
    }
}
