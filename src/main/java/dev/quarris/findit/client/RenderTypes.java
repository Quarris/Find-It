package dev.quarris.findit.client;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.quarris.findit.ModRef;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public abstract class RenderTypes extends RenderType {

    public static final RenderPipeline.Snippet LINES_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_COLOR_FOG_SNIPPET)
        .withVertexShader("core/rendertype_lines")
        .withFragmentShader("core/rendertype_lines")
        .withUniform("LineWidth", UniformType.FLOAT)
        .withUniform("ScreenSize", UniformType.VEC2)
        .withBlend(BlendFunction.TRANSLUCENT)
        .withCull(false)
        .withVertexFormat(DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES)
        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
        .withColorWrite(true)
        .buildSnippet();

    public static final RenderPipeline LINES_PIPELINE = RenderPipeline.builder(LINES_SNIPPET).withLocation(ModRef.res("pipeline/lines")).build();

    public static final RenderType LINES = create(
        "find_it_outlines",
        1536,
        LINES_PIPELINE,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
            /*.setDepthTestState(DepthTestFunction.NO_DEPTH_TEST)
            .setShaderState(RENDERTYPE_LINES_SHADER)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setWriteMaskState(COLOR_DEPTH_WRITE)
            .setCullState(NO_CULL)*/
    );

    public RenderTypes(String p_173178_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }


    public static RenderType lines() {
        return LINES;
    }
}
