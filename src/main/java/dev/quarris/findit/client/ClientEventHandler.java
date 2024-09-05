package dev.quarris.findit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.quarris.findit.ModRef;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID)
public class ClientEventHandler {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().level != null) {
            FindManager.tick();
        }
    }

    @SubscribeEvent
    private static void glowyglow(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }

        Level level = Minecraft.getInstance().level;
        MultiBufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        int counter = FindManager.getCounter();
        float alpha = counter < 20 ? counter / 20f : 1;

        for (BlockPos pose : FindManager.getPoses()) {
            BlockState state = level.getBlockState(pose);
            VoxelShape shape = state.getShape(level, pose).move(pose.getX(), pose.getY(), pose.getZ());
            LevelRenderer.renderVoxelShape(poseStack, buffer.getBuffer(RenderTypes.lines()), shape, 0, 0, 0, 1, 0.5f, 1, alpha, true);
        }

        poseStack.popPose();

    }
}
