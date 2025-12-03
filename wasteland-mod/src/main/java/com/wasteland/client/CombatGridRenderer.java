package com.wasteland.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wasteland.WastelandMod;
import com.wasteland.combat.CombatManager;
import com.wasteland.combat.Combatant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.Set;

/**
 * Renders the tactical grid overlay during combat.
 * Shows valid moves, enemy positions, and spell targeting.
 */
@Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CombatGridRenderer {

    /**
     * Render grid overlay after entities but before GUI
     */
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            return;
        }

        CombatManager combat = CombatManager.getInstance();
        if (!combat.isInCombat()) {
            return; // Not in combat, don't render grid
        }

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        // Use client minecraft instance to get render buffers
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

        // Render valid move highlights (green)
        renderValidMoves(poseStack, bufferSource, cameraPos, combat);

        // Render enemy positions (red)
        renderEnemyPositions(poseStack, bufferSource, cameraPos, combat);

        // Render grid lines
        renderGridLines(poseStack, bufferSource, cameraPos, combat);
    }

    /**
     * Render green highlights for valid movement positions
     */
    private static void renderValidMoves(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos, CombatManager combat) {
        Set<BlockPos> validMoves = combat.getValidMoves();

        for (BlockPos pos : validMoves) {
            renderSquareHighlight(poseStack, bufferSource, cameraPos, pos, 0.0f, 1.0f, 0.0f, 0.3f);
        }
    }

    /**
     * Render red highlights for enemy positions
     */
    private static void renderEnemyPositions(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos, CombatManager combat) {
        for (Combatant combatant : combat.getCombatants()) {
            if (!combatant.isPlayer() && combatant.isAlive()) {
                BlockPos pos = combatant.getEntity().blockPosition();
                renderSquareHighlight(poseStack, bufferSource, cameraPos, pos, 1.0f, 0.0f, 0.0f, 0.4f);
            }
        }
    }

    /**
     * Render grid lines around combat area
     */
    private static void renderGridLines(PoseStack poseStack, MultiBufferSource bufferSource, Vec3 cameraPos, CombatManager combat) {
        BlockPos center = combat.getGridCenter();
        if (center == null) {
            return;
        }

        int radius = CombatManager.GRID_RADIUS;

        // Render grid lines in white with low alpha
        for (int x = center.getX() - radius; x <= center.getX() + radius; x++) {
            for (int z = center.getZ() - radius; z <= center.getZ() + radius; z++) {
                BlockPos pos = new BlockPos(x, center.getY(), z);
                renderGridOutline(poseStack, bufferSource, cameraPos, pos, 1.0f, 1.0f, 1.0f, 0.15f);
            }
        }
    }

    /**
     * Render a filled square highlight at a block position
     */
    private static void renderSquareHighlight(PoseStack poseStack, MultiBufferSource bufferSource,
                                             Vec3 cameraPos, BlockPos pos,
                                             float r, float g, float b, float alpha) {
        poseStack.pushPose();

        // Translate to block position relative to camera
        poseStack.translate(
            pos.getX() - cameraPos.x,
            pos.getY() - cameraPos.y + 0.01, // Slightly above ground to avoid z-fighting
            pos.getZ() - cameraPos.z
        );

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.debugLineStrip(1.0));

        // Draw filled quad
        drawQuad(builder, matrix, 0, 0, 1, 1, r, g, b, alpha);

        poseStack.popPose();
    }

    /**
     * Render an outline around a grid square
     */
    private static void renderGridOutline(PoseStack poseStack, MultiBufferSource bufferSource,
                                         Vec3 cameraPos, BlockPos pos,
                                         float r, float g, float b, float alpha) {
        poseStack.pushPose();

        poseStack.translate(
            pos.getX() - cameraPos.x,
            pos.getY() - cameraPos.y + 0.02,
            pos.getZ() - cameraPos.z
        );

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer builder = bufferSource.getBuffer(RenderType.lines());

        // Draw outline (4 lines forming a square)
        drawLine(builder, matrix, 0, 0, 1, 0, r, g, b, alpha); // Bottom
        drawLine(builder, matrix, 1, 0, 1, 1, r, g, b, alpha); // Right
        drawLine(builder, matrix, 1, 1, 0, 1, r, g, b, alpha); // Top
        drawLine(builder, matrix, 0, 1, 0, 0, r, g, b, alpha); // Left

        poseStack.popPose();
    }

    /**
     * Draw a quad (filled rectangle)
     */
    private static void drawQuad(VertexConsumer builder, Matrix4f matrix,
                                 float x1, float z1, float x2, float z2,
                                 float r, float g, float b, float alpha) {
        builder.vertex(matrix, x1, 0, z1).color(r, g, b, alpha).endVertex();
        builder.vertex(matrix, x2, 0, z1).color(r, g, b, alpha).endVertex();
        builder.vertex(matrix, x2, 0, z2).color(r, g, b, alpha).endVertex();
        builder.vertex(matrix, x1, 0, z2).color(r, g, b, alpha).endVertex();
    }

    /**
     * Draw a line
     */
    private static void drawLine(VertexConsumer builder, Matrix4f matrix,
                                 float x1, float z1, float x2, float z2,
                                 float r, float g, float b, float alpha) {
        builder.vertex(matrix, x1, 0, z1).color(r, g, b, alpha).normal(0, 1, 0).endVertex();
        builder.vertex(matrix, x2, 0, z2).color(r, g, b, alpha).normal(0, 1, 0).endVertex();
    }
}
