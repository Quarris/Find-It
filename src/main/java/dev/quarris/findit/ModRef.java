package dev.quarris.findit;

import net.minecraft.resources.ResourceLocation;

public class ModRef {

    public static final String ID = "findit";

    public static final int FIND_DISTANCE = 32;

    public static ResourceLocation res(String id) {
        return ResourceLocation.fromNamespaceAndPath(ID, id);
    }
}
