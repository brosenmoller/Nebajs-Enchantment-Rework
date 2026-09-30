package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.event.XpSourceEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.CaveVinesBlock;
import net.minecraft.world.level.block.CaveVinesPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Both vine blocks delegate to the static CaveVines.use, which always harvests when berries are present
@Mixin({CaveVinesBlock.class, CaveVinesPlantBlock.class})
public abstract class CaveVinesMixin
{
    @Inject(method = "useWithoutItem", at = @At("HEAD"))
    private void nebajsenchantmentrework$awardHarvestXp(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir)
    {
        if (!CaveVines.hasGlowBerries(state)) return;

        XpSourceEvents.awardBerryHarvestXp(level, pos);
    }
}
