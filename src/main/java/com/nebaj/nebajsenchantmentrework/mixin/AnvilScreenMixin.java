package com.nebaj.nebajsenchantmentrework.mixin;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin
{
    @ModifyConstant(method = "extractLabels", constant = @Constant(intValue = 40))
    private int nebajsenchantmentrework$removeTooExpensiveThreshold(int tooExpensiveCost)
    {
        return Integer.MAX_VALUE;
    }
}
