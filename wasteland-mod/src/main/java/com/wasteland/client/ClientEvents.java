package com.wasteland.client;

import com.wasteland.WastelandMod;
import com.wasteland.character.CharacterManager;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.client.gui.CharacterSheetScreen;
import com.wasteland.client.gui.EquipmentScreen;
import com.wasteland.client.gui.RaceSelectionScreen;
import com.wasteland.client.gui.SkillsScreen;
import com.wasteland.client.gui.SpellsScreen;
import com.wasteland.client.gui.SpellCastScreen;
import com.wasteland.combat.CombatDetection;
import com.wasteland.magic.SpellTargeting;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side event handler for keybinds and UI
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {
    private static boolean hasCheckedRaceSelection = false;

    /**
     * Handle client tick for checking keybinds
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            // Reset flag when player disconnects
            hasCheckedRaceSelection = false;
            return;
        }

        // Check if player needs to select a race (only once per session)
        if (!hasCheckedRaceSelection) {
            hasCheckedRaceSelection = true;
            if (!CharacterManager.hasCharacter(mc.player.getUUID())) {
                // Show race selection screen
                mc.setScreen(new RaceSelectionScreen());
                return; // Don't process keybinds until race is selected
            }
        }

        // Get player character (returns null if not created yet)
        PlayerCharacter character = CharacterManager.getCharacter(mc.player.getUUID());
        if (character == null) {
            // Race not selected yet, don't process keybinds
            return;
        }

        // Check for 'i' key (equipment screen) - doesn't require character selection
        if (KeyBindings.EQUIPMENT_SCREEN.consumeClick()) {
            mc.setScreen(new EquipmentScreen(mc.player));
        }

        // Check for @ key (character sheet) - using Shift+2
        if (KeyBindings.CHARACTER_SHEET.consumeClick()) {
            mc.setScreen(new CharacterSheetScreen(character));
        }

        // Check for 'm' key (skills)
        if (KeyBindings.SKILLS_SCREEN.consumeClick()) {
            mc.setScreen(new SkillsScreen(character));
        }

        // Check for 'M' key (Shift+M) (spell memorize)
        if (KeyBindings.SPELL_MEMORIZE.consumeClick()) {
            mc.setScreen(new SpellsScreen(character));
        }

        // Check for 'z' key (spell cast) - open spell selection screen
        if (KeyBindings.SPELL_CAST.consumeClick()) {
            mc.setScreen(new SpellCastScreen(character));
        }

        // Spell targeting controls (only active when targeting)
        SpellTargeting targeting = SpellTargeting.getInstance();
        if (targeting.isTargeting()) {
            // Tab - cycle to next target
            if (KeyBindings.CYCLE_TARGET.consumeClick()) {
                targeting.cycleTarget();
            }

            // Enter - confirm spell cast
            if (KeyBindings.CONFIRM_TARGET.consumeClick()) {
                targeting.confirmCast(mc.player);
            }

            // Escape - cancel targeting
            if (KeyBindings.CANCEL_TARGET.consumeClick()) {
                targeting.cancelTargeting();
            }
        }

        // Check for 'C' key (toggle auto-combat mode)
        if (KeyBindings.TOGGLE_AUTO_COMBAT.consumeClick()) {
            CombatDetection.toggleAutoCombatMode();
        }
    }

    /**
     * Register keybinds (client-side only)
     */
    @Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.CHARACTER_SHEET);
            event.register(KeyBindings.EQUIPMENT_SCREEN);
            event.register(KeyBindings.SKILLS_SCREEN);
            event.register(KeyBindings.SPELL_MEMORIZE);
            event.register(KeyBindings.SPELL_CAST);
            event.register(KeyBindings.SPELL_LIST);
            event.register(KeyBindings.ABILITIES);
            event.register(KeyBindings.TOGGLE_AUTO_COMBAT);
            event.register(KeyBindings.CYCLE_TARGET);
            event.register(KeyBindings.CONFIRM_TARGET);
            event.register(KeyBindings.CANCEL_TARGET);
        }
    }
}
