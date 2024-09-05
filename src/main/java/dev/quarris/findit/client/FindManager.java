package dev.quarris.findit.client;

import dev.quarris.findit.Config;
import dev.quarris.findit.ModRef;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Client side
public class FindManager {

    private static int timer = 0;
    private static final Set<BlockPos> POSES = new HashSet<>();

    public static void tick() {
        if (timer > 0) {
            timer--;
            if (timer == 0) {
                POSES.clear();
            }
        }
    }

    public static Set<BlockPos> getPoses() {
        return Collections.unmodifiableSet(POSES);
    }

    public static int getTimer() {
        return timer;
    }

    public static void setPoses(Collection<BlockPos> poses) {
        FindManager.POSES.clear();
        FindManager.POSES.addAll(poses);
        FindManager.timer = Config.outlineTimer;
    }



}
