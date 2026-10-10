package com.nebaj.nebajsenchantmentrework.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.zombie.Drowned;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Drowned.class)
public abstract class DrownedMixin
{
    // Vanilla's default hand drop chance is 0.085
    @Unique
    private static final float TRIDENT_DROP_CHANCE = 0.25f;

    @Inject(method = "populateDefaultEquipmentSlots", at = @At("TAIL"))
    private void nebajsenchantmentrework$raiseTridentDropChance(RandomSource random, DifficultyInstance difficulty, CallbackInfo ci)
    {
        Drowned self = (Drowned) (Object) this;
        if (!self.getMainHandItem().is(Items.TRIDENT)) return;

        self.setDropChance(EquipmentSlot.MAINHAND, TRIDENT_DROP_CHANCE);
    }
}
