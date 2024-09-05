package dev.quarris.findit;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = ModRef.ID, bus = EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue OUTLINE_COLOR = BUILDER
        .comment(
            "Hex color of the outline to render.",
            "Format: 0xRRGGBB, where RR is red, GG is green, and BB is blue.",
            "Example: 0x4287f5",
            "Default: 0xffffff. (Yes I know it looks different. Use hex, it's easier.)"
        ).defineInRange("outline_color", 0xffffff, 0x000000, 0xffffff);

    public static final ModConfigSpec.IntValue OUTLINE_TIMER = BUILDER
        .comment(
            "How long (in ticks) will the outline appear for."
        ).defineInRange("outline_timer", 100, 10, 300);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int outlineColor;
    public static int outlineTimer;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        outlineColor = OUTLINE_COLOR.getAsInt();
        outlineTimer = OUTLINE_TIMER.getAsInt();
    }
}
