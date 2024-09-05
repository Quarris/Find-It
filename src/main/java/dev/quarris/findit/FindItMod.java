package dev.quarris.findit;

import dev.quarris.findit.network.FindItemPayload;
import dev.quarris.findit.network.RequestItemSearchPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(ModRef.ID)
public class FindItMod {

    private static ItemStack lastStack = ItemStack.EMPTY;
    private static long lastCheckTime = 0;

    public FindItMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::registerKeymapping);
        NeoForge.EVENT_BUS.addListener(this::clickInScreen);
        NeoForge.EVENT_BUS.addListener(this::cacheLastHoveredItem);

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    public void registerKeymapping(RegisterKeyMappingsEvent event) {
        event.register(ModRef.SEARCH_KEY.get());
    }

    public void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(RequestItemSearchPayload.TYPE, RequestItemSearchPayload.STREAM_CODEC, RequestItemSearchPayload::handlePayload);
        registrar.playToClient(FindItemPayload.TYPE, FindItemPayload.STREAM_CODEC, FindItemPayload::handlePayload);
    }

    public void cacheLastHoveredItem(RenderTooltipEvent.Pre event) {
        if (Minecraft.getInstance().level != null && !event.getItemStack().isEmpty()) {
            lastStack = event.getItemStack();
            lastCheckTime = Minecraft.getInstance().level.getGameTime();
        }
    }

    public void clickInScreen(ScreenEvent.KeyPressed.Pre event) {
        if (ModRef.SEARCH_KEY.get().matches(event.getKeyCode(), event.getScanCode())) {
            if (Minecraft.getInstance().level.getGameTime() - lastCheckTime < 2) {
                Minecraft.getInstance().getConnection().send(new RequestItemSearchPayload(BuiltInRegistries.ITEM.getKey(lastStack.getItem())));
            }
        }
    }
}
