package com.nebaj.nebajsenchantmentrework.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.nebaj.nebajsenchantmentrework.enchantment.LocationRestrictedEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.stream.Stream;

// Restricted enchantments may only come from loot that names them directly, so drop them from tag-based or unrestricted pools.
@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin
{
    @Shadow @Final private Optional<HolderSet<Enchantment>> options;

    @SuppressWarnings("unchecked")
    @ModifyExpressionValue(method = "run", at = @At(value = "INVOKE", target = "Ljava/util/Optional;orElseGet(Ljava/util/function/Supplier;)Ljava/lang/Object;"))
    private Object nebajsenchantmentrework$excludeLocationRestricted(Object candidates)
    {
        boolean namesEnchantmentsDirectly = options.isPresent() && !(options.get() instanceof HolderSet.Named);
        if (namesEnchantmentsDirectly) return candidates;

        return LocationRestrictedEnchantments.withoutRestricted((Stream<Holder<Enchantment>>) candidates);
    }
}
