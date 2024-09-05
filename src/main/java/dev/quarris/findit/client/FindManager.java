package dev.quarris.findit.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Client side
public class FindManager {

    private static long time = -1;
    private static final Set<BlockPos> POSES = new HashSet<>();

    public static void tick(Level level) {
        if (time > 0 && level.getGameTime() > time + 100) {
            POSES.clear();
        }
    }

    public static Set<BlockPos> getPoses() {
        return Collections.unmodifiableSet(POSES);
    }

    public static void setPoses(Collection<BlockPos> poses, long time) {
        FindManager.POSES.clear();
        FindManager.POSES.addAll(poses);
        FindManager.time = time;
    }



}
