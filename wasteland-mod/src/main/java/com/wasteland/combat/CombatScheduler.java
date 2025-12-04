package com.wasteland.combat;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.wasteland.WastelandMod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Schedules combat actions with delays
 * Allows enemy turns to be visible before advancing to next turn
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class CombatScheduler {

    private static class ScheduledTask {
        final Runnable task;
        int ticksRemaining;

        ScheduledTask(Runnable task, int delay) {
            this.task = task;
            this.ticksRemaining = delay;
        }
    }

    private static final List<ScheduledTask> tasks = new ArrayList<>();

    /**
     * Schedule a task to run after a delay
     * @param task The runnable to execute
     * @param delayTicks Number of ticks to wait (20 = 1 second)
     */
    public static void scheduleAfterDelay(Runnable task, int delayTicks) {
        tasks.add(new ScheduledTask(task, delayTicks));
        System.out.println("[Scheduler] Scheduled task with " + delayTicks + " tick delay");
    }

    /**
     * Cancel all pending tasks
     */
    public static void cancelAll() {
        tasks.clear();
        System.out.println("[Scheduler] Cancelled all tasks");
    }

    /**
     * Tick all scheduled tasks
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        // Tick down all tasks
        Iterator<ScheduledTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            ScheduledTask task = iterator.next();
            task.ticksRemaining--;

            if (task.ticksRemaining <= 0) {
                // Task is ready, execute it
                try {
                    task.task.run();
                } catch (Exception e) {
                    System.err.println("[Scheduler] Error executing task: " + e.getMessage());
                    e.printStackTrace();
                }
                iterator.remove();
            }
        }
    }

    /**
     * Check if any tasks are pending
     */
    public static boolean hasPendingTasks() {
        return !tasks.isEmpty();
    }

    /**
     * Get number of pending tasks
     */
    public static int getPendingCount() {
        return tasks.size();
    }
}
