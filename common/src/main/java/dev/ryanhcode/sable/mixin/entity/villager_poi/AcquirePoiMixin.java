package dev.ryanhcode.sable.mixin.entity.villager_poi;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SubLevelPoiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AcquirePoi.class)
public class AcquirePoiMixin {

    /**
     * When a villager is standing on a contraption, redirect the PoiManager scan center from the
     * villager's global position to its plotyard position so it can find job sites inside the sublevel.
     */
    @Redirect(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/PathfinderMob;blockPosition()Lnet/minecraft/core/BlockPos;")
    )
    private static BlockPos sable$redirectScanCenter(final PathfinderMob mob) {
        final SubLevel sl = Sable.HELPER.getTrackingSubLevel(mob);
        if (sl == null) return mob.blockPosition();
        return SubLevelPoiUtil.toPlotyard(sl, mob.position());
    }
}
