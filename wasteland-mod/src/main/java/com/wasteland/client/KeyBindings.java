package com.wasteland.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * DCSS-style keybindings for character management
 */
public class KeyBindings {
    public static final String CATEGORY = "key.categories.wasteland";

    // DCSS keybinds
    public static final KeyMapping CHARACTER_SHEET = new KeyMapping(
            "key.wasteland.character_sheet",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_MULTIPLY, // @ key (Shift+2 on US keyboard)
            CATEGORY
    );

    public static final KeyMapping SKILLS_SCREEN = new KeyMapping(
            "key.wasteland.skills",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            CATEGORY
    );

    public static final KeyMapping SPELL_MEMORIZE = new KeyMapping(
            "key.wasteland.spell_memorize",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_M, // Shift+M
            CATEGORY
    );

    public static final KeyMapping SPELL_CAST = new KeyMapping(
            "key.wasteland.spell_cast",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            CATEGORY
    );

    public static final KeyMapping SPELL_LIST = new KeyMapping(
            "key.wasteland.spell_list",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z, // Shift+Z
            CATEGORY
    );

    public static final KeyMapping ABILITIES = new KeyMapping(
            "key.wasteland.abilities",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_A,
            CATEGORY
    );

    public static final KeyMapping EQUIPMENT_SCREEN = new KeyMapping(
            "key.wasteland.equipment",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I, // I for Inventory/Equipment
            CATEGORY
    );

    // Combat keybinds
    public static final KeyMapping TOGGLE_AUTO_COMBAT = new KeyMapping(
            "key.wasteland.toggle_auto_combat",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_C, // C for Combat mode
            CATEGORY
    );

    // Targeting keybinds (for spells and combat)
    public static final KeyMapping CYCLE_TARGET = new KeyMapping(
            "key.wasteland.cycle_target",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_TAB,
            CATEGORY
    );

    public static final KeyMapping CONFIRM_TARGET = new KeyMapping(
            "key.wasteland.confirm_target",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_ENTER,
            CATEGORY
    );

    public static final KeyMapping CANCEL_TARGET = new KeyMapping(
            "key.wasteland.cancel_target",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_ESCAPE,
            CATEGORY
    );

    // TEMPORARY: Workaround for broken right-click
    public static final KeyMapping USE_INTERACT = new KeyMapping(
            "key.wasteland.use_interact",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_U, // U for Use/Interact
            CATEGORY
    );
}
