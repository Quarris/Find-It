package dev.quarris.findit.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public class ClientRef {

    public static final Lazy<KeyMapping> SEARCH_KEY = Lazy.of(() -> new KeyMapping("key.findit.search", KeyConflictContext.GUI, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Y, "key.categories.findit"));

}
