package com.wasteland.client;

import com.wasteland.WastelandMod;
import com.wasteland.character.CharacterManager;
import com.wasteland.character.PlayerCharacter;
import com.wasteland.client.gui.CharacterSheetScreen;
import com.wasteland.client.gui.SkillsScreen;
import com.wasteland.client.gui.SpellsScreen;
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

    /**
     * Handle client tick for checking keybinds
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // Get player character
        PlayerCharacter character = CharacterManager.getOrCreateDefault(mc.player.getUUID());

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

        // Check for 'z' key (spell cast)
        if (KeyBindings.SPELL_CAST.consumeClick()) {
            // TODO: Implement spell casting UI
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Spell casting not yet implemented"),
                    true
                );
            }
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
            event.register(KeyBindings.SKILLS_SCREEN);
            event.register(KeyBindings.SPELL_MEMORIZE);
            event.register(KeyBindings.SPELL_CAST);
            event.register(KeyBindings.SPELL_LIST);
            event.register(KeyBindings.ABILITIES);
        }
    }
}
