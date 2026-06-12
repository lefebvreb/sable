package dev.ryanhcode.sable.util;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class SubLevelPoiUtil {

    private SubLevelPoiUtil() {}

    public static @Nullable SubLevel getSubLevelForPos(final Level level, final BlockPos pos) {
        return Sable.HELPER.getContaining(level, pos);
    }

    /** Converts a global entity position to the plotyard BlockPos within the given sublevel. */
    public static BlockPos toPlotyard(final SubLevel sl, final Vec3 globalPos) {
        return BlockPos.containing(sl.logicalPose().transformPositionInverse(globalPos));
    }
}
