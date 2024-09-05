package dev.quarris.findit.client;

import dev.quarris.findit.ModRef;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler {


    @SubscribeEvent
    public static void registerKeymapping(RegisterKeyMappingsEvent event) {
        event.register(ClientRef.SEARCH_KEY.get());
    }

}
