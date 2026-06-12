package dev.ryanhcode.sable.mixin.entity.villager_poi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SubLevelPoiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ValidateNearbyPoi.class)
public class ValidateNearbyPoiMixin {

    @WrapOperation(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z")
    )
    private static boolean sable$fixProximityCheck(
        final BlockPos storedPos, final Position entityPos, final double dist, final Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) final LivingEntity entity
    ) {
        final SubLevel sl = SubLevelPoiUtil.getSubLevelForPos(entity.level(), storedPos);
        if (sl == null) return original.call(storedPos, entityPos, dist);

        if (Sable.HELPER.getTrackingSubLevel(entity) == sl) {
            // Entity is on the contraption — compare in plotyard space.
            return storedPos.closerToCenterThan(
                SubLevelPoiUtil.toPlotyard(sl, entity.position()).getCenter(), dist
            );
        }

        // Entity is off the contraption — compare current global position of the job site.
        final Vec3 globalJobSitePos = Sable.HELPER.projectOutOfSubLevel(entity.level(), storedPos.getCenter());
        return globalJobSitePos.closerThan(entity.position(), dist);
    }
}
