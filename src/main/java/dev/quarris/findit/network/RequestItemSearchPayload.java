package dev.quarris.findit.network;

import com.google.common.collect.Lists;
import dev.quarris.findit.ModRef;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public record RequestItemSearchPayload(ResourceLocation itemName) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<RequestItemSearchPayload> TYPE = new Type<>(ModRef.res("request_item_search"));

    public static final StreamCodec<ByteBuf, RequestItemSearchPayload> STREAM_CODEC = StreamCodec.composite(
        ResourceLocation.STREAM_CODEC,
        RequestItemSearchPayload::itemName,
        RequestItemSearchPayload::new
    );

    public static void handlePayload(final RequestItemSearchPayload payload, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (BuiltInRegistries.ITEM.containsKey(payload.itemName())) {
                Item item = BuiltInRegistries.ITEM.get(payload.itemName());
                ServerPlayer player = (ServerPlayer) ctx.player();
                Set<BlockPos> poses = new HashSet<>();
                int chunkOffset = Math.ceilDiv(ModRef.Constants.FIND_DISTANCE, 16);
                for (int chunkX = -chunkOffset; chunkX <= chunkOffset; chunkX++) {
                    for (int chunkZ = -chunkOffset; chunkZ <= chunkOffset; chunkZ++) {
                        for (Map.Entry<BlockPos, BlockEntity> entry : player.serverLevel().getChunk(player.chunkPosition().x + chunkX, player.chunkPosition().z + chunkZ).getBlockEntities().entrySet()) {
                            BlockPos pos = entry.getKey();
                            if (player.distanceToSqr(pos.getCenter()) > ModRef.Constants.FIND_DISTANCE * ModRef.Constants.FIND_DISTANCE) {
                                continue;
                            }

                            BlockEntity be = entry.getValue();
                            if (be instanceof RandomizableContainer container && container.getLootTable() != null) {
                                continue;
                            }

                            IItemHandler items = player.serverLevel().getCapability(Capabilities.ItemHandler.BLOCK, pos, null);

                            if (items == null) continue;

                            for (int slot = 0; slot < items.getSlots(); slot++) {
                                ItemStack stack = items.getStackInSlot(slot);
                                if (stack.is(item)) {
                                    poses.add(pos);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (!poses.isEmpty()) {
                    player.closeContainer();
                    ctx.reply(new FindItemPayload(Lists.newArrayList(poses)));
                }
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
