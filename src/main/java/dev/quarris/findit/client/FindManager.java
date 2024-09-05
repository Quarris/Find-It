package dev.quarris.findit.client;

import dev.quarris.findit.ModRef;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Client side
public class FindManager {

    private static int counter = 0;
    private static final Set<BlockPos> POSES = new HashSet<>();

    public static void tick() {
        if (counter > 0) {
            counter--;
            if (counter == 0) {
                POSES.clear();
            }
        }
    }

    public static Set<BlockPos> getPoses() {
        return Collections.unmodifiableSet(POSES);
    }

    public static int getCounter() {
        return counter;
    }

    public static void setPoses(Collection<BlockPos> poses) {
        FindManager.POSES.clear();
        FindManager.POSES.addAll(poses);
        FindManager.counter = ModRef.Constants.MAX_COUNTER;
    }



}
