package dev.ryanhcode.sable.mixin.entity.villager_poi;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SubLevelPoiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(ValidateNearbyPoi.class)
public class ValidateNearbyPoiMixin {

    private static final Logger LOGGER = LoggerFactory.getLogger("sable.poi");

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
            final boolean result = storedPos.closerToCenterThan(
                SubLevelPoiUtil.toPlotyard(sl, entity.position()).getCenter(), dist
            );
            if (!result && entity instanceof Villager v) {
                final Vec3 global = Sable.HELPER.projectOutOfSubLevel(entity.level(), storedPos.getCenter());
                LOGGER.warn("[POI] {} @ {} lost job site: distance check failed (on-contraption) plotyard={} global={}",
                    v.getUUID(), v.blockPosition(), storedPos, global);
            }
            return result;
        }

        final Vec3 globalJobSitePos = Sable.HELPER.projectOutOfSubLevel(entity.level(), storedPos.getCenter());
        final boolean result = globalJobSitePos.closerThan(entity.position(), dist);
        if (!result && entity instanceof Villager v) {
            LOGGER.warn("[POI] {} @ {} lost job site: distance check failed (off-contraption) plotyard={} global={}",
                v.getUUID(), v.blockPosition(), storedPos, globalJobSitePos);
        }
        return result;
    }

    /** Catches the "POI block no longer exists" path. */
    @WrapOperation(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;exists(Lnet/minecraft/core/BlockPos;Ljava/util/function/Predicate;)Z")
    )
    private static boolean sable$logPoiNotFound(
        final PoiManager poiManager, final BlockPos pos, final Predicate<Holder<PoiType>> predicate,
        final Operation<Boolean> original,
        @Local(argsOnly = true, ordinal = 0) final LivingEntity entity
    ) {
        final boolean exists = original.call(poiManager, pos, predicate);
        if (!exists && entity instanceof Villager v) {
            final SubLevel sl = SubLevelPoiUtil.getSubLevelForPos(entity.level(), pos);
            if (sl != null) {
                LOGGER.warn("[POI] {} @ {} lost job site: POI no longer exists in PoiManager plotyard={}",
                    v.getUUID(), v.blockPosition(), pos);
            } else {
                LOGGER.warn("[POI] {} @ {} lost job site: POI no longer exists in PoiManager pos={}",
                    v.getUUID(), v.blockPosition(), pos);
            }
        }
        return exists;
    }
}
