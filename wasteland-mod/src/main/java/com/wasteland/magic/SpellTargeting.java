package com.wasteland.magic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wasteland.combat.CombatManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles spell targeting for both entity and location-based spells.
 * Manages target selection, cycling, and visual feedback.
 */
public class SpellTargeting {
    private static final Logger LOGGER = LogManager.getLogger();

    private static SpellTargeting instance;

    // Current targeting state
    private boolean isTargeting = false;
    private Spell activeSpell = null;
    private TargetType targetType = null;

    // Entity targeting
    private LivingEntity selectedEntity = null;
    private List<LivingEntity> availableTargets = new ArrayList<>();
    private int currentTargetIndex = 0;

    // Location targeting
    private BlockPos selectedLocation = null;

    // Visual settings
    private static final float TARGET_RANGE_COLOR_R = 1.0f;
    private static final float TARGET_RANGE_COLOR_G = 0.8f;
    private static final float TARGET_RANGE_COLOR_B = 0.0f;
    private static final float TARGET_RANGE_COLOR_A = 0.3f;

    private SpellTargeting() {}

    public static SpellTargeting getInstance() {
        if (instance == null) {
            instance = new SpellTargeting();
        }
        return instance;
    }

    /**
     * Start targeting for a spell
     */
    public void startTargeting(Spell spell, Player caster) {
        if (spell == null || caster == null) {
            LOGGER.warn("Cannot start targeting with null spell or caster");
            return;
        }

        this.activeSpell = spell;
        this.targetType = spell.getTargetType();
        this.isTargeting = true;

        // Initialize based on target type
        if (targetType.requiresTarget()) {
            initializeEntityTargeting(caster, spell.getBaseRange());
        } else if (targetType.requiresLocation()) {
            initializeLocationTargeting(caster);
        }

        LOGGER.info("Started targeting for spell: {} (type: {})",
            spell.getDisplayName(), targetType.name());
    }

    /**
     * Initialize entity targeting
     */
    private void initializeEntityTargeting(Player caster, int range) {
        availableTargets.clear();
        selectedEntity = null;
        currentTargetIndex = 0;

        // Find all hostile entities within range
        Vec3 casterPos = caster.position();
        AABB searchBox = new AABB(casterPos.x - range, casterPos.y - range, casterPos.z - range,
                                   casterPos.x + range, casterPos.y + range, casterPos.z + range);

        List<LivingEntity> nearbyEntities = caster.level().getEntitiesOfClass(
            LivingEntity.class, searchBox,
            entity -> entity != caster && entity.isAlive() && isHostile(entity)
        );

        // Sort by distance
        nearbyEntities.sort(Comparator.comparingDouble(e -> e.distanceToSqr(caster)));

        // Filter by range
        for (LivingEntity entity : nearbyEntities) {
            if (caster.distanceTo(entity) <= range) {
                availableTargets.add(entity);
            }
        }

        // Auto-select nearest target
        if (!availableTargets.isEmpty()) {
            selectedEntity = availableTargets.get(0);
            currentTargetIndex = 0;
        }

        LOGGER.debug("Found {} valid targets within range {}", availableTargets.size(), range);
    }

    /**
     * Initialize location targeting
     */
    private void initializeLocationTargeting(Player caster) {
        selectedLocation = null;

        // Get block player is looking at
        Minecraft mc = Minecraft.getInstance();
        if (mc.hitResult instanceof BlockHitResult blockHit) {
            selectedLocation = blockHit.getBlockPos();
        }
    }

    /**
     * Cycle to next target (Tab key)
     */
    public void cycleTarget() {
        if (!isTargeting || !targetType.requiresTarget()) {
            return;
        }

        if (availableTargets.isEmpty()) {
            LOGGER.debug("No targets available to cycle");
            return;
        }

        currentTargetIndex = (currentTargetIndex + 1) % availableTargets.size();
        selectedEntity = availableTargets.get(currentTargetIndex);

        LOGGER.debug("Cycled to target {}/{}", currentTargetIndex + 1, availableTargets.size());
    }

    /**
     * Cycle to previous target (Shift+Tab)
     */
    public void cyclePreviousTarget() {
        if (!isTargeting || !targetType.requiresTarget()) {
            return;
        }

        if (availableTargets.isEmpty()) {
            return;
        }

        currentTargetIndex--;
        if (currentTargetIndex < 0) {
            currentTargetIndex = availableTargets.size() - 1;
        }
        selectedEntity = availableTargets.get(currentTargetIndex);

        LOGGER.debug("Cycled to target {}/{}", currentTargetIndex + 1, availableTargets.size());
    }

    /**
     * Select target by clicking on entity
     */
    public void selectTarget(LivingEntity entity) {
        if (!isTargeting || !targetType.requiresTarget()) {
            return;
        }

        if (availableTargets.contains(entity)) {
            selectedEntity = entity;
            currentTargetIndex = availableTargets.indexOf(entity);
            LOGGER.debug("Manually selected target: {}", entity.getName().getString());
        } else {
            LOGGER.warn("Attempted to select invalid target: {}", entity.getName().getString());
        }
    }

    /**
     * Select location by clicking
     */
    public void selectLocation(BlockPos pos) {
        if (!isTargeting || !targetType.requiresLocation()) {
            return;
        }

        selectedLocation = pos;
        LOGGER.debug("Selected location: {}", pos);
    }

    /**
     * Confirm spell cast with current target
     * @return true if spell was cast successfully
     */
    public boolean confirmCast(Player caster) {
        if (!isTargeting || activeSpell == null) {
            return false;
        }

        boolean success = false;

        try {
            if (targetType.requiresTarget()) {
                // Entity-targeted spell
                if (selectedEntity != null && selectedEntity.isAlive()) {
                    success = activeSpell.cast(caster, selectedEntity, null, caster.level());
                } else {
                    caster.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§cNo valid target selected!"),
                        true
                    );
                }
            } else if (targetType.requiresLocation()) {
                // Location-targeted spell
                if (selectedLocation != null) {
                    success = activeSpell.cast(caster, null, selectedLocation, caster.level());
                } else {
                    caster.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("§cNo location selected!"),
                        true
                    );
                }
            } else {
                // Self or non-targeted spell
                success = activeSpell.cast(caster, null, null, caster.level());
            }
        } finally {
            cancelTargeting();
        }

        return success;
    }

    /**
     * Cancel targeting
     */
    public void cancelTargeting() {
        isTargeting = false;
        activeSpell = null;
        targetType = null;
        selectedEntity = null;
        selectedLocation = null;
        availableTargets.clear();
        currentTargetIndex = 0;

        LOGGER.debug("Cancelled spell targeting");
    }

    /**
     * Check if currently targeting
     */
    public boolean isTargeting() {
        return isTargeting;
    }

    /**
     * Get currently selected entity
     */
    public LivingEntity getSelectedEntity() {
        return selectedEntity;
    }

    /**
     * Get currently selected location
     */
    public BlockPos getSelectedLocation() {
        return selectedLocation;
    }

    /**
     * Get active spell being targeted
     */
    public Spell getActiveSpell() {
        return activeSpell;
    }

    /**
     * Get available targets
     */
    public List<LivingEntity> getAvailableTargets() {
        return new ArrayList<>(availableTargets);
    }

    /**
     * Render targeting visuals
     */
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, Player player) {
        if (!isTargeting || activeSpell == null) {
            return;
        }

        if (targetType.requiresTarget() && selectedEntity != null) {
            renderEntityTarget(poseStack, bufferSource, selectedEntity);
        } else if (targetType.requiresLocation() && selectedLocation != null) {
            renderLocationTarget(poseStack, bufferSource, selectedLocation);
        }
    }

    /**
     * Render entity target indicator
     */
    private void renderEntityTarget(PoseStack poseStack, MultiBufferSource bufferSource, LivingEntity target) {
        // Draw bounding box around target
        AABB box = target.getBoundingBox().inflate(0.1);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        // Render box outline
        renderBox(poseStack, vertexConsumer, box, 1.0f, 0.0f, 0.0f, 1.0f);
    }

    /**
     * Render location target indicator
     */
    private void renderLocationTarget(PoseStack poseStack, MultiBufferSource bufferSource, BlockPos pos) {
        // Draw box at target location
        AABB box = new AABB(pos).inflate(0.02);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

        // Render box outline
        renderBox(poseStack, vertexConsumer, box, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    /**
     * Helper to render a box
     */
    private void renderBox(PoseStack poseStack, VertexConsumer consumer, AABB box,
                          float r, float g, float b, float a) {
        // This is a simplified version - in practice you'd want proper box rendering
        // For now, just a placeholder
    }

    /**
     * Check if entity is hostile
     */
    private boolean isHostile(LivingEntity entity) {
        // Check if it's a monster or otherwise hostile
        return entity.getType().getCategory() == net.minecraft.world.entity.MobCategory.MONSTER;
    }
}
