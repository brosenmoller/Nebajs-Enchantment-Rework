package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.nebaj.nebajsenchantmentrework.client.MiningEnchantmentBar;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// Swaps the bar under the hotbar for the Surging charge or Momentum build-up the same way vanilla swaps in the horse jump bar; the xp level number is still drawn on top
@Mixin(Hud.class)
public abstract class HudMixin
{
    @WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void nebajsenchantmentrework$extractMiningBarBackground(ContextualBar bar, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original)
    {
        original.call(nebajsenchantmentrework$selectBar(bar), graphics, deltaTracker);
    }

    @WrapOperation(method = "extractHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"))
    private void nebajsenchantmentrework$extractMiningBarRenderState(ContextualBar bar, GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, Operation<Void> original)
    {
        original.call(nebajsenchantmentrework$selectBar(bar), graphics, deltaTracker);
    }

    @Unique
    private static ContextualBar nebajsenchantmentrework$selectBar(ContextualBar vanillaBar)
    {
        return MiningEnchantmentBar.shouldReplaceContextualBar(Minecraft.getInstance()) ? MiningEnchantmentBar.INSTANCE : vanillaBar;
    }
}
