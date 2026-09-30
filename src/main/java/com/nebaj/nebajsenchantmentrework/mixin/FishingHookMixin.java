package com.nebaj.nebajsenchantmentrework.mixin;

import com.nebaj.nebajsenchantmentrework.data.XpSettingsLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(FishingHook.class)
public abstract class FishingHookMixin
{
    @ModifyArg(method = "retrieve", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ExperienceOrb;<init>(Lnet/minecraft/world/level/Level;DDDI)V"), index = 4)
    private int nebajsenchantmentrework$applyFishingXp(int vanillaXp)
    {
        Entity self = (Entity) (Object) this;
        return XpSettingsLoader.INSTANCE.current().fishingXp().sample(self.getRandom());
    }
}
