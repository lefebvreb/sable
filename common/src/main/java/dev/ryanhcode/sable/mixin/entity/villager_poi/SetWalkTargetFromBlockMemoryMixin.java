package dev.ryanhcode.sable.mixin.entity.villager_poi;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SubLevelPoiUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SetWalkTargetFromBlockMemory.class)
public class SetWalkTargetFromBlockMemoryMixin {

    @Redirect(
        method = "*",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/npc/Villager;blockPosition()Lnet/minecraft/core/BlockPos;")
    )
    private static BlockPos sable$redirectBlockPos(final Villager villager) {
        final SubLevel sl = Sable.HELPER.getTrackingSubLevel(villager);
        if (sl == null) return villager.blockPosition();
        return SubLevelPoiUtil.toPlotyard(sl, villager.position());
    }
}
