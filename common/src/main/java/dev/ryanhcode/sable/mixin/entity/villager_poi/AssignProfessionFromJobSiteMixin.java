package dev.ryanhcode.sable.mixin.entity.villager_poi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SubLevelPoiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AssignProfessionFromJobSite.class)
public class AssignProfessionFromJobSiteMixin {

    @WrapOperation(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/core/BlockPos;closerToCenterThan(Lnet/minecraft/core/Position;D)Z")
    )
    private static boolean sable$fixProximityCheck(
        final BlockPos potentialSitePos, final Position entityPos, final double dist, final Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) final Villager villager
    ) {
        final SubLevel sl = SubLevelPoiUtil.getSubLevelForPos(villager.level(), potentialSitePos);
        if (sl == null) return original.call(potentialSitePos, entityPos, dist);
        return potentialSitePos.closerToCenterThan(
            SubLevelPoiUtil.toPlotyard(sl, villager.position()).getCenter(), dist
        );
    }
}
