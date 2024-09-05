package dev.quarris.findit;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ModRef {

    public static final String ID = "findit";

    public static final Lazy<KeyMapping> SEARCH_KEY = Lazy.of(() -> new KeyMapping("key.findit.search",KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, "key.categories.findit"));

    public static ResourceLocation res(String id) {
        return ResourceLocation.fromNamespaceAndPath(ID, id);
    }

    public static class Constants {
        public static final int FIND_DISTANCE = 32;
    }

}
