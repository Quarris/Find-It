package dev.quarris.findit.network;

import dev.quarris.findit.ModRef;
import dev.quarris.findit.client.FindManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;
import java.util.Set;

public record FindItemPayload(List<BlockPos> positions) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FindItemPayload> TYPE = new CustomPacketPayload.Type<>(ModRef.res("find_item"));

    public static final StreamCodec<ByteBuf, FindItemPayload> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()),
        FindItemPayload::positions,
        FindItemPayload::new
    );

    public static void handlePayload(final FindItemPayload payload, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level level = ctx.player().level();
            FindManager.setPoses(payload.positions, level.getGameTime());
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
