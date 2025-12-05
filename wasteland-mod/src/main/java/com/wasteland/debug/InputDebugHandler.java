package com.wasteland.debug;

import com.wasteland.WastelandMod;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Debug handler to log ALL input events and see why right-click isn't working
 * REMOVE THIS AFTER DEBUGGING
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputDebugHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static int eventCounter = 0;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMouseInput(InputEvent.MouseButton event) {
        eventCounter++;
        LOGGER.info("[INPUT-DEBUG #{}] MouseButton: button={}, action={}, canceled={}",
            eventCounter, event.getButton(), event.getAction(), event.isCanceled());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        eventCounter++;
        LOGGER.info("[INPUT-DEBUG #{}] RightClickBlock: hand={}, pos={}, face={}, canceled={}",
            eventCounter, event.getHand(), event.getPos(), event.getFace(), event.isCanceled());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        eventCounter++;
        LOGGER.info("[INPUT-DEBUG #{}] RightClickItem: hand={}, item={}, canceled={}",
            eventCounter, event.getHand(), event.getItemStack().getItem(), event.isCanceled());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        eventCounter++;
        LOGGER.info("[INPUT-DEBUG #{}] RightClickEmpty: hand={}, canceled={}",
            eventCounter, event.getHand(), event.isCanceled());
    }
}
