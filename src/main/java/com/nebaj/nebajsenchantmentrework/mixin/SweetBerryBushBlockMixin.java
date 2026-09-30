package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.event.XpSourceEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SweetBerryBushBlock.class)
public abstract class SweetBerryBushBlockMixin
{
    // The setBlock call is only reached when berries are actually harvested
    @Inject(method = "useWithoutItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"))
    private void nebajsenchantmentrework$awardHarvestXp(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    {
        XpSourceEvents.awardBerryHarvestXp(level, pos);
    }
}
