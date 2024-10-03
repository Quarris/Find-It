package dev.quarris.findit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.quarris.findit.Config;
import dev.quarris.findit.ModRef;
import dev.quarris.findit.network.RequestItemSearchPayload;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID)
public class ClientEventHandler {

    private static ItemStack lastStack = ItemStack.EMPTY;
    private static long lastCheckTime = 0;

    @SubscribeEvent
    public static void cacheLastHoveredItem(RenderTooltipEvent.Pre event) {
        if (Minecraft.getInstance().level != null && !event.getItemStack().isEmpty()) {
            lastStack = event.getItemStack();
            lastCheckTime = Minecraft.getInstance().level.getGameTime();
        }
    }

    @SubscribeEvent
    public static void clickInScreen(ScreenEvent.KeyPressed.Pre event) {
        if (Minecraft.getInstance().level != null && ClientRef.SEARCH_KEY.get().matches(event.getKeyCode(), event.getScanCode())) {
            if (Minecraft.getInstance().level.getGameTime() - lastCheckTime < 2) {
                Minecraft.getInstance().getConnection().send(new RequestItemSearchPayload(BuiltInRegistries.ITEM.getKey(lastStack.getItem())));
            }
        }
    }

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

        int counter = FindManager.getTimer();

        float alpha = counter < 20 ? counter / 20f : 1;
        float red = Config.outlineColor >> 16 & 0xff;
        float green = Config.outlineColor >> 8 & 0xff;
        float blue = Config.outlineColor & 0xff;

        for (BlockPos pose : FindManager.getPoses()) {
            BlockState state = level.getBlockState(pose);
            VoxelShape shape = state.getShape(level, pose).move(pose.getX(), pose.getY(), pose.getZ());
            LevelRenderer.renderVoxelShape(poseStack, buffer.getBuffer(RenderTypes.lines()), shape, 0, 0, 0, red / 0xff, green / 0xff, blue / 0xff, alpha, true);
        }

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("findit")
                .then(Commands.literal("color")
                    .then(Commands.argument("red", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("green", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("blue", IntegerArgumentType.integer(0, 255))
                                .executes(ctx -> {
                                    int red = IntegerArgumentType.getInteger(ctx, "red");
                                    int green = IntegerArgumentType.getInteger(ctx, "green");
                                    int blue = IntegerArgumentType.getInteger(ctx, "blue");
                                    int color = (red << 16) + (green << 8) + blue;
                                    Config.OUTLINE_COLOR.set(color);
                                    Config.OUTLINE_COLOR.save();
                                    Config.reload();
                                    ctx.getSource().sendSuccess(() -> Component.literal("The color has been changed.").withStyle(Style.EMPTY.withColor(color)), false);
                                    return 1;
                                })))))
                .then(Commands.literal("timer")
                    .then(Commands.argument("timer", IntegerArgumentType.integer(10, 300))
                        .executes(ctx -> {
                            int timer = IntegerArgumentType.getInteger(ctx, "timer");
                            Config.OUTLINE_TIMER.set(timer);
                            Config.OUTLINE_TIMER.save();
                            Config.reload();
                            ctx.getSource().sendSuccess(() -> Component.literal("The outline disappearance timer has been changed."), false);
                            return 1;
                        }))));
    }
}
