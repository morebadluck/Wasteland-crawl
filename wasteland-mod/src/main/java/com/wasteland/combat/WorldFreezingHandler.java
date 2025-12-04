package com.wasteland.combat;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.wasteland.WastelandMod;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Freezes non-combatant entities during turn-based combat using event interception.
 * This is cleaner than using NoAI flags as it doesn't modify entity state.
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldFreezingHandler {

    private static boolean combatActive = false;
    private static final Set<UUID> combatantUUIDs = new HashSet<>();

    /**
     * Activate world freezing for combat
     * @param combatants UUIDs of entities participating in combat (these will NOT be frozen)
     */
    public static void freeze(Set<UUID> combatants) {
        combatActive = true;
        combatantUUIDs.clear();
        combatantUUIDs.addAll(combatants);
        System.out.println("[WorldFreeze] World frozen. " + combatants.size() + " combatants active.");
    }

    /**
     * Deactivate world freezing
     */
    public static void unfreeze() {
        combatActive = false;
        combatantUUIDs.clear();
        System.out.println("[WorldFreeze] World unfrozen.");
    }

    /**
     * Check if world is currently frozen
     */
    public static boolean isFrozen() {
        return combatActive;
    }

    /**
     * Intercept entity tick events and cancel them for non-combatants
     */
    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        if (!combatActive) {
            return; // World not frozen, allow all ticks
        }

        LivingEntity entity = event.getEntity();
        UUID entityUUID = entity.getUUID();

        // Allow combatants to tick normally
        if (combatantUUIDs.contains(entityUUID)) {
            return;
        }

        // Freeze all non-combatants by cancelling their tick
        event.setCanceled(true);

        // Also zero out any movement they might have accumulated
        if (!entity.getDeltaMovement().equals(net.minecraft.world.phys.Vec3.ZERO)) {
            entity.setDeltaMovement(net.minecraft.world.phys.Vec3.ZERO);
        }
    }

    /**
     * Get number of currently frozen entities (for debugging)
     */
    public static int getFrozenCount() {
        return combatantUUIDs.size();
    }
}
